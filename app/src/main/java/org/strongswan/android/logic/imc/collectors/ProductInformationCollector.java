package org.strongswan.android.logic.imc.collectors;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.ProductInformationAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/ProductInformationCollector.class */
public class ProductInformationCollector implements Collector {
    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        return new ProductInformationAttribute();
    }
}
