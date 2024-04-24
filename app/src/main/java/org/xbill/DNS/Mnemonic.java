package org.xbill.DNS;

import java.util.HashMap;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Mnemonic.class */
class Mnemonic {
    public static final int CASE_LOWER = 3;
    public static final int CASE_SENSITIVE = 1;
    public static final int CASE_UPPER = 2;
    private static Integer[] cachedInts = new Integer[64];
    private String description;
    private boolean numericok;
    private String prefix;
    private int wordcase;
    private HashMap strings = new HashMap();
    private HashMap values = new HashMap();
    private int max = Integer.MAX_VALUE;

    static {
        int i7 = 0;
        while (true) {
            Integer[] numArr = cachedInts;
            if (i7 >= numArr.length) {
                return;
            }
            numArr[i7] = new Integer(i7);
            i7++;
        }
    }

    public Mnemonic(String str, int i7) {
        this.description = str;
        this.wordcase = i7;
    }

    private int parseNumeric(String str) {
        try {
            int parseInt = Integer.parseInt(str);
            if (parseInt < 0) {
                return -1;
            }
            if (parseInt <= this.max) {
                return parseInt;
            }
            return -1;
        } catch (NumberFormatException e8) {
            return -1;
        }
    }

    private String sanitize(String str) {
        int i7 = this.wordcase;
        if (i7 == 2) {
            return str.toUpperCase();
        }
        String str2 = str;
        if (i7 == 3) {
            str2 = str.toLowerCase();
        }
        return str2;
    }

    public static Integer toInteger(int i7) {
        if (i7 >= 0) {
            Integer[] numArr = cachedInts;
            if (i7 < numArr.length) {
                return numArr[i7];
            }
        }
        return new Integer(i7);
    }

    public void add(int i7, String str) {
        check(i7);
        Integer integer = toInteger(i7);
        String sanitize = sanitize(str);
        this.strings.put(sanitize, integer);
        this.values.put(integer, sanitize);
    }

    public void addAlias(int i7, String str) {
        check(i7);
        Integer integer = toInteger(i7);
        this.strings.put(sanitize(str), integer);
    }

    public void addAll(Mnemonic mnemonic) {
        if (this.wordcase == mnemonic.wordcase) {
            this.strings.putAll(mnemonic.strings);
            this.values.putAll(mnemonic.values);
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(mnemonic.description);
            stringBuffer.append(": wordcases do not match");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public void check(int i7) {
        if (i7 < 0 || i7 > this.max) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.description);
            stringBuffer.append(" ");
            stringBuffer.append(i7);
            stringBuffer.append("is out of range");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public String getText(int i7) {
        check(i7);
        String str = (String) this.values.get(toInteger(i7));
        if (str != null) {
            return str;
        }
        String num = Integer.toString(i7);
        String str2 = num;
        if (this.prefix != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.prefix);
            stringBuffer.append(num);
            str2 = stringBuffer.toString();
        }
        return str2;
    }

    public int getValue(String str) {
        int parseNumeric;
        String sanitize = sanitize(str);
        Integer num = (Integer) this.strings.get(sanitize);
        if (num != null) {
            return num.intValue();
        }
        String str2 = this.prefix;
        if (str2 != null && sanitize.startsWith(str2) && (parseNumeric = parseNumeric(sanitize.substring(this.prefix.length()))) >= 0) {
            return parseNumeric;
        }
        if (this.numericok) {
            return parseNumeric(sanitize);
        }
        return -1;
    }

    public void setMaximum(int i7) {
        this.max = i7;
    }

    public void setNumericAllowed(boolean z7) {
        this.numericok = z7;
    }

    public void setPrefix(String str) {
        this.prefix = sanitize(str);
    }
}
