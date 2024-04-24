package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DClass.class */
public final class DClass {
    public static final int ANY = 255;
    public static final int CH = 3;
    public static final int CHAOS = 3;
    public static final int HESIOD = 4;
    public static final int HS = 4;
    public static final int IN = 1;
    public static final int NONE = 254;
    private static Mnemonic classes;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DClass$DClassMnemonic.class */
    public static class DClassMnemonic extends Mnemonic {
        public DClassMnemonic() {
            super("DClass", 2);
            setPrefix("CLASS");
        }

        @Override // org.xbill.DNS.Mnemonic
        public void check(int i7) {
            DClass.check(i7);
        }
    }

    static {
        DClassMnemonic dClassMnemonic = new DClassMnemonic();
        classes = dClassMnemonic;
        dClassMnemonic.add(1, "IN");
        classes.add(3, "CH");
        classes.addAlias(3, "CHAOS");
        classes.add(4, "HS");
        classes.addAlias(4, "HESIOD");
        classes.add(254, "NONE");
        classes.add(255, "ANY");
    }

    private DClass() {
    }

    public static void check(int i7) {
        if (i7 < 0 || i7 > 65535) {
            throw new InvalidDClassException(i7);
        }
    }

    public static String string(int i7) {
        return classes.getText(i7);
    }

    public static int value(String str) {
        return classes.getValue(str);
    }
}
