package org.xbill.DNS;

import org.xbill.DNS.utils.base64;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DHCIDRecord.class */
public class DHCIDRecord extends Record {
    private static final long serialVersionUID = -8214820200808997707L;
    private byte[] data;

    public DHCIDRecord() {
    }

    public DHCIDRecord(Name name, int i7, long j7, byte[] bArr) {
        super(name, 49, i7, j7);
        this.data = bArr;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new DHCIDRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.data = tokenizer.getBase64();
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.data = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        return base64.toString(this.data);
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeByteArray(this.data);
    }
}
