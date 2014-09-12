package app.behavior;

import app.tool.IConnectionProvider;

public interface IInjectionProvider {
	IConnectionProvider getConnectionProvider();
	
	IVoteRepository getVoteRepository();
	ISubmitArticleRepository getSubmitArticleRepository();
	IUserDataRepository getUserDataRepository();
	IAdminRepository getAdminRepository();
	
	boolean isDebug();
	
}
