package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TTL.class */
public final class TTL {
    public static final long MAX_VALUE = 2147483647L;

    private TTL() {
    }

    public static void check(long j7) {
        if (j7 < 0 || j7 > MAX_VALUE) {
            throw new InvalidTTLException(j7);
        }
    }

    public static String format(long j7) {
        check(j7);
        StringBuffer stringBuffer = new StringBuffer();
        long j8 = j7 % 60;
        long j9 = j7 / 60;
        long j10 = j9 % 60;
        long j11 = j9 / 60;
        long j12 = j11 % 24;
        long j13 = j11 / 24;
        long j14 = j13 % 7;
        long j15 = j13 / 7;
        if (j15 > 0) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(j15);
            stringBuffer2.append("W");
            stringBuffer.append(stringBuffer2.toString());
        }
        if (j14 > 0) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(j14);
            stringBuffer3.append("D");
            stringBuffer.append(stringBuffer3.toString());
        }
        if (j12 > 0) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append(j12);
            stringBuffer4.append("H");
            stringBuffer.append(stringBuffer4.toString());
        }
        if (j10 > 0) {
            StringBuffer stringBuffer5 = new StringBuffer();
            stringBuffer5.append(j10);
            stringBuffer5.append("M");
            stringBuffer.append(stringBuffer5.toString());
        }
        if (j8 > 0 || (j15 == 0 && j14 == 0 && j12 == 0 && j10 == 0)) {
            StringBuffer stringBuffer6 = new StringBuffer();
            stringBuffer6.append(j8);
            stringBuffer6.append("S");
            stringBuffer.append(stringBuffer6.toString());
        }
        return stringBuffer.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00d5 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static long parse(java.lang.String r5, boolean r6) {
        /*
            Method dump skipped, instructions count: 290
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.TTL.parse(java.lang.String, boolean):long");
    }

    public static long parseTTL(String str) {
        return parse(str, true);
    }
}
