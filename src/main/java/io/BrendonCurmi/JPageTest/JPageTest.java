package io.BrendonCurmi.JPageTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * <p>JPageTest is a Java wrapper for the online web performance tool <a href="https://webpagetest.org/">WebPageTest</a>.
 * This project provides an API to be able to simply and effectively run performance tests on your webpages.</p>
 * <p><b>WebPageTest Disclaimer:</b> As per WebPageTest, the public (free) provision is up to 200 page loads per day.
 * Each run, first or repeat view counts as a page load. Ex: 10 runs, first and repeat view, would be a total of 20
 * page loads.</p>
 * <p>To maximise API calls, these tests only call the first view.</p>
 * <br/>
 * <p>Firstly, you will need to create an API key on <a href="https://www.webpagetest.org/getkey.php">WebPageTest</a>.</p>
 * <p>Then the API key can be passed in this class' constructor:</p>
 * <pre><code>
 *     JPageTest jPageTest = new JPageTest("[API KEY]");
 * </code></pre>
 * <p>To run a web performance test on a single webpage, you can use the thread-blocking {@link #runSingleTest(int, String)}</p>
 * <p>For example to run 5 tests on www.example.com, you can use:</p>
 * <pre><code>
 *     jPageTest.runSingleTest(5, "https://www.example.com");
 * </code></pre>
 * <p>To run a web performance test on two webpages, you can use the thread-blocking {@link #runComparativeTest(int, String, String)}</p>
 * <p>For example to run 5 tests each for www.example1.com and www.example2.com (total 10 tests), you can use:</p>
 * <pre><code>
 *     jPageTest.runComparativeTest(5, "https://www.example1.com", "https://www.example2.com");
 * </code></pre>
 * <p>Another way to test two pages is by running two single-page tests:</p>
 * <pre><code>
 *     List<Data> data1 = jPageTest.runSingleTest(5, "https://www.example1.com");
 *     List<Data> data2 = jPageTest.runSingleTest(5, "https://www.example2.com");
 * </code></pre>
 * <p>However since {@link #runSingleTest(int, String)} is thread-blocking, the first page tests will need to finish
 * before the second page tests can start, so using {@link #runComparativeTest(int, String, String)} is recommended.</p>
 * <br/>
 * <p>For more information on the WebPageTest REST API, visit
 * <a href="https://sites.google.com/a/webpagetest.org/docs/advanced-features/webpagetest-restful-apis">RESTful APIs</a></p>
 */
public class JPageTest {

    /**
     * The WebPageTest API Key.
     */
    private final String API_KEY;
    /**
     * The WebPageTest test API endpoint.
     */
    private static final String TEST_API_URL = "https://www.webpagetest.org/runtest.php";

    /**
     * The number of times to try get the results from the test, before timing out.
     */
    private static final int TIME_OUT_TRIES = 10;
    /**
     * The number of seconds to wait between each try.
     */
    private static final int WAIT_TIME = 30;

    /**
     * Constructor for the main project class.
     *
     * @param apiKey the key for the WebPageTest API.
     */
    public JPageTest(String apiKey) {
        this.API_KEY = apiKey;
    }

    /**
     * Sends the initial request to start the WebPageTest's test using the
     * {@value TEST_API_URL} endpoint, and then returns it's output in JSON
     * format. The output will be regarding the test itself, not the data
     * from the test.
     *
     * @param runs    number of times to run the test for each page.
     * @param pageURL the url of the page to test.
     * @return the payload of the test.
     * @throws IOException if an I/O error occurs.
     * @see #parse(JSONObject)
     */
    public JSONObject request(int runs, String pageURL) throws IOException {
        Objects.nonNull(TEST_API_URL);
        URLBuilder url = new URLBuilder(TEST_API_URL);

        // Set API key
        url.addParam("k", API_KEY);

        // Set URL to test
        url.addParam("url", pageURL);

        // Get JSON response
        url.addParam("f", "json");

        // Set number of runs
        url.addParam("runs", runs);

        // Set first view only
        url.addParam("fvonly", "1");

        return readJsonFromUrl(url.toString());
    }

    /**
     * A thread-blocking way to parse the payload of the test and extract the data from the results.
     *
     * @param testPayload the payload of the test.
     * @return a list of the Data results. The size of the list will be equal
     * to the number of runs.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if any thread has interrupted the current
     *                              thread. The interrupted status of the current thread is cleared when
     *                              this exception is thrown.
     * @throws Exception            if data cannot be retrieved.
     * @see #request(int, String)
     */
    public List<Data> parse(JSONObject testPayload) throws Exception {
        List<Data> results = new ArrayList<>();
        try {
            testPayload = testPayload.getJSONObject("data");
        } catch (JSONException ex) {
            throw new JSONException(testPayload.getInt("statusCode") + " - " + testPayload.getString("statusText"), ex);
        }

        String dataURL = testPayload.getString("jsonUrl");

        // Collect the data from the WebPageTest REST API endpoint. If the data
        // hasnt been pushed to the endpoint yet, an org.json.JSONException is thrown.
        // Wait {@value WAIT_TIME} seconds then try again. If there is still no
        // data after {@value TIME_OUT_TRIES} tries, it fails and an exception is thrown.

        JSONObject dataPayload = null, data = null;
        for (int i = 1; i <= TIME_OUT_TRIES; i++) {
            try {
                dataPayload = readJsonFromUrl(dataURL).getJSONObject("data");

                // Test to check if data has been populated - "runs" is only populated once all tests finished
                dataPayload.getJSONObject("runs").getJSONObject("1").getJSONObject("firstView").getInt("firstContentfulPaint");

                data = dataPayload.getJSONObject("runs");
                break;
            } catch (JSONException ex) {
                // WebPageTest is still running and JSON hasnt been updated yet
                if (i != TIME_OUT_TRIES) Thread.sleep(WAIT_TIME * 1000);
                else throw new Exception("The data could not be retrieved from: " + dataURL, ex);
            }
        }

        if (data != null) {
            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject view = data.getJSONObject(key).getJSONObject("firstView");
                results.add(new Data(view, dataPayload.getString("url")));
            }
        }
        return results;
    }

    /**
     * Runs a WebPageTest performance test on the single specified page
     * for the specified number of runs.
     *
     * @param runs    the number of tests per page.
     * @param pageURL the page to test.
     * @return a list of the Data results. The size of the list will be equal
     * to the number of runs.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if any thread has interrupted the current
     *                              thread. The interrupted status of the current thread is cleared when
     *                              this exception is thrown.
     * @throws Exception            if data cannot be retrieved.
     */
    public List<Data> runSingleTest(int runs, String pageURL) throws Exception {
        if (runs < 1) return new ArrayList<>();
        JSONObject testPayload = request(runs, pageURL);
        return parse(testPayload);
    }

    /**
     * Runs a WebPageTest performance test on the two specified pages for
     * the specified number of runs per page.
     *
     * @param runs     the number of tests per page.
     * @param page1URL the first page to test.
     * @param page2URL the second page to test.
     * @return a list of an array of Data results.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if any thread has interrupted the current
     *                              thread. The interrupted status of the current thread is cleared when
     *                              this exception is thrown.
     * @throws Exception            if data cannot be retrieved.
     */
    public List<Data[]> runComparativeTest(int runs, String page1URL, String page2URL) throws Exception {
        List<Data[]> results = new ArrayList<>();
        if (runs < 1) return results;
        JSONObject[] payloads = new JSONObject[]{request(runs, page1URL), request(runs, page2URL)};
        List<Data> page1Data = parse(payloads[0]), page2Data = parse(payloads[1]);

        // If the runs and result sizes don't match, fix value of runs to not overflow.
        // Usually happens if runs exceeds the runs per call limit of public (free) provisions.
        if (page1Data.size() == page2Data.size() && runs > page1Data.size()) runs = page1Data.size();
        else runs = Math.min(page1Data.size(), page2Data.size());

        for (int i = 0; i < runs; i++) results.add(new Data[]{page1Data.get(i), page2Data.get(i)});
        return results;
    }

    /**
     * Reads the data from the specified url and extracts it as a {@link JSONObject}.
     *
     * @param url the url of the data.
     * @return the data from the url as a JSONObject.
     * @throws IOException if an I/O error occurs.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException {
        try (InputStream inputStream = new URL(url).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return new JSONObject(read(reader));
        }
    }

    /**
     * Reads the data from the specified reader and returns it as a string.
     *
     * @param reader the reader.
     * @return the contents of the reader.
     * @throws IOException if an I/O error occurs.
     */
    private static String read(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) builder.append((char) cp);
        return builder.toString();
    }
}
