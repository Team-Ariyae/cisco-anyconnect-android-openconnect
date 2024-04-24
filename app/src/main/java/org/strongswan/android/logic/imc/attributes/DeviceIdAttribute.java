package org.strongswan.android.logic.imc.attributes;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/DeviceIdAttribute.class */
public class DeviceIdAttribute implements Attribute {
    private String mDeviceId;

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        return this.mDeviceId.getBytes();
    }

    public void setDeviceId(String str) {
        this.mDeviceId = str;
    }
}
