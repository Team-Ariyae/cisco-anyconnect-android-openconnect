package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Cache.class */
public class Cache {
    private static final int defaultMaxEntries = 50000;
    private CacheMap data;
    private int dclass;
    private int maxcache;
    private int maxncache;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Cache$CacheMap.class */
    public static class CacheMap extends LinkedHashMap {
        private int maxsize;

        public CacheMap(int i7) {
            super(16, 0.75f, true);
            this.maxsize = i7;
        }

        public int getMaxSize() {
            return this.maxsize;
        }

        @Override // java.util.LinkedHashMap
        public boolean removeEldestEntry(Map.Entry entry) {
            return this.maxsize >= 0 && size() > this.maxsize;
        }

        public void setMaxSize(int i7) {
            this.maxsize = i7;
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Cache$CacheRRset.class */
    public static class CacheRRset extends RRset implements Element {
        private static final long serialVersionUID = 5971755205903597024L;
        public int credibility;
        public int expire;

        public CacheRRset(RRset rRset, int i7, long j7) {
            super(rRset);
            this.credibility = i7;
            this.expire = Cache.limitExpire(rRset.getTTL(), j7);
        }

        public CacheRRset(Record record, int i7, long j7) {
            this.credibility = i7;
            this.expire = Cache.limitExpire(record.getTTL(), j7);
            addRR(record);
        }

        @Override // org.xbill.DNS.Cache.Element
        public final int compareCredibility(int i7) {
            return this.credibility - i7;
        }

        @Override // org.xbill.DNS.Cache.Element
        public final boolean expired() {
            return ((int) (System.currentTimeMillis() / 1000)) >= this.expire;
        }

        @Override // org.xbill.DNS.RRset
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(super.toString());
            stringBuffer.append(" cl = ");
            stringBuffer.append(this.credibility);
            return stringBuffer.toString();
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Cache$Element.class */
    public interface Element {
        int compareCredibility(int i7);

        boolean expired();

        int getType();
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Cache$NegativeElement.class */
    public static class NegativeElement implements Element {
        public int credibility;
        public int expire;
        public Name name;
        public int type;

        public NegativeElement(Name name, int i7, SOARecord sOARecord, int i8, long j7) {
            this.name = name;
            this.type = i7;
            long minimum = sOARecord != null ? sOARecord.getMinimum() : 0L;
            this.credibility = i8;
            this.expire = Cache.limitExpire(minimum, j7);
        }

        @Override // org.xbill.DNS.Cache.Element
        public final int compareCredibility(int i7) {
            return this.credibility - i7;
        }

        @Override // org.xbill.DNS.Cache.Element
        public final boolean expired() {
            return ((int) (System.currentTimeMillis() / 1000)) >= this.expire;
        }

        @Override // org.xbill.DNS.Cache.Element
        public int getType() {
            return this.type;
        }

        public String toString() {
            StringBuffer p;
            StringBuffer stringBuffer = new StringBuffer();
            if (this.type == 0) {
                p = a.p("NXDOMAIN ");
                p.append(this.name);
            } else {
                p = a.p("NXRRSET ");
                p.append(this.name);
                p.append(" ");
                p.append(Type.string(this.type));
            }
            stringBuffer.append(p.toString());
            stringBuffer.append(" cl = ");
            stringBuffer.append(this.credibility);
            return stringBuffer.toString();
        }
    }

    public Cache() {
        this(1);
    }

    public Cache(int i7) {
        this.maxncache = -1;
        this.maxcache = -1;
        this.dclass = i7;
        this.data = new CacheMap(defaultMaxEntries);
    }

    public Cache(String str) {
        this.maxncache = -1;
        this.maxcache = -1;
        this.data = new CacheMap(defaultMaxEntries);
        Master master = new Master(str);
        while (true) {
            Record nextRecord = master.nextRecord();
            if (nextRecord == null) {
                return;
            } else {
                addRecord(nextRecord, 0, master);
            }
        }
    }

    private void addElement(Name name, Element element) {
        synchronized (this) {
            V v7 = this.data.get(name);
            if (v7 == 0) {
                this.data.put(name, element);
                return;
            }
            int type = element.getType();
            if (v7 instanceof List) {
                List list = (List) v7;
                for (int i7 = 0; i7 < list.size(); i7++) {
                    if (((Element) list.get(i7)).getType() == type) {
                        list.set(i7, element);
                        return;
                    }
                }
                list.add(element);
            } else {
                Element element2 = (Element) v7;
                if (element2.getType() == type) {
                    this.data.put(name, element);
                } else {
                    LinkedList linkedList = new LinkedList();
                    linkedList.add(element2);
                    linkedList.add(element);
                    this.data.put(name, linkedList);
                }
            }
        }
    }

    private Element[] allElements(Object obj) {
        synchronized (this) {
            if (!(obj instanceof List)) {
                return new Element[]{(Element) obj};
            }
            List list = (List) obj;
            return (Element[]) list.toArray(new Element[list.size()]);
        }
    }

    private Object exactName(Name name) {
        V v7;
        synchronized (this) {
            v7 = this.data.get(name);
        }
        return v7;
    }

    private Element findElement(Name name, int i7, int i8) {
        synchronized (this) {
            Object exactName = exactName(name);
            if (exactName == null) {
                return null;
            }
            return oneElement(name, exactName, i7, i8);
        }
    }

    private RRset[] findRecords(Name name, int i7, int i8) {
        SetResponse lookupRecords = lookupRecords(name, i7, i8);
        if (lookupRecords.isSuccessful()) {
            return lookupRecords.answers();
        }
        return null;
    }

    private final int getCred(int i7, boolean z7) {
        if (i7 == 1) {
            return z7 ? 4 : 3;
        }
        if (i7 == 2) {
            return z7 ? 4 : 3;
        }
        if (i7 == 3) {
            return 1;
        }
        throw new IllegalArgumentException("getCred: invalid section");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int limitExpire(long j7, long j8) {
        long j9 = j7;
        if (j8 >= 0) {
            j9 = j7;
            if (j8 < j7) {
                j9 = j8;
            }
        }
        long currentTimeMillis = (System.currentTimeMillis() / 1000) + j9;
        if (currentTimeMillis < 0 || currentTimeMillis > TTL.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) currentTimeMillis;
    }

    private static void markAdditional(RRset rRset, Set set) {
        if (rRset.first().getAdditionalName() == null) {
            return;
        }
        Iterator rrs = rRset.rrs();
        while (rrs.hasNext()) {
            Name additionalName = ((Record) rrs.next()).getAdditionalName();
            if (additionalName != null) {
                set.add(additionalName);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x0055, code lost:
    
        if (r6.getType() == r7) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.xbill.DNS.Cache.Element oneElement(org.xbill.DNS.Name r5, java.lang.Object r6, int r7, int r8) {
        /*
            r4 = this;
            r0 = r4
            monitor-enter(r0)
            r0 = r7
            r1 = 255(0xff, float:3.57E-43)
            if (r0 == r1) goto L8f
            r0 = r6
            boolean r0 = r0 instanceof java.util.List     // Catch: java.lang.Throwable -> L8b
            if (r0 == 0) goto L45
            r0 = r6
            java.util.List r0 = (java.util.List) r0     // Catch: java.lang.Throwable -> L8b
            r10 = r0
            r0 = 0
            r9 = r0
        L19:
            r0 = r9
            r1 = r10
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L8b
            if (r0 >= r1) goto L5b
            r0 = r10
            r1 = r9
            java.lang.Object r0 = r0.get(r1)     // Catch: java.lang.Throwable -> L8b
            org.xbill.DNS.Cache$Element r0 = (org.xbill.DNS.Cache.Element) r0     // Catch: java.lang.Throwable -> L8b
            r6 = r0
            r0 = r6
            int r0 = r0.getType()     // Catch: java.lang.Throwable -> L8b
            r1 = r7
            if (r0 != r1) goto L3f
            goto L5d
        L3f:
            int r9 = r9 + 1
            goto L19
        L45:
            r0 = r6
            org.xbill.DNS.Cache$Element r0 = (org.xbill.DNS.Cache.Element) r0     // Catch: java.lang.Throwable -> L8b
            r6 = r0
            r0 = r6
            int r0 = r0.getType()     // Catch: java.lang.Throwable -> L8b
            r9 = r0
            r0 = r9
            r1 = r7
            if (r0 != r1) goto L5b
            goto L5d
        L5b:
            r0 = 0
            r6 = r0
        L5d:
            r0 = r6
            if (r0 != 0) goto L65
            r0 = r4
            monitor-exit(r0)
            r0 = 0
            return r0
        L65:
            r0 = r6
            boolean r0 = r0.expired()     // Catch: java.lang.Throwable -> L8b
            if (r0 == 0) goto L78
            r0 = r4
            r1 = r5
            r2 = r7
            r0.removeElement(r1, r2)     // Catch: java.lang.Throwable -> L8b
            r0 = r4
            monitor-exit(r0)
            r0 = 0
            return r0
        L78:
            r0 = r6
            r1 = r8
            int r0 = r0.compareCredibility(r1)     // Catch: java.lang.Throwable -> L8b
            r7 = r0
            r0 = r4
            monitor-exit(r0)
            r0 = r7
            if (r0 >= 0) goto L89
            r0 = 0
            return r0
        L89:
            r0 = r6
            return r0
        L8b:
            r5 = move-exception
            goto L9b
        L8f:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch: java.lang.Throwable -> L8b
            r5 = r0
            r0 = r5
            java.lang.String r1 = "oneElement(ANY)"
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L8b
            r0 = r5
            throw r0     // Catch: java.lang.Throwable -> L8b
        L9b:
            r0 = r4
            monitor-exit(r0)
            r0 = r5
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.Cache.oneElement(org.xbill.DNS.Name, java.lang.Object, int, int):org.xbill.DNS.Cache$Element");
    }

    private void removeElement(Name name, int i7) {
        synchronized (this) {
            V v7 = this.data.get(name);
            if (v7 == 0) {
                return;
            }
            if (v7 instanceof List) {
                List list = (List) v7;
                for (int i8 = 0; i8 < list.size(); i8++) {
                    if (((Element) list.get(i8)).getType() == i7) {
                        list.remove(i8);
                        if (list.size() == 0) {
                            this.data.remove(name);
                        }
                        return;
                    }
                }
            } else if (((Element) v7).getType() == i7) {
                this.data.remove(name);
            }
        }
    }

    private void removeName(Name name) {
        synchronized (this) {
            this.data.remove(name);
        }
    }

    public SetResponse addMessage(Message message) {
        SetResponse setResponse;
        RRset rRset;
        SetResponse setResponse2;
        RRset rRset2;
        RRset rRset3;
        Name name;
        SetResponse setResponse3;
        boolean flag = message.getHeader().getFlag(5);
        Record question = message.getQuestion();
        int rcode = message.getHeader().getRcode();
        boolean check = Options.check("verbosecache");
        if ((rcode != 0 && rcode != 3) || question == null) {
            return null;
        }
        Name name2 = question.getName();
        int type = question.getType();
        int dClass = question.getDClass();
        HashSet hashSet = new HashSet();
        RRset[] sectionRRsets = message.getSectionRRsets(1);
        SetResponse setResponse4 = null;
        Name name3 = name2;
        int i7 = 0;
        boolean z7 = false;
        while (true) {
            setResponse = setResponse4;
            if (i7 >= sectionRRsets.length) {
                break;
            }
            if (sectionRRsets[i7].getDClass() != dClass) {
                name = name3;
                setResponse3 = setResponse4;
            } else {
                int type2 = sectionRRsets[i7].getType();
                Name name4 = sectionRRsets[i7].getName();
                int cred = getCred(1, flag);
                if ((type2 == type || type == 255) && name4.equals(name3)) {
                    addRRset(sectionRRsets[i7], cred);
                    SetResponse setResponse5 = setResponse4;
                    if (name3 == name2) {
                        if (setResponse4 == null) {
                            setResponse4 = new SetResponse(6);
                        }
                        setResponse4.addRRset(sectionRRsets[i7]);
                        setResponse5 = setResponse4;
                    }
                    markAdditional(sectionRRsets[i7], hashSet);
                    z7 = true;
                    setResponse4 = setResponse5;
                } else if (type2 == 5 && name4.equals(name3)) {
                    addRRset(sectionRRsets[i7], cred);
                    if (name3 == name2) {
                        setResponse4 = new SetResponse(4, sectionRRsets[i7]);
                    }
                    name = ((CNAMERecord) sectionRRsets[i7].first()).getTarget();
                    setResponse3 = setResponse4;
                } else {
                    name = name3;
                    setResponse3 = setResponse4;
                    if (type2 == 39) {
                        name = name3;
                        setResponse3 = setResponse4;
                        if (name3.subdomain(name4)) {
                            addRRset(sectionRRsets[i7], cred);
                            if (name3 == name2) {
                                setResponse4 = new SetResponse(5, sectionRRsets[i7]);
                            }
                            try {
                                name3 = name3.fromDNAME((DNAMERecord) sectionRRsets[i7].first());
                            } catch (NameTooLongException e8) {
                                setResponse = setResponse4;
                            }
                        }
                    }
                }
                i7++;
            }
            name3 = name;
            setResponse4 = setResponse3;
            i7++;
        }
        RRset[] sectionRRsets2 = message.getSectionRRsets(2);
        int i8 = 0;
        RRset rRset4 = null;
        RRset rRset5 = null;
        while (true) {
            rRset = rRset5;
            if (i8 >= sectionRRsets2.length) {
                break;
            }
            if (sectionRRsets2[i8].getType() == 6 && name3.subdomain(sectionRRsets2[i8].getName())) {
                rRset3 = sectionRRsets2[i8];
                rRset2 = rRset4;
            } else {
                rRset2 = rRset4;
                rRset3 = rRset;
                if (sectionRRsets2[i8].getType() == 2) {
                    rRset2 = rRset4;
                    rRset3 = rRset;
                    if (name3.subdomain(sectionRRsets2[i8].getName())) {
                        rRset2 = sectionRRsets2[i8];
                        rRset3 = rRset;
                    }
                }
            }
            i8++;
            rRset4 = rRset2;
            rRset5 = rRset3;
        }
        if (z7) {
            setResponse2 = setResponse;
            if (rcode == 0) {
                setResponse2 = setResponse;
                if (rRset4 != null) {
                    addRRset(rRset4, getCred(2, flag));
                    markAdditional(rRset4, hashSet);
                    setResponse2 = setResponse;
                }
            }
        } else {
            int i9 = type;
            if (rcode == 3) {
                i9 = 0;
            }
            if (rcode == 3 || rRset != null || rRset4 == null) {
                addNegative(name3, i9, rRset != null ? (SOARecord) rRset.first() : null, getCred(2, flag));
                setResponse2 = setResponse;
                if (setResponse == null) {
                    setResponse2 = SetResponse.ofType(rcode == 3 ? 1 : 2);
                }
            } else {
                addRRset(rRset4, getCred(2, flag));
                markAdditional(rRset4, hashSet);
                setResponse2 = setResponse;
                if (setResponse == null) {
                    setResponse2 = new SetResponse(3, rRset4);
                }
            }
        }
        RRset[] sectionRRsets3 = message.getSectionRRsets(3);
        for (int i10 = 0; i10 < sectionRRsets3.length; i10++) {
            int type3 = sectionRRsets3[i10].getType();
            if ((type3 == 1 || type3 == 28 || type3 == 38) && hashSet.contains(sectionRRsets3[i10].getName())) {
                addRRset(sectionRRsets3[i10], getCred(3, flag));
            }
        }
        if (check) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("addMessage: ");
            stringBuffer.append(setResponse2);
            printStream.println(stringBuffer.toString());
        }
        return setResponse2;
    }

    public void addNegative(Name name, int i7, SOARecord sOARecord, int i8) {
        synchronized (this) {
            long ttl = sOARecord != null ? sOARecord.getTTL() : 0L;
            Element findElement = findElement(name, i7, 0);
            if (ttl != 0) {
                Element element = findElement;
                if (findElement != null) {
                    element = findElement;
                    if (findElement.compareCredibility(i8) <= 0) {
                        element = null;
                    }
                }
                if (element == null) {
                    addElement(name, new NegativeElement(name, i7, sOARecord, i8, this.maxncache));
                }
            } else if (findElement != null && findElement.compareCredibility(i8) <= 0) {
                removeElement(name, i7);
            }
        }
    }

    public void addRRset(RRset rRset, int i7) {
        synchronized (this) {
            long ttl = rRset.getTTL();
            Name name = rRset.getName();
            int type = rRset.getType();
            Element findElement = findElement(name, type, 0);
            if (ttl != 0) {
                Element element = findElement;
                if (findElement != null) {
                    element = findElement;
                    if (findElement.compareCredibility(i7) <= 0) {
                        element = null;
                    }
                }
                if (element == null) {
                    addElement(name, rRset instanceof CacheRRset ? (CacheRRset) rRset : new CacheRRset(rRset, i7, this.maxcache));
                }
            } else if (findElement != null && findElement.compareCredibility(i7) <= 0) {
                removeElement(name, type);
            }
        }
    }

    public void addRecord(Record record, int i7, Object obj) {
        synchronized (this) {
            Name name = record.getName();
            int rRsetType = record.getRRsetType();
            if (Type.isRR(rRsetType)) {
                Element findElement = findElement(name, rRsetType, i7);
                if (findElement == null) {
                    addRRset(new CacheRRset(record, i7, this.maxcache), i7);
                } else if (findElement.compareCredibility(i7) == 0 && (findElement instanceof CacheRRset)) {
                    ((CacheRRset) findElement).addRR(record);
                }
            }
        }
    }

    public void clearCache() {
        synchronized (this) {
            this.data.clear();
        }
    }

    public RRset[] findAnyRecords(Name name, int i7) {
        return findRecords(name, i7, 2);
    }

    public RRset[] findRecords(Name name, int i7) {
        return findRecords(name, i7, 3);
    }

    public void flushName(Name name) {
        removeName(name);
    }

    public void flushSet(Name name, int i7) {
        removeElement(name, i7);
    }

    public int getDClass() {
        return this.dclass;
    }

    public int getMaxCache() {
        return this.maxcache;
    }

    public int getMaxEntries() {
        return this.data.getMaxSize();
    }

    public int getMaxNCache() {
        return this.maxncache;
    }

    public int getSize() {
        return this.data.size();
    }

    public SetResponse lookup(Name name, int i7, int i8) {
        synchronized (this) {
            int labels = name.labels();
            int i9 = labels;
            while (i9 >= 1) {
                boolean z7 = i9 == 1;
                boolean z8 = i9 == labels;
                Name name2 = z7 ? Name.root : z8 ? name : new Name(name, labels - i9);
                Object obj = this.data.get(name2);
                if (obj != null) {
                    if (z8 && i7 == 255) {
                        SetResponse setResponse = new SetResponse(6);
                        int i10 = 0;
                        for (Element element : allElements(obj)) {
                            if (element.expired()) {
                                removeElement(name2, element.getType());
                            } else if ((element instanceof CacheRRset) && element.compareCredibility(i8) >= 0) {
                                setResponse.addRRset((CacheRRset) element);
                                i10++;
                            }
                        }
                        if (i10 > 0) {
                            return setResponse;
                        }
                    } else if (z8) {
                        Element oneElement = oneElement(name2, obj, i7, i8);
                        if (oneElement != null && (oneElement instanceof CacheRRset)) {
                            SetResponse setResponse2 = new SetResponse(6);
                            setResponse2.addRRset((CacheRRset) oneElement);
                            return setResponse2;
                        }
                        if (oneElement != null) {
                            return new SetResponse(2);
                        }
                        Element oneElement2 = oneElement(name2, obj, 5, i8);
                        if (oneElement2 != null && (oneElement2 instanceof CacheRRset)) {
                            return new SetResponse(4, (CacheRRset) oneElement2);
                        }
                    } else {
                        Element oneElement3 = oneElement(name2, obj, 39, i8);
                        if (oneElement3 != null && (oneElement3 instanceof CacheRRset)) {
                            return new SetResponse(5, (CacheRRset) oneElement3);
                        }
                    }
                    Element oneElement4 = oneElement(name2, obj, 2, i8);
                    if (oneElement4 != null && (oneElement4 instanceof CacheRRset)) {
                        return new SetResponse(3, (CacheRRset) oneElement4);
                    }
                    if (z8 && oneElement(name2, obj, 0, i8) != null) {
                        return SetResponse.ofType(1);
                    }
                }
                i9--;
            }
            return SetResponse.ofType(0);
        }
    }

    public SetResponse lookupRecords(Name name, int i7, int i8) {
        return lookup(name, i7, i8);
    }

    public void setMaxCache(int i7) {
        this.maxcache = i7;
    }

    public void setMaxEntries(int i7) {
        this.data.setMaxSize(i7);
    }

    public void setMaxNCache(int i7) {
        this.maxncache = i7;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        synchronized (this) {
            Iterator it = this.data.values().iterator();
            while (it.hasNext()) {
                for (Element element : allElements(it.next())) {
                    stringBuffer.append(element);
                    stringBuffer.append("\n");
                }
            }
        }
        return stringBuffer.toString();
    }
}
