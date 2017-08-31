package com.enefyod.app;

public class CommandLineArgumentParser {
    private static final String IP_ADDRESS_PATTERN =
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final String PORT_PATTERN =
            "(0)|([1-9]\\d{0,2})|([1-5]\\d{3})|([6-9]\\d{3})" +
                    "|([1-5]\\d{4})|(6[0-4]\\\\d{3})|(65[0-4]\\d{2})|(655[0-2]\\d)|(6553[0-5])";

    private String ipAddress;
    private String port;

    public void parse(String[] args) {
        if (args.length < 2) {
            System.out.println("Wrong usage\n" +
                    "Usage:\n" +
                    "    java -jar snmp-list.jar ipAddress port");
            throw new IllegalArgumentException("Wrong usage");
        }

        if (!args[0].matches(IP_ADDRESS_PATTERN)) {
            System.out.println("Invalid IP ipAddress format");
            throw new IllegalArgumentException("Invalid IP ipAddress format");
        }

        if (!args[1].matches(PORT_PATTERN)) {
            System.out.println("Invalid port format");
            throw new IllegalArgumentException("Invalid port format");
        }

        ipAddress = args[0];
        port = args[1];
    }

    public String getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
