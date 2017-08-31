package com.enefyod.app;

public class Launcher {
    public static void main(String[] args) throws Exception {
        CommandLineArgumentParser argumentParser = new CommandLineArgumentParser();
        argumentParser.parse(args);
    }
}
