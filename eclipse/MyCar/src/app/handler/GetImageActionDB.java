package app.handler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.ImageTool;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamNotNull;
import app.tool.VerifyTool.ParamShouldBeValue;

import com.alibaba.fastjson.JSONArray;

public class GetImageActionDB extends InjectorAction {

	private Dimension fbShareSize;
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	
	@Override
	public void onInitTransaction() throws Exception {
		super.onInitTransaction();
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
		String jsbase64 = po.getPhoto();
		String base64 = Tool.jsBase64toOriginBase64(jsbase64);
		
		BufferedImage image = Tool.decodeToImage(base64);
		
		MAIN:
		{
			if( outputType!= null ){
				VerifyTool.verify(infoProvider, new ParamShouldBeValue("outputType", new String[]{"origin", "fb", "100", "site"}));
				
				if( outputType.equalsIgnoreCase("fb") ){
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
			byte[] imageBytes = Tool.image2bytes(image);
			ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
			try{
				Tool.serviceImage(response, "png", bais);
				break MAIN;
			}finally{
				try{ bais.close(); }catch(Exception e){}
			}
		}
		return null;
	}
}
