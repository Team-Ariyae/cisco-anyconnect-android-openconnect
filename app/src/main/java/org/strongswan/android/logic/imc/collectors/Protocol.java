package org.strongswan.android.logic.imc.collectors;

import com.v2ray.ang.dto.V2rayConfig;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/Protocol.class */
public enum Protocol {
    TCP((byte) 6, V2rayConfig.DEFAULT_NETWORK, "tcp6"),
    UDP((byte) 17, "udp", "udp6");

    private String[] mNames;
    private final byte mValue;

    Protocol(byte b8, String... strArr) {
        this.mValue = b8;
        this.mNames = strArr;
    }

    public static Protocol fromName(String str) {
        for (Protocol protocol : values()) {
            for (String str2 : protocol.mNames) {
                if (str2.equalsIgnoreCase(str)) {
                    return protocol;
                }
            }
        }
        return null;
    }

    public byte getValue() {
        return this.mValue;
    }
}
