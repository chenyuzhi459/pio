package io.sugo.pio.client.selector;

/**
 */
public interface Server
{
    public String getScheme();
    public String getHost();
    public String getAddress();
    public int getPort();
}
