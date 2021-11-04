package tasks.evaluation;

import java.util.*;

public class Task2 {
    private static final Scanner scanner = new Scanner(System.in);

    public static String beautify(Map<Integer, Float> map) {
        StringBuilder sb = new StringBuilder();

        for (int key : map.keySet()) {
            sb.append(String.format("%d %.2f\n", key, map.get(key) * 100)
                            .replace(",", "."));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        var salvation = new ExpressionSolver().solve(scanner.nextLine());
        System.out.print(beautify(salvation));
    }
}

class Node {
    @Override
    public String toString() { return src; }
    protected String src;

    private int from;
    private int to;

    public boolean isOperator() { return Operators.contain(src); }

    public static Node create(String str) {
        if (Operators.contain(str)) {
            return Operators.get(str);
        }

        if (str.contains("d")) {
            String temp = str.substring(str.indexOf("d") + 1);
            int val = Integer.parseInt(temp);

            return new Node(str,1, val);
        }

        int val = Integer.parseInt(str);
        return new Node(str, val, val);
    }

    public Node() {}

    private Node(String src, int minIncluded, int maxIncluded) {
        this.src = src;
        from = minIncluded;
        to = maxIncluded;
    }

    public Map<Integer, Float> toMap() {
        var result = new HashMap<Integer, Float>();
        var avg = 1f / (to - from + 1f);

        for (int i = from; i <= to; i++)
            result.put(i, avg);

        return result;
    }
}

class Operators {

    private static final HashMap<String, Operator> operators;

    static {
        var multiply =  new BiOperator("*", 3) {
            @Override public int eval(int op1, int op2) { return op1 * op2; }};
        var sum =       new BiOperator("+", 2) {
            @Override public int eval(int op1, int op2) { return op1 + op2; }};
        var subtract =  new BiOperator("-", 2) {
            @Override public int eval(int op1, int op2) { return op1 - op2; }};
        var greater =   new BiOperator(">", 1) {
            @Override public int eval(int op1, int op2) { return op1 > op2 ? 1 : 0; } };
        var leftPar = new Operator("(", 0);
        var rightPar = new Operator(")", 0);

        operators = new HashMap<>();
        operators.put(multiply.toString(), multiply);
        operators.put(sum.toString(), sum);
        operators.put(subtract.toString(), subtract);
        operators.put(greater.toString(), greater);
        operators.put(leftPar.toString(), leftPar);
        operators.put(rightPar.toString(), rightPar);
    }

    public static boolean contain(String str) { return operators.containsKey(str); }

    public static Operator get(String str) { return operators.get(str); }
}

class Operator extends Node {
    public Operator(String src, int precedence) {
        this.src = src;
        this.precedence = precedence;
    }

    private final int precedence;

    public int getPrecedence() { return precedence; }
}

abstract class BiOperator extends Operator {
    public BiOperator(String src, int precedence) { super(src, precedence); }

    public abstract int eval(int op1, int op2);

    public Map<Integer, Float> probabilities(Map<Integer, Float> p1, Map<Integer, Float> p2) {
        Map<Integer, Float> probs = new HashMap<>();

        for (int key1 : p1.keySet()) {
            for (int key2 : p2.keySet()) {
                int result = eval(key1, key2);

                if (!probs.containsKey(result)) probs.put(result, 0f);

                var prob1 = p1.get(key1);
                var prob2 = p2.get(key2);
                probs.put(result, probs.get(result) + prob1 * prob2);
            }
        }

        return probs;
    }
}

class ExpressionSolver {
    List<Node> toListOfNodes(String str) {
        StringBuilder sb = new StringBuilder();
        List<Node> result = new ArrayList<>();

        for (char c : str.toCharArray()) {
            if (Operators.contain(Character.toString(c)))
                sb.append(" ").append(c).append(" ");
            else sb.append(c);
        }

        var args = sb.toString()
                .replaceAll(" {2}", " ")
                .trim().split(" ");

        for (String arg : args) {
            result.add(Node.create(arg));
        }

        return result;
    }

    public Map<Integer, Float> solve(String str) {
        Stack<Map<Integer, Float>> values = new Stack<>();
        Stack<String> ops = new Stack<>();

        var nodes = toListOfNodes(str);

        for (Node current : nodes) {
            if (!current.isOperator()) {
                values.push(current.toMap());
            }

            else if (current.toString().equals("(")) {
                ops.push(current.toString());
            }

            else if (current.toString().equals(")")) {
                while (!ops.peek().equals("(")) {
                    var other = values.pop();
                    BiOperator op = (BiOperator) Operators.get(ops.pop());

                    values.push(op.probabilities(values.pop(), other));
                }

                if(!ops.empty()) ops.pop();
            }

            else {
                while (!ops.empty() &&
                        Operators.get(ops.peek()).getPrecedence()
                        >= Operators.get(current.toString()).getPrecedence()) {

                    var other = values.pop();
                    BiOperator op = (BiOperator) Operators.get(ops.pop());

                    values.push(op.probabilities(values.pop(), other));
                }

                ops.push(current.toString());
            }
        }

        while (!ops.empty()) {
            var other = values.pop();
            BiOperator op = (BiOperator) Operators.get(ops.pop());

            values.push(op.probabilities(values.pop(), other));
        }

        return new TreeMap<>(values.pop());
    }
}