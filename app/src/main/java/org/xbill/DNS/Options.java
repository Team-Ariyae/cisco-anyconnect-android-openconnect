package org.xbill.DNS;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Options.class */
public final class Options {
    private static Map table;

    static {
        try {
            refresh();
        } catch (SecurityException e8) {
        }
    }

    private Options() {
    }

    public static boolean check(String str) {
        Map map = table;
        boolean z7 = false;
        if (map == null) {
            return false;
        }
        if (map.get(str.toLowerCase()) != null) {
            z7 = true;
        }
        return z7;
    }

    public static void clear() {
        table = null;
    }

    public static int intValue(String str) {
        String value = value(str);
        if (value == null) {
            return -1;
        }
        try {
            int parseInt = Integer.parseInt(value);
            if (parseInt > 0) {
                return parseInt;
            }
            return -1;
        } catch (NumberFormatException e8) {
            return -1;
        }
    }

    public static void refresh() {
        String property = System.getProperty("dnsjava.options");
        if (property != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String nextToken = stringTokenizer.nextToken();
                int indexOf = nextToken.indexOf(61);
                if (indexOf == -1) {
                    set(nextToken);
                } else {
                    set(nextToken.substring(0, indexOf), nextToken.substring(indexOf + 1));
                }
            }
        }
    }

    public static void set(String str) {
        if (table == null) {
            table = new HashMap();
        }
        table.put(str.toLowerCase(), "true");
    }

    public static void set(String str, String str2) {
        if (table == null) {
            table = new HashMap();
        }
        table.put(str.toLowerCase(), str2.toLowerCase());
    }

    public static void unset(String str) {
        Map map = table;
        if (map == null) {
            return;
        }
        map.remove(str.toLowerCase());
    }

    public static String value(String str) {
        Map map = table;
        if (map == null) {
            return null;
        }
        return (String) map.get(str.toLowerCase());
    }
}
