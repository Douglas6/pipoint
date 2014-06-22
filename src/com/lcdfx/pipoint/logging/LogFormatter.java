package com.lcdfx.pipoint.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss,SSS");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		sb.append(DATE_FORMAT.format(new Date(record.getMillis())) + " ")
				.append(record.getLevel().getLocalizedName() + " ")
				.append("[" + record.getSourceClassName() + "] ")
				.append(formatMessage(record))
				.append(LINE_SEPARATOR);

		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.close();
			sb.append(sw.toString());
		}

		return sb.toString();
	}
}
