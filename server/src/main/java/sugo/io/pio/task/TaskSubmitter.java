package sugo.io.pio.task;

import org.apache.spark.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TaskSubmitter {
    public static void submit(ClusterType clusterType, Task task) {
        final String mainClass;
        switch (clusterType) {
            case YARN:
                mainClass = "org.apache.spark.deploy.yarn.Client";
                break;
            case STANDALONE:
            case MESOS:
            default:
                mainClass = "org.apache.spark.deploy.rest.RestSubmissionClient";
        }

        List<String> argsList = new ArrayList<String>();
        argsList.add("--class");
        argsList.add("sugo.io.pio.CreateWorkFlow");

        runMain(argsList.toArray(new String[0]), mainClass);
    }

    private static void runMain(
            String[] childArgs,
            String childMainClass) {
        Class<?> mainClass;
        try {
            mainClass = Utils.classForName(childMainClass);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null,  new Object[] { childArgs});
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
