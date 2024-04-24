package org.xbill.DNS;

import java.util.Date;
import org.xbill.DNS.utils.HMAC;
import org.xbill.DNS.utils.base64;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TSIG.class */
public class TSIG {
    public static final short FUDGE = 300;
    public static final Name HMAC;
    public static final Name HMAC_MD5;
    private static final String HMAC_MD5_STR = "HMAC-MD5.SIG-ALG.REG.INT.";
    public static final Name HMAC_SHA1;
    private static final String HMAC_SHA1_STR = "hmac-sha1.";
    public static final Name HMAC_SHA224;
    private static final String HMAC_SHA224_STR = "hmac-sha224.";
    public static final Name HMAC_SHA256;
    private static final String HMAC_SHA256_STR = "hmac-sha256.";
    public static final Name HMAC_SHA384;
    private static final String HMAC_SHA384_STR = "hmac-sha384.";
    public static final Name HMAC_SHA512;
    private static final String HMAC_SHA512_STR = "hmac-sha512.";
    private Name alg;
    private String digest;
    private int digestBlockLength;
    private byte[] key;
    private Name name;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TSIG$StreamVerifier.class */
    public static class StreamVerifier {
        private TSIG key;
        private TSIGRecord lastTSIG;
        private int lastsigned;
        private int nresponses = 0;
        private HMAC verifier;

        public StreamVerifier(TSIG tsig, TSIGRecord tSIGRecord) {
            this.key = tsig;
            this.verifier = new HMAC(tsig.digest, this.key.digestBlockLength, this.key.key);
            this.lastTSIG = tSIGRecord;
        }

        public int verify(Message message, byte[] bArr) {
            TSIGRecord tsig = message.getTSIG();
            int i7 = this.nresponses + 1;
            this.nresponses = i7;
            if (i7 == 1) {
                int verify = this.key.verify(message, bArr, this.lastTSIG);
                if (verify == 0) {
                    byte[] signature = tsig.getSignature();
                    DNSOutput dNSOutput = new DNSOutput();
                    dNSOutput.writeU16(signature.length);
                    this.verifier.update(dNSOutput.toByteArray());
                    this.verifier.update(signature);
                }
                this.lastTSIG = tsig;
                return verify;
            }
            if (tsig != null) {
                message.getHeader().decCount(3);
            }
            byte[] wire = message.getHeader().toWire();
            if (tsig != null) {
                message.getHeader().incCount(3);
            }
            this.verifier.update(wire);
            this.verifier.update(bArr, wire.length, (tsig == null ? bArr.length : message.tsigstart) - wire.length);
            if (tsig == null) {
                if (this.nresponses - this.lastsigned >= 100) {
                    message.tsigState = 4;
                    return 1;
                }
                message.tsigState = 2;
                return 0;
            }
            this.lastsigned = this.nresponses;
            this.lastTSIG = tsig;
            if (!tsig.getName().equals(this.key.name) || !tsig.getAlgorithm().equals(this.key.alg)) {
                if (Options.check("verbose")) {
                    System.err.println("BADKEY failure");
                }
                message.tsigState = 4;
                return 17;
            }
            DNSOutput dNSOutput2 = new DNSOutput();
            long time = tsig.getTimeSigned().getTime() / 1000;
            dNSOutput2.writeU16((int) (time >> 32));
            dNSOutput2.writeU32(time & 4294967295L);
            dNSOutput2.writeU16(tsig.getFudge());
            this.verifier.update(dNSOutput2.toByteArray());
            if (!this.verifier.verify(tsig.getSignature())) {
                if (Options.check("verbose")) {
                    System.err.println("BADSIG failure");
                }
                message.tsigState = 4;
                return 16;
            }
            this.verifier.clear();
            DNSOutput dNSOutput3 = new DNSOutput();
            dNSOutput3.writeU16(tsig.getSignature().length);
            this.verifier.update(dNSOutput3.toByteArray());
            this.verifier.update(tsig.getSignature());
            message.tsigState = 1;
            return 0;
        }
    }

    static {
        Name fromConstantString = Name.fromConstantString(HMAC_MD5_STR);
        HMAC_MD5 = fromConstantString;
        HMAC = fromConstantString;
        HMAC_SHA1 = Name.fromConstantString(HMAC_SHA1_STR);
        HMAC_SHA224 = Name.fromConstantString(HMAC_SHA224_STR);
        HMAC_SHA256 = Name.fromConstantString(HMAC_SHA256_STR);
        HMAC_SHA384 = Name.fromConstantString(HMAC_SHA384_STR);
        HMAC_SHA512 = Name.fromConstantString(HMAC_SHA512_STR);
    }

