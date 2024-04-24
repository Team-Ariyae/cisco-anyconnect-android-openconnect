package app.openconnect.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNStatusCallback.class */
public interface IOpenVPNStatusCallback extends IInterface {

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNStatusCallback$Default.class */
    public static class Default implements IOpenVPNStatusCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // app.openconnect.api.IOpenVPNStatusCallback
        public void newStatus(String str, String str2, String str3, String str4) {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNStatusCallback$Stub.class */
    public static abstract class Stub extends Binder implements IOpenVPNStatusCallback {
        private static final String DESCRIPTOR = "app.openconnect.api.IOpenVPNStatusCallback";
        public static final int TRANSACTION_newStatus = 1;

        /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNStatusCallback$Stub$Proxy.class */
        public static class Proxy implements IOpenVPNStatusCallback {
            public static IOpenVPNStatusCallback sDefaultImpl;
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // app.openconnect.api.IOpenVPNStatusCallback
            public void newStatus(String str, String str2, String str3, String str4) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().newStatus(str, str2, str3, str4);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOpenVPNStatusCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IOpenVPNStatusCallback)) ? new Proxy(iBinder) : (IOpenVPNStatusCallback) queryLocalInterface;
        }

        public static IOpenVPNStatusCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iOpenVPNStatusCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iOpenVPNStatusCallback;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i7, Parcel parcel, Parcel parcel2, int i8) {
            if (i7 == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                newStatus(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                return true;
            }
            if (i7 != 1598968902) {
                return super.onTransact(i7, parcel, parcel2, i8);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void newStatus(String str, String str2, String str3, String str4);
}
