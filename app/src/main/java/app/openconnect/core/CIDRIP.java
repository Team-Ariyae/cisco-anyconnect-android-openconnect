package app.openconnect.core;

import java.util.Locale;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/CIDRIP.class */
class CIDRIP {
    public int len;
    public String mIp;

    public CIDRIP(String str) {
        String[] split = str.split("/");
        this.mIp = split[0];
        this.len = split[1].matches("^[0-9]+$") ? Integer.parseInt(split[1]) : maskToLen(split[1]);
        int i7 = this.len;
        if (i7 < 0 || i7 > 32) {
            this.len = 32;
        }
        normalise();
    }

    public CIDRIP(String str, int i7) {
        this.mIp = str;
        this.len = i7;
    }

    public CIDRIP(String str, String str2) {
        this.mIp = str;
        this.len = maskToLen(str2);
    }

    public static long getInt(String str) {
        return (Long.parseLong(str.split("\\.")[0]) << 24) + 0 + (Integer.parseInt(r0[1]) << 16) + (Integer.parseInt(r0[2]) << 8) + Integer.parseInt(r0[3]);
    }

    private static int maskToLen(String str) {
        long j7 = getInt(str) + 4294967296L;
        int i7 = 0;
        while ((1 & j7) == 0) {
            i7++;
            j7 >>= 1;
        }
        if (j7 != (8589934591L >> i7)) {
            return 32;
        }
        return 32 - i7;
    }

    public long getInt() {
        return getInt(this.mIp);
    }

    public boolean normalise() {
        long j7 = getInt(this.mIp);
        long j8 = (4294967295L << (32 - this.len)) & j7;
        boolean z7 = false;
        if (j8 != j7) {
            z7 = true;
            this.mIp = String.format("%d.%d.%d.%d", Long.valueOf(((-16777216) & j8) >> 24), Long.valueOf((16711680 & j8) >> 16), Long.valueOf((65280 & j8) >> 8), Long.valueOf(j8 & 255));
        }
        return z7;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s/%d", this.mIp, Integer.valueOf(this.len));
    }
}
