package io.sugo.pio.util;

import io.sugo.pio.operator.OperatorCreationException;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.Operator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 */
public class OperatorService {
    /**
     * Maps operator keys as defined in the OperatorsCore.xml to operator descriptions.
     */
    private static final Map<String, OperatorDescription> KEYS_TO_DESCRIPTIONS = new HashMap<>();

    /** Set of all Operator classes registered. */
    private static final Set<Class<? extends Operator>> REGISTERED_OPERATOR_CLASSES = new HashSet<>();

    /**
     * Registers the given operator description. Please note that two different descriptions must
     * not have the same name. Otherwise the second description overwrite the first in the
     * description map.
     *
     * If there's no icon defined for the given {@link OperatorDescription}, the group icon will be
     * set here.
     *
     * @throws OperatorCreationException
     */
    public static void registerOperator(OperatorDescription description)
            throws OperatorCreationException {
        // register in maps
        KEYS_TO_DESCRIPTIONS.put(description.getKey(), description);
        REGISTERED_OPERATOR_CLASSES.add(description.getOperatorClass());
    }

    /**
     * Returns the operator descriptions for the operators which uses the given class. Performs a
     * linear search through all operator descriptions.
     */
    public static OperatorDescription[] getOperatorDescriptions(Class<?> clazz) {
        if (clazz == null) {
            return new OperatorDescription[0];
        }
        List<OperatorDescription> result = new ArrayList<>(1);
        for (OperatorDescription current : KEYS_TO_DESCRIPTIONS.values()) {
            if (current.getOperatorClass().equals(clazz)) {
                result.add(current);
            }
        }
        return result.toArray(new OperatorDescription[result.size()]);
    }


    /**
     * <p>
     * Use this method to create an operator from an operator class. This is the only method which
     * ensures operator existence checks during compile time (and not during runtime) and the usage
     * of this method is therefore the recommended way for operator creation.
     * </p>
     *
     * <p>
     * It is, however, not possible to create some generic operators with this method (this mainly
     * applies to the Weka operators). Please use the method {@link #createOperator(Class)} for
     * those generic operators.
     * </p>
     *
     * <p>
     * If you try to create a generic operator with this method, the OperatorDescription will not be
     * unique for the given class and an OperatorCreationException is thrown.
     * </p>
     *
     * <p>
     * Please note that is is not necessary to cast the operator to the desired class.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Operator> T createOperator(Class<T> clazz) throws OperatorCreationException {
        /*OperatorDescription[] descriptions = getOperatorDescriptions(clazz);
        if (descriptions.length == 0) {
            throw new OperatorCreationException(OperatorCreationException.NO_DESCRIPTION_ERROR, clazz.getName(), null);
        } else if (descriptions.length > 1) {
            List<OperatorDescription> nonDeprecated = new LinkedList<>();
            for (OperatorDescription od : descriptions) {
                    nonDeprecated.add(od);
            }
            if (nonDeprecated.size() > 1) {
                throw new OperatorCreationException(OperatorCreationException.NO_UNIQUE_DESCRIPTION_ERROR, clazz.getName(),
                        null);
            }
            return (T) nonDeprecated.get(0).createOperatorInstance();
        } else {
            return (T) descriptions[0].createOperatorInstance();
        }*/

        // Create operator without 'OperatorDescription'
        Operator operator = null;
        try {
            java.lang.reflect.Constructor<? extends Operator> constructor = clazz.getConstructor(new Class[] { });
            operator = constructor.newInstance(new Object[] { });
        } catch (NoSuchMethodException e) {
            throw new OperatorCreationException(OperatorCreationException.NO_CONSTRUCTOR_ERROR,
                    "(" + clazz.getName() + ")", e);
        } catch (IllegalAccessException e) {
            throw new OperatorCreationException(OperatorCreationException.ILLEGAL_ACCESS_ERROR,
                    "(" + clazz.getName() + ")", e);
        } catch (InstantiationException e) {
            throw new OperatorCreationException(OperatorCreationException.INSTANTIATION_ERROR,
                    "(" + clazz.getName() + ")", e);
        } catch (InvocationTargetException e) {
            throw new OperatorCreationException(OperatorCreationException.CONSTRUCTION_ERROR,
                    "(" + clazz.getName() + ")", e);
        } catch (Throwable t) {
            throw new OperatorCreationException(OperatorCreationException.INSTANTIATION_ERROR, "(" + clazz.getName() + ")",
                    t);
        }

        return (T)operator;
    }
}
