package io.BrendonCurmi.JPageTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * A data class storing a payload JSONObject and the URL.
 */
public class Data {

    /**
     * The REST payload.
     */
    private final JSONObject OBJ;
    /**
     * The URL.
     */
    private final String URL;

    /**
     * Creates a Data object.
     *
     * @param obj the payload.
     * @param url the tested URL.
     */
    public Data(JSONObject obj, String url) {
        this.OBJ = obj;
        this.URL = url;
    }

    public JSONObject obj() {
        return OBJ;
    }

    public String getURL() {
        return URL;
    }

    /**
     * Gets the value object associated with the specified key. If the key
     * isn't found, will return "-" instead.
     * @param key the key string.
     * @return the value object of the key; or "-" if not found.
     */
    public Object get(String key) {
        try {
            return obj().get(key);
        } catch (JSONException ex) {
            return "-";
        }
    }

    /**
     * Gets the string value associated with the specified key.
     * @param key the key string.
     * @return the string value of the key.
     */
    public String getString(String key) {
        return (String) this.get(key);
    }

    /**
     * Gets the integer value associated with the specified key. If the key
     * isn't found, will return 0 instead.
     * @param key the key string.
     * @return the integer value of the key; or 0 if not found.
     */
    public int getInt(String key) {
        try {
            return obj().getInt(key);
        } catch (JSONException ex) {
            return 0;
        }
    }

    /**
     * Gets the value in seconds to 3d.p. associated with the specified key. If the key
     * isn't found, will return 0 (as BigDecimal) instead.
     * Example:
     * <pre><code>data.getSeconds("loadTime");</code></pre>
     * Using this method as shown above is equivalent to using:
     * <pre><code>Time.millisToSeconds(data.obj().getInt("loadTime"));</code></pre>
     * @param key the key string.
     * @return the seconds value of the key; or 0 if not found.
     * @see Time#millisToSeconds(long)
     */
    public BigDecimal getSeconds(String key) {
        try {
            return Time.millisToSeconds(obj().getInt(key));
        } catch (JSONException ex) {
            return BigDecimal.valueOf(0);
        }
    }
}
