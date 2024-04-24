package org.xbill.DNS;

import androidx.activity.result.a;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/X25Record.class */
public class X25Record extends Record {
    private static final long serialVersionUID = 4267576252335579764L;
    private byte[] address;

    public X25Record() {
    }

    public X25Record(Name name, int i7, long j7, String str) {
        super(name, 19, i7, j7);
        byte[] checkAndConvertAddress = checkAndConvertAddress(str);
        this.address = checkAndConvertAddress;
        if (checkAndConvertAddress != null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("invalid PSDN address ");
        stringBuffer.append(str);
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    private static final byte[] checkAndConvertAddress(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        for (int i7 = 0; i7 < length; i7++) {
            char charAt = str.charAt(i7);
            if (!Character.isDigit(charAt)) {
                return null;
            }
            bArr[i7] = (byte) charAt;
        }
        return bArr;
    }

    public String getAddress() {
        return Record.byteArrayToString(this.address, false);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new X25Record();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        String string = tokenizer.getString();
        byte[] checkAndConvertAddress = checkAndConvertAddress(string);
        this.address = checkAndConvertAddress;
        if (checkAndConvertAddress == null) {
            throw a.u("invalid PSDN address ", string, tokenizer);
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.address = dNSInput.readCountedString();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        return Record.byteArrayToString(this.address, true);
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeCountedString(this.address);
    }
}
