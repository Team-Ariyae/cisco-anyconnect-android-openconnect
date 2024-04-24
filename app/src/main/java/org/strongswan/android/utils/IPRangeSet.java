package org.strongswan.android.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/IPRangeSet.class */
public class IPRangeSet implements Iterable<IPRange> {
    private TreeSet<IPRange> mRanges = new TreeSet<>();

    /* renamed from: org.strongswan.android.utils.IPRangeSet$1, reason: invalid class name */
    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/IPRangeSet$1.class */
    public class AnonymousClass1 implements Iterable<IPRange> {
        public final IPRangeSet this$0;

        public AnonymousClass1(IPRangeSet iPRangeSet) {
            this.this$0 = iPRangeSet;
        }

        @Override // java.lang.Iterable
        public Iterator<IPRange> iterator() {
            return new Iterator<IPRange>(this) { // from class: org.strongswan.android.utils.IPRangeSet.1.1
                private Iterator<IPRange> mIterator;
                private List<IPRange> mSubnets;
                public final AnonymousClass1 this$1;

                {
                    this.this$1 = this;
                    this.mIterator = this.this$0.mRanges.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    List<IPRange> list = this.mSubnets;
                    return (list != null && list.size() > 0) || this.mIterator.hasNext();
                }

                @Override // java.util.Iterator
                public IPRange next() {
                    List<IPRange> list = this.mSubnets;
                    if (list == null || list.size() == 0) {
                        this.mSubnets = this.mIterator.next().toSubnets();
                    }
                    return this.mSubnets.remove(0);
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    public static IPRangeSet fromString(String str) {
        IPRangeSet iPRangeSet = new IPRangeSet();
        if (str != null) {
            for (String str2 : str.split("\\s+")) {
                try {
                    iPRangeSet.add(new IPRange(str2));
                } catch (Exception e8) {
                    return null;
                }
            }
        }
        return iPRangeSet;
    }

    public void add(IPRange iPRange) {
        IPRange merge;
        IPRange iPRange2 = iPRange;
        if (this.mRanges.contains(iPRange)) {
            return;
        }
        while (true) {
            Iterator<IPRange> it = this.mRanges.iterator();
            while (it.hasNext()) {
                merge = it.next().merge(iPRange2);
                if (merge != null) {
                    break;
                }
            }
            this.mRanges.add(iPRange2);
            return;
            it.remove();
            iPRange2 = merge;
        }
    }

    public void add(IPRangeSet iPRangeSet) {
        if (iPRangeSet == this) {
            return;
        }
        Iterator<IPRange> it = iPRangeSet.mRanges.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    public void addAll(Collection<? extends IPRange> collection) {
        Iterator<? extends IPRange> it = collection.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    @Override // java.lang.Iterable
    public Iterator<IPRange> iterator() {
        return this.mRanges.iterator();
    }

    public void remove(IPRange iPRange) {
        ArrayList arrayList = new ArrayList();
        Iterator<IPRange> it = this.mRanges.iterator();
        while (it.hasNext()) {
            IPRange next = it.next();
            List<IPRange> remove = next.remove(iPRange);
            if (remove.size() == 0) {
                it.remove();
            } else if (!remove.get(0).equals(next)) {
                it.remove();
                arrayList.addAll(remove);
            }
        }
        this.mRanges.addAll(arrayList);
    }

    public void remove(IPRangeSet iPRangeSet) {
        if (iPRangeSet == this) {
            this.mRanges.clear();
            return;
        }
        Iterator<IPRange> it = iPRangeSet.mRanges.iterator();
        while (it.hasNext()) {
            remove(it.next());
        }
    }

    public int size() {
        return this.mRanges.size();
    }

    public Iterable<IPRange> subnets() {
        return new AnonymousClass1(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<IPRange> it = this.mRanges.iterator();
        while (it.hasNext()) {
            IPRange next = it.next();
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(next.toString());
        }
        return sb.toString();
    }
}
