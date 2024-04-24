package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MFRecord.class */
public class MFRecord extends SingleNameBase {
    private static final long serialVersionUID = -6670449036843028169L;

    public MFRecord() {
    }

    public MFRecord(Name name, int i7, long j7, Name name2) {
        super(name, 4, i7, j7, name2, "mail agent");
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
        return new MFRecord();
    }
}
