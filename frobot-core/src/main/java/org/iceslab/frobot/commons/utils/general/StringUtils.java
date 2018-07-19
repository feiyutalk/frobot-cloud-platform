package org.iceslab.frobot.commons.utils.general;
public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }
}
