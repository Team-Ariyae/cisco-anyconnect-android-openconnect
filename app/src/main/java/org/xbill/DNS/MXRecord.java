package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/MXRecord.class */
public class MXRecord extends U16NameBase {
    private static final long serialVersionUID = 2914841027584208546L;

    public MXRecord() {
    }

    public MXRecord(Name name, int i7, long j7, int i8, Name name2) {
        super(name, 15, i7, j7, i8, "priority", name2, "target");
    }

    @Override // org.xbill.DNS.Record
    public Name getAdditionalName() {
        return getNameField();
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new MXRecord();
    }

    public int getPriority() {
        return getU16Field();
    }

    public Name getTarget() {
        return getNameField();
    }

    @Override // org.xbill.DNS.U16NameBase, org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.u16Field);
        this.nameField.toWire(dNSOutput, compression, z7);
    }
}
