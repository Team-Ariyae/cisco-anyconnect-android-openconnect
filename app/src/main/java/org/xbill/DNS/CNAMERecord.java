package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/CNAMERecord.class */
public class CNAMERecord extends SingleCompressedNameBase {
    private static final long serialVersionUID = -4020373886892538580L;

    public CNAMERecord() {
    }

    public CNAMERecord(Name name, int i7, long j7, Name name2) {
        super(name, 5, i7, j7, name2, "alias");
    }

    public Name getAlias() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new CNAMERecord();
    }

    public Name getTarget() {
        return getSingleName();
    }
}
