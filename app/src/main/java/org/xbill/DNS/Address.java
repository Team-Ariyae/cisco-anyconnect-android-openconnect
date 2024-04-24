package org.xbill.DNS;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Address.class */
public final class Address {
    public static final int IPv4 = 1;
    public static final int IPv6 = 2;

    private Address() {
    }

    private static InetAddress addrFromRecord(String str, Record record) {
        return InetAddress.getByAddress(str, (record instanceof ARecord ? ((ARecord) record).getAddress() : ((AAAARecord) record).getAddress()).getAddress());
    }

    public static int addressLength(int i7) {
        if (i7 == 1) {
            return 4;
        }
        if (i7 == 2) {
            return 16;
        }
        throw new IllegalArgumentException("unknown address family");
    }

    public static int familyOf(InetAddress inetAddress) {
        if (inetAddress instanceof Inet4Address) {
            return 1;
        }
        if (inetAddress instanceof Inet6Address) {
            return 2;
        }
        throw new IllegalArgumentException("unknown address family");
    }

    public static InetAddress[] getAllByName(String str) {
        try {
            return new InetAddress[]{getByAddress(str)};
        } catch (UnknownHostException e8) {
            Record[] lookupHostName = lookupHostName(str, true);
            InetAddress[] inetAddressArr = new InetAddress[lookupHostName.length];
            for (int i7 = 0; i7 < lookupHostName.length; i7++) {
                inetAddressArr[i7] = addrFromRecord(str, lookupHostName[i7]);
            }
            return inetAddressArr;
        }
    }

