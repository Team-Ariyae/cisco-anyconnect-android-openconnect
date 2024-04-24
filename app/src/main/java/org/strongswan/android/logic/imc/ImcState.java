package org.strongswan.android.logic.imc;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/ImcState.class */
public enum ImcState {
    UNKNOWN(0),
    ALLOW(1),
    BLOCK(2),
    ISOLATE(3);

    private final int mValue;

    ImcState(int i7) {
        this.mValue = i7;
    }

    public static ImcState fromValue(int i7) {
        for (ImcState imcState : values()) {
            if (imcState.mValue == i7) {
                return imcState;
            }
        }
        return null;
    }

    public int getValue() {
        return this.mValue;
    }
}
