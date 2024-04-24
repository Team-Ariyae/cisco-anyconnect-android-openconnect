package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ExtendedFlags.class */
public final class ExtendedFlags {
    public static final int DO = 32768;
    private static Mnemonic extflags;

    static {
        Mnemonic mnemonic = new Mnemonic("EDNS Flag", 3);
        extflags = mnemonic;
        mnemonic.setMaximum(Message.MAXLENGTH);
        extflags.setPrefix("FLAG");
        extflags.setNumericAllowed(true);
        extflags.add(32768, "do");
    }

    private ExtendedFlags() {
    }

    public static String string(int i7) {
        return extflags.getText(i7);
    }

    public static int value(String str) {
        return extflags.getValue(str);
    }
}
