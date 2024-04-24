package app.openconnect.api;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/APIVpnProfile.class */
public class APIVpnProfile implements Parcelable {
    public static final Parcelable.Creator<APIVpnProfile> CREATOR = new Parcelable.Creator<APIVpnProfile>() { // from class: app.openconnect.api.APIVpnProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public APIVpnProfile createFromParcel(Parcel parcel) {
            return new APIVpnProfile(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public APIVpnProfile[] newArray(int i7) {
            return new APIVpnProfile[i7];
        }
    };
    public final String mName;
    public final String mUUID;
    public final boolean mUserEditable;

    public APIVpnProfile(Parcel parcel) {
        this.mUUID = parcel.readString();
        this.mName = parcel.readString();
        this.mUserEditable = parcel.readInt() != 0;
    }

    public APIVpnProfile(String str, String str2, boolean z7) {
        this.mUUID = str;
        this.mName = str2;
        this.mUserEditable = z7;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i7) {
        parcel.writeString(this.mUUID);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mUserEditable ? 0 : 1);
    }
}
