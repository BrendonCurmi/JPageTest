package io.BrendonCurmi.JPageTest;

import io.BrendonCurmi.JPageTest.Data;
import io.BrendonCurmi.JPageTest.JPageTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.BrendonCurmi.JPageTest.Time.millisToSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void tests() throws Exception {
        JPageTest test = new JPageTest(System.getenv("WPT_API_KEY"));
        if (testURL()
                && testSingle(test)
                && testComparative(test)) {
            System.out.println("] Tests Successful! :D");
        } else System.out.println("] Tests Failed :(");
    }

    private boolean testURL() {
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
        }

        url.addParam("url", "foo");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php?url=foo");

        url.addParam("url2", "bar");
        assertEquals(url.toString(), "https://www.webpagetest.org/runtest.php?url=foo&url2=bar");

        return true;
    }

    private boolean testSingle(JPageTest test) throws Exception {
        List<Data> results = test.runSingleTest(2, "https://www.example.com");
        for (Data data : results) {
            System.out.println("View:");
            System.out.println("  Load Time: " + millisToSeconds(data.obj().getInt("loadTime")));
            System.out.println("  First Contentful Paint: " + millisToSeconds(data.obj().getInt("firstContentfulPaint")));
            System.out.println("  Document Complete Time: " + millisToSeconds(data.obj().getInt("docTime")));
            System.out.println("  Fully Loaded Time: " + millisToSeconds(data.obj().getInt("fullyLoaded")));
        }
        return true;
    }

    private boolean testComparative(JPageTest test) throws Exception {
        List<Data[]> results2 = test.runComparativeTest(2, "https://www.example.com", "https://www.example.org");
        for (Data[] dataPairs : results2) {
            for (Data data : dataPairs) {
                System.out.println("View " + data.getURL() + ":");
                System.out.println("  Load Time: " + millisToSeconds(data.getInt("loadTime")));
                System.out.println("  First Contentful Paint: " + millisToSeconds(data.getInt("firstContentfulPaint")));
                System.out.println("  Document Complete Time: " + millisToSeconds(data.getInt("docTime")));
                System.out.println("  Fully Loaded Time: " + millisToSeconds(data.getInt("fullyLoaded")));
            }
        }
        return true;
    }
}
