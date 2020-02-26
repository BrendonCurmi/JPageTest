package io.BrendonCurmi.JPageTest;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.BrendonCurmi.JPageTest.Time.millisToSeconds;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    public void testTests() {
        try {
            JPageTest test = new JPageTest(System.getenv("WPT_API_KEY"));
            testSingle(test);
            testComparative(test);
            System.out.println("] Tests Successful! :D");
        } catch (Exception ex) {
            System.out.println("] Tests Failed :(");
            fail(ex);
        }
    }

    private void testSingle(JPageTest test) throws Exception {
        List<Data> results = test.runSingleTest(2, "https://www.example.com");
        for (Data data : results) {
            System.out.println("View:");
            System.out.println("  Load Time: " + data.getSeconds("loadTime"));
            System.out.println("  First Contentful Paint: " + data.getSeconds("firstContentfulPaint"));
        }
        assertNotEquals(0, results.size());

        results = test.runSingleTest(0, "https://www.example.com");
        assertEquals(0, results.size());

        results = test.runSingleTest(-10, "https://www.example.com");
        assertEquals(0, results.size());
    }

    private void testComparative(JPageTest test) throws Exception {
        List<Data[]> results = test.runComparativeTest(2, "https://www.example.com", "https://www.example.org");
        for (Data[] dataPairs : results) {
            for (Data data : dataPairs) {
                System.out.println("View " + data.getURL() + ":");
                System.out.println("  Load Time: " + data.getSeconds("loadTime"));
                System.out.println("  First Contentful Paint: " + data.getSeconds("firstContentfulPaint"));
            }
        }
        assertNotEquals(0, results.size());

        results = test.runComparativeTest(0, "https://www.example.com", "https://www.example.org");
        assertEquals(0, results.size());

        results = test.runComparativeTest(-10, "https://www.example.com", "https://www.example.org");
        assertEquals(0, results.size());
    }

    @Test
    public void testURL() {
        URLBuilder url = new URLBuilder("https://www.webpagetest.org/runtest.php");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php");

        url = new URLBuilder("https://www.webpagetest.org/runtest.php?test=true");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php?test=true");

        url = new URLBuilder("https://www.webpagetest.org/runtest.php?");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php");

        try {
            url = new URLBuilder("?");
        } catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException successfully thrown!");
            assertEquals(ex.getMessage(), "The url cannot be just a '?'");
        }

        url.addParam("url", "foo");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php?url=foo");

        url.addParam("url2", "bar");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php?url=foo&url2=bar");
    }

    @Test
    public void testTime() {
        assertEquals(new BigDecimal("0.000"), millisToSeconds(0));
        assertEquals(new BigDecimal("0.001"), millisToSeconds(1));
        assertEquals(new BigDecimal("234.567"), millisToSeconds(234567));
        assertEquals(new BigDecimal("-234.567"), millisToSeconds(-234567));
    }
}
