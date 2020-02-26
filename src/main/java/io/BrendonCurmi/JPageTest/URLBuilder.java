package io.BrendonCurmi.JPageTest;

public class URLBuilder {

    private final StringBuilder URL;

    /**
     * Creates the specified URL.
     *
     * @param url the URL to create.
     */
    public URLBuilder(String url) {
        // remove trailing "?" if any
        if (url.endsWith("?")) {
            if (url.length() > 1) url = url.substring(0, url.length() - 1);
            else throw new IllegalArgumentException("The url cannot be just a '?'");
        }
        this.URL = new StringBuilder(url);
    }

    /**
     * Adds the specified parameter key-value pair to the URL.
     *
     * @param key   the parameter key.
     * @param value the parameter value.
     */
    public void addParam(String key, Object value) {
        if (URL.indexOf("?") > 0) URL.append("&");
        else URL.append("?");
        URL.append(key).append("=").append(value);
    }

    @Override
    public String toString() {
        return URL.toString();
    }
}
