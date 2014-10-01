package app.handler.unuse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.handler.InjectorAction;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.FrontController;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.ImageTool;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamNotNull;
import app.tool.VerifyTool.ParamShouldBeValue;

import com.alibaba.fastjson.JSONArray;

/**
 * @deprecated 不從檔案抓圖片了。改為從DB抓
 * @author hanyu
 *
 */
public class GetImageAction extends InjectorAction {

	private Dimension fbShareSize;
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	public void setController(FrontController controller){
		super.setController(controller);
		JSONArray ary = this.getController().getConfig().getCustom().getJSONArray("fb-share-size");
		int w = ary.getInteger(0);
		int h = ary.getInteger(1);
		fbShareSize = new Dimension(w, h);
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		VerifyTool.verify(infoProvider, new ParamNotNull("articleId"));
		
		String articleId = infoProvider.getParameter("articleId");
		String outputType = infoProvider.getParameter("outputType");
		
		SubmitArticlePO po = this.getSubmitArticleRepository().Read(articleId);

		if (po == null) {
			return new DefaultResult("no article [" + articleId + "]");
		}

		String filepath = super.getSubmitArticleRepository().getDeskPath(po.getFilename());
		FileInputStream flinp = new FileInputStream(filepath);
		MAIN:
		try{
			
			if( outputType!= null ){
				VerifyTool.verify(infoProvider, new ParamShouldBeValue("outputType", new String[]{"origin", "fb", "100", "site"}));
				
				if( outputType.equalsIgnoreCase("fb") ){
					BufferedImage image = ImageIO.read(flinp);
					BufferedImage resized = ImageTool.resize(image, (int)fbShareSize.getWidth(), (int)fbShareSize.getHeight(), Color.black);
					
					byte[] imageBytes = Tool.image2bytes(resized);
					ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
					try{
						Tool.serviceImage(response, "png", bais);
						break MAIN;
					}finally{
						try{ bais.close(); }catch(Exception e){}
					}
				}
				
				if( outputType.equalsIgnoreCase("100") ){
					BufferedImage image = ImageIO.read(flinp);
					BufferedImage resized = ImageTool.resize(image, 100, 100, Color.black);
					
					byte[] imageBytes = Tool.image2bytes(resized);
					ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
					try{
						Tool.serviceImage(response, "png", bais);
						break MAIN;
					}finally{
						try{ bais.close(); }catch(Exception e){}
					}
				}
				
				if( outputType.equalsIgnoreCase("site") ){
					BufferedImage image = ImageIO.read(flinp);
					BufferedImage resized = ImageTool.resize(image, 470, 299, Color.black);
					
					byte[] imageBytes = Tool.image2bytes(resized);
					ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
					try{
						Tool.serviceImage(response, "png", bais);
						break MAIN;
					}finally{
						try{ bais.close(); }catch(Exception e){}
					}
				}
			}
			Tool.serviceImage(response, "png", flinp);
			
		}finally{
			try{ flinp.close(); }catch(Exception e){}
		}
		return null;
	}

	
}
