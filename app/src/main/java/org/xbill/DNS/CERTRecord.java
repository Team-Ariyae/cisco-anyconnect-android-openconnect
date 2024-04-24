package org.xbill.DNS;

import androidx.activity.result.a;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.utils.base64;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/CERTRecord.class */
public class CERTRecord extends Record {
    public static final int OID = 254;
    public static final int PGP = 3;
    public static final int PKIX = 1;
    public static final int SPKI = 2;
    public static final int URI = 253;
    private static final long serialVersionUID = 4763014646517016835L;
    private int alg;
    private byte[] cert;
    private int certType;
    private int keyTag;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/CERTRecord$CertificateType.class */
    public static class CertificateType {
        public static final int ACPKIX = 7;
        public static final int IACPKIX = 8;
        public static final int IPGP = 6;
        public static final int IPKIX = 4;
        public static final int ISPKI = 5;
        public static final int OID = 254;
        public static final int PGP = 3;
        public static final int PKIX = 1;
        public static final int SPKI = 2;
        public static final int URI = 253;
        private static Mnemonic types;

        static {
            Mnemonic mnemonic = new Mnemonic("Certificate type", 2);
            types = mnemonic;
            mnemonic.setMaximum(Message.MAXLENGTH);
            types.setNumericAllowed(true);
            types.add(1, "PKIX");
            types.add(2, "SPKI");
            types.add(3, "PGP");
            types.add(1, "IPKIX");
            types.add(2, "ISPKI");
            types.add(3, "IPGP");
            types.add(3, "ACPKIX");
            types.add(3, "IACPKIX");
            types.add(253, "URI");
            types.add(254, "OID");
        }

        private CertificateType() {
        }

        public static String string(int i7) {
            return types.getText(i7);
        }

        public static int value(String str) {
            return types.getValue(str);
        }
    }

    public CERTRecord() {
    }

    public CERTRecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, 37, i7, j7);
        this.certType = Record.checkU16("certType", i8);
        this.keyTag = Record.checkU16("keyTag", i9);
        this.alg = Record.checkU8("alg", i10);
        this.cert = bArr;
    }

    public int getAlgorithm() {
        return this.alg;
    }

    public byte[] getCert() {
        return this.cert;
    }

    public int getCertType() {
        return this.certType;
    }

    public int getKeyTag() {
        return this.keyTag;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new CERTRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        String string = tokenizer.getString();
        int value = CertificateType.value(string);
        this.certType = value;
        if (value < 0) {
            throw a.u("Invalid certificate type: ", string, tokenizer);
        }
        this.keyTag = tokenizer.getUInt16();
        String string2 = tokenizer.getString();
        int value2 = DNSSEC.Algorithm.value(string2);
        this.alg = value2;
        if (value2 < 0) {
            throw a.u("Invalid algorithm: ", string2, tokenizer);
        }
        this.cert = tokenizer.getBase64();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.certType = dNSInput.readU16();
        this.keyTag = dNSInput.readU16();
        this.alg = dNSInput.readU8();
        this.cert = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        String base64Var;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.certType);
        stringBuffer.append(" ");
        stringBuffer.append(this.keyTag);
        stringBuffer.append(" ");
        stringBuffer.append(this.alg);
        if (this.cert != null) {
            if (Options.check("multiline")) {
                stringBuffer.append(" (\n");
                base64Var = base64.formatString(this.cert, 64, "\t", true);
            } else {
                stringBuffer.append(" ");
                base64Var = base64.toString(this.cert);
            }
            stringBuffer.append(base64Var);
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.certType);
        dNSOutput.writeU16(this.keyTag);
        dNSOutput.writeU8(this.alg);
        dNSOutput.writeByteArray(this.cert);
    }
}
