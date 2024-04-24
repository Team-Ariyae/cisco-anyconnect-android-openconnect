package org.xbill.DNS;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Zone.class */
public class Zone implements Serializable {
    public static final int PRIMARY = 1;
    public static final int SECONDARY = 2;
    private static final long serialVersionUID = -9220510891189510942L;
    private RRset NS;
    private SOARecord SOA;
    private Map data;
    private int dclass;
    private boolean hasWild;
    private Name origin;
    private Object originNode;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Zone$ZoneIterator.class */
    public class ZoneIterator implements Iterator {
        private int count;
        private RRset[] current;
        private final Zone this$0;
        private boolean wantLastSOA;
        private Iterator zentries;

        public ZoneIterator(Zone zone, boolean z7) {
            this.this$0 = zone;
            synchronized (zone) {
                this.zentries = zone.data.entrySet().iterator();
            }
            this.wantLastSOA = z7;
            RRset[] allRRsets = zone.allRRsets(zone.originNode);
            this.current = new RRset[allRRsets.length];
            int i7 = 2;
            for (int i8 = 0; i8 < allRRsets.length; i8++) {
                int type = allRRsets[i8].getType();
                if (type == 6) {
                    this.current[0] = allRRsets[i8];
                } else if (type == 2) {
                    this.current[1] = allRRsets[i8];
                } else {
                    this.current[i7] = allRRsets[i8];
                    i7++;
                }
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null || this.wantLastSOA;
        }

        @Override // java.util.Iterator
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            RRset[] rRsetArr = this.current;
            if (rRsetArr == null) {
                this.wantLastSOA = false;
                Zone zone = this.this$0;
                return zone.oneRRset(zone.originNode, 6);
            }
            int i7 = this.count;
            int i8 = i7 + 1;
            this.count = i8;
            RRset rRset = rRsetArr[i7];
            if (i8 == rRsetArr.length) {
                this.current = null;
                while (true) {
                    if (!this.zentries.hasNext()) {
                        break;
                    }
                    Map.Entry entry = (Map.Entry) this.zentries.next();
                    if (!entry.getKey().equals(this.this$0.origin)) {
                        RRset[] allRRsets = this.this$0.allRRsets(entry.getValue());
                        if (allRRsets.length != 0) {
                            this.current = allRRsets;
                            this.count = 0;
                            break;
                        }
                    }
                }
            }
            return rRset;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Zone(Name name, int i7, String str) {
        this.dclass = 1;
        ZoneTransferIn newAXFR = ZoneTransferIn.newAXFR(name, str, (TSIG) null);
        newAXFR.setDClass(i7);
        fromXFR(newAXFR);
    }

    public Zone(Name name, String str) {
        this.dclass = 1;
        this.data = new TreeMap();
        if (name == null) {
            throw new IllegalArgumentException("no zone name specified");
        }
        Master master = new Master(str, name);
        this.origin = name;
        while (true) {
            Record nextRecord = master.nextRecord();
            if (nextRecord == null) {
                validate();
                return;
            }
            maybeAddRecord(nextRecord);
        }
    }

    public Zone(Name name, Record[] recordArr) {
        this.dclass = 1;
        this.data = new TreeMap();
        if (name == null) {
            throw new IllegalArgumentException("no zone name specified");
        }
        this.origin = name;
        for (Record record : recordArr) {
            maybeAddRecord(record);
        }
        validate();
    }

    public Zone(ZoneTransferIn zoneTransferIn) {
        this.dclass = 1;
        fromXFR(zoneTransferIn);
    }

    private void addRRset(Name name, RRset rRset) {
        synchronized (this) {
            if (!this.hasWild && name.isWild()) {
                this.hasWild = true;
            }
            Object obj = this.data.get(name);
            if (obj == null) {
                this.data.put(name, rRset);
                return;
            }
            int type = rRset.getType();
            if (obj instanceof List) {
                List list = (List) obj;
                for (int i7 = 0; i7 < list.size(); i7++) {
                    if (((RRset) list.get(i7)).getType() == type) {
                        list.set(i7, rRset);
                        return;
                    }
                }
                list.add(rRset);
            } else {
                RRset rRset2 = (RRset) obj;
                if (rRset2.getType() == type) {
                    this.data.put(name, rRset);
                } else {
                    LinkedList linkedList = new LinkedList();
                    linkedList.add(rRset2);
                    linkedList.add(rRset);
                    this.data.put(name, linkedList);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RRset[] allRRsets(Object obj) {
        synchronized (this) {
            if (!(obj instanceof List)) {
                return new RRset[]{(RRset) obj};
            }
            List list = (List) obj;
            return (RRset[]) list.toArray(new RRset[list.size()]);
        }
    }

    private Object exactName(Name name) {
        Object obj;
        synchronized (this) {
            obj = this.data.get(name);
        }
        return obj;
    }

    private RRset findRRset(Name name, int i7) {
        synchronized (this) {
            Object exactName = exactName(name);
            if (exactName == null) {
                return null;
            }
            return oneRRset(exactName, i7);
        }
    }

    private void fromXFR(ZoneTransferIn zoneTransferIn) {
        this.data = new TreeMap();
        this.origin = zoneTransferIn.getName();
        Iterator it = zoneTransferIn.run().iterator();
        while (it.hasNext()) {
            maybeAddRecord((Record) it.next());
        }
        if (!zoneTransferIn.isAXFR()) {
            throw new IllegalArgumentException("zones can only be created from AXFRs");
        }
        validate();
    }

    private SetResponse lookup(Name name, int i7) {
        RRset oneRRset;
        RRset oneRRset2;
        synchronized (this) {
            if (!name.subdomain(this.origin)) {
                return SetResponse.ofType(1);
            }
            int labels = name.labels();
            int labels2 = this.origin.labels();
            int i8 = labels2;
            while (i8 <= labels) {
                boolean z7 = i8 == labels2;
                boolean z8 = i8 == labels;
                Object exactName = exactName(z7 ? this.origin : z8 ? name : new Name(name, labels - i8));
                if (exactName != null) {
                    if (!z7 && (oneRRset2 = oneRRset(exactName, 2)) != null) {
                        return new SetResponse(3, oneRRset2);
                    }
                    if (z8 && i7 == 255) {
                        SetResponse setResponse = new SetResponse(6);
                        for (RRset rRset : allRRsets(exactName)) {
                            setResponse.addRRset(rRset);
                        }
                        return setResponse;
                    }
                    if (z8) {
                        RRset oneRRset3 = oneRRset(exactName, i7);
                        if (oneRRset3 != null) {
                            SetResponse setResponse2 = new SetResponse(6);
                            setResponse2.addRRset(oneRRset3);
                            return setResponse2;
                        }
                        RRset oneRRset4 = oneRRset(exactName, 5);
                        if (oneRRset4 != null) {
                            return new SetResponse(4, oneRRset4);
                        }
                    } else {
                        RRset oneRRset5 = oneRRset(exactName, 39);
                        if (oneRRset5 != null) {
                            return new SetResponse(5, oneRRset5);
                        }
                    }
                    if (z8) {
                        return SetResponse.ofType(2);
                    }
                }
                i8++;
            }
            if (this.hasWild) {
                int i9 = 0;
                while (i9 < labels - labels2) {
                    i9++;
                    Object exactName2 = exactName(name.wild(i9));
                    if (exactName2 != null && (oneRRset = oneRRset(exactName2, i7)) != null) {
                        SetResponse setResponse3 = new SetResponse(6);
                        setResponse3.addRRset(oneRRset);
                        return setResponse3;
                    }
                }
            }
            return SetResponse.ofType(1);
        }
    }

    private final void maybeAddRecord(Record record) {
        int type = record.getType();
        Name name = record.getName();
        if (type != 6 || name.equals(this.origin)) {
            if (name.subdomain(this.origin)) {
                addRecord(record);
            }
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("SOA owner ");
            stringBuffer.append(name);
            stringBuffer.append(" does not match zone origin ");
            stringBuffer.append(this.origin);
            throw new IOException(stringBuffer.toString());
        }
    }

    private void nodeToString(StringBuffer stringBuffer, Object obj) {
        for (RRset rRset : allRRsets(obj)) {
            Iterator rrs = rRset.rrs();
            while (rrs.hasNext()) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(rrs.next());
                stringBuffer2.append("\n");
                stringBuffer.append(stringBuffer2.toString());
            }
            Iterator sigs = rRset.sigs();
            while (sigs.hasNext()) {
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append(sigs.next());
                stringBuffer3.append("\n");
                stringBuffer.append(stringBuffer3.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RRset oneRRset(Object obj, int i7) {
        synchronized (this) {
            if (i7 == 255) {
                throw new IllegalArgumentException("oneRRset(ANY)");
            }
            if (obj instanceof List) {
                List list = (List) obj;
                for (int i8 = 0; i8 < list.size(); i8++) {
                    RRset rRset = (RRset) list.get(i8);
                    if (rRset.getType() == i7) {
                        return rRset;
                    }
                }
            } else {
                RRset rRset2 = (RRset) obj;
                if (rRset2.getType() == i7) {
                    return rRset2;
                }
            }
            return null;
        }
    }

    private void removeRRset(Name name, int i7) {
        synchronized (this) {
            Object obj = this.data.get(name);
            if (obj == null) {
                return;
            }
            if (obj instanceof List) {
                List list = (List) obj;
                for (int i8 = 0; i8 < list.size(); i8++) {
                    if (((RRset) list.get(i8)).getType() == i7) {
                        list.remove(i8);
                        if (list.size() == 0) {
                            this.data.remove(name);
                        }
                        return;
                    }
                }
            } else if (((RRset) obj).getType() == i7) {
                this.data.remove(name);
            }
        }
    }

    private void validate() {
        Object exactName = exactName(this.origin);
        this.originNode = exactName;
        if (exactName == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.origin);
            stringBuffer.append(": no data specified");
            throw new IOException(stringBuffer.toString());
        }
        RRset oneRRset = oneRRset(exactName, 6);
        if (oneRRset == null || oneRRset.size() != 1) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(this.origin);
            stringBuffer2.append(": exactly 1 SOA must be specified");
            throw new IOException(stringBuffer2.toString());
        }
        this.SOA = (SOARecord) oneRRset.rrs().next();
        RRset oneRRset2 = oneRRset(this.originNode, 2);
        this.NS = oneRRset2;
        if (oneRRset2 != null) {
            return;
        }
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(this.origin);
        stringBuffer3.append(": no NS set specified");
        throw new IOException(stringBuffer3.toString());
    }

    public Iterator AXFR() {
        return new ZoneIterator(this, true);
    }

    public void addRRset(RRset rRset) {
        addRRset(rRset.getName(), rRset);
    }

    public void addRecord(Record record) {
        Name name = record.getName();
        int rRsetType = record.getRRsetType();
        synchronized (this) {
            RRset findRRset = findRRset(name, rRsetType);
            if (findRRset == null) {
                addRRset(name, new RRset(record));
            } else {
                findRRset.addRR(record);
            }
        }
    }

    public RRset findExactMatch(Name name, int i7) {
        Object exactName = exactName(name);
        if (exactName == null) {
            return null;
        }
        return oneRRset(exactName, i7);
    }

    public SetResponse findRecords(Name name, int i7) {
        return lookup(name, i7);
    }

    public int getDClass() {
        return this.dclass;
    }

    public RRset getNS() {
        return this.NS;
    }

    public Name getOrigin() {
        return this.origin;
    }

    public SOARecord getSOA() {
        return this.SOA;
    }

    public Iterator iterator() {
        return new ZoneIterator(this, false);
    }

    public void removeRecord(Record record) {
        Name name = record.getName();
        int rRsetType = record.getRRsetType();
        synchronized (this) {
            RRset findRRset = findRRset(name, rRsetType);
            if (findRRset == null) {
                return;
            }
            if (findRRset.size() == 1 && findRRset.first().equals(record)) {
                removeRRset(name, rRsetType);
            } else {
                findRRset.deleteRR(record);
            }
        }
    }

    public String toMasterFile() {
        String stringBuffer;
        Map.Entry entry;
        synchronized (this) {
            Iterator it = this.data.entrySet().iterator();
            StringBuffer stringBuffer2 = new StringBuffer();
            Object obj = this.originNode;
            while (true) {
                nodeToString(stringBuffer2, obj);
                do {
                    if (it.hasNext()) {
                        entry = (Map.Entry) it.next();
                    } else {
                        stringBuffer = stringBuffer2.toString();
                    }
                } while (this.origin.equals(entry.getKey()));
                obj = entry.getValue();
            }
        }
        return stringBuffer;
    }

    public String toString() {
        return toMasterFile();
    }
}
