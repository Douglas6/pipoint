package com.lcdfx.pipoint;

public class PiPointUtils {
	public static long stringToSeconds(String string) {
		long seconds = 0;
		String[] values = string.split(":");
		seconds = Long.parseLong(values[0]) * 3600 + Long.parseLong(values[1]) * 60 + Long.parseLong(values[2]);
		
		return seconds;
	}
	
	public static String secondsToString(long seconds) {
		long h = seconds / 3600;
		long m = (seconds - (h * 3600)) / 60;
		long s = seconds - (h * 3600) - (m * 60);
		
		return String.format("%02d:%02d:%02d", h, m, s);
	}
}
