package io.sugo.pio.common.task;

import io.sugo.pio.common.TaskStatus;

/**
 */
public abstract class TrainingTask implements Task {
    protected String id;
    protected String url;
    protected String jarPath;
    protected String classPath;

    public TrainingTask(String id, String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public TaskStatus run() throws Exception {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
