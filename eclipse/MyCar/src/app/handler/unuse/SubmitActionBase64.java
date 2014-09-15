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
