package org.strongswan.android.security;

import java.security.Provider;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/security/LocalCertificateKeyStoreProvider.class */
public class LocalCertificateKeyStoreProvider extends Provider {
    private static final long serialVersionUID = 3515038332469843219L;

    public LocalCertificateKeyStoreProvider() {
        super("LocalCertificateKeyStoreProvider", 1.0d, "KeyStore provider for local certificates");
        put("KeyStore.LocalCertificateStore", LocalCertificateKeyStoreSpi.class.getName());
    }
}
