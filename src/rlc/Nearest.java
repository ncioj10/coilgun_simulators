package rlc;/*
 * coilsim.Nearest.java - provides helper functions to display numbers.
 * This class was written for
 * a) simple wrapper around java.text.NumberFormat for fractions
 * b) provide special formatting for "two significant figures" for any size number
 *
 * Created on October 12, 2003, 9:56 PM
 * @author  Barry (barry@coilgun.info)
 */
import java.text.*;

public final class Nearest {
    /**
     * Helper function to convert small numbers into engineering units.
     * @param d is number to convert (seconds, farads, henries, amps, etc)
     * @param places must be 2 or 3
     * @param sUnit is a unit to append ("s", "F", "H", "A", etc)
     * @return string representation using correct engineering units
     */
    public final String toStringEng(double d, int places, String sUnit) {
        String s;
        if (d == 0)                 // special case: zero has only one digit (not 0.0)
            s = "0";
        else if (d < 0.999E-9)      // should show pico's?
            s = toStringSigFig(d*1E12, places) + " p";
        else if (d < 0.999E-6)      // should show nano's?
            s = toStringSigFig(d*1E9, places) + " n";
        else if (d < 0.999E-3)      // should show micro's?
            s = toStringSigFig(d*1E6, places) + " u";
        else if (d < 0.999E0)       // should show milli's?
            s = toStringSigFig(d*1E3, places) + " m";
        else if (d < 0.999E3)       // should show plain units?
            s = toStringSigFig(d*1E0, places) + " ";
        else if (d < 0.999E6)       // should show kilo's?
            s = toStringSigFig(d*1E-3, places) + " K";
        else if (d < 0.999E9)       // should show mega's?
            s = toStringSigFig(d*1E-6, places) + " M";
        else if (d < 0.999E12)      // should show giga's'?
            s = toStringSigFig(d*1E-9, places) + " G";
        else if (d < 0.999E15)      // should show tera's'?
            s = toStringSigFig(d*1E-12, places) + " T";
        else
            s = "no convert!";

        if (d != 0)                 // special case: zero has no units
            s += sUnit;

        return s;
    }
    
    /**
     * coilsim.Nearest.toStringSigFig
     *
     * @param f double
     * @param places must be 2 or 3
     * @return string rep of a double rounded to TWO or THREE significant figures.
     * For example: 0.12 - 1.2 - 12 - 120 - 1,200 - 12,000
     */
    final String toStringSigFig(double f, int places) {
        switch (places) {
        case 2:
            {
                if (f < 0.09999)
                    return Thousandth(f);
                if (f < 0.9999)
                    return Hundredth(f);
                else if (f < 9.999)
                    return Tenth(f);
                else if (f < 99.99)
                    return Unit(f);
                else if (f < 999.9)
                    return RoundTo(f,10);
                else if (f < 10000)
                    return RoundTo(f,100);
                else if (f < 100000)
                    return RoundTo(f,1000);
                else if (f < 1000000)
                    return RoundTo(f,10000);
                else if (f < 10000000)
                    return RoundTo(f,100000);
                else
                    return Double.toString(f);
            }
        case 3:
            {
                if (f < 0.09999)
                    return TenThousandth(f);
                if (f < 0.9999)
                    return Thousandth(f);
                else if (f < 9.999)
                    return Hundredth(f);
                else if (f < 99.99)
                    return Tenth(f);
                else if (f < 999.9)
                    return Unit(f);
                else if (f < 9999)
                    return RoundTo(f,10);
                else if (f < 99990)
                    return RoundTo(f,100);
                else if (f < 999900)
                    return RoundTo(f,1000);
                else if (f < 9999000)
                    return RoundTo(f,10000);
                else
                    return Double.toString(f);
            }
        default:
                return "error!";
        }
    }
    
    /**
     * coilsim.Nearest.RoundTo
     * 
     * @param f - number to be rounded
     * @param place - use 0.1 to round to nearest tenth, 10 to round to nearest tens, etc.
     * @return string rep of a double rounded to specified place value
     * See also http://java.sun.com/j2se/1.4.1/docs/api/java/text/NumberFormat.html
     */
    final String RoundTo(double f, double place) {
        String s;
        int nNearest = (int)(f / place + 0.5);
        int nRounded = (int)(nNearest * place);
        numbers.setMinimumFractionDigits(0);
        numbers.setMaximumFractionDigits(0);
        numbers.setGroupingUsed(true);      // insert comma for thousands
        s = numbers.format(nRounded);
        return s;
    }

