package org.xbill.DNS.utils;

import java.io.ByteArrayOutputStream;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/utils/base16.class */
public class base16 {
    private static final String Base16 = "0123456789ABCDEF";

    private base16() {
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:25:0x008e
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
        */
    public static byte[] fromString(java.lang.String r5) {
        /*
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r1 = r0
            r1.<init>()
            r9 = r0
            r0 = r5
            byte[] r0 = r0.getBytes()
            r5 = r0
            r0 = 0
            r7 = r0
            r0 = 0
            r6 = r0
        L12:
            r0 = r6
            r1 = r5
            int r1 = r1.length
            if (r0 >= r1) goto L30
            r0 = r5
            r1 = r6
            r0 = r0[r1]
            char r0 = (char) r0
            boolean r0 = java.lang.Character.isWhitespace(r0)
            if (r0 != 0) goto L2a
            r0 = r9
            r1 = r5
            r2 = r6
            r1 = r1[r2]
            r0.write(r1)
        L2a:
            int r6 = r6 + 1
            goto L12
        L30:
            r0 = r9
            byte[] r0 = r0.toByteArray()
            r11 = r0
            r0 = r11
            int r0 = r0.length
            r1 = 2
            int r0 = r0 % r1
            if (r0 == 0) goto L41
            r0 = 0
            return r0
        L41:
            r0 = r9
            r0.reset()
            java.io.DataOutputStream r0 = new java.io.DataOutputStream
            r1 = r0
            r2 = r9
            r1.<init>(r2)
            r5 = r0
            r0 = r7
            r6 = r0
        L52:
            r0 = r6
            r1 = r11
            int r1 = r1.length
            if (r0 >= r1) goto L88
            java.lang.String r0 = "0123456789ABCDEF"
            r1 = r11
            r2 = r6
            r1 = r1[r2]
            char r1 = (char) r1
            char r1 = java.lang.Character.toUpperCase(r1)
            int r0 = r0.indexOf(r1)
            byte r0 = (byte) r0
            r7 = r0
            java.lang.String r0 = "0123456789ABCDEF"
            r1 = r11
            r2 = r6
            r3 = 1
            int r2 = r2 + r3
            r1 = r1[r2]
            char r1 = (char) r1
            char r1 = java.lang.Character.toUpperCase(r1)
            int r0 = r0.indexOf(r1)
            byte r0 = (byte) r0
            r8 = r0
            r0 = r5
            r1 = r7
            r2 = 4
            int r1 = r1 << r2
            r2 = r8
            int r1 = r1 + r2
            r0.writeByte(r1)     // Catch: java.io.IOException -> L8e
        L82:
            int r6 = r6 + 2
            goto L52
        L88:
            r0 = r9
            byte[] r0 = r0.toByteArray()
            return r0
        L8e:
            r10 = move-exception
            goto L82
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.utils.base16.fromString(java.lang.String):byte[]");
    }

    public static String toString(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte b8 : bArr) {
            short s7 = (short) (b8 & 255);
            byteArrayOutputStream.write(Base16.charAt((byte) (s7 >> 4)));
            byteArrayOutputStream.write(Base16.charAt((byte) (s7 & 15)));
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
}
