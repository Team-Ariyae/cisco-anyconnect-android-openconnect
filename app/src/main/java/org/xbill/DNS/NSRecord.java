package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSRecord.class */
public class NSRecord extends SingleCompressedNameBase {
    private static final long serialVersionUID = 487170758138268838L;

    public NSRecord() {
    }

    public NSRecord(Name name, int i7, long j7, Name name2) {
        super(name, 2, i7, j7, name2, "target");
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NSRecord();
    }

    public Name getTarget() {
        return getSingleName();
    }
}
