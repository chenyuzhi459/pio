package sugo.io.pio.util;

import org.apache.hadoop.yarn.lib.ZKClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kitty on 16-12-14.
 */
public class ZkUtils {
    private ZkUtils(){}
    private static final ZkUtils zkUtils = new ZkUtils();
    private static ZKClient zk = null;

    public static ZkUtils getInstance(String host) throws IOException {
        zk = new ZKClient(host);
        return zkUtils;
    }

    public String getData(String path) throws IOException, InterruptedException {
        return zk.getServiceData(path);
    }

}
