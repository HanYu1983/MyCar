package app.behavior;

import app.tool.IRepository;

public interface IVoteRepository extends IRepository<VotePO> {
	boolean Vote(VotePO vote)throws Exception;
	boolean isCanVoteToday(VotePO vote)throws Exception;
	int getVoteCount(String articleId)throws Exception;
}
