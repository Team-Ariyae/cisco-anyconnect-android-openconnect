package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MGRecord.class */
public class MGRecord extends SingleNameBase {
    private static final long serialVersionUID = -3980055550863644582L;

    public MGRecord() {
    }

    public MGRecord(Name name, int i7, long j7, Name name2) {
        super(name, 8, i7, j7, name2, "mailbox");
    }

    public Name getMailbox() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MGRecord();
    }
}