    /**
     * coilsim.Nearest.Unit
     * 
     * @param f double
     * @return string rep of a double rounded using ROUND_HALF_EVEN to nearest integer
     * @see java.text.DecimalFormat
     */
    final String Unit(double f) {
        numbers.setMinimumFractionDigits(0);
        numbers.setMaximumFractionDigits(0);
        numbers.setGroupingUsed(true);
        return numbers.format(f);
    }

    /**
     * coilsim.Nearest.Tenth
     * 
     * @param f double
     * @return string rep of a double rounded to nearest tenth
     */
    final String Tenth(double f) {
        numbers.setMinimumFractionDigits(1);
        numbers.setMaximumFractionDigits(1);
        return numbers.format(f);
    }

    /**
     * coilsim.Nearest.Hundredth
     * 
     * @param f double
     * @return string representation of a double rounded to nearest hundredth
     */
    final String Hundredth(double f) {
        numbers.setMinimumFractionDigits(2);
        numbers.setMaximumFractionDigits(2);
        return numbers.format(f);
    }

    /**
     * coilsim.Nearest.Thousandth
     * 
     * @param f double
     * @return string representation of a double rounded to nearest thousandth
     */
    final String Thousandth(double f) {
        numbers.setMinimumFractionDigits(3);
        numbers.setMaximumFractionDigits(3);
        return numbers.format(f);
    }
    
    /**
     * coilsim.Nearest.TenThousandth
     * 
     * @param f double
     * @return string representation of a double rounded to nearest thousandth
     */
    final String TenThousandth(double f) {
        numbers.setMinimumFractionDigits(4);
        numbers.setMaximumFractionDigits(4);
        return numbers.format(f);
    }

    /**
     * coilsim.Nearest.Decade
     * Example: coilsim.Nearest.Decade(36.5) will return 100
     * This helps you choose the axis for a graph that will include given values.
     *
     * @param x float
     * @return powers of ten
     */
    static float Decade(double x) {
        double log = Math.log(x)/Math.log(10.); // take log base 10
        double decade = Math.ceil(log);         // find next highest integer
        double result = Math.pow(10., decade);  // compute the power of 10
        return (float)result;
    }

    /**
     * coilsim.Nearest.HalfDecade
     * Example: coilsim.Nearest.HalfDecade(36.5) will return 40
     * The Decade() function is all well and good, but we need another
     * graphing scale between 1 and 10, because the curve gets too small
     * when it's squeezed into the bottom tenth of a graph.
     * So this function finds the nearest 40 vs 100 scale to contain the data.
     * Why not 50 vs 100? Because "40" puts the transition about in the middle
     * of the graph, closer to the square root of ten (3.16).
     *
     * @param x float
     * @return powers of ten
     */
    static float HalfDecade(double x) {
        final double FRACTION4 = Math.log(4)/Math.log(10);

        double log = Math.log(x)/Math.log(10.); // take log base 10
        log = Math.floor(log*10000.F)/10000.F;
        double mantissa = log - Math.floor(log);// find the fractional part of the log

        double decade;
        if (mantissa == 0) {
            decade = Math.ceil(log);
        } else if (mantissa > FRACTION4) {
            // 5.001 ... 9.9999
            decade = Math.ceil(log);            // find next highest decade of 10
        } else {
            // 1.001 ... 5.000
            decade = Math.floor(log) + FRACTION4;   // next highest decade of 5 (e.g. 5, 50, 500...)
        }

        double result = Math.pow(10., decade);  // compute the power (1, 5, 10, 50, 100, ...)
        return (float)result;
    }

