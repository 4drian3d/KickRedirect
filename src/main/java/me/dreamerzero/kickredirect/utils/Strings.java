package me.dreamerzero.kickredirect.utils;

public final class Strings {
    public static boolean containsIgnoreCase(final String st, final String other) {
        final int length = other.length();
        final int limit = st.length() - length;
        for (int i = 0; i <= limit; i++) {
            if (st.regionMatches(true, i, other, 0, length)) {
                return true;
            }
        }
        return false;
    }
}
