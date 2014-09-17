package app.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ITransaction {
	
	void onDestoryTransaction();
	void onInitTransaction() throws Exception;
	ITransaction prototype();
	FrontController getController();
	void setController(FrontController controller);
	
	DefaultResult transaction(HttpServletRequest request, HttpServletResponse response)throws Exception;
	
	public static class DefaultAction implements ITransaction, Cloneable{

		private FrontController controller;
	
		public FrontController getController() {
			return controller;
		}

		public void setController(FrontController controller) {
			this.controller = controller;
		}

		public ITransaction prototype() {
			try {
				return (ITransaction)this.clone();
			} catch (CloneNotSupportedException e) {
				// never
			}
			return null;
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			ITransaction clone = (ITransaction) super.clone();
			try {
				clone.onInitTransaction();
			} catch (Exception e) {
				// nothing todo
			}
			return clone;
		}

		public void onDestoryTransaction() {
			
			
		}

		public DefaultResult transaction(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			if( isNeedCache() == false ){
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
				response.setDateHeader("Expires", 1);
			}
			return DefaultResult.Success;
		}
		
		public boolean isNeedCache(){
			return true;
		}

		public void onInitTransaction() throws Exception {
		
		}
	}
}