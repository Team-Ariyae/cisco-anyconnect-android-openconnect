package org.xbill.DNS.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.TextParseException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/spi/DNSJavaNameService.class */
public class DNSJavaNameService implements InvocationHandler {
    public static Class array$$B;
    public static Class array$Ljava$net$InetAddress;
    private static final String domainProperty = "sun.net.spi.nameservice.domain";
    private static final String nsProperty = "sun.net.spi.nameservice.nameservers";
    private static final String v6Property = "java.net.preferIPv6Addresses";
    private boolean preferV6;

    public DNSJavaNameService() {
        this.preferV6 = false;
        String property = System.getProperty(nsProperty);
        String property2 = System.getProperty(domainProperty);
        String property3 = System.getProperty(v6Property);
        if (property != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            String[] strArr = new String[stringTokenizer.countTokens()];
            int i7 = 0;
            while (stringTokenizer.hasMoreTokens()) {
                strArr[i7] = stringTokenizer.nextToken();
                i7++;
            }
            try {
                Lookup.setDefaultResolver(new ExtendedResolver(strArr));
            } catch (UnknownHostException e8) {
                System.err.println("DNSJavaNameService: invalid sun.net.spi.nameservice.nameservers");
            }
        }
        if (property2 != null) {
            try {
                Lookup.setDefaultSearchPath(new String[]{property2});
            } catch (TextParseException e9) {
                System.err.println("DNSJavaNameService: invalid sun.net.spi.nameservice.domain");
            }
        }
        if (property3 == null || !property3.equalsIgnoreCase("true")) {
            return;
        }
        this.preferV6 = true;
    }

    public static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e8) {
            throw new NoClassDefFoundError().initCause(e8);
        }
    }

    public String getHostByAddr(byte[] bArr) {
        Record[] run = new Lookup(ReverseMap.fromAddress(InetAddress.getByAddress(bArr)), 12).run();
        if (run != null) {
            return ((PTRRecord) run[0]).getTarget().toString();
        }
        throw new UnknownHostException();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) {
        try {
            if (method.getName().equals("getHostByAddr")) {
                return getHostByAddr((byte[]) objArr[0]);
            }
            if (method.getName().equals("lookupAllHostAddr")) {
                InetAddress[] lookupAllHostAddr = lookupAllHostAddr((String) objArr[0]);
                Class<?> returnType = method.getReturnType();
                Class cls = array$Ljava$net$InetAddress;
                Class cls2 = cls;
                if (cls == null) {
                    cls2 = class$("[Ljava.net.InetAddress;");
                    array$Ljava$net$InetAddress = cls2;
                }
                if (returnType.equals(cls2)) {
                    return lookupAllHostAddr;
                }
                Class cls3 = array$$B;
                Class cls4 = cls3;
                if (cls3 == null) {
                    cls4 = class$("[[B");
                    array$$B = cls4;
                }
                if (returnType.equals(cls4)) {
                    int length = lookupAllHostAddr.length;
                    byte[] bArr = new byte[length];
                    for (int i7 = 0; i7 < length; i7++) {
                        bArr[i7] = lookupAllHostAddr[i7].getAddress();
                    }
                    return bArr;
                }
            }
            throw new IllegalArgumentException("Unknown function name or arguments.");
        } catch (Throwable th) {
            System.err.println("DNSJavaNameService: Unexpected error.");
            th.printStackTrace();
            throw th;
        }
    }

    public InetAddress[] lookupAllHostAddr(String str) {
        try {
            Name name = new Name(str);
            Record[] run = this.preferV6 ? new Lookup(name, 28).run() : null;
            Record[] recordArr = run;
            if (run == null) {
                recordArr = new Lookup(name, 1).run();
            }
            Record[] recordArr2 = recordArr;
            if (recordArr == null) {
                recordArr2 = recordArr;
                if (!this.preferV6) {
                    recordArr2 = new Lookup(name, 28).run();
                }
            }
            if (recordArr2 == null) {
                throw new UnknownHostException(str);
            }
            InetAddress[] inetAddressArr = new InetAddress[recordArr2.length];
            for (int i7 = 0; i7 < recordArr2.length; i7++) {
                Record record = recordArr2[i7];
                if (record instanceof ARecord) {
                    inetAddressArr[i7] = ((ARecord) record).getAddress();
                } else {
                    inetAddressArr[i7] = ((AAAARecord) record).getAddress();
                }
            }
            return inetAddressArr;
        } catch (TextParseException e8) {
            throw new UnknownHostException(str);
        }
    }
}
