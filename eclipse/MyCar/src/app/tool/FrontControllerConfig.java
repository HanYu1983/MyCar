package app.tool;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class FrontControllerConfig {
	private Map<String, String> actions;
	private JSONObject custom;

	public Map<String, String> getActions() {
		return actions;
	}

	public void setActions(Map<String, String> actions) {
		this.actions = actions;
	}

	public JSONObject getCustom() {
		return custom;
	}

	public void setCustom(JSONObject custom) {
		this.custom = custom;
	}
	
	
}
