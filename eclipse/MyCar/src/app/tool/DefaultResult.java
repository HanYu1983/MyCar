package app.tool;

public class DefaultResult {
	public static DefaultResult NotSuccess = new DefaultResult("");
	public static DefaultResult Success = new DefaultResult("", true);
	
	private boolean success;
	private Object info;
	private boolean debug;

	public DefaultResult(Object info) {
		this(info, false);
	}

	public DefaultResult(Object info, boolean success) {
		this.setSuccess(success);
		this.info = info;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}