package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.operator.execution.UnitExecutionFactory;
import io.sugo.pio.operator.execution.UnitExecutor;
import io.sugo.pio.ports.*;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;

/**
 */
public class ExecutionUnit implements Serializable {

    private OperatorChain enclosingOperator;
    private List<Operator> operators;

    @JsonCreator
    public ExecutionUnit(
            @JsonProperty("operators") List<Operator> operators
    ) {
        this.operators = operators;
    }

    public void setEnclosingOperator(OperatorChain enclosingOperator) {
        this.enclosingOperator = enclosingOperator;
    }

    public List<InputPort> getAllInputPorts(){
        List<InputPort> inputPorts = new ArrayList<>();
        for(Operator opt : operators){
            inputPorts.addAll(opt.getInputPorts());
        }
        return inputPorts;
    }

    /** Helper class to count the number of dependencies of an operator. */
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
            for (OutputPort out : child.getOutputPorts()) {
                InputPort dest = out.getDestination();
                if (dest != null) {
                    counter.incNumEdges(dest.getPortOwner().getOperator());
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
            for (OutputPort out : first.getOutputPorts()) {
                InputPort dest = out.getDestination();
                if (dest != null) {
                    Operator destOp = dest.getPortOwner().getOperator();
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


    /** Returns an unmodifiable view of the operators contained in this process. */
    @JsonProperty("operators")
    public List<Operator> getOperators() {
        return Collections.unmodifiableList(new ArrayList<>(operators));
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
