package com.mugi.peti.kozat.utilities;

public class StringFormatter
{
    static final String REMOVE_UNNECESSARY_LINES_REGEX = "(?m)^\\s*$[\n\r]{1,}";

    static final String REMOVE_UNNECESSARY_WHITESPACES_REGEX = "\\s+";

    static final String CHECK_SPECIAL_CHARS_REGEX = "[`~!@#$%^&*()_+\\\\[\\\\]\\\\\\\\;\\',./{}|:\\\"<>?]";

    public static boolean checkIfStringIsNullOrEmpty(String stringToCheck){

        return stringToCheck.isEmpty() || removeUnnecessaryLinesFromString(stringToCheck).trim().length() == 0;
    }

    public static String removeUnnecessaryLinesFromString(String stringToModify)
    {
        return  stringToModify.replaceAll(REMOVE_UNNECESSARY_LINES_REGEX, "");
    }

    public static boolean checkIfStringContainsSpecialChars(String stringToCheck){
        return stringToCheck.matches(CHECK_SPECIAL_CHARS_REGEX);
    }

    public static String removeUnnecessaryWhiteSpaces(String stringToCheck){
        return stringToCheck.replaceAll(REMOVE_UNNECESSARY_WHITESPACES_REGEX, " ");
    }
}
