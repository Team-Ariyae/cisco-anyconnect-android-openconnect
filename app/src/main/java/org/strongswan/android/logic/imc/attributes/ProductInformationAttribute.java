package org.strongswan.android.logic.imc.attributes;

import org.strongswan.android.utils.BufferedByteWriter;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/ProductInformationAttribute.class */
public class ProductInformationAttribute implements Attribute {
    private final String PRODUCT_NAME = "Android";
    private final short PRODUCT_ID = 0;

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        BufferedByteWriter bufferedByteWriter = new BufferedByteWriter();
        bufferedByteWriter.put24(PrivateEnterpriseNumber.GOOGLE.getValue());
        bufferedByteWriter.put16((short) 0);
        bufferedByteWriter.put("Android".getBytes());
        return bufferedByteWriter.toByteArray();
    }
}
