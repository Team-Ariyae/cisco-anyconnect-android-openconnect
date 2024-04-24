package app.openconnect.core;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVPNManagement.class */
public interface OpenVPNManagement {
    public static final int mBytecountInterval = 2;

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVPNManagement$pauseReason.class */
    public enum pauseReason {
        noNetwork,
        userPause,
        screenOff
    }

    void pause();

    void prefChanged();

    void reconnect();

    void resume();

    boolean stopVPN();
}
