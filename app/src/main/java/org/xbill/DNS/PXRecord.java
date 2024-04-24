package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/PXRecord.class */
public class PXRecord extends Record {
    private static final long serialVersionUID = 1811540008806660667L;
    private Name map822;
    private Name mapX400;
    private int preference;

    public PXRecord() {
    }

    public PXRecord(Name name, int i7, long j7, int i8, Name name2, Name name3) {
        super(name, 26, i7, j7);
        this.preference = Record.checkU16("preference", i8);
        this.map822 = Record.checkName("map822", name2);
        this.mapX400 = Record.checkName("mapX400", name3);
    }

    public Name getMap822() {
        return this.map822;
    }

    public Name getMapX400() {
        return this.mapX400;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new PXRecord();
    }

    public int getPreference() {
        return this.preference;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.preference = tokenizer.getUInt16();
        this.map822 = tokenizer.getName(name);
        this.mapX400 = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.preference = dNSInput.readU16();
        this.map822 = new Name(dNSInput);
        this.mapX400 = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.preference);
        stringBuffer.append(" ");
        stringBuffer.append(this.map822);
        stringBuffer.append(" ");
        stringBuffer.append(this.mapX400);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.preference);
        this.map822.toWire(dNSOutput, null, z7);
        this.mapX400.toWire(dNSOutput, null, z7);
    }
}
