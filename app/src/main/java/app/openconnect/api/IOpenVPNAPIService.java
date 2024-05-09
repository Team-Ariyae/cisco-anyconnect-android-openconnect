package app.openconnect.api;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import app.openconnect.api.IOpenVPNStatusCallback;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNAPIService.class */
public interface IOpenVPNAPIService extends IInterface {

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNAPIService$Default.class */
    public static class Default implements IOpenVPNAPIService {
        @Override // app.openconnect.api.IOpenVPNAPIService
        public boolean addVPNProfile(String str, String str2) {
            return false;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void disconnect() {
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public List<APIVpnProfile> getProfiles() {
            return null;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public Intent prepare(String str) {
            return null;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public Intent prepareVPNService() {
            return null;
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void registerStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void startProfile(String str) {
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void startVPN(String str) {
        }

        @Override // app.openconnect.api.IOpenVPNAPIService
        public void unregisterStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNAPIService$Stub.class */
    public static abstract class Stub extends Binder implements IOpenVPNAPIService {
        private static final String DESCRIPTOR = "app.openconnect.api.IOpenVPNAPIService";
        public static final int TRANSACTION_addVPNProfile = 3;
        public static final int TRANSACTION_disconnect = 7;
        public static final int TRANSACTION_getProfiles = 1;
        public static final int TRANSACTION_prepare = 5;
        public static final int TRANSACTION_prepareVPNService = 6;
        public static final int TRANSACTION_registerStatusCallback = 8;
        public static final int TRANSACTION_startProfile = 2;
        public static final int TRANSACTION_startVPN = 4;
        public static final int TRANSACTION_unregisterStatusCallback = 9;

        /* loaded from: TehShop-dex2jar.jar:app/openconnect/api/IOpenVPNAPIService$Stub$Proxy.class */
        public static class Proxy implements IOpenVPNAPIService {
            public static IOpenVPNAPIService sDefaultImpl;
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public boolean addVPNProfile(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    boolean z7 = false;
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().addVPNProfile(str, str2);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z7 = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z7;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public void disconnect() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().disconnect();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public List<APIVpnProfile> getProfiles() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                            return Stub.getDefaultImpl().getProfiles();
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    obtain2.readException();
                    return obtain2.createTypedArrayList(APIVpnProfile.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public Intent prepare(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    try {
                        if (!this.mRemote.transact(5, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                            return Stub.getDefaultImpl().prepare(str);
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    obtain2.readException();
                    Intent intent = obtain2.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return intent;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public Intent prepareVPNService() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        if (!this.mRemote.transact(6, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                            return Stub.getDefaultImpl().prepareVPNService();
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    obtain2.readException();
                    Intent intent = obtain2.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return intent;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public void registerStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iOpenVPNStatusCallback != null ? iOpenVPNStatusCallback.asBinder() : null);
                    try {
                        if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                        } else {
                            Stub.getDefaultImpl().registerStatusCallback(iOpenVPNStatusCallback);
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public void startProfile(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    try {
                        if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                        } else {
                            Stub.getDefaultImpl().startProfile(str);
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public void startVPN(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startVPN(str);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // app.openconnect.api.IOpenVPNAPIService
            public void unregisterStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iOpenVPNStatusCallback != null ? iOpenVPNStatusCallback.asBinder() : null);
                    try {
                        if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                        } else {
                            Stub.getDefaultImpl().unregisterStatusCallback(iOpenVPNStatusCallback);
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOpenVPNAPIService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IOpenVPNAPIService)) ? new Proxy(iBinder) : (IOpenVPNAPIService) queryLocalInterface;
        }

        public static IOpenVPNAPIService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IOpenVPNAPIService iOpenVPNAPIService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iOpenVPNAPIService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iOpenVPNAPIService;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i7, Parcel parcel, Parcel parcel2, int i8) {
            if (i7 == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i7) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<APIVpnProfile> profiles = getProfiles();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(profiles);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    startProfile(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean addVPNProfile = addVPNProfile(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(addVPNProfile ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    startVPN(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    Intent prepare = prepare(parcel.readString());
                    parcel2.writeNoException();
                    if (prepare == null) {
                        parcel2.writeInt(0);
                        return true;
                    }
                    parcel2.writeInt(1);
                    prepare.writeToParcel(parcel2, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    Intent prepareVPNService = prepareVPNService();
                    parcel2.writeNoException();
                    if (prepareVPNService == null) {
                        parcel2.writeInt(0);
                        return true;
                    }
                    parcel2.writeInt(1);
                    prepareVPNService.writeToParcel(parcel2, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    disconnect();
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerStatusCallback(IOpenVPNStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterStatusCallback(IOpenVPNStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                default:
                    try {
                        return super.onTransact(i7, parcel, parcel2, i8);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    boolean addVPNProfile(String str, String str2);

    void disconnect();

    List<APIVpnProfile> getProfiles();

    Intent prepare(String str);

    Intent prepareVPNService();

    void registerStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback);

    void startProfile(String str);

    void startVPN(String str);

    void unregisterStatusCallback(IOpenVPNStatusCallback iOpenVPNStatusCallback);
}
