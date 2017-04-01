package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.constant.ProcessConstant;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.nio.model.AbstractDataResultSetReader;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.SubsetAttributeFilter;
import io.sugo.pio.operator.preprocessing.sampling.SamplingOperator;
import io.sugo.pio.parameter.ParameterTypeList;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.server.http.dto.OperatorParamDto;
import io.sugo.pio.server.process.ProcessManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/pio/process/drain/")
public class DrainPrediction {
    private static final Logger log = new Logger(DrainPrediction.class);

    private final ObjectMapper jsonMapper;
    private final ProcessManager processManager;

    @Inject
    public DrainPrediction(
            @Json ObjectMapper jsonMapper,
            ProcessManager processManager
    ) {
        this.jsonMapper = jsonMapper;
        this.processManager = processManager;
    }

    @POST
    @Path("/train/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response train(@PathParam("tenantId") final String tenantId, List<OperatorParamDto> paramList) {
        try {
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"), tenantId);

            List<OperatorParamDto> attributeFilterParam = buildAttributeFilterParam(paramList);
            List<OperatorParamDto> exampleFilterParam = buildExampleFilterParam(paramList, false);
            List<OperatorParamDto> samplingParam = buildSamplingParam();

            OperatorProcess trainProcess = getBuiltInProcess(tenantId, ProcessConstant.Type.DRAIN_TRAINING);

            // 1.Not found built-in process, then create one.
            if (trainProcess == null) {
                log.info("Not found drain training built-in process by tenantId:%s, and prepare to new process using the template.", tenantId);
                trainProcess = processManager.createByTemplate(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            }
            // Refresh cache to ensure that is the same object when process running
            processManager.add2cache(trainProcess);

            // 2.Update operator parameters
            Operator csvOperator = getOperator(trainProcess, ProcessConstant.OperatorType.CSVExampleSource);
            processManager.updateParameter(trainProcess.getId(), csvOperator.getName(), paramList);

            Operator attributeFilterOperator = getOperator(trainProcess, ProcessConstant.OperatorType.AttributeFilter);
            processManager.updateParameter(trainProcess.getId(), attributeFilterOperator.getName(), attributeFilterParam);

            Operator exampleFilterOperator = getOperator(trainProcess, ProcessConstant.OperatorType.ExampleFilter);
            processManager.updateParameter(trainProcess.getId(), exampleFilterOperator.getName(), exampleFilterParam);

            Operator samplingOperator = getOperator(trainProcess, ProcessConstant.OperatorType.SamplingOperator);
            processManager.updateParameter(trainProcess.getId(), samplingOperator.getName(), samplingParam);
            log.info("Update operator parameters of 'drain_training' process successfully. tenantId:%s", tenantId);

            // 3.Run process
            trainProcess = processManager.runSyn(trainProcess.getId());
            log.info("Run 'drain_training' process successfully. tenantId:%s", tenantId);

            // 4.Return the training results
            Operator applyModelOperator = getOperator(trainProcess, ProcessConstant.OperatorType.ModelApplier);
            Operator performanceOperator = getOperator(trainProcess, ProcessConstant.OperatorType.PolynominalClassificationPerformanceEvaluator);
            IOContainer container = applyModelOperator.getResult();

            List<IOObject> performanceIOObjects = performanceOperator.getResult().getIoObjects();
            container.addIoObjects(performanceIOObjects);

            return Response.ok(container).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/predict/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response predict(@PathParam("tenantId") final String tenantId, List<OperatorParamDto> paramList) {
        try {
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"), tenantId);

            List<OperatorParamDto> attributeFilterParam = buildAttributeFilterParam(paramList);
            List<OperatorParamDto> exampleFilterParam = buildExampleFilterParam(paramList, true);
            OperatorProcess predictionProcess = getBuiltInProcess(tenantId, ProcessConstant.Type.DRAIN_PREDICTION);

            // 1.Not found built-in process, then create one.
            if (predictionProcess == null) {
                log.info("Not found drain prediction built-in process by tenantId:%s, and prepare to new process using the template.", tenantId);
                predictionProcess = processManager.createByTemplate(tenantId, ProcessConstant.Type.DRAIN_PREDICTION);
            }
            // Refresh cache to ensure that is the same object when process running
            processManager.add2cache(predictionProcess);

            // 2.Update operator parameters
            Operator csvOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.CSVExampleSource);
            processManager.updateParameter(predictionProcess.getId(), csvOperator.getName(), paramList);

            Operator attributeFilterOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.AttributeFilter);
            processManager.updateParameter(predictionProcess.getId(), attributeFilterOperator.getName(), attributeFilterParam);

            Operator exampleFilterOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.ExampleFilter);
            processManager.updateParameter(predictionProcess.getId(), exampleFilterOperator.getName(), exampleFilterParam);
            log.info("Update operator parameters of 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 3.Get model from decision tree operator of 'drain_training' process
            OperatorProcess trainProcess = processManager.getFromCache(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            if (trainProcess == null) {
                // Not hit the cache, then load from database
                trainProcess = getBuiltInProcess(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            }

            Operator decisionTreeOperator = getOperator(trainProcess, ProcessConstant.OperatorType.ParallelDecisionTreeLearner);
            IOObject treeModel = decisionTreeOperator.getOutputPorts().getPort(PortConstant.MODEL).getAnyDataOrNull();
            MetaData metaData = decisionTreeOperator.getOutputPorts().getPort(PortConstant.MODEL).getMetaData();
            if (treeModel == null) {
                log.info("Get decision tree model from 'drain_training' process, but the result obtained is null, " +
                        "then run the 'drain_training' process again. tenantId:%s", tenantId);
                OperatorProcess newRunProcess = processManager.runSyn(trainProcess.getId());
                decisionTreeOperator = getOperator(newRunProcess, ProcessConstant.OperatorType.ParallelDecisionTreeLearner);
                treeModel = decisionTreeOperator.getOutputPorts().getPort(PortConstant.MODEL).getAnyDataOrNull();
                metaData = decisionTreeOperator.getOutputPorts().getPort(PortConstant.MODEL).getMetaData();
            }
            log.info("Get decision tree model from 'drain_training' process successfully. tenantId:%s", tenantId);

            // 4.Set decision tree model to 'drain_prediction' process
            Operator applyModelOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.ModelApplier);
            applyModelOperator.getInputPorts().getPort(PortConstant.MODEL).receive(treeModel);
            applyModelOperator.getInputPorts().getPort(PortConstant.MODEL).receiveMD(metaData);
            log.info("Set decision tree model to 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 5.Run 'drain_prediction' process
            predictionProcess = processManager.runSyn(predictionProcess.getId());
            log.info("Run 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 6.Return the prediction results
            applyModelOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.ModelApplier);
            IOContainer container = applyModelOperator.getResult();

            return Response.ok(container).build();

        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private OperatorProcess getBuiltInProcess(String tenantId, String processType) {
        return processManager.getBuiltIn(tenantId, processType);
    }

    private Operator getOperator(OperatorProcess process, String operatorType) {
        List<Operator> operators = process.getRootOperator().getExecutionUnit().getOperators();
        if (operators != null && !operators.isEmpty()) {
            for (Operator operator : operators) {
                if (operatorType.equals(operator.getType())) {
                    return operator;
                }
            }
        }

        return null;
    }

    private List<OperatorParamDto> buildAttributeFilterParam(List<OperatorParamDto> paramList) {
        List<String> selectedColumns = new ArrayList<>();
        int labelCount = 0;
        int idCount = 0;
        if (paramList != null && !paramList.isEmpty()) {
            for (OperatorParamDto operatorParam : paramList) {
                if (AbstractDataResultSetReader.PARAMETER_META_DATA.equals(operatorParam.getKey())) {
                    List<String[]> columnList = ParameterTypeList.transformString2List(operatorParam.getValue());
                    for (String[] columnArray : columnList) { // [index, attributeName.isSelected.valueType.role]
                        if (columnArray.length != 2) {
                            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.number_must_be_two"));
                        }
                        String clolumnInfo = columnArray[1]; // attributeName.isSelected.valueType.role
                        String[] infoArray = clolumnInfo.split("\\.");
                        String columnName = infoArray[0];
                        boolean isSelected = Boolean.valueOf(infoArray[1]);
                        String role = infoArray[3];

                        if (isSelected) {
                            selectedColumns.add(columnName);
                            if (Attributes.ID_NAME.equals(role)) {
                                idCount++;
                            }
                            if (Attributes.LABEL_NAME.equals(role)) {
                                labelCount++;
                            }
                        }
                    }
                }
            }
        }

        if (selectedColumns.isEmpty()) {
            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.attribute_empty"));
        }
        if (idCount > 1) {
            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.id_attribute_more_than_one"));
        }
        if (labelCount == 0) {
            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.label_attribute_null"));
        }
        if (labelCount > 1) {
            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.label_attribute_more_than_one"));
        }

        OperatorParamDto param = new OperatorParamDto();
        String selectedColumn = org.apache.commons.lang.StringUtils.join(selectedColumns.toArray(), ';');
        param.setKey(SubsetAttributeFilter.PARAMETER_ATTRIBUTES);
        param.setValue(selectedColumn);

        return Collections.singletonList(param);
    }

    private List<OperatorParamDto> buildExampleFilterParam(List<OperatorParamDto> paramList, boolean excludeLabelColumn) {
        List<String> filterColumns = new ArrayList<>();
        if (paramList != null && !paramList.isEmpty()) {
            for (OperatorParamDto operatorParam : paramList) {
                if (AbstractDataResultSetReader.PARAMETER_META_DATA.equals(operatorParam.getKey())) {
                    List<String[]> columnList = ParameterTypeList.transformString2List(operatorParam.getValue());
                    for (String[] columnArray : columnList) { // [index, attributeName.isSelected.valueType.role]
                        if (columnArray.length != 2) {
                            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.number_must_be_two"));
                        }
                        String clolumnInfo = columnArray[1]; // attributeName.isSelected.valueType.role
                        String[] infoArray = clolumnInfo.split("\\.");
                        String columnName = infoArray[0];
                        boolean isSelected = Boolean.valueOf(infoArray[1]);
                        String role = infoArray[3];

                        if (isSelected) {
                            if (excludeLabelColumn && Attributes.LABEL_NAME.equals(role)) {
                                continue;
                            }
                            filterColumns.add(ExampleFilter.PARAMETER_FILTERS_LIST + ":" + columnName + ".is_not_missing.");
                        }
                    }
                }
            }
        }

        if (filterColumns.isEmpty()) {
            throw new IllegalArgumentException(I18N.getErrorMessage("pio.error.metadata.parameters.attribute_empty"));
        }

        OperatorParamDto param = new OperatorParamDto();
        String filterColumn = org.apache.commons.lang.StringUtils.join(filterColumns.toArray(), ';');
        param.setKey(ExampleFilter.PARAMETER_FILTERS_LIST);
        param.setValue(filterColumn);

        return Collections.singletonList(param);
    }

    private List<OperatorParamDto> buildSamplingParam() {
        List<OperatorParamDto> paramList = new ArrayList<>();

        OperatorParamDto param1 = new OperatorParamDto();
        param1.setKey(SamplingOperator.PARAMETER_SAMPLE);
        param1.setValue("2");

        OperatorParamDto param2 = new OperatorParamDto();
        param2.setKey(SamplingOperator.PARAMETER_SAMPLE_PROBABILITY);
        param2.setValue("0.6");

        paramList.add(param1);
        paramList.add(param2);

        return paramList;
    }
}
