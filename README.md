# JPageTest

[![build](https://github.com/BrendonCurmi/JPageTest/workflows/Master%20Branch/badge.svg)](https://github.com/BrendonCurmi/JPageTest/actions)
[![license](https://img.shields.io/github/license/BrendonCurmi/JPageTest)](https://github.com/BrendonCurmi/JPageTest/blob/master/LICENSE)

JPageTest is a Java wrapper for the [WebPageTest](https://webpagetest.org/) API.

**WebPageTest Notices:**
- The public (free) provision is up to 200 page loads per day. Each run, first or repeat view counts as a page load. Ex: 10 runs, first and repeat view, would be a total of 20 page loads.
  - To maximise API calls, these methods only call the first view.
  
- The public (free) provision only allows up to 9 test runs per call.
  - It is recommended to use `for-each` loops when iterating the test results, to prevent overflow. 

- Sometimes WebPageTest randomly doesn't collect pieces of data.

For more information on the WebPageTest REST API, please visit [RESTful APIs](https://sites.google.com/a/webpagetest.org/docs/advanced-features/webpagetest-restful-apis).

Getting Started
====
Firstly, you will need to create an API key on [WebPageTest](https://www.webpagetest.org/getkey.php).

If you are using [Maven](http://maven.apache.org/), you can install the library by adding the below fragment to your pom.xml file, replacing `(version)` with an appropriate release:
```xml
<dependencies>
  <dependency>
    <groupId>io.BrendonCurmi</groupId>
    <artifactId>JPageTest</artifactId>
    <version>(version)</version>
  </dependency>
</dependencies>
```

If not using Maven, you can download the JAR binary from [Releases](https://github.com/BrendonCurmi/JPageTest/releases) or [Packages](https://github.com/BrendonCurmi/JPageTest/packages).

Usage
====
**Sample.java**

```java
import java.util.List;
import io.BrendonCurmi.JPageTest.JPageTest;
import io.BrendonCurmi.JPageTest.Data;

public class Sample {

    public static void main(String[] args) {
        // Pass the API key from WebPageTest to the class handler
        JPageTest jPageTest = new JPageTest("sample_key");

        try {
            // Run 5 tests on "https://www.example.com/" (total 5 tests (first view only))
            List<Data> singleTestResults = jPageTest.runSingleTest(5, "https://www.example.com/");
            for (Data data : singleTestResults) { // Iterate through each test (data of n'th test)
                System.out.println("View " + data.getURL() + ":");
                System.out.println("  Load Time: " + data.getSeconds("loadTime"));
                System.out.println("  First Contentful Paint: " + data.getSeconds("firstContentfulPaint"));
                System.out.println("  Document Complete Time: " + data.getSeconds("docTime"));
                System.out.println("  Fully Loaded Time: " + data.getSeconds("fullyLoaded"));
            }

            // Run 5 tests simultaneously on "https://www.example.com/" and "https://www.example.org/" each (total 10 tests (first view only))
            List<Data[]> comparativeTestResults = jPageTest.runComparativeTest(5, "https://www.example.com/", "https://www.example.org/");
            for (Data[] dataPairs : comparativeTestResults) { // Iterate through each test pair (data pair of n'th test from both URLs)
                for (Data data : dataPairs) {
                    System.out.println("View " + data.getURL() + ":");
                    System.out.println("  Load Time: " + data.getSeconds("loadTime"));
                    System.out.println("  First Contentful Paint: " + data.getSeconds("firstContentfulPaint"));
                    System.out.println("  Document Complete Time: " + data.getSeconds("docTime"));
                    System.out.println("  Fully Loaded Time: " + data.getSeconds("fullyLoaded"));
                }
            }
        } catch (Exception ex) {
            // If there are issues retrieving data, with the JSON, with I/O, or with the thread
            // Non-existent string keys will not result in an exception here - default values will be returned
            System.err.println(ex.getMessage());
        }
    }
}
```

It is recommended to use `for-each` loops instead of `for` loops when iterating the test results, to avoid overflow if users have the public (free) WebPageTest API provisioning limiting test runs to 9 per call.

Dependencies
====
- [JSON in Java](https://mvnrepository.com/artifact/org.json/json)
- [JUnit Jupiter API](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api)