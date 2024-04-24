package app.openconnect.core;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVPN.class */
public class OpenVPN {

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/OpenVPN$ConnectionStatus.class */
    public enum ConnectionStatus {
        LEVEL_CONNECTED,
        LEVEL_VPNPAUSED,
        LEVEL_CONNECTING_SERVER_REPLIED,
        LEVEL_CONNECTING_NO_SERVER_REPLY_YET,
        LEVEL_NONETWORK,
        LEVEL_NOTCONNECTED,
        LEVEL_AUTH_FAILED,
        LEVEL_WAITING_FOR_USER_INPUT,
        UNKNOWN_LEVEL
    }

    public static void logError(int i7, Object... objArr) {
    }

    public static void logError(String str) {
    }

    public static void logInfo(int i7, Object... objArr) {
    }

    public static void logInfo(String str) {
    }
}
