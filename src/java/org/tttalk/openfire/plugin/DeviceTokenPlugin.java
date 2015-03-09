package org.tttalk.openfire.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

	private String online(String address) {

		String appname = Utils.getAppName(address);
		String clientid = Utils.getClientId(address);
		Map<String, String> params = new HashMap<>();
		params.put("task", "online");
		params.put("devicetoken", address);
		params.put("clientid", clientid);
		params.put("appname", appname);

		params = Utils.genParams(clientid, params);

		return Utils.get(Utils.getDeviceTokenUrl(), params);
	}

	private String offline(String address) {
		Map<String, String> params = new HashMap<>();
		params.put("task", "offline");
		params.put("devicetoken", address);

		params = Utils.genParams(Utils.getClientId(address), params);

		return Utils.get(Utils.getDeviceTokenUrl(), params);
	}

}
