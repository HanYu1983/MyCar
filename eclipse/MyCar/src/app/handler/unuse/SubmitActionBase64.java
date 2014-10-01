package app.handler.unuse;

import java.awt.image.BufferedImage;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.handler.InjectorAction;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamNotNull;
/**
 * @deprecated 不存圖片到硬碟了。因為websphere說不定會有限制取存
 * @author hanyu
 *
 */
public class SubmitActionBase64 extends InjectorAction {

	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		VerifyTool.verify(infoProvider, new ParamNotNull("image"));
		VerifyTool.verify(infoProvider, new ParamNotNull("comment"));
		VerifyTool.verify(infoProvider, new ParamNotNull("fbid"));
		VerifyTool.verify(infoProvider, new ParamNotNull("fbname"));
		
		String fbid = infoProvider.getParameter("fbid");
		String fbname = infoProvider.getParameter("fbname");
		String comment = infoProvider.getParameter("comment");
		String image64 = infoProvider.getParameter("image");
		
		fbname= URLDecoder.decode(fbname, "utf-8");
		comment = URLDecoder.decode(comment, "utf-8");
		image64 = Tool.jsBase64toOriginBase64(image64);
		
		BufferedImage image = Tool.decodeToImage(image64);
		
		String ext = "png";
		String convertFilename = Tool.getSidWithCalendar()+"."+ext;
		
		byte[] imageBytes = Tool.image2bytes(image);
		
		SubmitArticlePO bean = new SubmitArticlePO();
		bean.setId(Tool.getSidWithCalendar());
		bean.setFbid(fbid);
		bean.setFbname(fbname);
		bean.setComment(comment);
		bean.setFilename(convertFilename);
		bean.setImage(imageBytes);
		bean.setSubmitDate(new Date());
		
		super.getSubmitArticleRepository().Create(bean.getId(), bean);
		
		return new DefaultResult(bean, true);
	}
}
