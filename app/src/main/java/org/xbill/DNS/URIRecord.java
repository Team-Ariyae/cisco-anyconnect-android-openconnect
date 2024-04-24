package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/URIRecord.class */
public class URIRecord extends Record {
    private static final long serialVersionUID = 7955422413971804232L;
    private int priority;
    private byte[] target;
    private int weight;

    public URIRecord() {
        this.target = new byte[0];
    }

    public URIRecord(Name name, int i7, long j7, int i8, int i9, String str) {
        super(name, 256, i7, j7);
        this.priority = Record.checkU16("priority", i8);
        this.weight = Record.checkU16("weight", i9);
        try {
            this.target = Record.byteArrayFromString(str);
        } catch (TextParseException e8) {
            throw new IllegalArgumentException(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new URIRecord();
    }

    public int getPriority() {
        return this.priority;
    }

    public String getTarget() {
        return Record.byteArrayToString(this.target, false);
    }

    public int getWeight() {
        return this.weight;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.priority = tokenizer.getUInt16();
        this.weight = tokenizer.getUInt16();
        try {
            this.target = Record.byteArrayFromString(tokenizer.getString());
        } catch (TextParseException e8) {
            throw tokenizer.exception(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.priority = dNSInput.readU16();
        this.weight = dNSInput.readU16();
        this.target = dNSInput.readCountedString();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(this.priority);
        stringBuffer2.append(" ");
        stringBuffer.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(this.weight);
        stringBuffer3.append(" ");
        stringBuffer.append(stringBuffer3.toString());
        stringBuffer.append(Record.byteArrayToString(this.target, true));
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.priority);
        dNSOutput.writeU16(this.weight);
        dNSOutput.writeCountedString(this.target);
    }
}
