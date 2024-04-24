package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.ArrayList;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Generator.class */
public class Generator {
    private long current;
    public final int dclass;
    public long end;
    public final String namePattern;
    public final Name origin;
    public final String rdataPattern;
    public long start;
    public long step;
    public final long ttl;
    public final int type;

    public Generator(long j7, long j8, long j9, String str, int i7, int i8, long j10, String str2, Name name) {
        if (j7 < 0 || j8 < 0 || j7 > j8 || j9 <= 0) {
            throw new IllegalArgumentException("invalid range specification");
        }
        if (!supportedType(i7)) {
            throw new IllegalArgumentException("unsupported type");
        }
        DClass.check(i8);
        this.start = j7;
        this.end = j8;
        this.step = j9;
        this.namePattern = str;
        this.type = i7;
        this.dclass = i8;
        this.ttl = j10;
        this.rdataPattern = str2;
        this.origin = name;
        this.current = j7;
    }

    /* JADX WARN: Code restructure failed: missing block: B:102:0x020b, code lost:
    
        if (r0 != 'x') goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x020e, code lost:
    
        r10 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x0211, code lost:
    
        r15 = 16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x021d, code lost:
    
        if (r0 != 'X') goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0220, code lost:
    
        r10 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x022a, code lost:
    
        if (r0 != 'd') goto L131;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x022d, code lost:
    
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x023d, code lost:
    
        throw new org.xbill.DNS.TextParseException("invalid base");
     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x0247, code lost:
    
        throw new org.xbill.DNS.TextParseException("invalid base");
     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x0248, code lost:
    
        r15 = 10;
        r11 = r10;
        r10 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x0165, code lost:
    
        r11 = r12;
        r12 = r11 + 1;
        r13 = r10;
        r19 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x0178, code lost:
    
        if (r12 >= r0.length) goto L147;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x017b, code lost:
    
        r13 = (char) (r0[r12] & 255);
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x018b, code lost:
    
        if (r13 == ',') goto L148;
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x0192, code lost:
    
        if (r13 != '}') goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:0x019c, code lost:
    
        if (r13 < '0') goto L136;
     */
    /* JADX WARN: Code restructure failed: missing block: B:127:0x01a3, code lost:
    
        if (r13 > '9') goto L137;
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x01a6, code lost:
    
        r10 = (char) (r13 - '0');
        r15 = (r15 * 10) + r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x01ca, code lost:
    
        throw new org.xbill.DNS.TextParseException("invalid width");
     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x01cb, code lost:
    
        r11 = r12;
        r19 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0138, code lost:
    
        throw new org.xbill.DNS.TextParseException("invalid offset");
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0139, code lost:
    
        r12 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x013d, code lost:
    
        r17 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0143, code lost:
    
        if (r11 == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0146, code lost:
    
        r17 = -r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x014b, code lost:
    
        r15 = 0;
        r13 = r10;
        r11 = r12;
        r19 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x015e, code lost:
    
        if (r10 != ',') goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x01d3, code lost:
    
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01db, code lost:
    
        if (r13 != ',') goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x01de, code lost:
    
        r11 = r11 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x01e6, code lost:
    
        if (r11 == r0.length) goto L138;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x01e9, code lost:
    
        r0 = (char) (r0[r11] & 255);
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01f9, code lost:
    
        if (r0 != 'o') goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x01fc, code lost:
    
        r10 = false;
        r15 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0257, code lost:
    
        r0 = r11 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0262, code lost:
    
        if (r0 == r0.length) goto L133;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x026c, code lost:
    
        if (r0[r0] != 125) goto L134;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x026f, code lost:
    
        r11 = r10;
        r10 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0283, code lost:
    
        throw new org.xbill.DNS.TextParseException("invalid modifiers");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String substitute(java.lang.String r6, long r7) {
        /*
            Method dump skipped, instructions count: 824
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.Generator.substitute(java.lang.String, long):java.lang.String");
    }

    public static boolean supportedType(int i7) {
        Type.check(i7);
        boolean z7 = true;
        if (i7 != 12) {
            z7 = true;
            if (i7 != 5) {
                z7 = true;
                if (i7 != 39) {
                    z7 = true;
                    if (i7 != 1) {
                        z7 = true;
                        if (i7 != 28) {
                            z7 = i7 == 2;
                        }
                    }
                }
            }
        }
        return z7;
    }

    public Record[] expand() {
        ArrayList arrayList = new ArrayList();
        long j7 = this.start;
        while (true) {
            long j8 = j7;
            if (j8 >= this.end) {
                return (Record[]) arrayList.toArray(new Record[arrayList.size()]);
            }
            arrayList.add(Record.fromString(Name.fromString(substitute(this.namePattern, this.current), this.origin), this.type, this.dclass, this.ttl, substitute(this.rdataPattern, this.current), this.origin));
            j7 = j8 + this.step;
        }
    }

    public Record nextRecord() {
        long j7 = this.current;
        if (j7 > this.end) {
            return null;
        }
        Name fromString = Name.fromString(substitute(this.namePattern, j7), this.origin);
        String substitute = substitute(this.rdataPattern, this.current);
        this.current += this.step;
        return Record.fromString(fromString, this.type, this.dclass, this.ttl, substitute, this.origin);
    }

    public String toString() {
        StringBuffer p = a.p("$GENERATE ");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.start);
        stringBuffer.append("-");
        stringBuffer.append(this.end);
        p.append(stringBuffer.toString());
        if (this.step > 1) {
            StringBuffer p7 = a.p("/");
            p7.append(this.step);
            p.append(p7.toString());
        }
        p.append(" ");
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(this.namePattern);
        stringBuffer2.append(" ");
        p.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(this.ttl);
        stringBuffer3.append(" ");
        p.append(stringBuffer3.toString());
        if (this.dclass != 1 || !Options.check("noPrintIN")) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append(DClass.string(this.dclass));
            stringBuffer4.append(" ");
            p.append(stringBuffer4.toString());
        }
        StringBuffer stringBuffer5 = new StringBuffer();
        stringBuffer5.append(Type.string(this.type));
        stringBuffer5.append(" ");
        p.append(stringBuffer5.toString());
        StringBuffer stringBuffer6 = new StringBuffer();
        stringBuffer6.append(this.rdataPattern);
        stringBuffer6.append(" ");
        p.append(stringBuffer6.toString());
        return p.toString();
    }
}
