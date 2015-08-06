package org.tttalk.openfire.plugin;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

/**
 */
public class DeviceTokenPlugin implements Plugin {
  private static final Logger log = LoggerFactory
      .getLogger(DeviceTokenPlugin.class);

  private LoginSessionEventListener listener = new LoginSessionEventListener();

  @Override
  public void initializePlugin(PluginManager manager, File pluginDirectory) {
    new JID(XMPPServer.getInstance().getServerInfo().getXMPPDomain());
    XMPPServer.getInstance().getMessageRouter();

    SessionEventDispatcher.addListener(listener);
  }

  @Override
  public void destroyPlugin() {
    SessionEventDispatcher.removeListener(listener);

    listener = null;
  }

  public String getUrl() {
    return Utils.getDeviceTokenUrl();
  }

  public void setUrl(String url) {
    Utils.setDeviceTokenUrl(url);
  }

  public String getAppSecret() {
    return Utils.getAppSecret();
  }

  public void setAppSecret(String secret) {
    Utils.setAppSecret(secret);
  }

  private class LoginSessionEventListener implements SessionEventListener {
    @Override
    public void sessionCreated(Session session) {
      String address = session.getAddress().toString();
      log.info("sessionCreated: " + address);
      if (Utils.isValidUser(address)) {
        online(address);
      }
    }

    @Override
    public void sessionDestroyed(Session session) {
      String address = session.getAddress().toString();
      log.info("sessionDestroyed: " + address);
      if (Utils.isValidUser(address)) {
        offline(address);
      }
    }

    @Override
    public void resourceBound(Session session) {
      // Do nothing.
    }

    @Override
    public void anonymousSessionCreated(Session session) {
      // ignore
    }

    @Override
    public void anonymousSessionDestroyed(Session session) {
      // ignore
    }
  }

  private void online(String address) {
    Thread thread = new OnlineThread(address);
    thread.start();
  }

  private void offline(String address) {
    Thread thread = new OfflineThread(address);
    thread.start();
  }

}
