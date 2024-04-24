package org.strongswan.android.logic.imc;

import android.content.Context;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.AttributeType;
import org.strongswan.android.logic.imc.collectors.Collector;
import org.strongswan.android.logic.imc.collectors.DeviceIdCollector;
import org.strongswan.android.logic.imc.collectors.InstalledPackagesCollector;
import org.strongswan.android.logic.imc.collectors.PortFilterCollector;
import org.strongswan.android.logic.imc.collectors.ProductInformationCollector;
import org.strongswan.android.logic.imc.collectors.SettingsCollector;
import org.strongswan.android.logic.imc.collectors.StringVersionCollector;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/AndroidImc.class */
public class AndroidImc {
    private final Context mContext;

    /* renamed from: org.strongswan.android.logic.imc.AndroidImc$1, reason: invalid class name */
    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/AndroidImc$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        public static final int[] $SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType;

        /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:36:0x005d
            	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
            */
        static {
            /*
                org.strongswan.android.logic.imc.attributes.AttributeType[] r0 = org.strongswan.android.logic.imc.attributes.AttributeType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                r4 = r0
                r0 = r4
                org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType = r0
                r0 = r4
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.IETF_PRODUCT_INFORMATION     // Catch: java.lang.NoSuchFieldError -> L4d
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L4d
                r2 = 1
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L4d
            L14:
                int[] r0 = org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType     // Catch: java.lang.NoSuchFieldError -> L4d java.lang.NoSuchFieldError -> L51
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.IETF_STRING_VERSION     // Catch: java.lang.NoSuchFieldError -> L51
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L51
                r2 = 2
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L51
            L1f:
                int[] r0 = org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType     // Catch: java.lang.NoSuchFieldError -> L51 java.lang.NoSuchFieldError -> L55
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.IETF_PORT_FILTER     // Catch: java.lang.NoSuchFieldError -> L55
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L55
                r2 = 3
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L55
            L2a:
                int[] r0 = org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType     // Catch: java.lang.NoSuchFieldError -> L55 java.lang.NoSuchFieldError -> L59
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.IETF_INSTALLED_PACKAGES     // Catch: java.lang.NoSuchFieldError -> L59
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L59
                r2 = 4
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L59
            L35:
                int[] r0 = org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType     // Catch: java.lang.NoSuchFieldError -> L59 java.lang.NoSuchFieldError -> L5d
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.ITA_SETTINGS     // Catch: java.lang.NoSuchFieldError -> L5d
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L5d
                r2 = 5
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L5d
            L40:
                int[] r0 = org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType     // Catch: java.lang.NoSuchFieldError -> L5d java.lang.NoSuchFieldError -> L61
                org.strongswan.android.logic.imc.attributes.AttributeType r1 = org.strongswan.android.logic.imc.attributes.AttributeType.ITA_DEVICE_ID     // Catch: java.lang.NoSuchFieldError -> L61
                int r1 = r1.ordinal()     // Catch: java.lang.NoSuchFieldError -> L61
                r2 = 6
                r0[r1] = r2     // Catch: java.lang.NoSuchFieldError -> L61
            L4c:
                return
            L4d:
                r4 = move-exception
                goto L14
            L51:
                r4 = move-exception
                goto L1f
            L55:
                r4 = move-exception
                goto L2a
            L59:
                r4 = move-exception
                goto L35
            L5d:
                r4 = move-exception
                goto L40
            L61:
                r4 = move-exception
                goto L4c
            */
            throw new UnsupportedOperationException("Method not decompiled: org.strongswan.android.logic.imc.AndroidImc.AnonymousClass1.m689clinit():void");
        }
    }

    public AndroidImc(Context context) {
        this.mContext = context;
    }

    public byte[] getMeasurement(int i7, int i8) {
        return getMeasurement(i7, i8, null);
    }

    public byte[] getMeasurement(int i7, int i8, String[] strArr) {
        Collector productInformationCollector;
        Attribute measurement;
        switch (AnonymousClass1.$SwitchMap$org$strongswan$android$logic$imc$attributes$AttributeType[AttributeType.fromValues(i7, i8).ordinal()]) {
            case 1:
                productInformationCollector = new ProductInformationCollector();
                break;
            case 2:
                productInformationCollector = new StringVersionCollector();
                break;
            case 3:
                productInformationCollector = new PortFilterCollector();
                break;
            case 4:
                productInformationCollector = new InstalledPackagesCollector(this.mContext);
                break;
            case 5:
                productInformationCollector = new SettingsCollector(this.mContext, strArr);
                break;
            case 6:
                productInformationCollector = new DeviceIdCollector(this.mContext);
                break;
            default:
                productInformationCollector = null;
                break;
        }
        if (productInformationCollector == null || (measurement = productInformationCollector.getMeasurement()) == null) {
            return null;
        }
        return measurement.getEncoding();
    }
}
