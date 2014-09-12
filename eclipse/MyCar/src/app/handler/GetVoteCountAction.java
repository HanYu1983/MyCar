package app.handler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.tool.DefaultResult;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamNotNull;

public class GetVoteCountAction extends InjectorAction {

	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		VerifyTool.verify(request, new ParamNotNull("articleId"));
		String[] articleIds = request.getParameterValues("articleId");
		List<Integer> ret = new ArrayList<Integer>();
		for(String articleId : articleIds){
			ret.add(this.getVoteRepository().getVoteCount(articleId));
		}
		return new DefaultResult(ret, true);
	}
}
