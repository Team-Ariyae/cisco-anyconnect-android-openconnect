package org.xbill.DNS;

import java.io.PrintStream;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Compression.class */
public class Compression {
    private static final int MAX_POINTER = 16383;
    private static final int TABLE_SIZE = 17;
    private boolean verbose = Options.check("verbosecompression");
    private Entry[] table = new Entry[17];

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Compression$Entry.class */
    public static class Entry {
        public Name name;
        public Entry next;
        public int pos;

        private Entry() {
        }
    }

    public void add(int i7, Name name) {
        if (i7 > MAX_POINTER) {
            return;
        }
        int hashCode = (name.hashCode() & Integer.MAX_VALUE) % 17;
        Entry entry = new Entry();
        entry.name = name;
        entry.pos = i7;
        Entry[] entryArr = this.table;
        entry.next = entryArr[hashCode];
        entryArr[hashCode] = entry;
        if (this.verbose) {
            PrintStream printStream = System.err;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Adding ");
            stringBuffer.append(name);
            stringBuffer.append(" at ");
            stringBuffer.append(i7);
            printStream.println(stringBuffer.toString());
        }
    }

    public int get(Name name) {
        int i7 = -1;
        for (Entry entry = this.table[(name.hashCode() & Integer.MAX_VALUE) % 17]; entry != null; entry = entry.next) {
            if (entry.name.equals(name)) {
                i7 = entry.pos;
            }
        }
        if (this.verbose) {
            PrintStream printStream = System.err;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Looking for ");
            stringBuffer.append(name);
            stringBuffer.append(", found ");
            stringBuffer.append(i7);
            printStream.println(stringBuffer.toString());
        }
        return i7;
    }
}
