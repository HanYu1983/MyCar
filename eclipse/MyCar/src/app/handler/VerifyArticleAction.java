package app.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.model.ShouldBeAdmin;
import app.tool.DefaultResult;
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
		VerifyTool.verify(request, new MethodShouldBePost());
		VerifyTool.verify(request, new ShouldBeAdmin(request.getSession(true)));
		VerifyTool.verify(request, new ParamShouldBeBoolean("verified"));
		VerifyTool.verify(request, new ParamNotNull("articleId"));
		String articleId = request.getParameter("articleId");
		boolean verified = Boolean.parseBoolean(request.getParameter("verified"));
		this.getSubmitArticleRepository().verifyArticle(articleId, verified);
		return DefaultResult.Success;
	}
}
