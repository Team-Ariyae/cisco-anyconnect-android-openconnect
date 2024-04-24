package org.xbill.DNS.spi;

import java.lang.reflect.Proxy;
import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/spi/DNSJavaNameServiceDescriptor.class */
public class DNSJavaNameServiceDescriptor implements NameServiceDescriptor {
    public static Class class$sun$net$spi$nameservice$NameService;
    private static NameService nameService;

    static {
        Class cls = class$sun$net$spi$nameservice$NameService;
        Class cls2 = cls;
        if (cls == null) {
            cls2 = class$("sun.net.spi.nameservice.NameService");
            class$sun$net$spi$nameservice$NameService = cls2;
        }
        ClassLoader classLoader = cls2.getClassLoader();
        Class cls3 = class$sun$net$spi$nameservice$NameService;
        Class cls4 = cls3;
        if (cls3 == null) {
            cls4 = class$("sun.net.spi.nameservice.NameService");
            class$sun$net$spi$nameservice$NameService = cls4;
        }
        nameService = (NameService) Proxy.newProxyInstance(classLoader, new Class[]{cls4}, new DNSJavaNameService());
    }

    public static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e8) {
            throw new NoClassDefFoundError().initCause(e8);
        }
    }

    public NameService createNameService() {
        return nameService;
    }

    public String getProviderName() {
        return "dnsjava";
    }

    public String getType() {
        return "dns";
    }
}
