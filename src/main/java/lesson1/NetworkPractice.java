package lesson1;

import java.net.NetworkInterface;
import java.net.SocketException;

public class NetworkPractice {

    public static void main(String[] args) throws SocketException {

        System.out.println("Name\tUp\tVirtual\tLoopback");
        System.out.println("----------------------------------------------------------");
        StringBuilder sb = new StringBuilder();

        NetworkInterface.networkInterfaces()
                .map(it -> toNetworkInsterfaceInfo(it, sb))
                .forEach(System.out::println);
    }

    private static String toNetworkInsterfaceInfo(final NetworkInterface ni, final StringBuilder sb) {
        try {
            sb
                    .append(ni)
                    .append(": ")
                    .append(ni.isUp())
                    .append(" ")
                    .append(ni.isVirtual())
                    .append(" ")
                    .append(ni.isLoopback());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String info = sb.toString();
        sb.setLength(0);
        return info;
    }
}
