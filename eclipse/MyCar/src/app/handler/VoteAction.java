package app.handler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.VotePO;
import app.tool.DefaultResult;
import app.tool.VerifyTool;
import app.tool.VerifyTool.MethodShouldBePost;
import app.tool.VerifyTool.ParamNotNull;

public class VoteAction extends InjectorAction {
	@Override
	protected boolean shouldConnectDB(){
		return true;
	}
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		VerifyTool.verify(request, new MethodShouldBePost());
		VerifyTool.verify(request, new ParamNotNull("fbid"));
		VerifyTool.verify(request, new ParamNotNull("articleId"));
		
		String fbid = request.getParameter("fbid");
		String articleId = request.getParameter("articleId");
		
		VotePO vote = new VotePO();
		vote.setFbid(fbid);
		vote.setVoteDate(new Date());
		vote.setArticleId(articleId);
		
		String query = request.getParameter("query");
		if(query!=null){
			boolean canVote = this.getVoteRepository().isCanVoteToday(vote);
			return new DefaultResult(canVote, true);
		}
		
		boolean voteOk = this.getVoteRepository().Vote(vote);
		return voteOk ? DefaultResult.Success : new DefaultResult("already vote");
	}
}
