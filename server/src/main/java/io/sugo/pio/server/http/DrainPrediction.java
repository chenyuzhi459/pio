package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.constant.ProcessConstant;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.server.http.dto.OperatorParamDto;
import io.sugo.pio.server.process.ProcessManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

            OperatorProcess trainProcess = getBuiltInProcess(tenantId, ProcessConstant.Type.DRAIN_TRAINING);

            // 1.Not found built-in process, then create one.
            if (trainProcess == null) {
                log.info("Not found drain training built-in process by tenantId:%s, and prepare to new process using the template.", tenantId);
                trainProcess = processManager.createByTemplate(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            }

            // 2.Update operator parameters
            Operator csvOperator = getOperator(trainProcess, ProcessConstant.OperatorType.CSVExampleSource);
            processManager.updateParameter(trainProcess.getId(), csvOperator.getName(), paramList);
            log.info("Update csv operator parameters of 'drain_training' process successfully. tenantId:%s", tenantId);

            // 3.Run process
            trainProcess = processManager.runSyn(trainProcess.getId());
            log.info("Run 'drain_training' process successfully. tenantId:%s", tenantId);

            return Response.ok(trainProcess).build();
        } catch (Exception e) {
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
            log.info("Update csv operator parameters of 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 3.Get model from decision tree operator of 'drain_training' process
            OperatorProcess trainProcess = processManager.getFromCache(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            if (trainProcess == null) {
                // Not hit the cache, then load from database
                trainProcess = getBuiltInProcess(tenantId, ProcessConstant.Type.DRAIN_TRAINING);
            }

            Operator decisionTreeOperator = getOperator(trainProcess, ProcessConstant.OperatorType.ParallelDecisionTreeLearner);
            IOObject treeModel = decisionTreeOperator.getOutputPorts().getPort(PortType.MODEL).getAnyDataOrNull();
            MetaData metaData = decisionTreeOperator.getOutputPorts().getPort(PortType.MODEL).getMetaData();
            if (treeModel == null) {
                log.info("Get decision tree model from 'drain_training' process, but the result obtained is null, " +
                        "then run the 'drain_training' process again. tenantId:%s", tenantId);
                OperatorProcess newRunProcess = processManager.runSyn(trainProcess.getId());
                decisionTreeOperator = getOperator(newRunProcess, ProcessConstant.OperatorType.ParallelDecisionTreeLearner);
                treeModel = decisionTreeOperator.getOutputPorts().getPort(PortType.MODEL).getAnyDataOrNull();
                metaData = decisionTreeOperator.getOutputPorts().getPort(PortType.MODEL).getMetaData();
            }
            log.info("Get decision tree model from 'drain_training' process successfully. tenantId:%s", tenantId);

            // 4.Set decision tree model to 'drain_prediction' process
            Operator applyModelOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.ModelApplier);
            applyModelOperator.getInputPorts().getPort(PortType.MODEL).receive(treeModel);
            applyModelOperator.getInputPorts().getPort(PortType.MODEL).receiveMD(metaData);
            log.info("Set decision tree model to 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 5.Run 'drain_prediction' process
            predictionProcess = processManager.runSyn(predictionProcess.getId());
            log.info("Run 'drain_prediction' process successfully. tenantId:%s", tenantId);

            // 6.Return the prediction results
            applyModelOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.ModelApplier);
            Operator performanceOperator = getOperator(predictionProcess, ProcessConstant.OperatorType.PolynominalClassificationPerformanceEvaluator);
            IOContainer container = applyModelOperator.getResult();
            container.getIoObjects().addAll(performanceOperator.getResult().getIoObjects());

            return Response.ok(container).build();

        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private OperatorProcess getBuiltInProcess(String tenantId, String processType) {
        return processManager.getBuiltIn(tenantId, processType);
    }

    private Operator getOperator(String tenantId, String processType, String operatorType) {
        OperatorProcess process = getBuiltInProcess(tenantId, processType);
        Preconditions.checkNotNull(process, I18N.getMessage("pio.error.process.not_found_by_tenant_id"), tenantId,
                ProcessConstant.Type.DRAIN_PREDICTION.equals(processType) ? I18N.getMessage("pio.ProcessType.drain_prediction") :
                        I18N.getMessage("pio.ProcessType.drain_training"));

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
}
