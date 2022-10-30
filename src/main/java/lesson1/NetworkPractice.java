package lesson1;

import java.net.NetworkInterface;
import java.net.SocketException;

public class NetworkPractice {

    private final int INITIAL_SB_CAPACITY = 1024;
    private final StringBuilder sb = new StringBuilder(INITIAL_SB_CAPACITY);

    public static void main(String[] args) throws SocketException {

        System.out.println("Name\tUp\tVirtual\tLoopback");
        System.out.println("----------------------------------------------------------");

        var app = new NetworkPractice();

        NetworkInterface.networkInterfaces()
                .map(app::toNetworkInterfaceInfo)
                .forEach(System.out::println);
    }

    private String toNetworkInterfaceInfo(final NetworkInterface ni) {
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
