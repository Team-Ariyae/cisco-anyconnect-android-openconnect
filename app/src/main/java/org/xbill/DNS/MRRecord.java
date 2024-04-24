package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MRRecord.class */
public class MRRecord extends SingleNameBase {
    private static final long serialVersionUID = -5617939094209927533L;

    public MRRecord() {
    }

    public MRRecord(Name name, int i7, long j7, Name name2) {
        super(name, 9, i7, j7, name2, "new name");
    }

    public Name getNewName() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MRRecord();
    }
}
