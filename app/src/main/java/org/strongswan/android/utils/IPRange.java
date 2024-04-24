package org.strongswan.android.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/IPRange.class */
public class IPRange implements Comparable<IPRange> {
    private final byte[] mBitmask;
    private byte[] mFrom;
    private Integer mPrefix;
    private byte[] mTo;

    public IPRange(String str) {
        this.mBitmask = new byte[]{Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1};
        if (!str.matches("(?i)^(([0-9.]+)|([0-9a-f:]+))(-(([0-9.]+)|([0-9a-f:]+))|(/\\d+))?$")) {
            throw new IllegalArgumentException("Invalid CIDR or range notation");
        }
        if (str.contains("-")) {
            String[] split = str.split("-");
            initializeFromRange(InetAddress.getByName(split[0]), InetAddress.getByName(split[1]));
        } else {
            String[] split2 = str.split("/");
            byte[] address = InetAddress.getByName(split2[0]).getAddress();
            initializeFromCIDR(address, split2.length > 1 ? Integer.parseInt(split2[1]) : address.length * 8);
        }
    }

    public IPRange(String str, int i7) {
        this(Utils.parseInetAddress(str), i7);
    }

    public IPRange(String str, String str2) {
        this(Utils.parseInetAddress(str), Utils.parseInetAddress(str2));
    }

    public IPRange(InetAddress inetAddress, int i7) {
        this(inetAddress.getAddress(), i7);
    }

    public IPRange(InetAddress inetAddress, InetAddress inetAddress2) {
        this.mBitmask = new byte[]{Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1};
        initializeFromRange(inetAddress, inetAddress2);
    }

    private IPRange(byte[] bArr, int i7) {
        this.mBitmask = new byte[]{Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1};
        initializeFromCIDR(bArr, i7);
    }

    private IPRange(byte[] bArr, byte[] bArr2) {
        this.mBitmask = new byte[]{Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1};
        this.mFrom = bArr;
        this.mTo = bArr2;
        determinePrefix();
    }

    private boolean adjacent(IPRange iPRange) {
        boolean z7 = true;
        if (compareAddr(this.mTo, iPRange.mFrom) < 0) {
            return compareAddr(inc((byte[]) this.mTo.clone()), iPRange.mFrom) == 0;
        }
        if (compareAddr(dec((byte[]) this.mFrom.clone()), iPRange.mTo) != 0) {
            z7 = false;
        }
        return z7;
    }

    private int compareAddr(byte[] bArr, byte[] bArr2) {
        if (bArr.length != bArr2.length) {
            return bArr.length >= bArr2.length ? 1 : -1;
        }
        for (int i7 = 0; i7 < bArr.length; i7++) {
            byte b8 = bArr[i7];
            byte b9 = bArr2[i7];
            if (b8 != b9) {
                return (b8 & 255) < (b9 & 255) ? -1 : 1;
            }
        }
        return 0;
    }

    private byte[] dec(byte[] bArr) {
        for (int length = bArr.length - 1; length >= 0; length--) {
            byte b8 = (byte) (bArr[length] - 1);
            bArr[length] = b8;
            if (b8 != -1) {
                break;
            }
        }
        return bArr;
    }

    private void determinePrefix() {
        this.mPrefix = Integer.valueOf(this.mFrom.length * 8);
        boolean z7 = true;
        for (int i7 = 0; i7 < this.mFrom.length; i7++) {
            for (int i8 = 0; i8 < 8; i8++) {
                byte[] bArr = this.mFrom;
                if (z7) {
                    byte b8 = bArr[i7];
                    byte b9 = this.mBitmask[i8];
                    if ((b8 & b9) != (b9 & this.mTo[i7])) {
                        this.mPrefix = Integer.valueOf((i7 * 8) + i8);
                        z7 = false;
                    }
                } else {
                    byte b10 = bArr[i7];
                    byte b11 = this.mBitmask[i8];
                    if ((b10 & b11) != 0 || (this.mTo[i7] & b11) == 0) {
                        this.mPrefix = null;
                        return;
                    }
                }
            }
        }
    }

    private byte[] inc(byte[] bArr) {
        for (int length = bArr.length - 1; length >= 0; length--) {
            byte b8 = (byte) (bArr[length] + 1);
            bArr[length] = b8;
            if (b8 != 0) {
                break;
            }
        }
        return bArr;
    }

