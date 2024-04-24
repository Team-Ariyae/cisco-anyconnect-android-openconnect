package org.xbill.DNS;

import java.util.ArrayList;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SetResponse.class */
public class SetResponse {
    public static final int CNAME = 4;
    public static final int DELEGATION = 3;
    public static final int DNAME = 5;
    public static final int NXDOMAIN = 1;
    public static final int NXRRSET = 2;
    public static final int SUCCESSFUL = 6;
    public static final int UNKNOWN = 0;
    private Object data;
    private int type;
    private static final SetResponse unknown = new SetResponse(0);
    private static final SetResponse nxdomain = new SetResponse(1);
    private static final SetResponse nxrrset = new SetResponse(2);

    private SetResponse() {
    }

    public SetResponse(int i7) {
        if (i7 < 0 || i7 > 6) {
            throw new IllegalArgumentException("invalid type");
        }
        this.type = i7;
        this.data = null;
    }

    public SetResponse(int i7, RRset rRset) {
        if (i7 < 0 || i7 > 6) {
            throw new IllegalArgumentException("invalid type");
        }
        this.type = i7;
        this.data = rRset;
    }

    public static SetResponse ofType(int i7) {
        switch (i7) {
            case 0:
                return unknown;
            case 1:
                return nxdomain;
            case 2:
                return nxrrset;
            case 3:
            case 4:
            case 5:
            case 6:
                SetResponse setResponse = new SetResponse();
                setResponse.type = i7;
                setResponse.data = null;
                return setResponse;
            default:
                throw new IllegalArgumentException("invalid type");
        }
    }

    public void addRRset(RRset rRset) {
        if (this.data == null) {
            this.data = new ArrayList();
        }
        ((List) this.data).add(rRset);
    }

    public RRset[] answers() {
        if (this.type != 6) {
            return null;
        }
        List list = (List) this.data;
        return (RRset[]) list.toArray(new RRset[list.size()]);
    }

    public CNAMERecord getCNAME() {
        return (CNAMERecord) ((RRset) this.data).first();
    }

    public DNAMERecord getDNAME() {
        return (DNAMERecord) ((RRset) this.data).first();
    }

    public RRset getNS() {
        return (RRset) this.data;
    }

    public boolean isCNAME() {
        return this.type == 4;
    }

    public boolean isDNAME() {
        return this.type == 5;
    }

    public boolean isDelegation() {
        return this.type == 3;
    }

    public boolean isNXDOMAIN() {
        boolean z7 = true;
        if (this.type != 1) {
            z7 = false;
        }
        return z7;
    }

    public boolean isNXRRSET() {
        return this.type == 2;
    }

    public boolean isSuccessful() {
        return this.type == 6;
    }

    public boolean isUnknown() {
        return this.type == 0;
    }

    public String toString() {
        StringBuffer stringBuffer;
        String str;
        switch (this.type) {
            case 0:
                return "unknown";
            case 1:
                return "NXDOMAIN";
            case 2:
                return "NXRRSET";
            case 3:
                stringBuffer = new StringBuffer();
                str = "delegation: ";
                break;
            case 4:
                stringBuffer = new StringBuffer();
                str = "CNAME: ";
                break;
            case 5:
                stringBuffer = new StringBuffer();
                str = "DNAME: ";
                break;
            case 6:
                return "successful";
            default:
                throw new IllegalStateException();
        }
        stringBuffer.append(str);
        stringBuffer.append(this.data);
        return stringBuffer.toString();
    }
}
