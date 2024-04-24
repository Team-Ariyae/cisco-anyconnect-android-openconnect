package app.openconnect.core;

import java.util.HashMap;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/FragCache.class */
public class FragCache {
    private static HashMap<String, String> mCache;

    public static String get(String str) {
        return get(null, str);
    }

    public static String get(String str, String str2) {
        String str3;
        synchronized (FragCache.class) {
            try {
                str3 = mCache.get(hashCode(str, str2));
            } catch (Throwable th) {
                throw th;
            }
        }
        return str3;
    }

    private static String hashCode(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str.hashCode());
        }
        sb.append(".");
        if (str2 != null) {
            sb.append(str2.hashCode());
        }
        return sb.toString();
    }

    public static void init() {
        synchronized (FragCache.class) {
            try {
                mCache = new HashMap<>();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void put(String str, String str2) {
        put(null, str, str2);
    }

    public static void put(String str, String str2, String str3) {
        synchronized (FragCache.class) {
            try {
                mCache.put(hashCode(str, str2), str3);
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
