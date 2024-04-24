package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;
import org.xbill.DNS.Tokenizer;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TypeBitmap.class */
final class TypeBitmap implements Serializable {
    private static final long serialVersionUID = -125354057735389003L;
    private TreeSet types;

    private TypeBitmap() {
        this.types = new TreeSet();
    }

    public TypeBitmap(DNSInput dNSInput) {
        this();
        while (dNSInput.remaining() > 0) {
            if (dNSInput.remaining() < 2) {
                throw new WireParseException("invalid bitmap descriptor");
            }
            int readU8 = dNSInput.readU8();
            if (readU8 < -1) {
                throw new WireParseException("invalid ordering");
            }
            int readU82 = dNSInput.readU8();
            if (readU82 > dNSInput.remaining()) {
                throw new WireParseException("invalid bitmap");
            }
            for (int i7 = 0; i7 < readU82; i7++) {
                int readU83 = dNSInput.readU8();
                if (readU83 != 0) {
                    for (int i8 = 0; i8 < 8; i8++) {
                        if (((1 << (7 - i8)) & readU83) != 0) {
                            this.types.add(Mnemonic.toInteger((i7 * 8) + (readU8 * 256) + i8));
                        }
                    }
                }
            }
        }
    }

    public TypeBitmap(Tokenizer tokenizer) {
        this();
        while (true) {
            Tokenizer.Token token = tokenizer.get();
            if (!token.isString()) {
                tokenizer.unget();
                return;
            }
            int value = Type.value(token.value);
            if (value < 0) {
                StringBuffer p = a.p("Invalid type: ");
                p.append(token.value);
                throw tokenizer.exception(p.toString());
            }
            this.types.add(Mnemonic.toInteger(value));
        }
    }

    public TypeBitmap(int[] iArr) {
        this();
        for (int i7 = 0; i7 < iArr.length; i7++) {
            Type.check(iArr[i7]);
            this.types.add(new Integer(iArr[i7]));
        }
    }

    private static void mapToWire(DNSOutput dNSOutput, TreeSet treeSet, int i7) {
        int intValue = ((((Integer) treeSet.last()).intValue() & 255) / 8) + 1;
        int[] iArr = new int[intValue];
        dNSOutput.writeU8(i7);
        dNSOutput.writeU8(intValue);
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            int intValue2 = ((Integer) it.next()).intValue();
            int i8 = (intValue2 & 255) / 8;
            iArr[i8] = (1 << (7 - (intValue2 % 8))) | iArr[i8];
        }
        for (int i9 = 0; i9 < intValue; i9++) {
            dNSOutput.writeU8(iArr[i9]);
        }
    }

    public boolean contains(int i7) {
        return this.types.contains(Mnemonic.toInteger(i7));
    }

    public boolean empty() {
        return this.types.isEmpty();
    }

    public int[] toArray() {
        int[] iArr = new int[this.types.size()];
        Iterator it = this.types.iterator();
        int i7 = 0;
        while (it.hasNext()) {
            iArr[i7] = ((Integer) it.next()).intValue();
            i7++;
        }
        return iArr;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = this.types.iterator();
        while (it.hasNext()) {
            stringBuffer.append(Type.string(((Integer) it.next()).intValue()));
            if (it.hasNext()) {
                stringBuffer.append(' ');
            }
        }
        return stringBuffer.toString();
    }

    public void toWire(DNSOutput dNSOutput) {
        if (this.types.size() == 0) {
            return;
        }
        int i7 = -1;
        TreeSet treeSet = new TreeSet();
        Iterator it = this.types.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            int i8 = intValue >> 8;
            int i9 = i7;
            if (i8 != i7) {
                if (treeSet.size() > 0) {
                    mapToWire(dNSOutput, treeSet, i7);
                    treeSet.clear();
                }
                i9 = i8;
            }
            treeSet.add(new Integer(intValue));
            i7 = i9;
        }
        mapToWire(dNSOutput, treeSet, i7);
    }
}
