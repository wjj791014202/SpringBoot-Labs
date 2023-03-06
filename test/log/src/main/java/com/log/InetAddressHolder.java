package com.log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

public final class InetAddressHolder {
    private static final Object cacheLock = new Object();
    private static InetAddress cachedLocalHost = null;
    private static String hostname = null;
    private static long cacheTime = 0L;
    private static final long maxCacheTime = 300000L;

    private InetAddressHolder() {
    }

    public static InetAddress getInetAddress() throws SocketException, UnknownHostException {
        InetAddress ret = null;
        synchronized(cacheLock) {
            long now = System.currentTimeMillis();
            if (cachedLocalHost != null) {
                if (now - cacheTime < 300000L) {
                    ret = cachedLocalHost;
                } else {
                    cachedLocalHost = null;
                }
            }

            if (ret == null) {
                InetAddress localAddr = innerGetInetAddress();
                cachedLocalHost = localAddr;
                cacheTime = now;
                ret = localAddr;
            }

            return ret;
        }
    }

    public static String getHostname() {
        if (hostname == null) {
            Class var0 = InetAddressHolder.class;
            synchronized(InetAddressHolder.class) {
                if (hostname == null) {
                    File hostnameFile = new File("/etc/hostname");
                    if (hostnameFile.exists()) {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(hostnameFile));
                            Throwable var3 = null;

                            try {
                                hostname = br.readLine();
                            } catch (Throwable var17) {
                                var3 = var17;
                                throw var17;
                            } finally {
                                if (br != null) {
                                    if (var3 != null) {
                                        try {
                                            br.close();
                                        } catch (Throwable var16) {
                                            var3.addSuppressed(var16);
                                        }
                                    } else {
                                        br.close();
                                    }
                                }

                            }
                        } catch (IOException var19) {
                            System.err.println("cannot read file /etc/hostname:" + var19.getMessage());
                        }
                    }

                    if (hostname == null) {
                        try {
                            hostname = InetAddress.getLocalHost().getHostName();
                            if (Objects.equals(hostname, "localhost")) {
                                hostname = getInetAddress().getHostName();
                            }
                        } catch (UnknownHostException | SocketException var15) {
                            System.err.println("cannot obtain hostname:" + var15.getMessage());
                            hostname = "localhost";
                        }
                    }
                }
            }
        }

        return hostname;
    }

    private static InetAddress innerGetInetAddress() throws SocketException, UnknownHostException {
        InetAddress candidateAddress = null;
        Enumeration ifaces = NetworkInterface.getNetworkInterfaces();

        while(ifaces.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface)ifaces.nextElement();
            Enumeration inetAddrs = iface.getInetAddresses();

            while(inetAddrs.hasMoreElements()) {
                InetAddress inetAddr = (InetAddress)inetAddrs.nextElement();
                if (!inetAddr.isLoopbackAddress() && !(inetAddr instanceof Inet6Address)) {
                    if (inetAddr.isSiteLocalAddress()) {
                        return inetAddr;
                    }

                    if (candidateAddress == null) {
                        candidateAddress = inetAddr;
                    }
                }
            }
        }

        if (candidateAddress != null) {
            return candidateAddress;
        } else {
            return InetAddress.getLocalHost();
        }
    }
}

