package app.behavior;

import app.tool.IRepository;

public interface IAdminRepository extends IRepository<AdminPO> {
	boolean verifyName(String name, String pwd)throws Exception;
}
