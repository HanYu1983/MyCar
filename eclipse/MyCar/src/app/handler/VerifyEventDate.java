package app.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.tool.IRequestInfoProvider;
import app.tool.VerifyException;
import app.tool.VerifyTool.IVerifyFunc;

public class VerifyEventDate implements IVerifyFunc {
	private Date endOfDate;

	public VerifyEventDate(String dateStr) throws Exception {
		endOfDate = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).parse(dateStr);
	}

	public VerifyEventDate(Date date) {
		endOfDate = date;
	}

	public void verify(IRequestInfoProvider request) throws VerifyException {
		Date now = new Date();
		if (now.after(endOfDate)) {
			throw new VerifyException("event out of date");
		}
	}
}
