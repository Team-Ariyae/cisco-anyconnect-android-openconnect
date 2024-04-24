package org.xbill.DNS;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SingleCompressedNameBase.class */
public abstract class SingleCompressedNameBase extends SingleNameBase {
    private static final long serialVersionUID = -236435396815460677L;

    public SingleCompressedNameBase() {
    }

    public SingleCompressedNameBase(Name name, int i7, int i8, long j7, Name name2, String str) {
        super(name, i7, i8, j7, name2, str);
    }

    @Override // org.xbill.DNS.SingleNameBase, org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        this.singleName.toWire(dNSOutput, compression, z7);
    }
}
