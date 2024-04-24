package org.strongswan.android.logic.imc.collectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.PortFilterAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/PortFilterCollector.class */
public class PortFilterCollector implements Collector {
    private static Pattern LISTEN = Pattern.compile("\\bLISTEN\\b");
    private static Pattern PROTOCOL = Pattern.compile("\\b(tcp|udp)6?\\b");
    private static Pattern PORT = Pattern.compile("[:]{1,3}(\\d{1,5})\\b(?!\\.)");

    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        PortFilterAttribute portFilterAttribute;
        Protocol fromName;
        try {
            try {
                Process exec = Runtime.getRuntime().exec("netstat -n");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                    portFilterAttribute = new PortFilterAttribute();
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            if (LISTEN.matcher(readLine).find()) {
                                Matcher matcher = PROTOCOL.matcher(readLine);
                                Matcher matcher2 = PORT.matcher(readLine);
                                if (matcher.find() && matcher2.find() && (fromName = Protocol.fromName(matcher.group())) != null) {
                                    portFilterAttribute.addPort(fromName, (short) Integer.parseInt(matcher2.group(1)));
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            exec.destroy();
                            PortFilterAttribute portFilterAttribute2 = portFilterAttribute;
                            throw th;
                        }
                    }
                    exec.destroy();
                } catch (Throwable th2) {
                    th = th2;
                    portFilterAttribute = null;
                }
            } catch (IOException e8) {
                e = e8;
                e.printStackTrace();
                portFilterAttribute = null;
                return portFilterAttribute;
            }
        } catch (IOException e9) {
            e = e9;
            e.printStackTrace();
            portFilterAttribute = null;
            return portFilterAttribute;
        }
        return portFilterAttribute;
    }
}
