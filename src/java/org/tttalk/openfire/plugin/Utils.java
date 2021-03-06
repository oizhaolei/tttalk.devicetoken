package org.tttalk.openfire.plugin;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final String TTTALK_DEVICE_URL = "tttalk.devicetoken.url";
	private static final String TTTALK_APP_SECRET = "tttalk.app.secret";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String getSource() {
		return "openfire";
	}

	public static String genSign(Map<String, String> params, String appkey) {
		// sign
		StringBuilder sb = new StringBuilder();
		sb.append(appkey);

		String[] keyArray = params.keySet().toArray(new String[params.size()]);
		Arrays.sort(keyArray);

		for (String key : keyArray) {
			String value = params.get(key);
			if (!Utils.isEmpty(value)) {
				sb.append(key).append(value);
			}
		}
		sb.append(getAppSecret());

		String sign = Utils.sha1(sb.toString());

		return sign;
	}

	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);

		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	public static String sha1(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0 || s.equals("null");
	}

	public static Map<String, String> genParams(String loginid,
			Map<String, String> params) {
		if (params == null) {
			params = new HashMap<String, String>();
		}

		params.put("source", getSource());
		params.put("loginid", loginid);
		String sign = Utils.genSign(params, loginid);
		params.put("sign", sign);

		return params;
	}

	public static String encodeParameters(Map<String, String> params) {
		StringBuffer buf = new StringBuffer();
		String[] keyArray = params.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		int j = 0;
		for (String key : keyArray) {
			String value = params.get(key);
			if (j++ != 0) {
				buf.append("&");
			}
			if (!Utils.isEmpty(value)) {
				try {
					buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
							.append(URLEncoder.encode(value, "UTF-8"));
				} catch (java.io.UnsupportedEncodingException neverHappen) {
					// throw new RuntimeException(neverHappen.getMessage(),
					// neverHappen);
				}
			}
		}

		return buf.toString();
	}

	public static String get(String url, Map<String, String> params) {
		String body = "";
		try {
			url += "?" + Utils.encodeParameters(params);

			body = HttpRequest.get(url).body();
		} catch (HttpRequestException e) {
			log.error("HttpRequestException: " + url, e);
		}
		return body;
	}

	public static void setDeviceTokenUrl(String url) {
		JiveGlobals.setProperty(TTTALK_DEVICE_URL, url);
	}

	public static String getDeviceTokenUrl() {
		return JiveGlobals.getProperty(TTTALK_DEVICE_URL,
				"http://ctalk2/tttalk.web/v3.1/openfire_devices.php");
	}

	public static void setAppSecret(String secret) {
		JiveGlobals.setProperty(TTTALK_APP_SECRET, secret);
	}

	public static String getAppSecret() {
		return JiveGlobals.getProperty(TTTALK_APP_SECRET,
				"2a9304125e25edaa5aff574153eafc95c97672c6");
	}

	public static String getClientId(String address) {
		return address
				.substring(address.indexOf("_") + 1, address.indexOf("@"));
	}

	public static boolean isValidUser(String address) {
		return address.startsWith("chinatalk_")
				|| address.startsWith("volunteer_");
	}

	public static String getAppName(String address) {
		return address.substring(0, address.indexOf("_"));
	}
}
