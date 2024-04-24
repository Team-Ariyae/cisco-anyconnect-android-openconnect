package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/PTRRecord.class */
public class PTRRecord extends SingleCompressedNameBase {
    private static final long serialVersionUID = -8321636610425434192L;

    public PTRRecord() {
    }

    public PTRRecord(Name name, int i7, long j7, Name name2) {
        super(name, 12, i7, j7, name2, "target");
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new PTRRecord();
    }

    public Name getTarget() {
        return getSingleName();
    }
}
