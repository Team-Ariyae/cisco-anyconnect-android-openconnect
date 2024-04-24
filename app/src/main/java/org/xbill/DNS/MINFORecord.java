package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MINFORecord.class */
public class MINFORecord extends Record {
    private static final long serialVersionUID = -3962147172340353796L;
    private Name errorAddress;
    private Name responsibleAddress;

    public MINFORecord() {
    }

    public MINFORecord(Name name, int i7, long j7, Name name2, Name name3) {
        super(name, 14, i7, j7);
        this.responsibleAddress = Record.checkName("responsibleAddress", name2);
        this.errorAddress = Record.checkName("errorAddress", name3);
    }

    public Name getErrorAddress() {
        return this.errorAddress;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MINFORecord();
    }

    public Name getResponsibleAddress() {
        return this.responsibleAddress;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.responsibleAddress = tokenizer.getName(name);
        this.errorAddress = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.responsibleAddress = new Name(dNSInput);
        this.errorAddress = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.responsibleAddress);
        stringBuffer.append(" ");
        stringBuffer.append(this.errorAddress);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.responsibleAddress.toWire(dNSOutput, null, z7);
        this.errorAddress.toWire(dNSOutput, null, z7);
    }
}
