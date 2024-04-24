package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MDRecord.class */
public class MDRecord extends SingleNameBase {
    private static final long serialVersionUID = 5268878603762942202L;

    public MDRecord() {
    }

    public MDRecord(Name name, int i7, long j7, Name name2) {
        super(name, 3, i7, j7, name2, "mail agent");
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return getSingleName();
    }

    public Name getMailAgent() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MDRecord();
    }
}
