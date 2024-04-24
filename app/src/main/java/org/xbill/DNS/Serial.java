package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Serial.class */
public final class Serial {
    private static final long MAX32 = 4294967295L;

    private Serial() {
    }

    public static int compare(long j7, long j8) {
        long j9;
        if (j7 < 0 || j7 > MAX32) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(j7);
            stringBuffer.append(" out of range");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        if (j8 < 0 || j8 > MAX32) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(j8);
            stringBuffer2.append(" out of range");
            throw new IllegalArgumentException(stringBuffer2.toString());
        }
        long j10 = j7 - j8;
        if (j10 >= MAX32) {
            j9 = j10 - 4294967296L;
        } else {
            j9 = j10;
            if (j10 < -4294967295L) {
                j9 = j10 + 4294967296L;
            }
        }
        return (int) j9;
    }

    public static long increment(long j7) {
        if (j7 >= 0 && j7 <= MAX32) {
            if (j7 == MAX32) {
                return 0L;
            }
            return j7 + 1;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(j7);
        stringBuffer.append(" out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }
}
