package io.sugo.pio.data.fetcher;

import java.util.List;

public interface DataFetcher
{
  String REDIS = "redis";

  byte[] readBytes();

  void writeBytes(byte[] buf);

  void close();

  String[] fetchData(List<String> itemIdSet, String field);

}
