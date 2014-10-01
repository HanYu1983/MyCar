package app.handler;

import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamShouldBeNumber;

public class GetArticleAction extends InjectorAction {
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		String top = infoProvider.getParameter("top");
		String queryCount = infoProvider.getParameter("queryCount");
		
		boolean shouldQueryCount = queryCount != null;

		if (top != null) {
			if(shouldQueryCount){
				int count = this.getSubmitArticleRepository().getCountByAll();
				return new DefaultResult(count, true);
			}
			VerifyTool.verify(infoProvider, new ParamShouldBeNumber("top"));
			int count = Integer.parseInt(top);
			List<?> result = this.getSubmitArticleRepository().getTop(count);
			return new DefaultResult(result, true);
		}
		
		String articleId = infoProvider.getParameter("articleId");
		if(articleId != null){
			SubmitArticlePO po = this.getSubmitArticleRepository().Read(articleId);
			if(po != null)
				return new DefaultResult(po, true);
			else
				return new DefaultResult("no article["+articleId+"]");
		}

		String fbid = infoProvider.getParameter("fbid");
		
		if (fbid != null) {
			if(shouldQueryCount){
				int count = this.getSubmitArticleRepository().getCountByFbid(fbid);
				return new DefaultResult(count, true);
			}
			
			VerifyTool.verify(infoProvider, new ParamShouldBeNumber("start"));
			VerifyTool.verify(infoProvider, new ParamShouldBeNumber("count"));

			int start = Integer.parseInt(infoProvider.getParameter("start"));
			int count = Integer.parseInt(infoProvider.getParameter("count"));

			List<?> result = this.getSubmitArticleRepository().getByFbid(fbid, start, count);
			return new DefaultResult(result, true);
		}
		
		String fbname = infoProvider.getParameter("fbname");
		if(fbname != null){
			fbname = URLDecoder.decode(fbname, "utf8");
			
			if(shouldQueryCount){
				int count = this.getSubmitArticleRepository().getCountByFbname(fbname);
				return new DefaultResult(count, true);
			}
			
			VerifyTool.verify(infoProvider, new ParamShouldBeNumber("start"));
			VerifyTool.verify(infoProvider, new ParamShouldBeNumber("count"));

			int start = Integer.parseInt(infoProvider.getParameter("start"));
			int count = Integer.parseInt(infoProvider.getParameter("count"));

			System.out.println("fbname:"+fbname);
			
			List<?> result = this.getSubmitArticleRepository().getByFbname(fbname, start, count);
			return new DefaultResult(result, true);
		}
		
		if(shouldQueryCount){
			int count = this.getSubmitArticleRepository().getCountByAll();
			return new DefaultResult(count, true);
		}
		
		VerifyTool.verify(infoProvider, new ParamShouldBeNumber("start"));
		VerifyTool.verify(infoProvider, new ParamShouldBeNumber("count"));
		
		int start = Integer.parseInt(infoProvider.getParameter("start"));
		int count = Integer.parseInt(infoProvider.getParameter("count"));
		
		List<?> result = this.getSubmitArticleRepository().getAll(start, count);
		return new DefaultResult(result, true);
	}
}
