package com.coship.ott.transport.action;

import java.lang.reflect.Type;

import com.coship.ott.constant.Constant;
import com.coship.ott.utils.Session;
import com.google.gson.Gson;

public class BaseAction {
	protected StringBuffer urlbuf = new StringBuffer().append("?version=")
			.append(Constant.DATA_INTERFACE_VERSION).append("&terminalType=")
			.append(Constant.TERMINAL_TYPE).append("&resolution=")
			.append(Constant.RESOLUTION).append("&userName=")
			.append(Session.getInstance().getUserName());
	protected String jsonData;
	protected Gson gson = new Gson();
	protected Type listType;
}
