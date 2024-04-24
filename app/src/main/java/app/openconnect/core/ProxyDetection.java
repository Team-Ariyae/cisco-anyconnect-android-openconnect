package app.openconnect.core;

import app.openconnect.VpnProfile;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/ProxyDetection.class */
public class ProxyDetection {
    public static SocketAddress detectProxy(VpnProfile vpnProfile) {
        try {
            Proxy firstProxy = getFirstProxy(new URL("http://localhost"));
            if (firstProxy == null) {
                return null;
            }
            SocketAddress address = firstProxy.address();
            if (address instanceof InetSocketAddress) {
                return address;
            }
            return null;
        } catch (MalformedURLException e8) {
            OpenVPN.logError(2131755330, e8.getLocalizedMessage());
            return null;
        } catch (URISyntaxException e9) {
            OpenVPN.logError(2131755330, e9.getLocalizedMessage());
            return null;
        }
    }

    public static Proxy getFirstProxy(URL url) {
        System.setProperty("java.net.useSystemProxies", "true");
        List<Proxy> select = ProxySelector.getDefault().select(url.toURI());
        if (select == null) {
            return null;
        }
        for (Proxy proxy : select) {
            if (proxy.address() != null) {
                return proxy;
            }
        }
        return null;
    }
}
