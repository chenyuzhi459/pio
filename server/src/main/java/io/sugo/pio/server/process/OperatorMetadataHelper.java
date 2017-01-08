package io.sugo.pio.server.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorMeta;

import java.util.*;

/**
 * Created by root on 17-1-5.
 */
public class OperatorMetadataHelper {
    private static final Logger log = new Logger(OperatorMetadataHelper.class);

    public static Map<String, OperatorMeta> getAllOperatorMetadata(ObjectMapper jsonMapper) {
        try {
            Map<String, OperatorMeta> metaMap = new HashMap<>();
            MapperConfig config = jsonMapper.getDeserializationConfig();
            AnnotationIntrospector ai = config.getAnnotationIntrospector();
            AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(OperatorMeta.class, ai, config);
            List<NamedType> subTypes = ai.findSubtypes(ac);

            if (subTypes != null) {
                for (NamedType subType : subTypes) {
                    Object metaObj = subType.getType().newInstance();
                    if (metaObj instanceof OperatorMeta) {
                        OperatorMeta instance = (OperatorMeta) metaObj;
                        metaMap.put(subType.getName(), instance);
                    }
                }
            }
            validate(jsonMapper, metaMap.keySet());
            return metaMap;
        } catch (Exception e) {
            log.error(e, "getAllOperatorMetadata error");
            throw new RuntimeException(e);
        }
    }

    private static void validate(ObjectMapper jsonMapper, Set<String> operatorMetaNames) throws JsonProcessingException {
        Set<String> operatorNames = getOperators(jsonMapper);
        for (String meta : operatorMetaNames) {
            if (operatorNames.contains(meta)) {
                operatorNames.remove(meta);
            } else {
                throw new RuntimeException(String.format("OperatorMeta %s must register with operator using the same name",
                        meta));
            }
        }
        if (!operatorNames.isEmpty()) {
            for (String operator : operatorNames) {
                log.warn("OperatorMeta not provided for Operator %s, the operator cannot be used directly!", operator);
            }
        }
    }

    public static Set<String> getOperators(ObjectMapper jsonMapper) {
        Set<String> operatorNames = new HashSet<>();
        MapperConfig config = jsonMapper.getDeserializationConfig();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(Operator.class, ai, config);
        List<NamedType> subTypes = ai.findSubtypes(ac);
        if (subTypes != null) {
            for (NamedType subType : subTypes) {
                operatorNames.add(subType.getName());
            }
        }
        return operatorNames;
    }

}
