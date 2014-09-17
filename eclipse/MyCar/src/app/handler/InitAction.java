package app.handler;

import app.model.Tool;
import app.tool.ITransaction;

public class InitAction extends ITransaction.DefaultAction {
	@Override
	public void onInitTransaction() throws Exception {
		Tool.makeTrustAllCertSetting();
	}
}
