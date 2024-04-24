package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/KXRecord.class */
public class KXRecord extends U16NameBase {
    private static final long serialVersionUID = 7448568832769757809L;

    public KXRecord() {
    }

    public KXRecord(Name name, int i7, long j7, int i8, Name name2) {
        super(name, 36, i7, j7, i8, "preference", name2, "target");
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return getNameField();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new KXRecord();
    }

    public int getPreference() {
        return getU16Field();
    }

    public Name getTarget() {
        return getNameField();
    }
}
