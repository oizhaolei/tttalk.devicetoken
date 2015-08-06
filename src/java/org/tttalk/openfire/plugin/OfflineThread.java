package org.tttalk.openfire.plugin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflineThread extends Thread {
  private static final Logger log = LoggerFactory
      .getLogger(OfflineThread.class);
  private String address;

  public OfflineThread(String address) {
    this.address = address;
  }

  public void run() {
    log.info("offline start: " + address);
    try {
      Map<String, String> params = new HashMap<>();
      params.put("task", "offline");
      params.put("devicetoken", address);

      params = Utils.genParams(Utils.getClientId(address), params);

      Utils.get(Utils.getDeviceTokenUrl(), params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
