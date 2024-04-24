package app.openconnect.core;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/NativeUtils.class */
public class NativeUtils {
    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("opvpnutil");
    }

    public static native void jniclose(int i7);

    public static native byte[] rsasign(byte[] bArr, int i7);
}
