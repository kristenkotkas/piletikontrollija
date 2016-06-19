package com.urban.ticket.control;

public class Result {
    private static String resultString;
    private static String returnlang;

    public static String getResult() {
        return resultString;
    }

    public static void setResult(String result) {
        resultString = result;
    }

    public static String getLang() {
        return returnlang;
    }

    public static void setLang(String land) {
        returnlang = land;
    }
}
