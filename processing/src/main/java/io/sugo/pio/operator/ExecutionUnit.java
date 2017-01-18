package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.execution.UnitExecutionFactory;
import io.sugo.pio.operator.execution.UnitExecutor;
import io.sugo.pio.ports.*;

import java.io.Serializable;
import java.util.*;

/**
 */
public class ExecutionUnit implements Serializable {
    private final PortOwner portOwner = new PortOwner() {
        @Override
        public Operator getOperator() {
            return getEnclosingOperator();
        }
    };

    private String name;

    private OperatorChain enclosingOperator;
    private final InputPorts innerInputPorts;
    private final OutputPorts innerOutputPorts;
    @JsonProperty("operators")
    private List<Operator> operators = new ArrayList<>();

    @JsonCreator
    public ExecutionUnit(
            OperatorChain enclosingOperator, String name
    ) {
        this.name = name;
        this.enclosingOperator = enclosingOperator;
        innerInputPorts = enclosingOperator.createInnerSinks(portOwner);
        innerOutputPorts = enclosingOperator.createInnerSources(portOwner);
    }

    public InputPorts getInnerSinks() {
        return innerInputPorts;
    }

    public OutputPorts getInnerSources() {
        return innerOutputPorts;
    }

    /**
     * Same as {@link #addOperator(Operator, boolean)}.
     */
    public int addOperator(Operator operator) {
        return addOperator(operator, true);
    }

    /**
     * Adds the operator to this execution unit.
     *
     * @param registerWithProcess Typically true. If false, the operator will not be registered with its parent
     *                            process.
     * @return the new index of the operator.
     */
    public int addOperator(Operator operator, boolean registerWithProcess) {
        if (operator == null) {
            throw new NullPointerException("operator cannot be null!");
        }
        if (operator instanceof ProcessRootOperator) {
            throw new IllegalArgumentException(
                    "'Process' operator cannot be added. It must always be the top-level operator!");
        }
        operators.add(operator);
        registerOperator(operator, registerWithProcess);
        return operators.size() - 1;
    }

    /**
     * Adds the operator to this execution unit. The operator at this index and all subsequent
     * operators are shifted to the right. The operator is registered automatically.
     */
    public void addOperator(Operator operator, int index) {
        if (operator == null) {
            throw new NullPointerException("operator cannot be null!");
        }
        if (operator instanceof ProcessRootOperator) {
            throw new IllegalArgumentException(
                    "'Process' operator cannot be added. It must always be the top-level operator!");
        }
        operators.add(index, operator);
        registerOperator(operator, true);
    }

    private void registerOperator(Operator operator, boolean registerWithProcess) {
        operator.setEnclosingExecutionUnit(this);
        OperatorProcess process = getEnclosingOperator().getProcess();
        if (process != null && registerWithProcess) {
            operator.registerOperator(process);
        }
    }

    public void setEnclosingOperator(OperatorChain enclosingOperator) {
        this.enclosingOperator = enclosingOperator;
    }

    /**
     * Helper class to count the number of dependencies of an operator.
     */
    private static class EdgeCounter {

        private final Map<Operator, Integer> numIncomingEdges = new LinkedHashMap<Operator, Integer>();

        private EdgeCounter(Collection<Operator> operators) {
            for (Operator op : operators) {
                numIncomingEdges.put(op, 0);
            }
        }

        private void incNumEdges(Operator op) {
            Integer num = numIncomingEdges.get(op);
            if (num == null) {
                // this can only happen if we add edges to inner ports of the enclosing operator.
                return;
            }
            num = num + 1;
            numIncomingEdges.put(op, num);
        }

        private int decNumEdges(Operator op) {
            Integer num = numIncomingEdges.get(op);
            // this can only happen if we add edges to inner ports of the enclosing operator.
            if (num == null) {
                return -1;
            }
            num = num - 1;
            assert num >= 0;
            numIncomingEdges.put(op, num);
            return num;
        }

        private LinkedList<Operator> getIndependentOperators() {
            LinkedList<Operator> independentOperators = new LinkedList<Operator>();
            for (Map.Entry<Operator, Integer> entry : numIncomingEdges.entrySet()) {
                if (entry.getValue() == null || entry.getValue() == 0) {
                    independentOperators.add(entry.getKey());
                }
            }
            return independentOperators;
        }
    }

    /**
     * Sorts the operators topologically, i.e. such that operator <var>i</var> in the returned
     * ordering has dependencies (i.e. connected {@link InputPort}s) only from operators
     * <var>0..i-1</var>.
     */
    public Vector<Operator> topologicalSort() {
        final Map<Operator, Integer> originalIndices = new HashMap<Operator, Integer>();
        for (int i = 0; i < operators.size(); i++) {
            originalIndices.put(operators.get(i), i);
        }
        EdgeCounter counter = new EdgeCounter(operators);
        for (Operator child : getOperators()) {
            for (OutputPort out : child.getOutputPorts().getAllPorts()) {
                InputPort dest = out.getDestination();
                if (dest != null) {
                    counter.incNumEdges(dest.getPorts().getOwner().getOperator());
                }
            }
        }
        Vector<Operator> sorted = new Vector<Operator>();
        PriorityQueue<Operator> independentOperators = new PriorityQueue<Operator>(Math.max(1, operators.size()),
                new Comparator<Operator>() {

                    @Override
                    public int compare(Operator o1, Operator o2) {
                        return originalIndices.get(o1) - originalIndices.get(o2);
                    }
                });
        independentOperators.addAll(counter.getIndependentOperators());
        while (!independentOperators.isEmpty()) {
            Operator first = independentOperators.poll();
            sorted.add(first);
            for (OutputPort out : first.getOutputPorts().getAllPorts()) {
                InputPort dest = out.getDestination();
                if (dest != null) {
                    Operator destOp = dest.getPorts().getOwner().getOperator();
                    if (counter.decNumEdges(destOp) == 0) {
                        // independentOperators.addFirst(destOp);
                        independentOperators.add(destOp);
                    }
                }
            }
        }
        return sorted;
    }

    public void transformMetaData() {
        List<Operator> sorted = topologicalSort();
        for (Operator op : sorted) {
            op.transformMetaData();
        }
    }


    /**
     * Returns an unmodifiable view of the operators contained in this process.
     */
    public List<Operator> getOperators() {
        return Collections.unmodifiableList(new ArrayList<>(operators));
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    /**
     * Use this method only in cases where you are sure that you don't want a
     * ConcurrentModificationException to occur when the list of operators is modified.
     */
    public Iterator<Operator> getOperatorIterator() {
        return operators.iterator();
    }

    /**
     * Returns the operator that contains this process as a subprocess.
     */
    public OperatorChain getEnclosingOperator() {
        return enclosingOperator;
    }

    /**
     * Executes the inner operators.
     */
    public void execute() {
        UnitExecutor executor = UnitExecutionFactory.getInstance().getExecutor(this);
        executor.execute(this);
    }
}