    public TSIG(String str, String str2) {
        this(HMAC_MD5, str, str2);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public TSIG(java.lang.String r6, java.lang.String r7, java.lang.String r8) {
        /*
            r5 = this;
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_MD5
            r9 = r0
            r0 = r5
            r1 = r9
            r2 = r7
            r3 = r8
            r0.<init>(r1, r2, r3)
            r0 = r6
            java.lang.String r1 = "hmac-md5"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L1f
            r0 = r5
            r1 = r9
            r0.alg = r1
            goto L74
        L1f:
            r0 = r6
            java.lang.String r1 = "hmac-sha1"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L34
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_SHA1
            r6 = r0
        L2c:
            r0 = r5
            r1 = r6
            r0.alg = r1
            goto L74
        L34:
            r0 = r6
            java.lang.String r1 = "hmac-sha224"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L44
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_SHA224
            r6 = r0
            goto L2c
        L44:
            r0 = r6
            java.lang.String r1 = "hmac-sha256"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L54
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_SHA256
            r6 = r0
            goto L2c
        L54:
            r0 = r6
            java.lang.String r1 = "hmac-sha384"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L64
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_SHA384
            r6 = r0
            goto L2c
        L64:
            r0 = r6
            java.lang.String r1 = "hmac-sha512"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L79
            org.xbill.DNS.Name r0 = org.xbill.DNS.TSIG.HMAC_SHA512
            r6 = r0
            goto L2c
        L74:
            r0 = r5
            r0.getDigest()
            return
        L79:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            java.lang.String r2 = "Invalid TSIG algorithm"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.TSIG.<init>(java.lang.String, java.lang.String, java.lang.String):void");
    }

    public TSIG(Name name, String str, String str2) {
        byte[] fromString = base64.fromString(str2);
        this.key = fromString;
        if (fromString == null) {
            throw new IllegalArgumentException("Invalid TSIG key string");
        }
        try {
            this.name = Name.fromString(str, Name.root);
            this.alg = name;
            getDigest();
        } catch (TextParseException e8) {
            throw new IllegalArgumentException("Invalid TSIG key name");
        }
    }

    public TSIG(Name name, Name name2, byte[] bArr) {
        this.name = name2;
        this.alg = name;
        this.key = bArr;
        getDigest();
    }

    public TSIG(Name name, byte[] bArr) {
        this(HMAC_MD5, name, bArr);
    }

    public static TSIG fromString(String str) {
        String[] split = str.split("[:/]", 3);
        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid TSIG key specification");
        }
        String[] strArr = split;
        if (split.length == 3) {
            try {
                return new TSIG(split[0], split[1], split[2]);
            } catch (IllegalArgumentException e8) {
                strArr = str.split("[:/]", 2);
            }
        }
        return new TSIG(HMAC_MD5, strArr[0], strArr[1]);
    }

    private void getDigest() {
        String str;
        int i7 = 64;
        if (this.alg.equals(HMAC_MD5)) {
            str = "md5";
        } else if (this.alg.equals(HMAC_SHA1)) {
            str = "sha-1";
        } else if (this.alg.equals(HMAC_SHA224)) {
            str = "sha-224";
        } else if (this.alg.equals(HMAC_SHA256)) {
            str = "sha-256";
        } else {
            i7 = 128;
            if (this.alg.equals(HMAC_SHA512)) {
                str = "sha-512";
            } else {
                if (!this.alg.equals(HMAC_SHA384)) {
                    throw new IllegalArgumentException("Invalid algorithm");
                }
                str = "sha-384";
            }
        }
        this.digest = str;
        this.digestBlockLength = i7;
    }

    public void apply(Message message, int i7, TSIGRecord tSIGRecord) {
        message.addRecord(generate(message, message.toWire(), i7, tSIGRecord), 3);
        message.tsigState = 3;
    }

    public void apply(Message message, TSIGRecord tSIGRecord) {
        apply(message, 0, tSIGRecord);
    }

    public void applyStream(Message message, TSIGRecord tSIGRecord, boolean z7) {
        if (z7) {
            apply(message, tSIGRecord);
            return;
        }
        Date date = new Date();
        HMAC hmac = new HMAC(this.digest, this.digestBlockLength, this.key);
        int intValue = Options.intValue("tsigfudge");
        if (intValue < 0 || intValue > 32767) {
            intValue = 300;
        }
        DNSOutput dNSOutput = new DNSOutput();
        dNSOutput.writeU16(tSIGRecord.getSignature().length);
        hmac.update(dNSOutput.toByteArray());
        hmac.update(tSIGRecord.getSignature());
        hmac.update(message.toWire());
        DNSOutput dNSOutput2 = new DNSOutput();
        long time = date.getTime() / 1000;
        dNSOutput2.writeU16((int) (time >> 32));
        dNSOutput2.writeU32(time & 4294967295L);
        dNSOutput2.writeU16(intValue);
        hmac.update(dNSOutput2.toByteArray());
        message.addRecord(new TSIGRecord(this.name, 255, 0L, this.alg, date, intValue, hmac.sign(), message.getHeader().getID(), 0, null), 3);
        message.tsigState = 3;
    }

    public TSIGRecord generate(Message message, byte[] bArr, int i7, TSIGRecord tSIGRecord) {
        byte[] bArr2;
        Date date = i7 != 18 ? new Date() : tSIGRecord.getTimeSigned();
        HMAC hmac = (i7 == 0 || i7 == 18) ? new HMAC(this.digest, this.digestBlockLength, this.key) : null;
        int intValue = Options.intValue("tsigfudge");
        if (intValue < 0 || intValue > 32767) {
            intValue = 300;
        }
        if (tSIGRecord != null) {
            DNSOutput dNSOutput = new DNSOutput();
            dNSOutput.writeU16(tSIGRecord.getSignature().length);
            if (hmac != null) {
                hmac.update(dNSOutput.toByteArray());
                hmac.update(tSIGRecord.getSignature());
            }
        }
        if (hmac != null) {
            hmac.update(bArr);
        }
        DNSOutput dNSOutput2 = new DNSOutput();
        this.name.toWireCanonical(dNSOutput2);
        dNSOutput2.writeU16(255);
        dNSOutput2.writeU32(0L);
        this.alg.toWireCanonical(dNSOutput2);
        long time = date.getTime() / 1000;
        dNSOutput2.writeU16((int) (time >> 32));
        dNSOutput2.writeU32(time & 4294967295L);
        dNSOutput2.writeU16(intValue);
        dNSOutput2.writeU16(i7);
        dNSOutput2.writeU16(0);
        if (hmac != null) {
            hmac.update(dNSOutput2.toByteArray());
        }
        byte[] sign = hmac != null ? hmac.sign() : new byte[0];
        if (i7 == 18) {
            DNSOutput dNSOutput3 = new DNSOutput();
            long time2 = new Date().getTime() / 1000;
            dNSOutput3.writeU16((int) (time2 >> 32));
            dNSOutput3.writeU32(time2 & 4294967295L);
            bArr2 = dNSOutput3.toByteArray();
        } else {
            bArr2 = null;
        }
        return new TSIGRecord(this.name, 255, 0L, this.alg, date, intValue, sign, message.getHeader().getID(), i7, bArr2);
    }

    public int recordLength() {
        return this.alg.length() + this.name.length() + 10 + 8 + 18 + 4 + 8;
    }

    public byte verify(Message message, byte[] bArr, int i7, TSIGRecord tSIGRecord) {
        message.tsigState = 4;
        TSIGRecord tsig = message.getTSIG();
        HMAC hmac = new HMAC(this.digest, this.digestBlockLength, this.key);
        if (tsig == null) {
            return (byte) 1;
        }
        if (!tsig.getName().equals(this.name) || !tsig.getAlgorithm().equals(this.alg)) {
            if (!Options.check("verbose")) {
                return (byte) 17;
            }
            System.err.println("BADKEY failure");
            return (byte) 17;
        }
        if (Math.abs(System.currentTimeMillis() - tsig.getTimeSigned().getTime()) > tsig.getFudge() * 1000) {
            if (!Options.check("verbose")) {
                return (byte) 18;
            }
            System.err.println("BADTIME failure");
            return (byte) 18;
        }
        if (tSIGRecord != null && tsig.getError() != 17 && tsig.getError() != 16) {
            DNSOutput dNSOutput = new DNSOutput();
            dNSOutput.writeU16(tSIGRecord.getSignature().length);
            hmac.update(dNSOutput.toByteArray());
            hmac.update(tSIGRecord.getSignature());
        }
        message.getHeader().decCount(3);
        byte[] wire = message.getHeader().toWire();
        message.getHeader().incCount(3);
        hmac.update(wire);
        hmac.update(bArr, wire.length, message.tsigstart - wire.length);
        DNSOutput dNSOutput2 = new DNSOutput();
        tsig.getName().toWireCanonical(dNSOutput2);
        dNSOutput2.writeU16(tsig.dclass);
        dNSOutput2.writeU32(tsig.ttl);
        tsig.getAlgorithm().toWireCanonical(dNSOutput2);
        long time = tsig.getTimeSigned().getTime() / 1000;
        dNSOutput2.writeU16((int) (time >> 32));
        dNSOutput2.writeU32(time & 4294967295L);
        dNSOutput2.writeU16(tsig.getFudge());
        dNSOutput2.writeU16(tsig.getError());
        if (tsig.getOther() != null) {
            dNSOutput2.writeU16(tsig.getOther().length);
            dNSOutput2.writeByteArray(tsig.getOther());
        } else {
            dNSOutput2.writeU16(0);
        }
        hmac.update(dNSOutput2.toByteArray());
        byte[] signature = tsig.getSignature();
        int digestLength = hmac.digestLength();
        int i8 = this.digest.equals("md5") ? 10 : digestLength / 2;
        if (signature.length > digestLength) {
            if (!Options.check("verbose")) {
                return (byte) 16;
            }
            System.err.println("BADSIG: signature too long");
            return (byte) 16;
        }
        if (signature.length < i8) {
            if (!Options.check("verbose")) {
                return (byte) 16;
            }
            System.err.println("BADSIG: signature too short");
            return (byte) 16;
        }
        if (hmac.verify(signature, true)) {
            message.tsigState = 1;
            return (byte) 0;
        }
        if (!Options.check("verbose")) {
            return (byte) 16;
        }
        System.err.println("BADSIG: signature verification");
        return (byte) 16;
    }

    public int verify(Message message, byte[] bArr, TSIGRecord tSIGRecord) {
        return verify(message, bArr, bArr.length, tSIGRecord);
    }
}
