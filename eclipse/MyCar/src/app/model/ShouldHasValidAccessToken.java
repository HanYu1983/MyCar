package app.model;

import java.io.IOException;

import app.tool.FBTool;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyException;
import app.tool.VerifyTool.ParamNotNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ShouldHasValidAccessToken extends ParamNotNull {
	
	private JSONObject authRes;
	
	public ShouldHasValidAccessToken(String name) {
		super(name);
	}

	public void verify(IRequestInfoProvider request) throws VerifyException {
		super.verify(request);
		String token = request.getParameter(name);
		try {
			String ret = FBTool.me(token);
			authRes = (JSONObject)JSON.parse(ret);
			boolean isError = authRes.containsKey("error");
			if(isError){
				throw new IOException("has fb error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new VerifyException("invalid access token");
		}
		
	}
	
	public String getFbid(){
		if( authRes == null )
			return "null fbid";
		else
			return authRes.getString("id");
	}

}
