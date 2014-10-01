package app.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.model.ShouldBeAdmin;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.MethodShouldBePost;
import app.tool.VerifyTool.ParamNotNull;
import app.tool.VerifyTool.ParamShouldBeBoolean;

public class VerifyArticleAction extends InjectorAction {
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		VerifyTool.verify(infoProvider, new MethodShouldBePost());
		VerifyTool.verify(infoProvider, new ShouldBeAdmin(request.getSession(true)));
		VerifyTool.verify(infoProvider, new ParamShouldBeBoolean("verified"));
		VerifyTool.verify(infoProvider, new ParamNotNull("articleId"));
		String articleId = infoProvider.getParameter("articleId");
		boolean verified = Boolean.parseBoolean(infoProvider.getParameter("verified"));
		this.getSubmitArticleRepository().verifyArticle(articleId, verified);
		return DefaultResult.Success;
	}
}
