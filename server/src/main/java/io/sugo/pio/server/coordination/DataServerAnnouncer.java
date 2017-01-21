package io.sugo.pio.server.coordination;

import java.io.IOException;

public interface DataServerAnnouncer
{
  public void announce(String id) throws IOException;

  public void unannounce() throws IOException;

  public boolean isAnnounced();
}
