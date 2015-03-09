<%@ page
   import="org.jivesoftware.openfire.XMPPServer,org.tttalk.openfire.plugin.DeviceTokenPlugin,org.jivesoftware.util.ParamUtils,java.util.HashMap,java.util.Map"
   errorPage="error.jsp"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>

<%
	boolean save = request.getParameter("save") != null;	
	String url = ParamUtils.getParameter(request, "url");
	String secret = ParamUtils.getParameter(request, "secret");
    
	DeviceTokenPlugin plugin = (DeviceTokenPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("tttalk.devicetoken");

	Map<String, String> errors = new HashMap<String, String>();	
	if (save) {
	  if (url == null || url.trim().length() < 1) {
	     errors.put("missingUrl", "missingUrl");
	  }
	  if (secret == null || secret.trim().length() < 1) {
	     errors.put("missingSecret", "missingSecret");
	  }
	  if (errors.size() == 0) {
	     plugin.setUrl(url);
	     plugin.setAppSecret(secret);
	     response.sendRedirect("devicetoken-form.jsp?settingsSaved=true");
	     return;
	  }		
	}
    
	url = plugin.getUrl();
	secret = plugin.getAppSecret();
%>

<html>
	<head>
	  <title>Set TTTalk URL for register/unregister OF_users</title>
	  <meta name="pageID" content="devicetoken-form"/>
	</head>
	<body>

<form action="devicetoken-form.jsp?save=true" method="post">

<div class="jive-contentBoxHeader">Enter the TTTalk url(openfire_devices.php) to register/unregister OF_users</div>
<div class="jive-contentBox">
   
	<% if (ParamUtils.getBooleanParameter(request, "settingsSaved")) { %>
   
	<div class="jive-success">
	<table cellpadding="0" cellspacing="0" border="0" style="margin-bottom:5px">
	<tbody>
	  <tr>
	     <td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
	     <td class="jive-icon-label">Settings saved successfully.</td>
	  </tr>
	</tbody>
	</table>
	</div>
   
	<% } %>

	<table cellpadding="3" cellspacing="0" border="0" width="100%">
	<tbody>
	  <tr>
	     <td width="5%" valign="middle">URL:&nbsp;</td>
	     <td width="95%"><input type="text" name="url" value="<%= url %>" style="width:50%"></td>
	     <% if (errors.containsKey("missingUrl")) { %>
	        <span class="jive-error-text">Please enter url</span>
	     <% } %> 
	  </tr>
	  <tr>
	     <td width="5%" valign="middle">Secret:&nbsp;</td>
	     <td width="95%"><input type="text" name="secret" value="<%= secret %>" style="width:50%"></td>
	     <% if (errors.containsKey("missingSecret")) { %>
	        <span class="jive-error-text">Please enter secret</span>
	     <% } %> 
	  </tr>
	</tbody>
	</table>
</div>
<input type="submit" value="Save"/>
</form>

</body>
</html>
