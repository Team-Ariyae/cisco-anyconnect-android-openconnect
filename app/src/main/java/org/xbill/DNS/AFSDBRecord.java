package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/AFSDBRecord.class */
public class AFSDBRecord extends U16NameBase {
    private static final long serialVersionUID = 3034379930729102437L;

    public AFSDBRecord() {
    }

    public AFSDBRecord(Name name, int i7, long j7, int i8, Name name2) {
        super(name, 18, i7, j7, i8, "subtype", name2, "host");
    }

    public Name getHost() {
        return getNameField();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new AFSDBRecord();
    }

    public int getSubtype() {
        return getU16Field();
    }
}
