package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Opcode.class */
public final class Opcode {
    public static final int IQUERY = 1;
    public static final int NOTIFY = 4;
    public static final int QUERY = 0;
    public static final int STATUS = 2;
    public static final int UPDATE = 5;
    private static Mnemonic opcodes;

    static {
        Mnemonic mnemonic = new Mnemonic("DNS Opcode", 2);
        opcodes = mnemonic;
        mnemonic.setMaximum(15);
        opcodes.setPrefix("RESERVED");
        opcodes.setNumericAllowed(true);
        opcodes.add(0, "QUERY");
        opcodes.add(1, "IQUERY");
        opcodes.add(2, "STATUS");
        opcodes.add(4, "NOTIFY");
        opcodes.add(5, "UPDATE");
    }

    private Opcode() {
    }

    public static String string(int i7) {
        return opcodes.getText(i7);
    }

    public static int value(String str) {
        return opcodes.getValue(str);
    }
}
