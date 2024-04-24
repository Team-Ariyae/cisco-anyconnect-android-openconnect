package org.xbill.DNS;

import java.io.EOFException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TCPClient.class */
final class TCPClient extends Client {
    public TCPClient(long j7) {
        super(SocketChannel.open(), j7);
    }

    private byte[] _recv(int i7) {
        SocketChannel socketChannel = (SocketChannel) this.key.channel();
        byte[] bArr = new byte[i7];
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        this.key.interestOps(1);
        int i8 = 0;
        while (i8 < i7) {
            try {
                if (this.key.isReadable()) {
                    long read = socketChannel.read(wrap);
                    if (read < 0) {
                        throw new EOFException();
                    }
                    int i9 = i8 + ((int) read);
                    i8 = i9;
                    if (i9 >= i7) {
                        continue;
                    } else {
                        if (System.currentTimeMillis() > this.endTime) {
                            throw new SocketTimeoutException();
                        }
                        i8 = i9;
                    }
                } else {
                    Client.blockUntil(this.key, this.endTime);
                }
            } finally {
                if (this.key.isValid()) {
                    this.key.interestOps(0);
                }
            }
        }
        return bArr;
    }

    public static byte[] sendrecv(SocketAddress socketAddress, SocketAddress socketAddress2, byte[] bArr, long j7) {
        TCPClient tCPClient = new TCPClient(j7);
        if (socketAddress != null) {
            try {
                tCPClient.bind(socketAddress);
            } finally {
                tCPClient.cleanup();
            }
        }
        tCPClient.connect(socketAddress2);
        tCPClient.send(bArr);
        return tCPClient.recv();
    }

    public static byte[] sendrecv(SocketAddress socketAddress, byte[] bArr, long j7) {
        return sendrecv(null, socketAddress, bArr, j7);
    }

    public void bind(SocketAddress socketAddress) {
        ((SocketChannel) this.key.channel()).socket().bind(socketAddress);
    }

    public void connect(SocketAddress socketAddress) {
        SocketChannel socketChannel = (SocketChannel) this.key.channel();
        if (socketChannel.connect(socketAddress)) {
            return;
        }
        this.key.interestOps(8);
        while (!socketChannel.finishConnect()) {
            try {
                if (!this.key.isConnectable()) {
                    Client.blockUntil(this.key, this.endTime);
                }
            } finally {
                if (this.key.isValid()) {
                    this.key.interestOps(0);
                }
            }
        }
    }

    public byte[] recv() {
        byte[] _recv = _recv(2);
        byte[] _recv2 = _recv(((_recv[0] & 255) << 8) + (_recv[1] & 255));
        SocketChannel socketChannel = (SocketChannel) this.key.channel();
        Client.verboseLog("TCP read", socketChannel.socket().getLocalSocketAddress(), socketChannel.socket().getRemoteSocketAddress(), _recv2);
        return _recv2;
    }

    public void send(byte[] bArr) {
        SocketChannel socketChannel = (SocketChannel) this.key.channel();
        Client.verboseLog("TCP write", socketChannel.socket().getLocalSocketAddress(), socketChannel.socket().getRemoteSocketAddress(), bArr);
        ByteBuffer wrap = ByteBuffer.wrap(new byte[]{(byte) (bArr.length >>> 8), (byte) (bArr.length & 255)});
        ByteBuffer wrap2 = ByteBuffer.wrap(bArr);
        this.key.interestOps(4);
        int i7 = 0;
        while (i7 < bArr.length + 2) {
            try {
                if (this.key.isWritable()) {
                    long write = socketChannel.write(new ByteBuffer[]{wrap, wrap2});
                    if (write < 0) {
                        throw new EOFException();
                    }
                    int i8 = i7 + ((int) write);
                    i7 = i8;
                    if (i8 >= bArr.length + 2) {
                        continue;
                    } else {
                        if (System.currentTimeMillis() > this.endTime) {
                            throw new SocketTimeoutException();
                        }
                        i7 = i8;
                    }
                } else {
                    Client.blockUntil(this.key, this.endTime);
                }
            } finally {
                if (this.key.isValid()) {
                    this.key.interestOps(0);
                }
            }
        }
    }
}
