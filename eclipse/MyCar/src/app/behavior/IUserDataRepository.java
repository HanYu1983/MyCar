package app.behavior;

import java.util.List;

import app.tool.IRepository;

public interface IUserDataRepository extends IRepository<UserDataPO> {
	int getCountByAll(String type)throws Exception;
	List<UserDataPO> getAll(String type, int start, int count)throws Exception;
	UserDataPO getUserDataByArticleId(String articleId)throws Exception;
	List<UserDataPO> markLucky(String submitType, int count)throws Exception;
}
