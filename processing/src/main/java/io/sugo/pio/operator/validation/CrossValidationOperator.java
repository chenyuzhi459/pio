//package io.sugo.pio.operator.validation;
//
//import io.sugo.pio.example.ExampleSet;
//import io.sugo.pio.example.set.SplittedExampleSet;
//import io.sugo.pio.operator.*;
//import io.sugo.pio.operator.performance.PerformanceVector;
//import io.sugo.pio.parameter.*;
//import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
//import io.sugo.pio.parameter.conditions.EqualTypeCondition;
//import io.sugo.pio.ports.Connection;
//import io.sugo.pio.ports.InputPort;
//import io.sugo.pio.ports.OutputPort;
//import io.sugo.pio.tools.Pair;
//import io.sugo.pio.tools.RandomGenerator;
//
//import java.util.*;
//
///**
// */
//public class CrossValidationOperator extends ParallelOperatorChain {
//    private static final String PARAMETER_SPLIT_ON_BATCH_ATTRIBUTE = "split_on_batch_attribute";
//    private static final String PARAMETER_NUMBER_OF_FOLDS = "number_of_folds";
//    private static final String PARAMETER_LEAVE_ONE_OUT = "leave_one_out";
//    private static final String PARAMETER_SAMPLING_TYPE = "sampling_type";
//
//    public CrossValidationOperator(List<Connection> connections, List<ExecutionUnit> execUnits, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
//        super(connections, execUnits, "crossValidation", inputPorts, outputPorts);
//    }
//
//    private IOObject train(ExampleSet trainSet) throws OperatorException {
//        this.trainingSetInnerOutput.deliver(trainSet);
//        this.getSubprocess(0).execute();
//        return this.modelInnerInput.getData(IOObject.class);
//    }
//
//    private Pair<List<PerformanceVector>, ExampleSet> test(ExampleSet testSet, IOObject model) throws OperatorException {
//        this.testSetInnerOutput.deliver(testSet);
//        this.throughExtender.passDataThrough();
//        this.modelInnerOutput.deliver(model);
//        this.getSubprocess(1).execute();
//        ArrayList perfVectors = new ArrayList(this.performanceOutputPortExtender.getManagedPairs().size());
//        Iterator iterator = this.performanceOutputPortExtender.getManagedPairs().iterator();
//
//        while(iterator.hasNext()) {
//            PortPair pair = (PortPair)iterator.next();
//            if(pair.getInputPort().isConnected()) {
//                perfVectors.add(pair.getInputPort().getData(PerformanceVector.class));
//            }
//        }
//
//        return new Pair(perfVectors, this.testResultSetInnerInput.getDataOrNull(ExampleSet.class));
//    }
//
//    public List<ParameterType> getParameterTypes() {
//        LinkedList types = new LinkedList();
//        types.add(new ParameterTypeBoolean("split_on_batch_attribute", "Use the special attribute \'batch\' to partition the data instead of randomly splitting the data. This gives you control over the exact examples which are used to train the model each fold.", false));
//        ParameterTypeBoolean type = new ParameterTypeBoolean("leave_one_out", "Set the number of validations to the number of examples. If set to true, number_of_folds is ignored", false);
//        type.registerDependencyCondition(new BooleanParameterCondition(this, "split_on_batch_attribute", false, false));
//        types.add(type);
//        ParameterTypeInt type1 = new ParameterTypeInt("number_of_folds", "Number of folds (aka number of subsets) for the cross validation.", 2, 2147483647, 10);
//        type1.registerDependencyCondition(new BooleanParameterCondition(this, "leave_one_out", false, false));
//        type1.registerDependencyCondition(new BooleanParameterCondition(this, "split_on_batch_attribute", false, false));
//        types.add(type1);
//        ParameterTypeCategory type2 = new ParameterTypeCategory("sampling_type", "Defines the sampling type of the cross validation (linear = consecutive subsets, shuffled = random subsets, stratified = random subsets with class distribution kept constant)", SplittedExampleSet.SAMPLING_NAMES, 3);
//        type2.registerDependencyCondition(new BooleanParameterCondition(this, "leave_one_out", false, false));
//        type2.registerDependencyCondition(new BooleanParameterCondition(this, "split_on_batch_attribute", false, false));
//        types.add(type2);
//        Iterator superTypes = RandomGenerator.getRandomGeneratorParameters(this).iterator();
//
//        while(superTypes.hasNext()) {
//            ParameterType addType = (ParameterType)superTypes.next();
//            addType.registerDependencyCondition(new BooleanParameterCondition(this, "leave_one_out", false, false));
//            addType.registerDependencyCondition(new BooleanParameterCondition(this, "split_on_batch_attribute", false, false));
//            addType.registerDependencyCondition(new EqualTypeCondition(this, "sampling_type", SplittedExampleSet.SAMPLING_NAMES, false, new int[]{1, 2, 3}));
//            types.add(addType);
//        }
//
//        List superTypes1 = super.getParameterTypes();
//        types.addAll(superTypes1);
//        return types;
//    }
//
//    public boolean supportsCapability(OperatorCapability capability) {
//        switch(capability.ordinal()) {
//            case 1:
//                return false;
//            case 2:
//                try {
//                    return getParameterAsInt(PARAMETER_SAMPLING_TYPE) != 2;
//                } catch (UndefinedParameterError var3) {
//                    return false;
//                }
//            default:
//                return true;
//        }
//    }
//
//    private static class CrossValidationResult {
//        private Pair<List<PerformanceVector>, ExampleSet> partialResult;
//        private IOObject modelResult;
//        private boolean isPartialResult;
//
//        public CrossValidationResult(Pair<List<PerformanceVector>, ExampleSet> partialResult) {
//            this.partialResult = partialResult;
//            this.isPartialResult = true;
//        }
//
//        public CrossValidationResult(IOObject model) {
//            this.modelResult = model;
//            this.isPartialResult = false;
//        }
//
//        public IOObject getModelResult() {
//            return this.modelResult;
//        }
//
//        public Pair<List<PerformanceVector>, ExampleSet> getPartialResult() {
//            return this.partialResult;
//        }
//
//        public boolean isPartialResult() {
//            return this.isPartialResult;
//        }
//    }
//}
