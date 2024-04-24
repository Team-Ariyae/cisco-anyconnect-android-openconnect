package org.xbill.DNS;

import org.strongswan.android.data.VpnProfileDataSource;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SRVRecord.class */
public class SRVRecord extends Record {
    private static final long serialVersionUID = -3886460132387522052L;
    private int port;
    private int priority;
    private Name target;
    private int weight;

    public SRVRecord() {
    }

    public SRVRecord(Name name, int i7, long j7, int i8, int i9, int i10, Name name2) {
        super(name, 33, i7, j7);
        this.priority = Record.checkU16("priority", i8);
        this.weight = Record.checkU16("weight", i9);
        this.port = Record.checkU16(VpnProfileDataSource.KEY_PORT, i10);
        this.target = Record.checkName("target", name2);
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return this.target;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new SRVRecord();
    }

    public int getPort() {
        return this.port;
    }

    public int getPriority() {
        return this.priority;
    }

    public Name getTarget() {
        return this.target;
    }

    public int getWeight() {
        return this.weight;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.priority = tokenizer.getUInt16();
        this.weight = tokenizer.getUInt16();
        this.port = tokenizer.getUInt16();
        this.target = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.priority = dNSInput.readU16();
        this.weight = dNSInput.readU16();
        this.port = dNSInput.readU16();
        this.target = new Name(dNSInput);
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
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append(this.port);
        stringBuffer4.append(" ");
        stringBuffer.append(stringBuffer4.toString());
        stringBuffer.append(this.target);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.priority);
        dNSOutput.writeU16(this.weight);
        dNSOutput.writeU16(this.port);
        this.target.toWire(dNSOutput, null, z7);
    }
}
