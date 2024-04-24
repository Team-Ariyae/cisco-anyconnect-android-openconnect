package org.xbill.DNS;

import androidx.activity.result.a;
import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/GenericEDNSOption.class */
public class GenericEDNSOption extends EDNSOption {
    private byte[] data;

    public GenericEDNSOption(int i7) {
        super(i7);
    }

    public GenericEDNSOption(int i7, byte[] bArr) {
        super(i7);
        this.data = Record.checkByteArrayLength("option data", bArr, Message.MAXLENGTH);
    }

    @Override // org.xbill.DNS.EDNSOption
    public void optionFromWire(DNSInput dNSInput) {
        this.data = dNSInput.readByteArray();
    }

    @Override // org.xbill.DNS.EDNSOption
    public String optionToString() {
        StringBuffer p = a.p("<");
        p.append(base16.toString(this.data));
        p.append(">");
        return p.toString();
    }

    @Override // org.xbill.DNS.EDNSOption
    public void optionToWire(DNSOutput dNSOutput) {
        dNSOutput.writeByteArray(this.data);
    }
}
