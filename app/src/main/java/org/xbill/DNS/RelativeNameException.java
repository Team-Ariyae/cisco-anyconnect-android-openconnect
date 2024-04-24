package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/RelativeNameException.class */
public class RelativeNameException extends IllegalArgumentException {
    public RelativeNameException(String str) {
        super(str);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public RelativeNameException(org.xbill.DNS.Name r4) {
        /*
            r3 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r1 = r0
            r1.<init>()
            r5 = r0
            r0 = r5
            java.lang.String r1 = "'"
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r5
            r1 = r4
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r5
            java.lang.String r1 = "' is not an absolute name"
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r3
            r1 = r5
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.RelativeNameException.<init>(org.xbill.DNS.Name):void");
    }
}
