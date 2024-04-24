package org.xbill.DNS;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SingleNameBase.class */
public abstract class SingleNameBase extends Record {
    private static final long serialVersionUID = -18595042501413L;
    public Name singleName;

    public SingleNameBase() {
    }

    public SingleNameBase(Name name, int i7, int i8, long j7) {
        super(name, i7, i8, j7);
    }

    public SingleNameBase(Name name, int i7, int i8, long j7, Name name2, String str) {
        super(name, i7, i8, j7);
        this.singleName = Record.checkName(str, name2);
    }

    public Name getSingleName() {
        return this.singleName;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.singleName = tokenizer.getName(name);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.singleName = new Name(dNSInput);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        return this.singleName.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.singleName.toWire(dNSOutput, null, z7);
    }
}
