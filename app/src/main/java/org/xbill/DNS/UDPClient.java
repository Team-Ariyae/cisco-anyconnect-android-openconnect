package org.xbill.DNS;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.SecureRandom;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/UDPClient.class */
final class UDPClient extends Client {
    private static final int EPHEMERAL_RANGE = 64511;
    private static final int EPHEMERAL_START = 1024;
    private static final int EPHEMERAL_STOP = 65535;
    private static SecureRandom prng = new SecureRandom();
    private static volatile boolean prng_initializing = true;
    private boolean bound;

    static {
        new Thread(new Runnable() { // from class: org.xbill.DNS.UDPClient.1
            @Override // java.lang.Runnable
            public void run() {
                UDPClient.prng.nextInt();
                boolean unused = UDPClient.prng_initializing = false;
            }
        }).start();
    }

    public UDPClient(long j7) {
        super(DatagramChannel.open(), j7);
        this.bound = false;
    }

    private void bind_random(InetSocketAddress inetSocketAddress) {
        if (prng_initializing) {
            try {
                Thread.sleep(2L);
            } catch (InterruptedException e8) {
            }
            if (prng_initializing) {
                return;
            }
        }
        DatagramChannel datagramChannel = (DatagramChannel) this.key.channel();
        for (int i7 = 0; i7 < 1024; i7++) {
            try {
                int nextInt = prng.nextInt(EPHEMERAL_RANGE) + 1024;
                datagramChannel.socket().bind(inetSocketAddress != null ? new InetSocketAddress(inetSocketAddress.getAddress(), nextInt) : new InetSocketAddress(nextInt));
                this.bound = true;
                return;
            } catch (SocketException e9) {
            }
        }
    }

    public static byte[] sendrecv(SocketAddress socketAddress, SocketAddress socketAddress2, byte[] bArr, int i7, long j7) {
        UDPClient uDPClient = new UDPClient(j7);
        try {
            uDPClient.bind(socketAddress);
            uDPClient.connect(socketAddress2);
            uDPClient.send(bArr);
            return uDPClient.recv(i7);
        } finally {
            uDPClient.cleanup();
        }
    }

    public static byte[] sendrecv(SocketAddress socketAddress, byte[] bArr, int i7, long j7) {
        return sendrecv(null, socketAddress, bArr, i7, j7);
    }

    public void bind(SocketAddress socketAddress) {
        if (socketAddress == null || ((socketAddress instanceof InetSocketAddress) && ((InetSocketAddress) socketAddress).getPort() == 0)) {
            bind_random((InetSocketAddress) socketAddress);
            if (this.bound) {
                return;
            }
        }
        if (socketAddress != null) {
            ((DatagramChannel) this.key.channel()).socket().bind(socketAddress);
            this.bound = true;
        }
    }

    public void connect(SocketAddress socketAddress) {
        if (!this.bound) {
            bind(null);
        }
        ((DatagramChannel) this.key.channel()).connect(socketAddress);
    }

    public byte[] recv(int i7) {
        DatagramChannel datagramChannel = (DatagramChannel) this.key.channel();
        byte[] bArr = new byte[i7];
        this.key.interestOps(1);
        while (!this.key.isReadable()) {
            try {
                Client.blockUntil(this.key, this.endTime);
            } finally {
                if (this.key.isValid()) {
                    this.key.interestOps(0);
                }
            }
        }
        long read = datagramChannel.read(ByteBuffer.wrap(bArr));
        if (read <= 0) {
            throw new EOFException();
        }
        int i8 = (int) read;
        byte[] bArr2 = new byte[i8];
        System.arraycopy(bArr, 0, bArr2, 0, i8);
        Client.verboseLog("UDP read", datagramChannel.socket().getLocalSocketAddress(), datagramChannel.socket().getRemoteSocketAddress(), bArr2);
        return bArr2;
    }

    public void send(byte[] bArr) {
        DatagramChannel datagramChannel = (DatagramChannel) this.key.channel();
        Client.verboseLog("UDP write", datagramChannel.socket().getLocalSocketAddress(), datagramChannel.socket().getRemoteSocketAddress(), bArr);
        datagramChannel.write(ByteBuffer.wrap(bArr));
    }
}
