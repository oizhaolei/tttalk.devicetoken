package org.tttalk.openfire.plugin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineThread extends Thread {
  private static final Logger log = LoggerFactory.getLogger(OnlineThread.class);
  private String address;

  public OnlineThread(String address) {
    this.address = address;
  }

  public void run() {
    try {
      log.info("online start: " + address);
      String appname = Utils.getAppName(address);
      String clientid = Utils.getClientId(address);
      Map<String, String> params = new HashMap<>();
      params.put("task", "online");
      params.put("devicetoken", address);
      params.put("clientid", clientid);
      params.put("appname", appname);

      params = Utils.genParams(clientid, params);

      Utils.get(Utils.getDeviceTokenUrl(), params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
