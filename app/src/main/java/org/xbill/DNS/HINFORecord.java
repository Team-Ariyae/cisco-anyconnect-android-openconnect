package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/HINFORecord.class */
public class HINFORecord extends Record {
    private static final long serialVersionUID = -4732870630947452112L;
    private byte[] cpu;
    private byte[] os;

    public HINFORecord() {
    }

    public HINFORecord(Name name, int i7, long j7, String str, String str2) {
        super(name, 13, i7, j7);
        try {
            this.cpu = Record.byteArrayFromString(str);
            this.os = Record.byteArrayFromString(str2);
        } catch (TextParseException e8) {
            throw new IllegalArgumentException(e8.getMessage());
        }
    }

    public String getCPU() {
        return Record.byteArrayToString(this.cpu, false);
    }

    public String getOS() {
        return Record.byteArrayToString(this.os, false);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new HINFORecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        try {
            this.cpu = Record.byteArrayFromString(tokenizer.getString());
            this.os = Record.byteArrayFromString(tokenizer.getString());
        } catch (TextParseException e8) {
            throw tokenizer.exception(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.cpu = dNSInput.readCountedString();
        this.os = dNSInput.readCountedString();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Record.byteArrayToString(this.cpu, true));
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.os, true));
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeCountedString(this.cpu);
        dNSOutput.writeCountedString(this.os);
    }
}
