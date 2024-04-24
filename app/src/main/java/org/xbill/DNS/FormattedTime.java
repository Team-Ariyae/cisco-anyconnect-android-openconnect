package org.xbill.DNS;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/FormattedTime.class */
final class FormattedTime {

    /* renamed from: w2, reason: collision with root package name */
    private static NumberFormat f5261w2;

    /* renamed from: w4, reason: collision with root package name */
    private static NumberFormat f5262w4;

    static {
        DecimalFormat decimalFormat = new DecimalFormat();
        f5261w2 = decimalFormat;
        decimalFormat.setMinimumIntegerDigits(2);
        DecimalFormat decimalFormat2 = new DecimalFormat();
        f5262w4 = decimalFormat2;
        decimalFormat2.setMinimumIntegerDigits(4);
        f5262w4.setGroupingUsed(false);
    }

    private FormattedTime() {
    }

    public static String format(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        StringBuffer stringBuffer = new StringBuffer();
        gregorianCalendar.setTime(date);
        stringBuffer.append(f5262w4.format(gregorianCalendar.get(1)));
        stringBuffer.append(f5261w2.format(gregorianCalendar.get(2) + 1));
        stringBuffer.append(f5261w2.format(gregorianCalendar.get(5)));
        stringBuffer.append(f5261w2.format(gregorianCalendar.get(11)));
        stringBuffer.append(f5261w2.format(gregorianCalendar.get(12)));
        stringBuffer.append(f5261w2.format(gregorianCalendar.get(13)));
        return stringBuffer.toString();
    }

    public static Date parse(String str) {
        if (str.length() != 14) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Invalid time encoding: ");
            stringBuffer.append(str);
            throw new TextParseException(stringBuffer.toString());
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        gregorianCalendar.clear();
        try {
            gregorianCalendar.set(Integer.parseInt(str.substring(0, 4)), Integer.parseInt(str.substring(4, 6)) - 1, Integer.parseInt(str.substring(6, 8)), Integer.parseInt(str.substring(8, 10)), Integer.parseInt(str.substring(10, 12)), Integer.parseInt(str.substring(12, 14)));
            return gregorianCalendar.getTime();
        } catch (NumberFormatException e8) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Invalid time encoding: ");
            stringBuffer2.append(str);
            throw new TextParseException(stringBuffer2.toString());
        }
    }
}
