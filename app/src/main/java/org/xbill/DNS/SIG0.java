package org.xbill.DNS;

import com.v2ray.ang.extension._ExtKt;
import java.security.PrivateKey;
import java.util.Date;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SIG0.class */
public class SIG0 {
    private static final short VALIDITY = 300;

    private SIG0() {
    }

    public static void signMessage(Message message, KEYRecord kEYRecord, PrivateKey privateKey, SIGRecord sIGRecord) {
        int intValue = Options.intValue("sig0validity");
        int i7 = intValue;
        if (intValue < 0) {
            i7 = 300;
        }
        long currentTimeMillis = System.currentTimeMillis();
        message.addRecord(DNSSEC.signMessage(message, sIGRecord, kEYRecord, privateKey, new Date(currentTimeMillis), new Date(currentTimeMillis + (i7 * _ExtKt.threshold))), 3);
    }

    public static void verifyMessage(Message message, byte[] bArr, KEYRecord kEYRecord, SIGRecord sIGRecord) {
        SIGRecord sIGRecord2;
        Record[] sectionArray = message.getSectionArray(3);
        int i7 = 0;
        while (true) {
            if (i7 >= sectionArray.length) {
                sIGRecord2 = null;
                break;
            } else {
                if (sectionArray[i7].getType() == 24 && ((SIGRecord) sectionArray[i7]).getTypeCovered() == 0) {
                    sIGRecord2 = (SIGRecord) sectionArray[i7];
                    break;
                }
                i7++;
            }
        }
        DNSSEC.verifyMessage(message, bArr, sIGRecord2, sIGRecord, kEYRecord);
    }
}
