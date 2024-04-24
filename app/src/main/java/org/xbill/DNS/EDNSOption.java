package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.Arrays;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/EDNSOption.class */
public abstract class EDNSOption {
    private final int code;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/EDNSOption$Code.class */
    public static class Code {
        public static final int CLIENT_SUBNET = 8;
        public static final int NSID = 3;
        private static Mnemonic codes;

        static {
            Mnemonic mnemonic = new Mnemonic("EDNS Option Codes", 2);
            codes = mnemonic;
            mnemonic.setMaximum(Message.MAXLENGTH);
            codes.setPrefix("CODE");
            codes.setNumericAllowed(true);
            codes.add(3, "NSID");
            codes.add(8, "CLIENT_SUBNET");
        }

        private Code() {
        }

        public static String string(int i7) {
            return codes.getText(i7);
        }

        public static int value(String str) {
            return codes.getValue(str);
        }
    }

    public EDNSOption(int i7) {
        this.code = Record.checkU16("code", i7);
    }

    public static EDNSOption fromWire(DNSInput dNSInput) {
        int readU16 = dNSInput.readU16();
        int readU162 = dNSInput.readU16();
        if (dNSInput.remaining() < readU162) {
            throw new WireParseException("truncated option");
        }
        int saveActive = dNSInput.saveActive();
        dNSInput.setActive(readU162);
        EDNSOption genericEDNSOption = readU16 != 3 ? readU16 != 8 ? new GenericEDNSOption(readU16) : new ClientSubnetOption() : new NSIDOption();
        genericEDNSOption.optionFromWire(dNSInput);
        dNSInput.restoreActive(saveActive);
        return genericEDNSOption;
    }

    public static EDNSOption fromWire(byte[] bArr) {
        return fromWire(new DNSInput(bArr));
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EDNSOption)) {
            return false;
        }
        EDNSOption eDNSOption = (EDNSOption) obj;
        if (this.code != eDNSOption.code) {
            return false;
        }
        return Arrays.equals(getData(), eDNSOption.getData());
    }

    public int getCode() {
        return this.code;
    }

    public byte[] getData() {
        DNSOutput dNSOutput = new DNSOutput();
        optionToWire(dNSOutput);
        return dNSOutput.toByteArray();
    }

    public int hashCode() {
        int i7 = 0;
        for (byte b8 : getData()) {
            i7 += (i7 << 3) + (b8 & 255);
        }
        return i7;
    }

    public abstract void optionFromWire(DNSInput dNSInput);

    public abstract String optionToString();

    public abstract void optionToWire(DNSOutput dNSOutput);

    public String toString() {
        StringBuffer p = a.p("{");
        p.append(Code.string(this.code));
        p.append(": ");
        p.append(optionToString());
        p.append("}");
        return p.toString();
    }

    public void toWire(DNSOutput dNSOutput) {
        dNSOutput.writeU16(this.code);
        int current = dNSOutput.current();
        dNSOutput.writeU16(0);
        optionToWire(dNSOutput);
        dNSOutput.writeU16At((dNSOutput.current() - current) - 2, current);
    }

    public byte[] toWire() {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput);
        return dNSOutput.toByteArray();
    }
}
