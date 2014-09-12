package app.behavior;

import java.util.List;

import app.tool.IRepository;

public interface ISubmitArticleRepository extends IRepository<SubmitArticlePO> {

	String getDeskPath(String dbpath);
	
	void verifyArticle(String articleKey, boolean verified)throws Exception;

	List<SubmitArticlePO> getTop(int count)throws Exception;
	List<SubmitArticlePO> getByFbid(String fbid, int start, int count)throws Exception;
	List<SubmitArticlePO> getByFbname(String fbname, int start, int count)throws Exception;
	List<SubmitArticlePO> getAll(int start, int count)throws Exception;
	List<SubmitArticlePO> getAllIgnoreVerified(int start, int count)throws Exception;
	
	int getCountByFbid(String fbid)throws Exception;
	int getCountByFbname(String fbname)throws Exception;
	int getCountByAll()throws Exception;
	int getCountByAllIgnoreVerified()throws Exception;
}
