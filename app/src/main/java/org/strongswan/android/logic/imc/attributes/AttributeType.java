package org.strongswan.android.logic.imc.attributes;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'IETF_TESTING' uses external variables
	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/AttributeType.class */
public final class AttributeType {
    private static final AttributeType[] $VALUES;
    public static final AttributeType IETF_ASSESSMENT_RESULT;
    public static final AttributeType IETF_ATTRIBUTE_REQUEST;
    public static final AttributeType IETF_FACTORY_DEFAULT_PWD_ENABLED;
    public static final AttributeType IETF_FORWARDING_ENABLED;
    public static final AttributeType IETF_INSTALLED_PACKAGES;
    public static final AttributeType IETF_NUMERIC_VERSION;
    public static final AttributeType IETF_OPERATIONAL_STATUS;
    public static final AttributeType IETF_PA_TNC_ERROR;
    public static final AttributeType IETF_PORT_FILTER;
    public static final AttributeType IETF_PRODUCT_INFORMATION;
    public static final AttributeType IETF_REMEDIATION_INSTRUCTIONS;
    public static final AttributeType IETF_RESERVED;
    public static final AttributeType IETF_STRING_VERSION;
    public static final AttributeType IETF_TESTING;
    public static final AttributeType ITA_DEVICE_ID;
    public static final AttributeType ITA_SETTINGS;
    private int mType;
    private PrivateEnterpriseNumber mVendor;

    static {
        PrivateEnterpriseNumber privateEnterpriseNumber = PrivateEnterpriseNumber.IETF;
        AttributeType attributeType = new AttributeType("IETF_TESTING", 0, privateEnterpriseNumber, 0);
        IETF_TESTING = attributeType;
        AttributeType attributeType2 = new AttributeType("IETF_ATTRIBUTE_REQUEST", 1, privateEnterpriseNumber, 1);
        IETF_ATTRIBUTE_REQUEST = attributeType2;
        AttributeType attributeType3 = new AttributeType("IETF_PRODUCT_INFORMATION", 2, privateEnterpriseNumber, 2);
        IETF_PRODUCT_INFORMATION = attributeType3;
        AttributeType attributeType4 = new AttributeType("IETF_NUMERIC_VERSION", 3, privateEnterpriseNumber, 3);
        IETF_NUMERIC_VERSION = attributeType4;
        AttributeType attributeType5 = new AttributeType("IETF_STRING_VERSION", 4, privateEnterpriseNumber, 4);
        IETF_STRING_VERSION = attributeType5;
        AttributeType attributeType6 = new AttributeType("IETF_OPERATIONAL_STATUS", 5, privateEnterpriseNumber, 5);
        IETF_OPERATIONAL_STATUS = attributeType6;
        AttributeType attributeType7 = new AttributeType("IETF_PORT_FILTER", 6, privateEnterpriseNumber, 6);
        IETF_PORT_FILTER = attributeType7;
        AttributeType attributeType8 = new AttributeType("IETF_INSTALLED_PACKAGES", 7, privateEnterpriseNumber, 7);
        IETF_INSTALLED_PACKAGES = attributeType8;
        AttributeType attributeType9 = new AttributeType("IETF_PA_TNC_ERROR", 8, privateEnterpriseNumber, 8);
        IETF_PA_TNC_ERROR = attributeType9;
        AttributeType attributeType10 = new AttributeType("IETF_ASSESSMENT_RESULT", 9, privateEnterpriseNumber, 9);
        IETF_ASSESSMENT_RESULT = attributeType10;
        AttributeType attributeType11 = new AttributeType("IETF_REMEDIATION_INSTRUCTIONS", 10, privateEnterpriseNumber, 10);
        IETF_REMEDIATION_INSTRUCTIONS = attributeType11;
        AttributeType attributeType12 = new AttributeType("IETF_FORWARDING_ENABLED", 11, privateEnterpriseNumber, 11);
        IETF_FORWARDING_ENABLED = attributeType12;
        AttributeType attributeType13 = new AttributeType("IETF_FACTORY_DEFAULT_PWD_ENABLED", 12, privateEnterpriseNumber, 12);
        IETF_FACTORY_DEFAULT_PWD_ENABLED = attributeType13;
        AttributeType attributeType14 = new AttributeType("IETF_RESERVED", 13, privateEnterpriseNumber, -1);
        IETF_RESERVED = attributeType14;
        PrivateEnterpriseNumber privateEnterpriseNumber2 = PrivateEnterpriseNumber.ITA;
        AttributeType attributeType15 = new AttributeType("ITA_SETTINGS", 14, privateEnterpriseNumber2, 4);
        ITA_SETTINGS = attributeType15;
        AttributeType attributeType16 = new AttributeType("ITA_DEVICE_ID", 15, privateEnterpriseNumber2, 8);
        ITA_DEVICE_ID = attributeType16;
        $VALUES = new AttributeType[]{attributeType, attributeType2, attributeType3, attributeType4, attributeType5, attributeType6, attributeType7, attributeType8, attributeType9, attributeType10, attributeType11, attributeType12, attributeType13, attributeType14, attributeType15, attributeType16};
    }

    private AttributeType(String str, int i7, PrivateEnterpriseNumber privateEnterpriseNumber, int i8) {
        this.mVendor = privateEnterpriseNumber;
        this.mType = i8;
    }

    public static AttributeType fromValues(int i7, int i8) {
        PrivateEnterpriseNumber fromValue = PrivateEnterpriseNumber.fromValue(i7);
        if (fromValue == null) {
            return null;
        }
        for (AttributeType attributeType : values()) {
            if (attributeType.mVendor == fromValue && attributeType.mType == i8) {
                return attributeType;
            }
        }
        return null;
    }

    public static AttributeType valueOf(String str) {
        return (AttributeType) Enum.valueOf(AttributeType.class, str);
    }

    public static AttributeType[] values() {
        return (AttributeType[]) $VALUES.clone();
    }

    public int getType() {
        return this.mType;
    }

    public PrivateEnterpriseNumber getVendor() {
        return this.mVendor;
    }
}
