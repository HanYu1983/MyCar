package app.handler;

import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.model.ShouldHasValidAccessToken;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.VerifyTool;
import app.tool.VerifyTool.MethodShouldBePost;
import app.tool.VerifyTool.ParamNotNull;

public class SubmitActionBase64StoreText extends InjectorAction {
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		VerifyTool.verify(request, new MethodShouldBePost());
		VerifyTool.verify(request, new ShouldHasValidAccessToken("accessToken"));
		VerifyTool.verify(request, new ParamNotNull("image"));
		VerifyTool.verify(request, new ParamNotNull("comment"));
		VerifyTool.verify(request, new ParamNotNull("fbid"));
		VerifyTool.verify(request, new ParamNotNull("fbname"));
		
		String fbid = request.getParameter("fbid");
		String fbname = request.getParameter("fbname");
		String comment = request.getParameter("comment");
		String image64 = request.getParameter("image");
		
		fbname= URLDecoder.decode(fbname, "utf-8");
		comment = URLDecoder.decode(comment, "utf-8");
		image64 = URLDecoder.decode(image64, "utf-8");
		
		SubmitArticlePO bean = new SubmitArticlePO();
		bean.setId(Tool.getSidWithCalendar());
		bean.setFbid(fbid);
		bean.setFbname(fbname);
		bean.setComment(comment);
		bean.setPhoto(image64);
		bean.setSubmitDate(new Date());
		
		super.getSubmitArticleRepository().Create(bean.getId(), bean);
		
		return new DefaultResult(bean, true);
	}
}
