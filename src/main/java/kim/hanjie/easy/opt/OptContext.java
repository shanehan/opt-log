package kim.hanjie.easy.opt;

import java.util.HashMap;
import java.util.Map;

/**
 * @author han
 * @date 2021/3/10
 */
public class OptContext {

    private static ThreadLocal<Map<String, String>> optContextHolder = new ThreadLocal<>();

    public static void put(String key, String value) {
        if (key == null) {
            return;
        }
        Map<String, String> map = optContextHolder.get();
        if (map == null) {
            map = new HashMap<>(16);
        }
        map.put(key, value);
        optContextHolder.set(map);
    }

    public static String get(String key) {
        if (key == null) {
            return null;
        }
        Map<String, String> map = optContextHolder.get();
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    public static void clean() {
        optContextHolder.remove();
    }

    public static Map<String, String> getCopyOfContextMap() {
        Map<String, String> map = optContextHolder.get();
        if (map == null) {
            return null;
        } else {
            return new HashMap<>(map);
        }
    }
}
