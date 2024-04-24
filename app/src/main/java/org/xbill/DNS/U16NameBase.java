package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/U16NameBase.class */
abstract class U16NameBase extends Record {
    private static final long serialVersionUID = -8315884183112502995L;
    public Name nameField;
    public int u16Field;

    public U16NameBase() {
    }

    public U16NameBase(Name name, int i7, int i8, long j7) {
        super(name, i7, i8, j7);
    }

    public U16NameBase(Name name, int i7, int i8, long j7, int i9, String str, Name name2, String str2) {
        super(name, i7, i8, j7);
        this.u16Field = Record.checkU16(str, i9);
        this.nameField = Record.checkName(str2, name2);
    }

    public Name getNameField() {
        return this.nameField;
    }

    public int getU16Field() {
        return this.u16Field;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.u16Field = tokenizer.getUInt16();
        this.nameField = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.u16Field = dNSInput.readU16();
        this.nameField = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.u16Field);
        stringBuffer.append(" ");
        stringBuffer.append(this.nameField);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.u16Field);
        this.nameField.toWire(dNSOutput, null, z7);
    }
}
