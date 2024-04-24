package org.strongswan.android.data;

import java.util.EnumSet;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'IKEV2_EAP' uses external variables
	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnType.class */
public final class VpnType {
    private static final VpnType[] $VALUES;
    public static final VpnType IKEV2_BYOD_EAP;
    public static final VpnType IKEV2_CERT;
    public static final VpnType IKEV2_CERT_EAP;
    public static final VpnType IKEV2_EAP;
    public static final VpnType IKEV2_EAP_TLS;
    private EnumSet<VpnTypeFeature> mFeatures;
    private String mIdentifier;

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnType$VpnTypeFeature.class */
    public enum VpnTypeFeature {
        CERTIFICATE,
        USER_PASS,
        BYOD
    }

    static {
        VpnTypeFeature vpnTypeFeature = VpnTypeFeature.USER_PASS;
        VpnType vpnType = new VpnType("IKEV2_EAP", 0, "ikev2-eap", EnumSet.of(vpnTypeFeature));
        IKEV2_EAP = vpnType;
        VpnTypeFeature vpnTypeFeature2 = VpnTypeFeature.CERTIFICATE;
        VpnType vpnType2 = new VpnType("IKEV2_CERT", 1, "ikev2-cert", EnumSet.of(vpnTypeFeature2));
        IKEV2_CERT = vpnType2;
        VpnType vpnType3 = new VpnType("IKEV2_CERT_EAP", 2, "ikev2-cert-eap", EnumSet.of(vpnTypeFeature, vpnTypeFeature2));
        IKEV2_CERT_EAP = vpnType3;
        VpnType vpnType4 = new VpnType("IKEV2_EAP_TLS", 3, "ikev2-eap-tls", EnumSet.of(vpnTypeFeature2));
        IKEV2_EAP_TLS = vpnType4;
        VpnType vpnType5 = new VpnType("IKEV2_BYOD_EAP", 4, "ikev2-byod-eap", EnumSet.of(vpnTypeFeature, VpnTypeFeature.BYOD));
        IKEV2_BYOD_EAP = vpnType5;
        $VALUES = new VpnType[]{vpnType, vpnType2, vpnType3, vpnType4, vpnType5};
    }

    private VpnType(String str, int i7, String str2, EnumSet enumSet) {
        this.mIdentifier = str2;
        this.mFeatures = enumSet;
    }

    public static VpnType fromIdentifier(String str) {
        for (VpnType vpnType : values()) {
            if (str.equals(vpnType.mIdentifier)) {
                return vpnType;
            }
        }
        return IKEV2_EAP;
    }

    public static VpnType valueOf(String str) {
        return (VpnType) Enum.valueOf(VpnType.class, str);
    }

    public static VpnType[] values() {
        return (VpnType[]) $VALUES.clone();
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public boolean has(VpnTypeFeature vpnTypeFeature) {
        return this.mFeatures.contains(vpnTypeFeature);
    }
}
