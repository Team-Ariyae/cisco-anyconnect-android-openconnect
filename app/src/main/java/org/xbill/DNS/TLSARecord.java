package org.xbill.DNS;

import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TLSARecord.class */
public class TLSARecord extends Record {
    private static final long serialVersionUID = 356494267028580169L;
    private byte[] certificateAssociationData;
    private int certificateUsage;
    private int matchingType;
    private int selector;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TLSARecord$CertificateUsage.class */
    public static class CertificateUsage {
        public static final int CA_CONSTRAINT = 0;
        public static final int DOMAIN_ISSUED_CERTIFICATE = 3;
        public static final int SERVICE_CERTIFICATE_CONSTRAINT = 1;
        public static final int TRUST_ANCHOR_ASSERTION = 2;

        private CertificateUsage() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TLSARecord$MatchingType.class */
    public static class MatchingType {
        public static final int EXACT = 0;
        public static final int SHA256 = 1;
        public static final int SHA512 = 2;

        private MatchingType() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TLSARecord$Selector.class */
    public static class Selector {
        public static final int FULL_CERTIFICATE = 0;
        public static final int SUBJECT_PUBLIC_KEY_INFO = 1;

        private Selector() {
        }
    }

    public TLSARecord() {
    }

    public TLSARecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, 52, i7, j7);
        this.certificateUsage = Record.checkU8("certificateUsage", i8);
        this.selector = Record.checkU8("selector", i9);
        this.matchingType = Record.checkU8("matchingType", i10);
        this.certificateAssociationData = Record.checkByteArrayLength("certificateAssociationData", bArr, Message.MAXLENGTH);
    }

    public final byte[] getCertificateAssociationData() {
        return this.certificateAssociationData;
    }

    public int getCertificateUsage() {
        return this.certificateUsage;
    }

    public int getMatchingType() {
        return this.matchingType;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new TLSARecord();
    }

    public int getSelector() {
        return this.selector;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.certificateUsage = tokenizer.getUInt8();
        this.selector = tokenizer.getUInt8();
        this.matchingType = tokenizer.getUInt8();
        this.certificateAssociationData = tokenizer.getHex();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.certificateUsage = dNSInput.readU8();
        this.selector = dNSInput.readU8();
        this.matchingType = dNSInput.readU8();
        this.certificateAssociationData = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.certificateUsage);
        stringBuffer.append(" ");
        stringBuffer.append(this.selector);
        stringBuffer.append(" ");
        stringBuffer.append(this.matchingType);
        stringBuffer.append(" ");
        stringBuffer.append(base16.toString(this.certificateAssociationData));
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU8(this.certificateUsage);
        dNSOutput.writeU8(this.selector);
        dNSOutput.writeU8(this.matchingType);
        dNSOutput.writeByteArray(this.certificateAssociationData);
    }
}
