package org.xbill.DNS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xbill.DNS.Tokenizer;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TXTBase.class */
abstract class TXTBase extends Record {
    private static final long serialVersionUID = -4319510507246305931L;
    public List strings;

    public TXTBase() {
    }

    public TXTBase(Name name, int i7, int i8, long j7) {
        super(name, i7, i8, j7);
    }

    public TXTBase(Name name, int i7, int i8, long j7, String str) {
        this(name, i7, i8, j7, Collections.singletonList(str));
    }

    public TXTBase(Name name, int i7, int i8, long j7, List list) {
        super(name, i7, i8, j7);
        if (list == null) {
            throw new IllegalArgumentException("strings must not be null");
        }
        this.strings = new ArrayList(list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            try {
                this.strings.add(Record.byteArrayFromString((String) it.next()));
            } catch (TextParseException e8) {
                throw new IllegalArgumentException(e8.getMessage());
            }
        }
    }

    public List getStrings() {
        ArrayList arrayList = new ArrayList(this.strings.size());
        for (int i7 = 0; i7 < this.strings.size(); i7++) {
            arrayList.add(Record.byteArrayToString((byte[]) this.strings.get(i7), false));
        }
        return arrayList;
    }

    public List getStringsAsByteArrays() {
        return this.strings;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.strings = new ArrayList(2);
        while (true) {
            Tokenizer.Token token = tokenizer.get();
            if (!token.isString()) {
                tokenizer.unget();
                return;
            } else {
                try {
                    this.strings.add(Record.byteArrayFromString(token.value));
                } catch (TextParseException e8) {
                    throw tokenizer.exception(e8.getMessage());
                }
            }
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.strings = new ArrayList(2);
        while (dNSInput.remaining() > 0) {
            this.strings.add(dNSInput.readCountedString());
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = this.strings.iterator();
        while (it.hasNext()) {
            stringBuffer.append(Record.byteArrayToString((byte[]) it.next(), true));
            if (it.hasNext()) {
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        Iterator it = this.strings.iterator();
        while (it.hasNext()) {
            dNSOutput.writeCountedString((byte[]) it.next());
        }
    }
}