    public static InetAddress getByAddress(String str) {
        byte[] byteArray = toByteArray(str, 1);
        if (byteArray != null) {
            return InetAddress.getByAddress(str, byteArray);
        }
        byte[] byteArray2 = toByteArray(str, 2);
        if (byteArray2 != null) {
            return InetAddress.getByAddress(str, byteArray2);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Invalid address: ");
        stringBuffer.append(str);
        throw new UnknownHostException(stringBuffer.toString());
    }

    public static InetAddress getByAddress(String str, int i7) {
        if (i7 != 1 && i7 != 2) {
            throw new IllegalArgumentException("unknown address family");
        }
        byte[] byteArray = toByteArray(str, i7);
        if (byteArray != null) {
            return InetAddress.getByAddress(str, byteArray);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Invalid address: ");
        stringBuffer.append(str);
        throw new UnknownHostException(stringBuffer.toString());
    }

    public static InetAddress getByName(String str) {
        try {
            return getByAddress(str);
        } catch (UnknownHostException e8) {
            return addrFromRecord(str, lookupHostName(str, false)[0]);
        }
    }

    public static String getHostName(InetAddress inetAddress) {
        Record[] run = new Lookup(ReverseMap.fromAddress(inetAddress), 12).run();
        if (run != null) {
            return ((PTRRecord) run[0]).getTarget().toString();
        }
        throw new UnknownHostException("unknown address");
    }

    public static boolean isDottedQuad(String str) {
        boolean z7 = true;
        if (toByteArray(str, 1) == null) {
            z7 = false;
        }
        return z7;
    }

    private static Record[] lookupHostName(String str, boolean z7) {
        Record[] run;
        Record[] run2;
        try {
            Lookup lookup = new Lookup(str, 1);
            Record[] run3 = lookup.run();
            if (run3 == null) {
                if (lookup.getResult() != 4 || (run2 = new Lookup(str, 28).run()) == null) {
                    throw new UnknownHostException("unknown host");
                }
                return run2;
            }
            if (z7 && (run = new Lookup(str, 28).run()) != null) {
                Record[] recordArr = new Record[run3.length + run.length];
                System.arraycopy(run3, 0, recordArr, 0, run3.length);
                System.arraycopy(run, 0, recordArr, run3.length, run.length);
                return recordArr;
            }
            return run3;
        } catch (TextParseException e8) {
            throw new UnknownHostException("invalid name");
        }
    }

    private static byte[] parseV4(String str) {
        byte[] bArr = new byte[4];
        int length = str.length();
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = 0; i10 < length; i10++) {
            char charAt = str.charAt(i10);
            if (charAt < '0' || charAt > '9') {
                if (charAt != '.' || i7 == 3 || i8 == 0) {
                    return null;
                }
                bArr[i7] = (byte) i9;
                i7++;
                i8 = 0;
                i9 = 0;
            } else {
                if (i8 == 3) {
                    return null;
                }
                if (i8 > 0 && i9 == 0) {
                    return null;
                }
                i8++;
                i9 = (charAt - '0') + (i9 * 10);
                if (i9 > 255) {
                    return null;
                }
            }
        }
        if (i7 != 3 || i8 == 0) {
            return null;
        }
        bArr[i7] = (byte) i9;
        return bArr;
    }

    private static byte[] parseV6(String str) {
        int i7;
        int i8;
        byte[] byteArray;
        byte[] bArr = new byte[16];
        String[] split = str.split(":", -1);
        int length = split.length - 1;
        if (split[0].length() != 0) {
            i7 = 0;
        } else {
            if (length + 0 <= 0 || split[1].length() != 0) {
                return null;
            }
            i7 = 1;
        }
        int i9 = length;
        if (split[length].length() == 0) {
            if (length - i7 <= 0 || split[length - 1].length() != 0) {
                return null;
            }
            i9 = length - 1;
        }
        if ((i9 - i7) + 1 > 8) {
            return null;
        }
        int i10 = 0;
        int i11 = i7;
        int i12 = -1;
        while (true) {
            i8 = i10;
            if (i11 > i9) {
                break;
            }
            if (split[i11].length() == 0) {
                if (i12 >= 0) {
                    return null;
                }
                i12 = i10;
            } else if (split[i11].indexOf(46) < 0) {
                for (int i13 = 0; i13 < split[i11].length(); i13++) {
                    try {
                        if (Character.digit(split[i11].charAt(i13), 16) < 0) {
                            return null;
                        }
                    } catch (NumberFormatException e8) {
                        return null;
                    }
                }
                int parseInt = Integer.parseInt(split[i11], 16);
                if (parseInt > 65535 || parseInt < 0) {
                    return null;
                }
                int i14 = i10 + 1;
                bArr[i10] = (byte) (parseInt >>> 8);
                i10 = i14 + 1;
                bArr[i14] = (byte) (parseInt & 255);
            } else {
                if (i11 < i9 || i11 > 6 || (byteArray = toByteArray(split[i11], 1)) == null) {
                    return null;
                }
                int i15 = 0;
                while (true) {
                    i8 = i10;
                    if (i15 >= 4) {
                        break;
                    }
                    bArr[i10] = byteArray[i15];
                    i15++;
                    i10++;
                }
            }
            i11++;
        }
        if (i8 < 16 && i12 < 0) {
            return null;
        }
        if (i12 >= 0) {
            int i16 = (16 - i8) + i12;
            System.arraycopy(bArr, i12, bArr, i16, i8 - i12);
            while (i12 < i16) {
                bArr[i12] = 0;
                i12++;
            }
        }
        return bArr;
    }

    public static int[] toArray(String str) {
        return toArray(str, 1);
    }

    public static int[] toArray(String str, int i7) {
        byte[] byteArray = toByteArray(str, i7);
        if (byteArray == null) {
            return null;
        }
        int[] iArr = new int[byteArray.length];
        for (int i8 = 0; i8 < byteArray.length; i8++) {
            iArr[i8] = byteArray[i8] & 255;
        }
        return iArr;
    }

    public static byte[] toByteArray(String str, int i7) {
        if (i7 == 1) {
            return parseV4(str);
        }
        if (i7 == 2) {
            return parseV6(str);
        }
        throw new IllegalArgumentException("unknown address family");
    }

    public static String toDottedQuad(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(bArr[0] & 255);
        stringBuffer.append(".");
        stringBuffer.append(bArr[1] & 255);
        stringBuffer.append(".");
        stringBuffer.append(bArr[2] & 255);
        stringBuffer.append(".");
        stringBuffer.append(bArr[3] & 255);
        return stringBuffer.toString();
    }

    public static String toDottedQuad(int[] iArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(iArr[0]);
        stringBuffer.append(".");
        stringBuffer.append(iArr[1]);
        stringBuffer.append(".");
        stringBuffer.append(iArr[2]);
        stringBuffer.append(".");
        stringBuffer.append(iArr[3]);
        return stringBuffer.toString();
    }

    public static InetAddress truncate(InetAddress inetAddress, int i7) {
        int addressLength = addressLength(familyOf(inetAddress)) * 8;
        if (i7 < 0 || i7 > addressLength) {
            throw new IllegalArgumentException("invalid mask length");
        }
        if (i7 == addressLength) {
            return inetAddress;
        }
        byte[] address = inetAddress.getAddress();
        int i8 = i7 / 8;
        for (int i9 = i8 + 1; i9 < address.length; i9++) {
            address[i9] = 0;
        }
        int i10 = 0;
        for (int i11 = 0; i11 < i7 % 8; i11++) {
            i10 |= 1 << (7 - i11);
        }
        address[i8] = (byte) (address[i8] & i10);
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e8) {
            throw new IllegalArgumentException("invalid address");
        }
    }
}
