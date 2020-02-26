package io.BrendonCurmi.JPageTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Time {

    private static final BigDecimal THOUSAND = new BigDecimal(1000);

    /**
     * Converts the specified duration from milliseconds into seconds to 3 d.p.
     *
     * @param duration the duration in milliseconds.
     * @return the duration in seconds to 3 decimal places.
     */
    public static BigDecimal millisToSeconds(long duration) {
        return new BigDecimal(duration).divide(THOUSAND, 3, RoundingMode.DOWN);
    }
}
