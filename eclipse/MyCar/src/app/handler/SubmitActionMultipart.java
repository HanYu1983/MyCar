package app.handler;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.model.MultipartRequestInfoProvider;
import app.model.ShouldHasValidAccessToken;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.MethodShouldBePost;
import app.tool.VerifyTool.ParamNotNull;

public class SubmitActionMultipart extends InjectorAction {
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new MultipartRequestInfoProvider(request, 4* 800* 600);
		
		ShouldHasValidAccessToken verifyAccessToken = new ShouldHasValidAccessToken("fbid", "accessToken");
		if( this.getController().isDebug() ){
			// nothing to do
		}else{
			VerifyTool.verify(infoProvider, new VerifyEventDate(super.getEventEndOfDate()));
			VerifyTool.verify(infoProvider, new MethodShouldBePost());
			VerifyTool.verify(infoProvider, verifyAccessToken);
		}
		VerifyTool.verify(infoProvider, new ParamNotNull("image"));
		VerifyTool.verify(infoProvider, new ParamNotNull("comment"));
		VerifyTool.verify(infoProvider, new ParamNotNull("fbname"));
		
		String fbid = infoProvider.getParameter("fbid");
		String fbname = infoProvider.getParameter("fbname");
		String comment = infoProvider.getParameter("comment");
		InputStream imageInput = infoProvider.getParameterObject("image");
		
		BufferedImage image = ImageIO.read(imageInput);
		try{ imageInput.close(); }catch(Exception e){}
		
		String image64 = Tool.encodeToString(image, "png");
		//important: add fakeType to simular base64 from javascript
		image64 = "fakeType,"+image64;
		
		fbname= URLDecoder.decode(fbname, "utf-8");
		comment = URLDecoder.decode(comment, "utf-8");
		
		SubmitArticlePO bean = new SubmitArticlePO();
		bean.setId(Tool.getSidWithCalendar());
		bean.setFbid(fbid);
		bean.setFbname(fbname);
		bean.setComment(comment);
		bean.setPhoto(image64);
		bean.setSubmitDate(new Date());
		
		super.getSubmitArticleRepository().Create(bean.getId(), bean);
		response.sendRedirect("web/upload02.html?articleId="+bean.getId());
		return null;
	}
}
