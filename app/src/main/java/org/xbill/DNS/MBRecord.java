package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MBRecord.class */
public class MBRecord extends SingleNameBase {
    private static final long serialVersionUID = 532349543479150419L;

    public MBRecord() {
    }

    public MBRecord(Name name, int i7, long j7, Name name2) {
        super(name, 7, i7, j7, name2, "mailbox");
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return getSingleName();
    }

    public Name getMailbox() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MBRecord();
    }
}
