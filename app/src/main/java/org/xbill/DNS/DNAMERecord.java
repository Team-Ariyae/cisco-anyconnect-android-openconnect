package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNAMERecord.class */
public class DNAMERecord extends SingleNameBase {
    private static final long serialVersionUID = 2670767677200844154L;

    public DNAMERecord() {
    }

    public DNAMERecord(Name name, int i7, long j7, Name name2) {
        super(name, 39, i7, j7, name2, "alias");
    }

    public Name getAlias() {
        return getSingleName();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new DNAMERecord();
    }

    public Name getTarget() {
        return getSingleName();
    }
}
