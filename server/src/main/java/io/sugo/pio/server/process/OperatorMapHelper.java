package io.sugo.pio.server.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;

import java.lang.reflect.Modifier;
import java.util.*;

public class OperatorMapHelper {
    private static final Logger log = new Logger(OperatorMapHelper.class);

    public static Map<String, OperatorMeta> getAllOperatorMetas(ObjectMapper jsonMapper) {
        try {
            Map<String, OperatorMeta> operatorMap = new HashMap<>();
            MapperConfig config = jsonMapper.getDeserializationConfig();
            AnnotationIntrospector ai = config.getAnnotationIntrospector();
            AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(Operator.class, ai, config);
            Collection<NamedType> subTypes = jsonMapper.getSubtypeResolver().collectAndResolveSubtypesByClass(config, ac);

            if (subTypes != null) {
                for (NamedType subType : subTypes) {
                    Class<?> clazz = subType.getType();
                    boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
                    if (!isAbstract) {
                        Object o = subType.getType().newInstance();
                        if (o instanceof Operator && !(o instanceof ProcessRootOperator)) {
                            Operator op = (Operator) o;
                            if (!operatorMap.containsKey(subType.getName())) {
                                OperatorMeta meta = OperatorMeta.create(subType.getName())
                                        .setFullName(op.getDefaultFullName())
                                        .setDescription(op.getDescription())
                                        .setSequence(op.getSequence())
                                        .setType(subType)
                                        .setGroup(op.getGroup());
                                operatorMap.put(subType.getName(), meta);
                            } else {
                                throw new RuntimeException(String.format("OperatorMeta %s has been registered",
                                        subType.getName()));
                            }
                        }
                    }
                }
            }
            return operatorMap;
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
