package org.strongswan.android.logic.imc.attributes;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/PrivateEnterpriseNumber.class */
public enum PrivateEnterpriseNumber {
    IETF(0),
    GOOGLE(11129),
    ITA(36906),
    UNASSIGNED(16777214),
    RESERVED(16777215);

    private int mValue;

    PrivateEnterpriseNumber(int i7) {
        this.mValue = i7;
    }

    public static PrivateEnterpriseNumber fromValue(int i7) {
        for (PrivateEnterpriseNumber privateEnterpriseNumber : values()) {
            if (privateEnterpriseNumber.mValue == i7) {
                return privateEnterpriseNumber;
            }
        }
        return null;
    }

    public int getValue() {
        return this.mValue;
    }
}
