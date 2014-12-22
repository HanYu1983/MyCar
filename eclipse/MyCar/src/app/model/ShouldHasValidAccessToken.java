package app.model;

import java.io.IOException;

import app.tool.FBTool;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyException;
import app.tool.VerifyTool.IVerifyFunc;
import app.tool.VerifyTool.ParamNotNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ShouldHasValidAccessToken implements IVerifyFunc {
	
	private ParamNotNull fbIdVerify, accessTokenVerify;
	private JSONObject authRes;
	
	public ShouldHasValidAccessToken(String fbidKey, String accessTokenKey ) {
		fbIdVerify = new ParamNotNull( fbidKey );
		accessTokenVerify = new ParamNotNull( accessTokenKey );
	}

	public void verify(IRequestInfoProvider request) throws VerifyException {
		fbIdVerify.verify(request);
		accessTokenVerify.verify(request);
		String fbId = request.getParameter(fbIdVerify.getName());
		String token = request.getParameter(accessTokenVerify.getName());
		try {
			String ret = FBTool.getUser(fbId, token);
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
	/*
	public String getFbid(){
		if( authRes == null )
			return "null fbid";
		else
			return authRes.getString("id");
	}*/

}
