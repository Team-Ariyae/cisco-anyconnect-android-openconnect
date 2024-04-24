package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/RPRecord.class */
public class RPRecord extends Record {
    private static final long serialVersionUID = 8124584364211337460L;
    private Name mailbox;
    private Name textDomain;

    public RPRecord() {
    }

    public RPRecord(Name name, int i7, long j7, Name name2, Name name3) {
        super(name, 17, i7, j7);
        this.mailbox = Record.checkName("mailbox", name2);
        this.textDomain = Record.checkName("textDomain", name3);
    }

    public Name getMailbox() {
        return this.mailbox;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new RPRecord();
    }

    public Name getTextDomain() {
        return this.textDomain;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.mailbox = tokenizer.getName(name);
        this.textDomain = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.mailbox = new Name(dNSInput);
        this.textDomain = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mailbox);
        stringBuffer.append(" ");
        stringBuffer.append(this.textDomain);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.mailbox.toWire(dNSOutput, null, z7);
        this.textDomain.toWire(dNSOutput, null, z7);
    }
}
