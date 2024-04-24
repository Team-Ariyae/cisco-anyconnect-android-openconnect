package org.strongswan.android.logic.imc.collectors;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.InstalledPackagesAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/InstalledPackagesCollector.class */
public class InstalledPackagesCollector implements Collector {
    private final PackageManager mPackageManager;

    public InstalledPackagesCollector(Context context) {
        this.mPackageManager = context.getPackageManager();
    }

    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        String str;
        String str2;
        InstalledPackagesAttribute installedPackagesAttribute = new InstalledPackagesAttribute();
        for (PackageInfo packageInfo : this.mPackageManager.getInstalledPackages(0)) {
            if ((packageInfo.applicationInfo.flags & 1) == 0 && (str = packageInfo.packageName) != null && (str2 = packageInfo.versionName) != null) {
                installedPackagesAttribute.addPackage(str, str2);
            }
        }
        return installedPackagesAttribute;
    }
}