    /**
     * coilsim.Nearest.ThirdDecade
     * Example: coilsim.Nearest.ThirdDecade(36.5) will return 40
     * This function finds the nearest 10 - 20 - 40 - 100 scale to contain the data.
     *
     * The Decade() and HalfDecade functions are all well and good.
     * But we STILL need another graphing scale between 4 and 10, because
     * the curve is still too small when it's squeezed into the bottom fourth
     * of a graph.
     *
     * @param x float
     * @return powers of ten
     */
    static float ThirdDecade(double x) {
        final double FRACTION2 = Math.log(2)/Math.log(10);
        final double FRACTION4 = Math.log(4)/Math.log(10);

        double log = Math.log(x)/Math.log(10.); // take log base 10
        log = Math.floor(log*10000.F)/10000.F;
        double mantissa = log - Math.floor(log);// find the fractional part of the log

        double decade;
        if (mantissa == 0) {
            decade = Math.ceil(log);
        } else if (mantissa < FRACTION2) {
            decade = Math.floor(log) + FRACTION2;   // 1.001 ... 2.000
        } else if (mantissa < FRACTION4) {
            decade = Math.floor(log) + FRACTION4;   // 2.001 ... 4.000
        } else {
            decade = Math.ceil(log);                // find next highest decade of 10
        }

        double result = Math.pow(10., decade);  // compute the power (1, 2, 4, 10, 20, 40, 100, ...)
        return (float)result;
    }
    static float FractionOfDecade(double x) {
        final double[] LogTable = { Math.log(1.2)/Math.log(10),
                                    Math.log(1.6)/Math.log(10),
                                    Math.log(2.0)/Math.log(10),
                                    Math.log(3.0)/Math.log(10),
                                    Math.log(4.0)/Math.log(10),
                                    Math.log(5.0)/Math.log(10),
                                    Math.log(6.0)/Math.log(10),
                                    Math.log(7.0)/Math.log(10),
                                    Math.log(8.0)/Math.log(10),
                                    Math.log(8.0)/Math.log(10)
        };
//        final double FRACTIONa = Math.log(1.2)/Math.log(10);
//        final double FRACTIONb = Math.log(1.6)/Math.log(10);
//        final double FRACTIONc = Math.log(2.0)/Math.log(10);
//        final double FRACTIONd = Math.log(3.0)/Math.log(10);
//        final double FRACTIONe = Math.log(4.0)/Math.log(10);
//        final double FRACTIONf = Math.log(5.0)/Math.log(10);
//        final double FRACTIONg = Math.log(6.0)/Math.log(10);
//        final double FRACTIONh = Math.log(8.0)/Math.log(10);

        double log = Math.log(x)/Math.log(10.);     // take log base 10
        log = Math.floor(log*10000.F)/10000.F;
        double mantissa = log - Math.floor(log);    // find the fractional part of the log

        double decade = Math.ceil(log);
        if (mantissa == 0) {
           decade = Math.ceil(log);
        } else {
            for (int ii=0; ii<LogTable.length; ii++) {
                if (mantissa < LogTable[ii]) {
                    decade = Math.floor(log) + LogTable[ii];
                    break;
                }
            }

//        } else if (mantissa < FRACTIONa) {
//            decade = Math.floor(log) + FRACTIONa;
//        } else if (mantissa < FRACTIONb) {
//            decade = Math.floor(log) + FRACTIONb;
//        } else if (mantissa < FRACTIONc) {
//            decade = Math.floor(log) + FRACTIONc;
//        } else if (mantissa < FRACTIONd) {
//            decade = Math.floor(log) + FRACTIONd;
//        } else if (mantissa < FRACTIONe) {
//            decade = Math.floor(log) + FRACTIONe;
//        } else if (mantissa < FRACTIONf) {
//            decade = Math.floor(log) + FRACTIONf;
//        } else if (mantissa < FRACTIONg) {
//             decade = Math.floor(log) + FRACTIONg;
//        } else if (mantissa < FRACTIONh) {
//            decade = Math.floor(log) + FRACTIONh;
//        } else {
//            decade = Math.ceil(log);                // find next highest decade of 10
        }

        double result = Math.pow(10., decade);  // compute the power (1, 2, 4, 10, 20, 40, 100, ...)
        return (float)result;
    }

    // fields of class coilsim.Nearest
    private final NumberFormat numbers = java.text.NumberFormat.getInstance();

}
