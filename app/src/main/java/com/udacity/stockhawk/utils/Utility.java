package com.udacity.stockhawk.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Utility {
    public static String createSymbol(String string, boolean useLongToday) {

        return useLongToday?string:string;
    }

    public static String createPrice(float price, boolean useLongToday) {

        DecimalFormat dollarFormat;
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        return useLongToday?dollarFormat.format(price):price+"";
    }

    public static String createAbsolute(float absoluteChange, boolean useLongToday) {
        DecimalFormat dollarFormatWithPlus;
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");

        return dollarFormatWithPlus.format(absoluteChange);
    }

    public static String createPercent(float percentageChange, boolean useLongToday) {
        DecimalFormat percentageFormat;
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        return percentageFormat.format(percentageChange / 100);
    }
}
