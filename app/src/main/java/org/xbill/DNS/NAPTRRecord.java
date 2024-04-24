package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NAPTRRecord.class */
public class NAPTRRecord extends Record {
    private static final long serialVersionUID = 5191232392044947002L;
    private byte[] flags;
    private int order;
    private int preference;
    private byte[] regexp;
    private Name replacement;
    private byte[] service;

    public NAPTRRecord() {
    }

    public NAPTRRecord(Name name, int i7, long j7, int i8, int i9, String str, String str2, String str3, Name name2) {
        super(name, 35, i7, j7);
        this.order = Record.checkU16("order", i8);
        this.preference = Record.checkU16("preference", i9);
        try {
            this.flags = Record.byteArrayFromString(str);
            this.service = Record.byteArrayFromString(str2);
            this.regexp = Record.byteArrayFromString(str3);
            this.replacement = Record.checkName("replacement", name2);
        } catch (TextParseException e8) {
            throw new IllegalArgumentException(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return this.replacement;
    }

    public String getFlags() {
        return Record.byteArrayToString(this.flags, false);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NAPTRRecord();
    }

    public int getOrder() {
        return this.order;
    }

    public int getPreference() {
        return this.preference;
    }

    public String getRegexp() {
        return Record.byteArrayToString(this.regexp, false);
    }

    public Name getReplacement() {
        return this.replacement;
    }

    public String getService() {
        return Record.byteArrayToString(this.service, false);
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.order = tokenizer.getUInt16();
        this.preference = tokenizer.getUInt16();
        try {
            this.flags = Record.byteArrayFromString(tokenizer.getString());
            this.service = Record.byteArrayFromString(tokenizer.getString());
            this.regexp = Record.byteArrayFromString(tokenizer.getString());
            this.replacement = tokenizer.getName(name);
        } catch (TextParseException e8) {
            throw tokenizer.exception(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.order = dNSInput.readU16();
        this.preference = dNSInput.readU16();
        this.flags = dNSInput.readCountedString();
        this.service = dNSInput.readCountedString();
        this.regexp = dNSInput.readCountedString();
        this.replacement = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.order);
        stringBuffer.append(" ");
        stringBuffer.append(this.preference);
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.flags, true));
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.service, true));
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.regexp, true));
        stringBuffer.append(" ");
        stringBuffer.append(this.replacement);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.order);
        dNSOutput.writeU16(this.preference);
        dNSOutput.writeCountedString(this.flags);
        dNSOutput.writeCountedString(this.service);
        dNSOutput.writeCountedString(this.regexp);
        this.replacement.toWire(dNSOutput, null, z7);
    }
}
