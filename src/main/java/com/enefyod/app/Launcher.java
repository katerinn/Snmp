package com.enefyod.app;

public class Launcher {
    public static void main(String[] args) throws Exception {
        CommandLineArgumentParser argumentParser = new CommandLineArgumentParser();
        try {
        argumentParser.parse(args);
        } catch (Exception e)
        {
            System.exit(-1);
        }

        SnmpInterfacesListPrinter snmpInterfaceListPrinter = new SnmpInterfacesListPrinter();
        snmpInterfaceListPrinter.printInterfacesList(argumentParser.getIpAddress(), argumentParser.getPort());
    }
}
