package app.handler;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.UserDataPO;
import app.model.ShouldHasValidAccessToken;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.VerifyTool;
import app.tool.VerifyTool.MethodShouldBePost;
import app.tool.VerifyTool.ParamNotNull;
import app.tool.VerifyTool.ParamShouldBeValue;

import com.alibaba.fastjson.JSON;

public class SubmitUserDataAction extends InjectorAction {

	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		VerifyTool.verify(request, new MethodShouldBePost());
		VerifyTool.verify(request, new ShouldHasValidAccessToken("accessToken"));
		VerifyTool.verify(request, new ParamNotNull("fbid"));
		VerifyTool.verify(request, new ParamNotNull("articleId"));
		VerifyTool.verify(request, new ParamShouldBeValue("submitType", new String[]{"submit", "vote"}));
		
		String fbid = request.getParameter("fbid");
		String name = request.getParameter("name");
		String gender = request.getParameter("gender");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String articleId = request.getParameter("articleId");
		String submitType = request.getParameter("submitType");
		
		if( name!= null )
			name = URLDecoder.decode(name, "utf-8");
		
		UserDataPO po = new UserDataPO();
		po.setFbid(fbid);
		po.setId(Tool.getSidWithCalendar());
		po.setName(name);
		if(gender!= null){
			VerifyTool.verify(request, new ParamShouldBeValue("gender", new String[]{"M", "F"}));
			po.setGender(gender.charAt(0));
		}
		po.setPhone(phone);
		po.setEmail(email);
		po.setArticleId(articleId);
		po.setSubmitType(submitType);
		po.setSubimtDate(new Date());
		
		this.getUserDataRepository().Create(po.getId(), po);
		
		String jsonStr = JSON.toJSONString(po);
		String encoded = URLEncoder.encode(jsonStr, "utf8");
		Cookie userCookie = new Cookie("userdata", encoded);
		userCookie.setComment("user comment");
		userCookie.setMaxAge(24*60*60);
	    userCookie.setPath("/");
	    response.addCookie(userCookie);
	    
		return new DefaultResult(po, true);
	}
	
}
