//package io.sugo.pio.operator;
//
//import io.sugo.pio.example.ExampleSet;
//import io.sugo.pio.parameter.ParameterType;
//import io.sugo.pio.parameter.ParameterTypeBoolean;
//import io.sugo.pio.parameter.UndefinedParameterError;
//import io.sugo.pio.ports.Connection;
//import io.sugo.pio.ports.InputPort;
//import io.sugo.pio.ports.OutputPort;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
///**
// */
//public abstract class ParallelOperatorChain extends OperatorChain {
//    public static String PARAMETER_ENABLE_PARALLEL_EXECUTION = "enable_parallel_execution";
//
//    public ParallelOperatorChain(List<Connection> connections,
//             List<ExecutionUnit> execUnits, String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
//        super(connections, execUnits, name, inputPorts, outputPorts);
//    }
//
//    protected <T> T getAndCheckForStop(final Future<T> future) throws OperatorException {
//        try {
//            return BackgroundExecutionService.executeBlockingTask(new Callable() {
//                public T call() throws ExecutionException, ProcessStoppedException {
//                    Object t = null;
//
//                    while(t == null) {
//                        try {
//                            t = future.get();
//                        } catch (InterruptedException var3) {
//                            ParallelOperatorChain.this.checkForStop();
//                        }
//                    }
//
//                    return t;
//                }
//            });
//        } catch (ProcessStoppedException e) {
//            throw e;
//        } catch (ExecutionException e) {
//            throw ExecutionExceptionHandling.processExecutionException(e, this.getProcess());
//        } catch (Exception var5) {
//            var5.printStackTrace();
//            throw new OperatorException("There seems to be a race condition in the parallel execution.", var5);
//        }
//    }
//
//    protected boolean checkParallelizability() {
//        boolean executeParallely = this.getParameterAsBoolean(PARAMETER_ENABLE_PARALLEL_EXECUTION);
//        if(executeParallely) {
//            Iterator var2 = this.getSubprocesses().iterator();
//
//            while(var2.hasNext()) {
//                ExecutionUnit unit = (ExecutionUnit)var2.next();
//                Iterator var4 = unit.getAllInnerOperators().iterator();
//
//                while(var4.hasNext()) {
//                    Operator operator = (Operator)var4.next();
//                    if(operator.isEnabled() && operator.hasBreakpoint()) {
//                        return false;
//                    }
//                }
//            }
//        }
//
//        return executeParallely;
//    }
//
//    protected <T extends IOObject> List<T> getDataCopy(List<IOObject> inputData) throws UndefinedParameterError {
//        ArrayList clonedInputData = new ArrayList(inputData.size());
//        Iterator var3 = inputData.iterator();
//
//        while(var3.hasNext()) {
//            IOObject object = (IOObject)var3.next();
//            clonedInputData.add(getDataCopy(object));
//        }
//
//        return clonedInputData;
//    }
//
//    protected <T extends IOObject> T getDataCopy(IOObject input) throws UndefinedParameterError {
//        if(input instanceof ExampleSet) {
//            ExampleSet set = (ExampleSet)input;
//            return MaterializeDataInMemory.materializeExampleSet(set);
//        } else {
//            return input != null? input.copy():null;
//        }
//    }
//
//    public List<ParameterType> getParameterTypes() {
//        List types = super.getParameterTypes();
//        types.add(new ParameterTypeBoolean(PARAMETER_ENABLE_PARALLEL_EXECUTION, "This enables parallel execution of the computation of the inner processes. Disable if you either run into memory problems or if you need sequential computing for using side effects like macro variable or Remember and Recall operators within the execution. The end result will be propagated to the outside process and can be used in the usual way. So only disable if you use side effects between the loops. Will be automatically enabled with break points in subprocesses or the stateful loop objects.", true));
//        return types;
//    }
//}