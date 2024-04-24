package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/RTRecord.class */
public class RTRecord extends U16NameBase {
    private static final long serialVersionUID = -3206215651648278098L;

    public RTRecord() {
    }

    public RTRecord(Name name, int i7, long j7, int i8, Name name2) {
        super(name, 21, i7, j7, i8, "preference", name2, "intermediateHost");
    }

    public Name getIntermediateHost() {
        return getNameField();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new RTRecord();
    }

    public int getPreference() {
        return getU16Field();
    }
}
