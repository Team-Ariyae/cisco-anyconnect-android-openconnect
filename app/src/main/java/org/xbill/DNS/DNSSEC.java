package org.xbill.DNS;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC.class */
public class DNSSEC {
    private static final int ASN1_INT = 2;
    private static final int ASN1_SEQ = 48;
    private static final int DSA_LEN = 20;
    private static final ECKeyInfo GOST = new ECKeyInfo(32, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD97", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD94", "A6", "1", "8D91E471E0989CDA27DF505A453F2B7635294F2DDF23E3B122ACC99C9E9F1E14", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6C611070995AD10045841B09B761B893");
    private static final ECKeyInfo ECDSA_P256 = new ECKeyInfo(32, "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551");
    private static final ECKeyInfo ECDSA_P384 = new ECKeyInfo(48, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", "B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", "3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973");

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$Algorithm.class */
    public static class Algorithm {
        public static final int DH = 2;
        public static final int DSA = 3;
        public static final int DSA_NSEC3_SHA1 = 6;
        public static final int ECC_GOST = 12;
        public static final int ECDSAP256SHA256 = 13;
        public static final int ECDSAP384SHA384 = 14;
        public static final int INDIRECT = 252;
        public static final int PRIVATEDNS = 253;
        public static final int PRIVATEOID = 254;
        public static final int RSAMD5 = 1;
        public static final int RSASHA1 = 5;
        public static final int RSASHA256 = 8;
        public static final int RSASHA512 = 10;
        public static final int RSA_NSEC3_SHA1 = 7;
        private static Mnemonic algs;

        static {
            Mnemonic mnemonic = new Mnemonic("DNSSEC algorithm", 2);
            algs = mnemonic;
            mnemonic.setMaximum(255);
            algs.setNumericAllowed(true);
            algs.add(1, "RSAMD5");
            algs.add(2, "DH");
            algs.add(3, "DSA");
            algs.add(5, "RSASHA1");
            algs.add(6, "DSA-NSEC3-SHA1");
            algs.add(7, "RSA-NSEC3-SHA1");
            algs.add(8, "RSASHA256");
            algs.add(10, "RSASHA512");
            algs.add(12, "ECC-GOST");
            algs.add(13, "ECDSAP256SHA256");
            algs.add(14, "ECDSAP384SHA384");
            algs.add(252, "INDIRECT");
            algs.add(253, "PRIVATEDNS");
            algs.add(254, "PRIVATEOID");
        }

        private Algorithm() {
        }

        public static String string(int i7) {
            return algs.getText(i7);
        }

        public static int value(String str) {
            return algs.getValue(str);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$DNSSECException.class */
    public static class DNSSECException extends Exception {
        public DNSSECException(String str) {
            super(str);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$ECKeyInfo.class */
    public static class ECKeyInfo {

        /* renamed from: a, reason: collision with root package name */
        public BigInteger f5258a;

        /* renamed from: b, reason: collision with root package name */
        public BigInteger f5259b;
        public EllipticCurve curve;
        public BigInteger gx;
        public BigInteger gy;
        public int length;

        /* renamed from: n, reason: collision with root package name */
        public BigInteger f5260n;
        public BigInteger p;
        public ECParameterSpec spec;

        public ECKeyInfo(int i7, String str, String str2, String str3, String str4, String str5, String str6) {
            this.length = i7;
            this.p = new BigInteger(str, 16);
            this.f5258a = new BigInteger(str2, 16);
            this.f5259b = new BigInteger(str3, 16);
            this.gx = new BigInteger(str4, 16);
            this.gy = new BigInteger(str5, 16);
            this.f5260n = new BigInteger(str6, 16);
            this.curve = new EllipticCurve(new ECFieldFp(this.p), this.f5258a, this.f5259b);
            this.spec = new ECParameterSpec(this.curve, new ECPoint(this.gx, this.gy), this.f5260n, 1);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$IncompatibleKeyException.class */
    public static class IncompatibleKeyException extends IllegalArgumentException {
        public IncompatibleKeyException() {
            super("incompatible keys");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$KeyMismatchException.class */
    public static class KeyMismatchException extends DNSSECException {
        private KEYBase key;
        private SIGBase sig;

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public KeyMismatchException(org.xbill.DNS.KEYBase r4, org.xbill.DNS.SIGBase r5) {
            /*
                r3 = this;
                java.lang.String r0 = "key "
                java.lang.StringBuffer r0 = androidx.activity.result.a.p(r0)
                r6 = r0
                r0 = r6
                r1 = r4
                org.xbill.DNS.Name r1 = r1.getName()
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = "/"
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                r1 = r4
                int r1 = r1.getAlgorithm()
                java.lang.String r1 = org.xbill.DNS.DNSSEC.Algorithm.string(r1)
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = "/"
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                r1 = r4
                int r1 = r1.getFootprint()
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = " "
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = "does not match signature "
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                r1 = r5
                org.xbill.DNS.Name r1 = r1.getSigner()
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = "/"
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                r1 = r5
                int r1 = r1.getAlgorithm()
                java.lang.String r1 = org.xbill.DNS.DNSSEC.Algorithm.string(r1)
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                java.lang.String r1 = "/"
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r6
                r1 = r5
                int r1 = r1.getFootprint()
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r3
                r1 = r6
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.DNSSEC.KeyMismatchException.<init>(org.xbill.DNS.KEYBase, org.xbill.DNS.SIGBase):void");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$MalformedKeyException.class */
    public static class MalformedKeyException extends DNSSECException {
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public MalformedKeyException(org.xbill.DNS.KEYBase r4) {
            /*
                r3 = this;
                java.lang.String r0 = "Invalid key data: "
                java.lang.StringBuffer r0 = androidx.activity.result.a.p(r0)
                r5 = r0
                r0 = r5
                r1 = r4
                java.lang.String r1 = r1.rdataToString()
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r3
                r1 = r5
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.DNSSEC.MalformedKeyException.<init>(org.xbill.DNS.KEYBase):void");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$NoSignatureException.class */
    public static class NoSignatureException extends DNSSECException {
        public NoSignatureException() {
            super("no signature found");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$SignatureExpiredException.class */
    public static class SignatureExpiredException extends DNSSECException {
        private Date now;
        private Date when;

        public SignatureExpiredException(Date date, Date date2) {
            super("signature expired");
            this.when = date;
            this.now = date2;
        }

        public Date getExpiration() {
            return this.when;
        }

        public Date getVerifyTime() {
            return this.now;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$SignatureNotYetValidException.class */
    public static class SignatureNotYetValidException extends DNSSECException {
        private Date now;
        private Date when;

        public SignatureNotYetValidException(Date date, Date date2) {
            super("signature is not yet valid");
            this.when = date;
            this.now = date2;
        }

        public Date getExpiration() {
            return this.when;
        }

        public Date getVerifyTime() {
            return this.now;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$SignatureVerificationException.class */
    public static class SignatureVerificationException extends DNSSECException {
        public SignatureVerificationException() {
            super("signature verification failed");
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSSEC$UnsupportedAlgorithmException.class */
    public static class UnsupportedAlgorithmException extends DNSSECException {
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public UnsupportedAlgorithmException(int r4) {
            /*
                r3 = this;
                java.lang.StringBuffer r0 = new java.lang.StringBuffer
                r1 = r0
                r1.<init>()
                r5 = r0
                r0 = r5
                java.lang.String r1 = "Unsupported algorithm: "
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r5
                r1 = r4
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r3
                r1 = r5
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.DNSSEC.UnsupportedAlgorithmException.<init>(int):void");
        }
    }

    private DNSSEC() {
    }

    private static int BigIntegerLength(BigInteger bigInteger) {
        return (bigInteger.bitLength() + 7) / 8;
    }

    private static byte[] DSASignaturefromDNS(byte[] bArr) {
        if (bArr.length != 41) {
            throw new SignatureVerificationException();
        }
        DNSInput dNSInput = new DNSInput(bArr);
        DNSOutput dNSOutput = new DNSOutput();
        dNSInput.readU8();
        byte[] readByteArray = dNSInput.readByteArray(20);
        int i7 = 21;
        int i8 = readByteArray[0] < 0 ? 21 : 20;
        byte[] readByteArray2 = dNSInput.readByteArray(20);
        if (readByteArray2[0] >= 0) {
            i7 = 20;
        }
        dNSOutput.writeU8(48);
        dNSOutput.writeU8(i8 + i7 + 4);
        dNSOutput.writeU8(2);
        dNSOutput.writeU8(i8);
        if (i8 > 20) {
            dNSOutput.writeU8(0);
        }
        dNSOutput.writeByteArray(readByteArray);
        dNSOutput.writeU8(2);
        dNSOutput.writeU8(i7);
        if (i7 > 20) {
            dNSOutput.writeU8(0);
        }
        dNSOutput.writeByteArray(readByteArray2);
        return dNSOutput.toByteArray();
    }

    private static byte[] DSASignaturetoDNS(byte[] bArr, int i7) {
        DNSInput dNSInput = new DNSInput(bArr);
        DNSOutput dNSOutput = new DNSOutput();
        dNSOutput.writeU8(i7);
        if (dNSInput.readU8() != 48) {
            throw new IOException();
        }
        dNSInput.readU8();
        if (dNSInput.readU8() != 2) {
            throw new IOException();
        }
        int readU8 = dNSInput.readU8();
        if (readU8 == 21) {
            if (dNSInput.readU8() != 0) {
                throw new IOException();
            }
        } else if (readU8 != 20) {
            throw new IOException();
        }
        dNSOutput.writeByteArray(dNSInput.readByteArray(20));
        if (dNSInput.readU8() != 2) {
            throw new IOException();
        }
        int readU82 = dNSInput.readU8();
        if (readU82 == 21) {
            if (dNSInput.readU8() != 0) {
                throw new IOException();
            }
        } else if (readU82 != 20) {
            throw new IOException();
        }
        dNSOutput.writeByteArray(dNSInput.readByteArray(20));
        return dNSOutput.toByteArray();
    }

    private static byte[] ECDSASignaturefromDNS(byte[] bArr, ECKeyInfo eCKeyInfo) {
        if (bArr.length != eCKeyInfo.length * 2) {
            throw new SignatureVerificationException();
        }
        DNSInput dNSInput = new DNSInput(bArr);
        DNSOutput dNSOutput = new DNSOutput();
        byte[] readByteArray = dNSInput.readByteArray(eCKeyInfo.length);
        int i7 = eCKeyInfo.length;
        int i8 = readByteArray[0] < 0 ? i7 + 1 : i7;
        byte[] readByteArray2 = dNSInput.readByteArray(i7);
        int i9 = eCKeyInfo.length;
        int i10 = i9;
        if (readByteArray2[0] < 0) {
            i10 = i9 + 1;
        }
        dNSOutput.writeU8(48);
        dNSOutput.writeU8(i8 + i10 + 4);
        dNSOutput.writeU8(2);
        dNSOutput.writeU8(i8);
        if (i8 > eCKeyInfo.length) {
            dNSOutput.writeU8(0);
        }
        dNSOutput.writeByteArray(readByteArray);
        dNSOutput.writeU8(2);
        dNSOutput.writeU8(i10);
        if (i10 > eCKeyInfo.length) {
            dNSOutput.writeU8(0);
        }
        dNSOutput.writeByteArray(readByteArray2);
        return dNSOutput.toByteArray();
    }

    private static byte[] ECDSASignaturetoDNS(byte[] bArr, ECKeyInfo eCKeyInfo) {
        DNSInput dNSInput = new DNSInput(bArr);
        DNSOutput dNSOutput = new DNSOutput();
        if (dNSInput.readU8() != 48) {
            throw new IOException();
        }
        dNSInput.readU8();
        if (dNSInput.readU8() != 2) {
            throw new IOException();
        }
        int readU8 = dNSInput.readU8();
        int i7 = eCKeyInfo.length;
        if (readU8 == i7 + 1) {
            if (dNSInput.readU8() != 0) {
                throw new IOException();
            }
        } else if (readU8 != i7) {
            throw new IOException();
        }
        dNSOutput.writeByteArray(dNSInput.readByteArray(eCKeyInfo.length));
        if (dNSInput.readU8() != 2) {
            throw new IOException();
        }
        int readU82 = dNSInput.readU8();
        int i8 = eCKeyInfo.length;
        if (readU82 == i8 + 1) {
            if (dNSInput.readU8() != 0) {
                throw new IOException();
            }
        } else if (readU82 != i8) {
            throw new IOException();
        }
        dNSOutput.writeByteArray(dNSInput.readByteArray(eCKeyInfo.length));
        return dNSOutput.toByteArray();
    }

    private static byte[] ECGOSTSignaturefromDNS(byte[] bArr, ECKeyInfo eCKeyInfo) {
        if (bArr.length == eCKeyInfo.length * 2) {
            return bArr;
        }
        throw new SignatureVerificationException();
    }

    public static String algString(int i7) {
        switch (i7) {
            case 1:
                return "MD5withRSA";
            case 2:
            case 4:
            case 9:
            case 11:
            default:
                throw new UnsupportedAlgorithmException(i7);
            case 3:
            case 6:
                return "SHA1withDSA";
            case 5:
            case 7:
                return "SHA1withRSA";
            case 8:
                return "SHA256withRSA";
            case 10:
                return "SHA512withRSA";
            case 12:
                return "GOST3411withECGOST3410";
            case 13:
                return "SHA256withECDSA";
            case 14:
                return "SHA384withECDSA";
        }
    }

    public static void checkAlgorithm(PrivateKey privateKey, int i7) {
        switch (i7) {
            case 1:
            case 5:
            case 7:
            case 8:
            case 10:
                if (!(privateKey instanceof RSAPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                return;
            case 2:
            case 4:
            case 9:
            case 11:
            default:
                throw new UnsupportedAlgorithmException(i7);
            case 3:
            case 6:
                if (!(privateKey instanceof DSAPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                return;
            case 12:
            case 13:
            case 14:
                if (!(privateKey instanceof ECPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                return;
        }
    }

    public static byte[] digestMessage(SIGRecord sIGRecord, Message message, byte[] bArr) {
        DNSOutput dNSOutput = new DNSOutput();
        digestSIG(dNSOutput, sIGRecord);
        if (bArr != null) {
            dNSOutput.writeByteArray(bArr);
        }
        message.toWire(dNSOutput);
        return dNSOutput.toByteArray();
    }

    public static byte[] digestRRset(RRSIGRecord rRSIGRecord, RRset rRset) {
        DNSOutput dNSOutput = new DNSOutput();
        digestSIG(dNSOutput, rRSIGRecord);
        int size = rRset.size();
        Record[] recordArr = new Record[size];
        Iterator rrs = rRset.rrs();
        Name name = rRset.getName();
        int labels = rRSIGRecord.getLabels() + 1;
        Name wild = name.labels() > labels ? name.wild(name.labels() - labels) : null;
        int i7 = size;
        while (rrs.hasNext()) {
            i7--;
            recordArr[i7] = (Record) rrs.next();
        }
        Arrays.sort(recordArr);
        DNSOutput dNSOutput2 = new DNSOutput();
        if (wild != null) {
            wild.toWireCanonical(dNSOutput2);
        } else {
            name.toWireCanonical(dNSOutput2);
        }
        dNSOutput2.writeU16(rRset.getType());
        dNSOutput2.writeU16(rRset.getDClass());
        dNSOutput2.writeU32(rRSIGRecord.getOrigTTL());
        for (int i8 = 0; i8 < size; i8++) {
            dNSOutput.writeByteArray(dNSOutput2.toByteArray());
            int current = dNSOutput.current();
            dNSOutput.writeU16(0);
            dNSOutput.writeByteArray(recordArr[i8].rdataToWireCanonical());
            int current2 = dNSOutput.current();
            dNSOutput.save();
            dNSOutput.jump(current);
            dNSOutput.writeU16((current2 - current) - 2);
            dNSOutput.restore();
        }
        return dNSOutput.toByteArray();
    }

    private static void digestSIG(DNSOutput dNSOutput, SIGBase sIGBase) {
        dNSOutput.writeU16(sIGBase.getTypeCovered());
        dNSOutput.writeU8(sIGBase.getAlgorithm());
        dNSOutput.writeU8(sIGBase.getLabels());
        dNSOutput.writeU32(sIGBase.getOrigTTL());
        dNSOutput.writeU32(sIGBase.getExpire().getTime() / 1000);
        dNSOutput.writeU32(sIGBase.getTimeSigned().getTime() / 1000);
        dNSOutput.writeU16(sIGBase.getFootprint());
        sIGBase.getSigner().toWireCanonical(dNSOutput);
    }

    private static byte[] fromDSAPublicKey(DSAPublicKey dSAPublicKey) {
        DNSOutput dNSOutput = new DNSOutput();
        BigInteger q7 = dSAPublicKey.getParams().getQ();
        BigInteger p = dSAPublicKey.getParams().getP();
        BigInteger g = dSAPublicKey.getParams().getG();
        BigInteger y5 = dSAPublicKey.getY();
        int length = (p.toByteArray().length - 64) / 8;
        dNSOutput.writeU8(length);
        writeBigInteger(dNSOutput, q7);
        writeBigInteger(dNSOutput, p);
        int i7 = (length * 8) + 64;
        writePaddedBigInteger(dNSOutput, g, i7);
        writePaddedBigInteger(dNSOutput, y5, i7);
        return dNSOutput.toByteArray();
    }

    private static byte[] fromECDSAPublicKey(ECPublicKey eCPublicKey, ECKeyInfo eCKeyInfo) {
        DNSOutput dNSOutput = new DNSOutput();
        BigInteger affineX = eCPublicKey.getW().getAffineX();
        BigInteger affineY = eCPublicKey.getW().getAffineY();
        writePaddedBigInteger(dNSOutput, affineX, eCKeyInfo.length);
        writePaddedBigInteger(dNSOutput, affineY, eCKeyInfo.length);
        return dNSOutput.toByteArray();
    }

    private static byte[] fromECGOSTPublicKey(ECPublicKey eCPublicKey, ECKeyInfo eCKeyInfo) {
        DNSOutput dNSOutput = new DNSOutput();
        BigInteger affineX = eCPublicKey.getW().getAffineX();
        BigInteger affineY = eCPublicKey.getW().getAffineY();
        writePaddedBigIntegerLittleEndian(dNSOutput, affineX, eCKeyInfo.length);
        writePaddedBigIntegerLittleEndian(dNSOutput, affineY, eCKeyInfo.length);
        return dNSOutput.toByteArray();
    }

    public static byte[] fromPublicKey(PublicKey publicKey, int i7) {
        switch (i7) {
            case 1:
            case 5:
            case 7:
            case 8:
            case 10:
                if (publicKey instanceof RSAPublicKey) {
                    return fromRSAPublicKey((RSAPublicKey) publicKey);
                }
                throw new IncompatibleKeyException();
            case 2:
            case 4:
            case 9:
            case 11:
            default:
                throw new UnsupportedAlgorithmException(i7);
            case 3:
            case 6:
                if (publicKey instanceof DSAPublicKey) {
                    return fromDSAPublicKey((DSAPublicKey) publicKey);
                }
                throw new IncompatibleKeyException();
            case 12:
                if (publicKey instanceof ECPublicKey) {
                    return fromECGOSTPublicKey((ECPublicKey) publicKey, GOST);
                }
                throw new IncompatibleKeyException();
            case 13:
                if (publicKey instanceof ECPublicKey) {
                    return fromECDSAPublicKey((ECPublicKey) publicKey, ECDSA_P256);
                }
                throw new IncompatibleKeyException();
            case 14:
                if (publicKey instanceof ECPublicKey) {
                    return fromECDSAPublicKey((ECPublicKey) publicKey, ECDSA_P384);
                }
                throw new IncompatibleKeyException();
        }
    }

    private static byte[] fromRSAPublicKey(RSAPublicKey rSAPublicKey) {
        DNSOutput dNSOutput = new DNSOutput();
        BigInteger publicExponent = rSAPublicKey.getPublicExponent();
        BigInteger modulus = rSAPublicKey.getModulus();
        int BigIntegerLength = BigIntegerLength(publicExponent);
        if (BigIntegerLength < 256) {
            dNSOutput.writeU8(BigIntegerLength);
        } else {
            dNSOutput.writeU8(0);
            dNSOutput.writeU16(BigIntegerLength);
        }
        writeBigInteger(dNSOutput, publicExponent);
        writeBigInteger(dNSOutput, modulus);
        return dNSOutput.toByteArray();
    }

    public static byte[] generateDSDigest(DNSKEYRecord dNSKEYRecord, int i7) {
        String str;
        try {
            if (i7 == 1) {
                str = "sha-1";
            } else if (i7 == 2) {
                str = "sha-256";
            } else if (i7 == 3) {
                str = "GOST3411";
            } else {
                if (i7 != 4) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("unknown DS digest type ");
                    stringBuffer.append(i7);
                    throw new IllegalArgumentException(stringBuffer.toString());
                }
                str = "sha-384";
            }
            MessageDigest messageDigest = MessageDigest.getInstance(str);
            messageDigest.update(dNSKEYRecord.getName().toWireCanonical());
            messageDigest.update(dNSKEYRecord.rdataToWireCanonical());
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e8) {
            throw new IllegalStateException("no message digest support");
        }
    }

    private static boolean matches(SIGBase sIGBase, KEYBase kEYBase) {
        return kEYBase.getAlgorithm() == sIGBase.getAlgorithm() && kEYBase.getFootprint() == sIGBase.getFootprint() && kEYBase.getName().equals(sIGBase.getSigner());
    }

    private static BigInteger readBigInteger(DNSInput dNSInput) {
        return new BigInteger(1, dNSInput.readByteArray());
    }

    private static BigInteger readBigInteger(DNSInput dNSInput, int i7) {
        return new BigInteger(1, dNSInput.readByteArray(i7));
    }

    private static BigInteger readBigIntegerLittleEndian(DNSInput dNSInput, int i7) {
        byte[] readByteArray = dNSInput.readByteArray(i7);
        reverseByteArray(readByteArray);
        return new BigInteger(1, readByteArray);
    }

    private static void reverseByteArray(byte[] bArr) {
        for (int i7 = 0; i7 < bArr.length / 2; i7++) {
            int length = (bArr.length - i7) - 1;
            byte b8 = bArr[i7];
            bArr[i7] = bArr[length];
            bArr[length] = b8;
        }
    }

    public static RRSIGRecord sign(RRset rRset, DNSKEYRecord dNSKEYRecord, PrivateKey privateKey, Date date, Date date2) {
        return sign(rRset, dNSKEYRecord, privateKey, date, date2, null);
    }

    public static RRSIGRecord sign(RRset rRset, DNSKEYRecord dNSKEYRecord, PrivateKey privateKey, Date date, Date date2, String str) {
        int algorithm = dNSKEYRecord.getAlgorithm();
        checkAlgorithm(privateKey, algorithm);
        RRSIGRecord rRSIGRecord = new RRSIGRecord(rRset.getName(), rRset.getDClass(), rRset.getTTL(), rRset.getType(), algorithm, rRset.getTTL(), date2, date, dNSKEYRecord.getFootprint(), dNSKEYRecord.getName(), null);
        rRSIGRecord.setSignature(sign(privateKey, dNSKEYRecord.getPublicKey(), algorithm, digestRRset(rRSIGRecord, rRset), str));
        return rRSIGRecord;
    }

    private static byte[] sign(PrivateKey privateKey, PublicKey publicKey, int i7, byte[] bArr, String str) {
        byte[] DSASignaturetoDNS;
        ECKeyInfo eCKeyInfo;
        try {
            Signature signature = str != null ? Signature.getInstance(algString(i7), str) : Signature.getInstance(algString(i7));
            signature.initSign(privateKey);
            signature.update(bArr);
            byte[] sign = signature.sign();
            if (publicKey instanceof DSAPublicKey) {
                try {
                    DSASignaturetoDNS = DSASignaturetoDNS(sign, (BigIntegerLength(((DSAPublicKey) publicKey).getParams().getP()) - 64) / 8);
                } catch (IOException e8) {
                    throw new IllegalStateException();
                }
            } else {
                DSASignaturetoDNS = sign;
                if (publicKey instanceof ECPublicKey) {
                    DSASignaturetoDNS = sign;
                    try {
                        switch (i7) {
                            case 12:
                                break;
                            case 13:
                                eCKeyInfo = ECDSA_P256;
                                break;
                            case 14:
                                eCKeyInfo = ECDSA_P384;
                                break;
                            default:
                                throw new UnsupportedAlgorithmException(i7);
                        }
                        DSASignaturetoDNS = ECDSASignaturetoDNS(sign, eCKeyInfo);
                    } catch (IOException e9) {
                        throw new IllegalStateException();
                    }
                }
            }
            return DSASignaturetoDNS;
        } catch (GeneralSecurityException e10) {
            throw new DNSSECException(e10.toString());
        }
    }

    public static SIGRecord signMessage(Message message, SIGRecord sIGRecord, KEYRecord kEYRecord, PrivateKey privateKey, Date date, Date date2) {
        int algorithm = kEYRecord.getAlgorithm();
        checkAlgorithm(privateKey, algorithm);
        SIGRecord sIGRecord2 = new SIGRecord(Name.root, 255, 0L, 0, algorithm, 0L, date2, date, kEYRecord.getFootprint(), kEYRecord.getName(), null);
        DNSOutput dNSOutput = new DNSOutput();
        digestSIG(dNSOutput, sIGRecord2);
        if (sIGRecord != null) {
            dNSOutput.writeByteArray(sIGRecord.getSignature());
        }
        dNSOutput.writeByteArray(message.toWire());
        sIGRecord2.setSignature(sign(privateKey, kEYRecord.getPublicKey(), algorithm, dNSOutput.toByteArray(), (String) null));
        return sIGRecord2;
    }

    private static PublicKey toDSAPublicKey(KEYBase kEYBase) {
        DNSInput dNSInput = new DNSInput(kEYBase.getKey());
        int readU8 = dNSInput.readU8();
        if (readU8 > 8) {
            throw new MalformedKeyException(kEYBase);
        }
        BigInteger readBigInteger = readBigInteger(dNSInput, 20);
        int i7 = (readU8 * 8) + 64;
        BigInteger readBigInteger2 = readBigInteger(dNSInput, i7);
        BigInteger readBigInteger3 = readBigInteger(dNSInput, i7);
        return KeyFactory.getInstance("DSA").generatePublic(new DSAPublicKeySpec(readBigInteger(dNSInput, i7), readBigInteger2, readBigInteger, readBigInteger3));
    }

    private static PublicKey toECDSAPublicKey(KEYBase kEYBase, ECKeyInfo eCKeyInfo) {
        DNSInput dNSInput = new DNSInput(kEYBase.getKey());
        return KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(new ECPoint(readBigInteger(dNSInput, eCKeyInfo.length), readBigInteger(dNSInput, eCKeyInfo.length)), eCKeyInfo.spec));
    }

    private static PublicKey toECGOSTPublicKey(KEYBase kEYBase, ECKeyInfo eCKeyInfo) {
        DNSInput dNSInput = new DNSInput(kEYBase.getKey());
        return KeyFactory.getInstance("ECGOST3410").generatePublic(new ECPublicKeySpec(new ECPoint(readBigIntegerLittleEndian(dNSInput, eCKeyInfo.length), readBigIntegerLittleEndian(dNSInput, eCKeyInfo.length)), eCKeyInfo.spec));
    }

    public static PublicKey toPublicKey(KEYBase kEYBase) {
        int algorithm = kEYBase.getAlgorithm();
        try {
            try {
                switch (algorithm) {
                    case 1:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                        return toRSAPublicKey(kEYBase);
                    case 2:
                    case 4:
                    case 9:
                    case 11:
                    default:
                        throw new UnsupportedAlgorithmException(algorithm);
                    case 3:
                    case 6:
                        return toDSAPublicKey(kEYBase);
                    case 12:
                        return toECGOSTPublicKey(kEYBase, GOST);
                    case 13:
                        return toECDSAPublicKey(kEYBase, ECDSA_P256);
                    case 14:
                        return toECDSAPublicKey(kEYBase, ECDSA_P384);
                }
            } catch (IOException e8) {
                throw new MalformedKeyException(kEYBase);
            }
        } catch (GeneralSecurityException e9) {
            throw new DNSSECException(e9.toString());
        }
    }

    private static PublicKey toRSAPublicKey(KEYBase kEYBase) {
        DNSInput dNSInput = new DNSInput(kEYBase.getKey());
        int readU8 = dNSInput.readU8();
        int i7 = readU8;
        if (readU8 == 0) {
            i7 = dNSInput.readU16();
        }
        BigInteger readBigInteger = readBigInteger(dNSInput, i7);
        return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(readBigInteger(dNSInput), readBigInteger));
    }

    private static byte[] trimByteArray(byte[] bArr) {
        if (bArr[0] != 0) {
            return bArr;
        }
        byte[] bArr2 = new byte[bArr.length - 1];
        System.arraycopy(bArr, 1, bArr2, 0, bArr.length - 1);
        return bArr2;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0024. Please report as an issue. */
    private static void verify(PublicKey publicKey, int i7, byte[] bArr, byte[] bArr2) {
        byte[] DSASignaturefromDNS;
        ECKeyInfo eCKeyInfo;
        if (publicKey instanceof DSAPublicKey) {
            try {
                DSASignaturefromDNS = DSASignaturefromDNS(bArr2);
            } catch (IOException e8) {
                throw new IllegalStateException();
            }
        } else {
            DSASignaturefromDNS = bArr2;
            if (publicKey instanceof ECPublicKey) {
                try {
                    switch (i7) {
                        case 12:
                            DSASignaturefromDNS = ECGOSTSignaturefromDNS(bArr2, GOST);
                            break;
                        case 13:
                            eCKeyInfo = ECDSA_P256;
                            DSASignaturefromDNS = ECDSASignaturefromDNS(bArr2, eCKeyInfo);
                            break;
                        case 14:
                            eCKeyInfo = ECDSA_P384;
                            DSASignaturefromDNS = ECDSASignaturefromDNS(bArr2, eCKeyInfo);
                            break;
                        default:
                            throw new UnsupportedAlgorithmException(i7);
                    }
                } catch (IOException e9) {
                    throw new IllegalStateException();
                }
            }
        }
        try {
            Signature signature = Signature.getInstance(algString(i7));
            signature.initVerify(publicKey);
            signature.update(bArr);
            if (signature.verify(DSASignaturefromDNS)) {
            } else {
                throw new SignatureVerificationException();
            }
        } catch (GeneralSecurityException e10) {
            throw new DNSSECException(e10.toString());
        }
    }

    public static void verify(RRset rRset, RRSIGRecord rRSIGRecord, DNSKEYRecord dNSKEYRecord) {
        if (!matches(rRSIGRecord, dNSKEYRecord)) {
            throw new KeyMismatchException(dNSKEYRecord, rRSIGRecord);
        }
        Date date = new Date();
        if (date.compareTo(rRSIGRecord.getExpire()) > 0) {
            throw new SignatureExpiredException(rRSIGRecord.getExpire(), date);
        }
        if (date.compareTo(rRSIGRecord.getTimeSigned()) < 0) {
            throw new SignatureNotYetValidException(rRSIGRecord.getTimeSigned(), date);
        }
        verify(dNSKEYRecord.getPublicKey(), rRSIGRecord.getAlgorithm(), digestRRset(rRSIGRecord, rRset), rRSIGRecord.getSignature());
    }

    public static void verifyMessage(Message message, byte[] bArr, SIGRecord sIGRecord, SIGRecord sIGRecord2, KEYRecord kEYRecord) {
        if (message.sig0start == 0) {
            throw new NoSignatureException();
        }
        if (!matches(sIGRecord, kEYRecord)) {
            throw new KeyMismatchException(kEYRecord, sIGRecord);
        }
        Date date = new Date();
        if (date.compareTo(sIGRecord.getExpire()) > 0) {
            throw new SignatureExpiredException(sIGRecord.getExpire(), date);
        }
        if (date.compareTo(sIGRecord.getTimeSigned()) < 0) {
            throw new SignatureNotYetValidException(sIGRecord.getTimeSigned(), date);
        }
        DNSOutput dNSOutput = new DNSOutput();
        digestSIG(dNSOutput, sIGRecord);
        if (sIGRecord2 != null) {
            dNSOutput.writeByteArray(sIGRecord2.getSignature());
        }
        Header header = (Header) message.getHeader().clone();
        header.decCount(3);
        dNSOutput.writeByteArray(header.toWire());
        dNSOutput.writeByteArray(bArr, 12, message.sig0start - 12);
        verify(kEYRecord.getPublicKey(), sIGRecord.getAlgorithm(), dNSOutput.toByteArray(), sIGRecord.getSignature());
    }

    private static void writeBigInteger(DNSOutput dNSOutput, BigInteger bigInteger) {
        dNSOutput.writeByteArray(trimByteArray(bigInteger.toByteArray()));
    }

    private static void writePaddedBigInteger(DNSOutput dNSOutput, BigInteger bigInteger, int i7) {
        byte[] trimByteArray = trimByteArray(bigInteger.toByteArray());
        if (trimByteArray.length > i7) {
            throw new IllegalArgumentException();
        }
        if (trimByteArray.length < i7) {
            dNSOutput.writeByteArray(new byte[i7 - trimByteArray.length]);
        }
        dNSOutput.writeByteArray(trimByteArray);
    }

    private static void writePaddedBigIntegerLittleEndian(DNSOutput dNSOutput, BigInteger bigInteger, int i7) {
        byte[] trimByteArray = trimByteArray(bigInteger.toByteArray());
        if (trimByteArray.length > i7) {
            throw new IllegalArgumentException();
        }
        reverseByteArray(trimByteArray);
        dNSOutput.writeByteArray(trimByteArray);
        if (trimByteArray.length < i7) {
            dNSOutput.writeByteArray(new byte[i7 - trimByteArray.length]);
        }
    }
}
