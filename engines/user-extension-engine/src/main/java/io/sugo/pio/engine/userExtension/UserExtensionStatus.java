package io.sugo.pio.engine.userExtension;

import io.sugo.pio.engine.data.output.Repository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by penghuan on 2017/4/6.
 */
public class UserExtensionStatus {
    private final static String success = "_SUCCESS";
    private final static String running = "_RUNNING";
    private final static String failed = "_FAILED";
    private final static String message = "_MESSAGE";
    public enum Info {
        General(null, null),
        Normal(0, ""),
        Unkown(1000, "未知错误"),
        CandidateEmpty(1001, "当前的条件查询不到数据，请尝试修改查询条件"),
        TargetEmpty(1002, "当前分群无法查询到数据，请尝试修改查询条件"),
        TimeOut(1003, "获取用户数据超时"),
        HasString(1004, "暂时只支持数字类型指标"),
        TargetClearEmpty(1005, "候选用户群为空, 请修改目标用户群");


        private Integer code;
        private String message;

        private Info(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("%d:%s", code, message);
        }

        public void parseString(String str) {
            String[] v = str.replaceAll("^\\s+", "").replaceAll("\\s+$", "").split(":");
            if (v.length >= 1) {
                setCode(Integer.parseInt(v[0]));
            }
            if (v.length >=2) {
                setMessage(v[1]);
            }
        }
    };

    public static void clear(Repository repository) {
        if (repository.exists(success)) {
            repository.delete(success);
        }
        if (repository.exists(running)) {
            repository.delete(running);
        }
        if (repository.exists(failed)) {
            repository.delete(failed);
        }
    }

    public static void setSuccess(Repository repository) {
        clear(repository);
        repository.create(success);
    }

    public static void setRunning(Repository repository) {
        clear(repository);
        repository.create(running);
    }

    public static void setFailed(Repository repository) {
        clear(repository);
        repository.create(failed);
    }

    public static void setFailed(Repository repository, Info info) {
        setFailed(repository);
        if (repository.exists(message)) {
            repository.delete(message);
        }
        repository.create(message);
        String file = repository.getPath() + "/" + message;
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print(String.format("%d:%s", info.getCode(), info.getMessage()));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isRunning(Repository repository) {
        return repository.exists(running);
    }

    public static Boolean isSuccess(Repository repository) {
        return repository.exists(success);
    }

    public static Boolean isFailed(Repository repository) {
        return repository.exists(failed);
    }

    public static Info getInfo(Repository repository) {
        if (!repository.exists(message)) {
            return Info.Normal;
        }
        Info info = Info.General;
        String file = repository.getPath() + "/" + message;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line =  br.readLine();
            if (line != null && !line.isEmpty()) {
                info.parseString(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
