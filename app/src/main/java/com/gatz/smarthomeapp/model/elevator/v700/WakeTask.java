package com.gatz.smarthomeapp.model.elevator.v700;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;

public class WakeTask {
	public static long ts = 0;
	public static int timeout = 90*1000;

	public static void refresh() {
		ts = System.currentTimeMillis();
	}

	public static void acquire() {
		ts = System.currentTimeMillis();

		dmsg req = new dmsg();
		dxml p = new dxml();
		p.setInt("/params/data", 0);
		p.setInt("/params/apk", 1);
		req.to("/ui/touch/event", p.toString());
	}

	public static Boolean timeout() {
//		if (Math.abs(System.currentTimeMillis()-ts) < timeout)
//			return false;
		return false;
	}
}
