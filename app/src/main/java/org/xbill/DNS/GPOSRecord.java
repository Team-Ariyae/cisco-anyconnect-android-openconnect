package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/GPOSRecord.class */
public class GPOSRecord extends Record {
    private static final long serialVersionUID = -6349714958085750705L;
    private byte[] altitude;
    private byte[] latitude;
    private byte[] longitude;

    public GPOSRecord() {
    }

    public GPOSRecord(Name name, int i7, long j7, double d8, double d9, double d10) {
        super(name, 27, i7, j7);
        validate(d8, d9);
        this.longitude = Double.toString(d8).getBytes();
        this.latitude = Double.toString(d9).getBytes();
        this.altitude = Double.toString(d10).getBytes();
    }

    public GPOSRecord(Name name, int i7, long j7, String str, String str2, String str3) {
        super(name, 27, i7, j7);
        try {
            this.longitude = Record.byteArrayFromString(str);
            this.latitude = Record.byteArrayFromString(str2);
            validate(getLongitude(), getLatitude());
            this.altitude = Record.byteArrayFromString(str3);
        } catch (TextParseException e8) {
            throw new IllegalArgumentException(e8.getMessage());
        }
    }

    private void validate(double d8, double d9) {
        if (d8 < -90.0d || d8 > 90.0d) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("illegal longitude ");
            stringBuffer.append(d8);
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        if (d9 < -180.0d || d9 > 180.0d) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("illegal latitude ");
            stringBuffer2.append(d9);
            throw new IllegalArgumentException(stringBuffer2.toString());
        }
    }

    public double getAltitude() {
        return Double.parseDouble(getAltitudeString());
    }

    public String getAltitudeString() {
        return Record.byteArrayToString(this.altitude, false);
    }

    public double getLatitude() {
        return Double.parseDouble(getLatitudeString());
    }

    public String getLatitudeString() {
        return Record.byteArrayToString(this.latitude, false);
    }

    public double getLongitude() {
        return Double.parseDouble(getLongitudeString());
    }

    public String getLongitudeString() {
        return Record.byteArrayToString(this.longitude, false);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new GPOSRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        try {
            this.longitude = Record.byteArrayFromString(tokenizer.getString());
            this.latitude = Record.byteArrayFromString(tokenizer.getString());
            this.altitude = Record.byteArrayFromString(tokenizer.getString());
            try {
                validate(getLongitude(), getLatitude());
            } catch (IllegalArgumentException e8) {
                throw new WireParseException(e8.getMessage());
            }
        } catch (TextParseException e9) {
            throw tokenizer.exception(e9.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.longitude = dNSInput.readCountedString();
        this.latitude = dNSInput.readCountedString();
        this.altitude = dNSInput.readCountedString();
        try {
            validate(getLongitude(), getLatitude());
        } catch (IllegalArgumentException e8) {
            throw new WireParseException(e8.getMessage());
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Record.byteArrayToString(this.longitude, true));
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.latitude, true));
        stringBuffer.append(" ");
        stringBuffer.append(Record.byteArrayToString(this.altitude, true));
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeCountedString(this.longitude);
        dNSOutput.writeCountedString(this.latitude);
        dNSOutput.writeCountedString(this.altitude);
    }
}
