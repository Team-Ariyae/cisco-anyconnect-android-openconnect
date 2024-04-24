package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.ByteArrayOutputStream;
import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NSAPRecord.class */
public class NSAPRecord extends Record {
    private static final long serialVersionUID = -1037209403185658593L;
    private byte[] address;

    public NSAPRecord() {
    }

    public NSAPRecord(Name name, int i7, long j7, String str) {
        super(name, 22, i7, j7);
        byte[] checkAndConvertAddress = checkAndConvertAddress(str);
        this.address = checkAndConvertAddress;
        if (checkAndConvertAddress != null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("invalid NSAP address ");
        stringBuffer.append(str);
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    private static final byte[] checkAndConvertAddress(String str) {
        if (!str.substring(0, 2).equalsIgnoreCase("0x")) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean z7 = false;
        int i7 = 0;
        for (int i8 = 2; i8 < str.length(); i8++) {
            char charAt = str.charAt(i8);
            if (charAt != '.') {
                int digit = Character.digit(charAt, 16);
                if (digit == -1) {
                    return null;
                }
                if (z7) {
                    i7 += digit;
                    byteArrayOutputStream.write(i7);
                    z7 = false;
                } else {
                    i7 = digit << 4;
                    z7 = true;
                }
            }
        }
        if (z7) {
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }

    public String getAddress() {
        return Record.byteArrayToString(this.address, false);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NSAPRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        String string = tokenizer.getString();
        byte[] checkAndConvertAddress = checkAndConvertAddress(string);
        this.address = checkAndConvertAddress;
        if (checkAndConvertAddress == null) {
            throw a.u("invalid NSAP address ", string, tokenizer);
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.address = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer p = a.p("0x");
        p.append(base16.toString(this.address));
        return p.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeByteArray(this.address);
    }
}