    private void initializeFromCIDR(byte[] bArr, int i7) {
        if (bArr.length != 4 && bArr.length != 16) {
            throw new IllegalArgumentException("Invalid address");
        }
        if (i7 < 0 || i7 > bArr.length * 8) {
            throw new IllegalArgumentException("Invalid prefix");
        }
        byte[] bArr2 = (byte[]) bArr.clone();
        byte b8 = (byte) (255 << (8 - (i7 % 8)));
        int i8 = i7 / 8;
        if (i8 < bArr.length) {
            bArr[i8] = (byte) (bArr[i8] & b8);
            bArr2[i8] = (byte) ((b8 ^ (-1)) | bArr2[i8]);
            int i9 = i8 + 1;
            Arrays.fill(bArr, i9, bArr.length, (byte) 0);
            Arrays.fill(bArr2, i9, bArr2.length, (byte) -1);
        }
        this.mFrom = bArr;
        this.mTo = bArr2;
        this.mPrefix = Integer.valueOf(i7);
    }

    private void initializeFromRange(InetAddress inetAddress, InetAddress inetAddress2) {
        byte[] address = inetAddress.getAddress();
        byte[] address2 = inetAddress2.getAddress();
        if (address.length != address2.length) {
            throw new IllegalArgumentException("Invalid range");
        }
        if (compareAddr(address, address2) < 0) {
            this.mFrom = address;
            this.mTo = address2;
        } else {
            this.mTo = address;
            this.mFrom = address2;
        }
        determinePrefix();
    }

    @Override // java.lang.Comparable
    public int compareTo(IPRange iPRange) {
        int compareAddr = compareAddr(this.mFrom, iPRange.mFrom);
        int i7 = compareAddr;
        if (compareAddr == 0) {
            i7 = compareAddr(this.mTo, iPRange.mTo);
        }
        return i7;
    }

