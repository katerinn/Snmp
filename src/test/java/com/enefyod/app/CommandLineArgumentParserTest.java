package com.enefyod.app;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CommandLineArgumentParserTest
{
    private CommandLineArgumentParser argumentParser = new CommandLineArgumentParser();

    @Test
    public void ipAddressShouldBeCorrect()
    {
        String[] args = new String[] {"127.0.0.1", "8080"};
        argumentParser.parse(args);

        assertEquals(argumentParser.getIpAddress(), "127.0.0.1");
    }

    @Test
    public void portShouldBeCorrect()
    {
        String[] args = new String[] {"127.0.0.1", "8080"};
        argumentParser.parse(args);

        assertEquals(argumentParser.getPort(), "8080");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ipAddressShouldNotBeCorrect()
    {
        String[] args = new String[] {"127.0.555.555", "8080"};
        argumentParser.parse(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void portShouldNotBeCorrect()
    {
        String[] args = new String[] {"127.0.0.1", "65536"};
        argumentParser.parse(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfWrongParametersCount()
    {
        String[] args = new String[] {};
        argumentParser.parse(args);
    }
}