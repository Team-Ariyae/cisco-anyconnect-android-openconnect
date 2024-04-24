package org.strongswan.android.logic.imc.attributes;

import org.strongswan.android.utils.BufferedByteWriter;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/StringVersionAttribute.class */
public class StringVersionAttribute implements Attribute {
    private String mBuildNumber;
    private String mVersionNumber;

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        BufferedByteWriter bufferedByteWriter = new BufferedByteWriter();
        bufferedByteWriter.putLen8(this.mVersionNumber.getBytes());
        bufferedByteWriter.putLen8(this.mBuildNumber.getBytes());
        bufferedByteWriter.put((byte) 0);
        return bufferedByteWriter.toByteArray();
    }

    public void setInternalBuildNumber(String str) {
        this.mBuildNumber = str;
    }

    public void setProductVersionNumber(String str) {
        this.mVersionNumber = str;
    }
}
