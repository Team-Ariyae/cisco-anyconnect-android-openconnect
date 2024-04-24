package org.xbill.DNS;

import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.strongswan.android.data.VpnProfileDataSource;
import org.xbill.DNS.KEYRecord;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ResolverConfig.class */
public class ResolverConfig {
    public static Class class$java$lang$String;
    public static Class class$org$xbill$DNS$ResolverConfig;
    private static ResolverConfig currentConfig;
    private String[] servers = null;
    private Name[] searchlist = null;
    private int ndots = -1;

    static {
        refresh();
    }

    public ResolverConfig() {
        if (findProperty() || findSunJVM()) {
            return;
        }
        if (this.servers == null || this.searchlist == null) {
            String property = System.getProperty("os.name");
            String property2 = System.getProperty("java.vendor");
            if (property.indexOf("Windows") != -1) {
                if (property.indexOf("95") == -1 && property.indexOf("98") == -1 && property.indexOf("ME") == -1) {
                    findNT();
                    return;
                } else {
                    find95();
                    return;
                }
            }
            if (property.indexOf("NetWare") != -1) {
                findNetware();
            } else if (property2.indexOf("Android") != -1) {
                findAndroid();
            } else {
                findUnix();
            }
        }
    }

    private void addSearch(String str, List list) {
        if (Options.check("verbose")) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("adding search ");
            stringBuffer.append(str);
            printStream.println(stringBuffer.toString());
        }
        try {
            Name fromString = Name.fromString(str, Name.root);
            if (list.contains(fromString)) {
                return;
            }
            list.add(fromString);
        } catch (TextParseException e8) {
        }
    }

    private void addServer(String str, List list) {
        if (list.contains(str)) {
            return;
        }
        if (Options.check("verbose")) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("adding server ");
            stringBuffer.append(str);
            printStream.println(stringBuffer.toString());
        }
        list.add(str);
    }

    public static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e8) {
            throw new NoClassDefFoundError().initCause(e8);
        }
    }

    private void configureFromLists(List list, List list2) {
        if (this.servers == null && list.size() > 0) {
            this.servers = (String[]) list.toArray(new String[0]);
        }
        if (this.searchlist != null || list2.size() <= 0) {
            return;
        }
        this.searchlist = (Name[]) list2.toArray(new Name[0]);
    }

    private void configureNdots(int i7) {
        if (this.ndots >= 0 || i7 <= 0) {
            return;
        }
        this.ndots = i7;
    }

    private void find95() {
        try {
            Runtime.getRuntime().exec("winipcfg /all /batch winipcfg.out").waitFor();
            findWin(new FileInputStream(new File("winipcfg.out")));
            new File("winipcfg.out").delete();
        } catch (Exception e8) {
        }
    }

    private void findAndroid() {
        ArrayList arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class<?> cls2 = class$java$lang$String;
            Class<?> cls3 = cls2;
            if (cls2 == null) {
                cls3 = class$("java.lang.String");
                class$java$lang$String = cls3;
            }
            Method method = cls.getMethod("get", cls3);
            for (int i7 = 0; i7 < 4; i7++) {
                String str = (String) method.invoke(null, new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4"}[i7]);
                if (str != null && ((str.matches("^\\d+(\\.\\d+){3}$") || str.matches("^[0-9a-f]+(:[0-9a-f]*)+:[0-9a-f]+$")) && !arrayList.contains(str))) {
                    arrayList.add(str);
                }
            }
        } catch (Exception e8) {
        }
        configureFromLists(arrayList, arrayList2);
    }

    private void findNT() {
        try {
            Process exec = Runtime.getRuntime().exec("ipconfig /all");
            findWin(exec.getInputStream());
            exec.destroy();
        } catch (Exception e8) {
        }
    }

    private void findNetware() {
        findResolvConf("sys:/etc/resolv.cfg");
    }

    private boolean findProperty() {
        ArrayList arrayList = new ArrayList(0);
        ArrayList arrayList2 = new ArrayList(0);
        String property = System.getProperty("dns.server");
        if (property != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            while (stringTokenizer.hasMoreTokens()) {
                addServer(stringTokenizer.nextToken(), arrayList);
            }
        }
        String property2 = System.getProperty("dns.search");
        if (property2 != null) {
            StringTokenizer stringTokenizer2 = new StringTokenizer(property2, ",");
            while (stringTokenizer2.hasMoreTokens()) {
                addSearch(stringTokenizer2.nextToken(), arrayList2);
            }
        }
        configureFromLists(arrayList, arrayList2);
        boolean z7 = false;
        if (this.servers != null) {
            z7 = false;
            if (this.searchlist != null) {
                z7 = true;
            }
        }
        return z7;
    }

    private void findResolvConf(String str) {
        int i7;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str)));
            List arrayList = new ArrayList(0);
            ArrayList arrayList2 = new ArrayList(0);
            int i8 = -1;
            while (true) {
                i7 = i8;
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    if (readLine.startsWith("nameserver")) {
                        int i9 = i8;
                        int i10 = i8;
                        StringTokenizer stringTokenizer = new StringTokenizer(readLine);
                        int i11 = i8;
                        stringTokenizer.nextToken();
                        int i12 = i8;
                        addServer(stringTokenizer.nextToken(), arrayList);
                    } else if (readLine.startsWith("domain")) {
                        int i13 = i8;
                        int i14 = i8;
                        StringTokenizer stringTokenizer2 = new StringTokenizer(readLine);
                        int i15 = i8;
                        stringTokenizer2.nextToken();
                        int i16 = i8;
                        if (stringTokenizer2.hasMoreTokens() && arrayList2.isEmpty()) {
                            int i17 = i8;
                            addSearch(stringTokenizer2.nextToken(), arrayList2);
                        }
                    } else if (readLine.startsWith("search")) {
                        int i18 = i8;
                        if (!arrayList2.isEmpty()) {
                            int i19 = i8;
                            arrayList2.clear();
                        }
                        int i20 = i8;
                        int i21 = i8;
                        StringTokenizer stringTokenizer3 = new StringTokenizer(readLine);
                        int i22 = i8;
                        stringTokenizer3.nextToken();
                        while (true) {
                            int i23 = i8;
                            if (stringTokenizer3.hasMoreTokens()) {
                                int i24 = i8;
                                addSearch(stringTokenizer3.nextToken(), arrayList2);
                            }
                        }
                    } else if (readLine.startsWith("options")) {
                        int i25 = i8;
                        int i26 = i8;
                        StringTokenizer stringTokenizer4 = new StringTokenizer(readLine);
                        int i27 = i8;
                        stringTokenizer4.nextToken();
                        int i28 = i8;
                        while (true) {
                            i8 = i28;
                            if (stringTokenizer4.hasMoreTokens()) {
                                int i29 = i28;
                                String nextToken = stringTokenizer4.nextToken();
                                int i30 = i28;
                                if (nextToken.startsWith("ndots:")) {
                                    int i31 = i28;
                                    i28 = parseNdots(nextToken);
                                }
                            }
                        }
                    }
                } catch (IOException e8) {
                    i8 = i7;
                }
            }
            i7 = i8;
            bufferedReader.close();
            configureFromLists(arrayList, arrayList2);
            configureNdots(i8);
        } catch (FileNotFoundException e9) {
        }
    }

    private boolean findSunJVM() {
        ArrayList arrayList = new ArrayList(0);
        ArrayList arrayList2 = new ArrayList(0);
        try {
            Class<?>[] clsArr = new Class[0];
            Object[] objArr = new Object[0];
            Class<?> cls = Class.forName("sun.net.dns.ResolverConfiguration");
            Object invoke = cls.getDeclaredMethod("open", clsArr).invoke(null, objArr);
            List list = (List) cls.getMethod("nameservers", clsArr).invoke(invoke, objArr);
            List list2 = (List) cls.getMethod("searchlist", clsArr).invoke(invoke, objArr);
            if (list.size() == 0) {
                return false;
            }
            if (list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    addServer((String) it.next(), arrayList);
                }
            }
            if (list2.size() > 0) {
                Iterator it2 = list2.iterator();
                while (it2.hasNext()) {
                    addSearch((String) it2.next(), arrayList2);
                }
            }
            configureFromLists(arrayList, arrayList2);
            return true;
        } catch (Exception e8) {
            return false;
        }
    }

    private void findUnix() {
        findResolvConf("/etc/resolv.conf");
    }

    private void findWin(InputStream inputStream) {
        int intValue = Integer.getInteger("org.xbill.DNS.windows.parse.buffer", KEYRecord.Flags.FLAG2).intValue();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, intValue);
        bufferedInputStream.mark(intValue);
        findWin(bufferedInputStream, null);
        if (this.servers == null) {
            try {
                bufferedInputStream.reset();
                findWin(bufferedInputStream, new Locale(BuildConfig.FLAVOR, BuildConfig.FLAVOR));
            } catch (IOException e8) {
            }
        }
    }

    private void findWin(InputStream inputStream, Locale locale) {
        String str;
        boolean z7;
        boolean z8;
        Class cls = class$org$xbill$DNS$ResolverConfig;
        Class cls2 = cls;
        if (cls == null) {
            cls2 = class$("org.xbill.DNS.ResolverConfig");
            class$org$xbill$DNS$ResolverConfig = cls2;
        }
        String name = cls2.getPackage().getName();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(name);
        stringBuffer.append(".windows.DNSServer");
        String stringBuffer2 = stringBuffer.toString();
        ResourceBundle bundle = locale != null ? ResourceBundle.getBundle(stringBuffer2, locale) : ResourceBundle.getBundle(stringBuffer2);
        String string = bundle.getString("host_name");
        String string2 = bundle.getString("primary_dns_suffix");
        String string3 = bundle.getString("dns_suffix");
        String string4 = bundle.getString(VpnProfileDataSource.KEY_DNS_SERVERS);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            while (true) {
                boolean z9 = false;
                boolean z10 = false;
                while (true) {
                    boolean z11 = z10;
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        configureFromLists(arrayList, arrayList2);
                        return;
                    }
                    StringTokenizer stringTokenizer = new StringTokenizer(readLine);
                    if (!stringTokenizer.hasMoreTokens()) {
                        break;
                    }
                    String nextToken = stringTokenizer.nextToken();
                    if (readLine.indexOf(":") != -1) {
                        z9 = false;
                        z11 = false;
                    }
                    if (readLine.indexOf(string) != -1) {
                        while (stringTokenizer.hasMoreTokens()) {
                            nextToken = stringTokenizer.nextToken();
                        }
                        try {
                            if (Name.fromString(nextToken, null).labels() == 1) {
                                z8 = z9;
                                z7 = z11;
                            } else {
                                addSearch(nextToken, arrayList2);
                                z8 = z9;
                                z7 = z11;
                            }
                        } catch (TextParseException e8) {
                            z8 = z9;
                            z7 = z11;
                        }
                    } else {
                        if (readLine.indexOf(string2) != -1) {
                            while (stringTokenizer.hasMoreTokens()) {
                                nextToken = stringTokenizer.nextToken();
                            }
                            str = nextToken;
                            if (nextToken.equals(":")) {
                                z8 = z9;
                                z7 = z11;
                            }
                        } else {
                            String str2 = nextToken;
                            if (!z9) {
                                if (readLine.indexOf(string3) != -1) {
                                    str2 = nextToken;
                                } else {
                                    String str3 = nextToken;
                                    if (!z11) {
                                        z8 = z9;
                                        z7 = z11;
                                        if (readLine.indexOf(string4) != -1) {
                                            str3 = nextToken;
                                        }
                                    }
                                    while (stringTokenizer.hasMoreTokens()) {
                                        str3 = stringTokenizer.nextToken();
                                    }
                                    if (str3.equals(":")) {
                                        z8 = z9;
                                        z7 = z11;
                                    } else {
                                        addServer(str3, arrayList);
                                        z7 = true;
                                        z8 = z9;
                                    }
                                }
                            }
                            while (stringTokenizer.hasMoreTokens()) {
                                str2 = stringTokenizer.nextToken();
                            }
                            str = str2;
                            if (str2.equals(":")) {
                                z7 = z11;
                                z8 = z9;
                            }
                        }
                        addSearch(str, arrayList2);
                        z8 = true;
                        z7 = z11;
                    }
                    z9 = z8;
                    z10 = z7;
                }
            }
        } catch (IOException e9) {
        }
    }

    public static ResolverConfig getCurrentConfig() {
        ResolverConfig resolverConfig;
        synchronized (ResolverConfig.class) {
            try {
                resolverConfig = currentConfig;
            } catch (Throwable th) {
                throw th;
            }
        }
        return resolverConfig;
    }

    private int parseNdots(String str) {
        String substring = str.substring(6);
        try {
            int parseInt = Integer.parseInt(substring);
            if (parseInt < 0) {
                return -1;
            }
            if (Options.check("verbose")) {
                PrintStream printStream = System.out;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("setting ndots ");
                stringBuffer.append(substring);
                printStream.println(stringBuffer.toString());
            }
            return parseInt;
        } catch (NumberFormatException e8) {
            return -1;
        }
    }

    public static void refresh() {
        ResolverConfig resolverConfig = new ResolverConfig();
        Class cls = class$org$xbill$DNS$ResolverConfig;
        Class cls2 = cls;
        if (cls == null) {
            cls2 = class$("org.xbill.DNS.ResolverConfig");
            class$org$xbill$DNS$ResolverConfig = cls2;
        }
        synchronized (cls2) {
            try {
                currentConfig = resolverConfig;
            } catch (Throwable th) {
                Class cls3 = cls2;
                throw th;
            }
        }
    }

    public int ndots() {
        int i7 = this.ndots;
        int i8 = i7;
        if (i7 < 0) {
            i8 = 1;
        }
        return i8;
    }

    public Name[] searchPath() {
        return this.searchlist;
    }

    public String server() {
        String[] strArr = this.servers;
        if (strArr == null) {
            return null;
        }
        return strArr[0];
    }

    public String[] servers() {
        return this.servers;
    }
}
