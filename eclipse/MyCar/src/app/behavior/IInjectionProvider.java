package app.behavior;

import java.util.Date;

import app.tool.IConnectionProvider;

public interface IInjectionProvider {
	IConnectionProvider getConnectionProvider();
	Date getEventEndOfDate();
	
	IVoteRepository getVoteRepository();
	ISubmitArticleRepository getSubmitArticleRepository();
	IUserDataRepository getUserDataRepository();
	IAdminRepository getAdminRepository();
	
	boolean isDebug();
	
}