    public boolean contains(IPRange iPRange) {
        return compareAddr(this.mFrom, iPRange.mFrom) <= 0 && compareAddr(iPRange.mTo, this.mTo) <= 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0023, code lost:
    
        if (compareTo2((org.strongswan.android.utils.IPRange) r4) == 0) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(java.lang.Object r4) {
        /*
            r3 = this;
            r0 = 0
            r6 = r0
            r0 = r6
            r5 = r0
            r0 = r4
            if (r0 == 0) goto L28
            r0 = r4
            boolean r0 = r0 instanceof org.strongswan.android.utils.IPRange
            if (r0 != 0) goto L14
            r0 = r6
            r5 = r0
            goto L28
        L14:
            r0 = r3
            r1 = r4
            if (r0 == r1) goto L26
            r0 = r6
            r5 = r0
            r0 = r3
            r1 = r4
            org.strongswan.android.utils.IPRange r1 = (org.strongswan.android.utils.IPRange) r1
            int r0 = r0.compareTo(r1)
            if (r0 != 0) goto L28
        L26:
            r0 = 1
            r5 = r0
        L28:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.utils.IPRange.equals(java.lang.Object):boolean");
    }

    public InetAddress getFrom() {
        try {
            return InetAddress.getByAddress(this.mFrom);
        } catch (UnknownHostException e8) {
            return null;
        }
    }

    public Integer getPrefix() {
        return this.mPrefix;
    }

    public InetAddress getTo() {
        try {
            return InetAddress.getByAddress(this.mTo);
        } catch (UnknownHostException e8) {
            return null;
        }
    }

    public IPRange merge(IPRange iPRange) {
        if (overlaps(iPRange)) {
            if (contains(iPRange)) {
                return this;
            }
            if (iPRange.contains(this)) {
                return iPRange;
            }
        } else if (!adjacent(iPRange)) {
            return null;
        }
        return new IPRange(compareAddr(this.mFrom, iPRange.mFrom) < 0 ? this.mFrom : iPRange.mFrom, compareAddr(this.mTo, iPRange.mTo) > 0 ? this.mTo : iPRange.mTo);
    }

    public boolean overlaps(IPRange iPRange) {
        return compareAddr(this.mTo, iPRange.mFrom) >= 0 && compareAddr(iPRange.mTo, this.mFrom) >= 0;
    }

    public List<IPRange> remove(IPRange iPRange) {
        ArrayList arrayList = new ArrayList();
        if (!overlaps(iPRange)) {
            arrayList.add(this);
        } else if (!iPRange.contains(this)) {
            if (compareAddr(this.mFrom, iPRange.mFrom) >= 0 || compareAddr(iPRange.mTo, this.mTo) >= 0) {
                arrayList.add(new IPRange(compareAddr(this.mFrom, iPRange.mFrom) < 0 ? this.mFrom : inc((byte[]) iPRange.mTo.clone()), compareAddr(this.mTo, iPRange.mTo) > 0 ? this.mTo : dec((byte[]) iPRange.mFrom.clone())));
            } else {
                arrayList.add(new IPRange(this.mFrom, dec((byte[]) iPRange.mFrom.clone())));
                arrayList.add(new IPRange(inc((byte[]) iPRange.mTo.clone()), this.mTo));
            }
        }
        return arrayList;
    }

    public String toString() {
        try {
            if (this.mPrefix != null) {
                return InetAddress.getByAddress(this.mFrom).getHostAddress() + "/" + this.mPrefix;
            }
            return InetAddress.getByAddress(this.mFrom).getHostAddress() + "-" + InetAddress.getByAddress(this.mTo).getHostAddress();
        } catch (UnknownHostException e8) {
            return super.toString();
        }
    }

    public List<IPRange> toSubnets() {
        int i7;
        boolean z7;
        boolean z8;
        boolean z9;
        IPRange iPRange = this;
        ArrayList arrayList = new ArrayList();
        if (iPRange.mPrefix == null) {
            byte[] bArr = (byte[]) iPRange.mFrom.clone();
            byte[] bArr2 = (byte[]) iPRange.mTo.clone();
            int i8 = 0;
            loop0: while (true) {
                i7 = 0;
                while (i8 < bArr.length) {
                    byte b8 = bArr[i8];
                    byte b9 = iPRange.mBitmask[i7];
                    if ((b8 & b9) != (b9 & bArr2[i8])) {
                        break loop0;
                    }
                    int i9 = i7 + 1;
                    i7 = i9;
                    if (i9 == 8) {
                        break;
                    }
                }
                i8++;
            }
            int i10 = (i8 * 8) + i7;
            int i11 = i7 + 1;
            int i12 = i8;
            int i13 = i11;
            if (i11 == 8) {
                i12 = i8 + 1;
                i13 = 0;
            }
            int length = bArr.length * 8;
            int length2 = bArr.length - 1;
            boolean z10 = true;
            boolean z11 = true;
            boolean z12 = false;
            boolean z13 = true;
            int i14 = i12;
            while (length2 >= i14) {
                int i15 = length2 == i14 ? i13 : 0;
                boolean z14 = z12;
                int i16 = 7;
                boolean z15 = z13;
                boolean z16 = z11;
                boolean z17 = z10;
                while (i16 >= i15) {
                    byte b10 = this.mBitmask[i16];
                    byte b11 = bArr[length2];
                    int i17 = b11 & b10;
                    if (!z14 && i17 != 0) {
                        arrayList.add(new IPRange((byte[]) bArr.clone(), length));
                        z7 = i17 == true ? 1 : 0;
                        z8 = false;
                    } else if (z14 && i17 == 0) {
                        bArr[length2] = (byte) (b11 ^ b10);
                        arrayList.add(new IPRange((byte[]) bArr.clone(), length));
                        z7 = true;
                        z8 = z17;
                    } else {
                        z7 = i17 == true ? 1 : 0;
                        z8 = z17;
                    }
                    byte b12 = bArr[length2];
                    int i18 = b10 ^ (-1);
                    bArr[length2] = (byte) (b12 & i18);
                    byte b13 = bArr2[length2];
                    int i19 = b13 & b10;
                    if (z15 && i19 == 0) {
                        arrayList.add(new IPRange((byte[]) bArr2.clone(), length));
                        z9 = i19 == true ? 1 : 0;
                        z16 = false;
                    } else if (z15 || i19 == 0) {
                        z9 = i19 == true ? 1 : 0;
                    } else {
                        bArr2[length2] = (byte) (b13 ^ b10);
                        arrayList.add(new IPRange((byte[]) bArr2.clone(), length));
                        z9 = false;
                    }
                    bArr2[length2] = (byte) (bArr2[length2] & i18);
                    length--;
                    i16--;
                    z14 = z7;
                    z15 = z9;
                    z16 = z16;
                    z17 = z8;
                }
                length2--;
                z12 = z14;
                z13 = z15;
                z11 = z16;
                z10 = z17;
            }
            if (z10 && z11) {
                iPRange = new IPRange((byte[]) bArr.clone(), i10);
            } else {
                if (!z10) {
                    if (z11) {
                        iPRange = new IPRange((byte[]) bArr2.clone(), i10 + 1);
                    }
                    Collections.sort(arrayList);
                    return arrayList;
                }
                iPRange = new IPRange((byte[]) bArr.clone(), i10 + 1);
            }
        }
        arrayList.add(iPRange);
        Collections.sort(arrayList);
        return arrayList;
    }
}
