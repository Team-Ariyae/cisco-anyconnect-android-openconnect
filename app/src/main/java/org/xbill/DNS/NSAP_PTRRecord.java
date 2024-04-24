package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSAP_PTRRecord.class */
public class NSAP_PTRRecord extends SingleNameBase {
    private static final long serialVersionUID = 2386284746382064904L;

    public NSAP_PTRRecord() {
    }

    public NSAP_PTRRecord(Name name, int i7, long j7, Name name2) {
        super(name, 23, i7, j7, name2, "target");
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NSAP_PTRRecord();
    }

    public Name getTarget() {
        return getSingleName();
    }
}
