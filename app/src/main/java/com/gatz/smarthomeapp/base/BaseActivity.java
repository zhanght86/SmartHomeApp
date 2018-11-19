package com.gatz.smarthomeapp.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gatz.smarthomeapp.model.elevator.v700.WakeTask;

@SuppressLint("HandlerLeak")
public class BaseActivity extends Activity {

	private Handler e_timer = null;
	protected Boolean bFinish = true;

	private Thread bThread = null;
	private Boolean bRun = true;
	public class ProcessThread implements Runnable {
		@Override
		public void run() {
			while(bRun) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				if (e_timer != null)
					e_timer.sendMessage(e_timer.obtainMessage());
			}
		}
	}

	public void onTimer() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();

		this.setup();
	}

	private void setup() {
		WakeTask.acquire();

		if (e_timer == null) {
			e_timer = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					onTimer();

					if (bFinish && WakeTask.timeout()) {
						tStop();
					}
				}
			};
		}
		this.tStart();
	}

	@Override
	public void onStop() {
		super.onStop();

		e_timer = null;
		this.tStop();
	}

	@Override
	public void onRestart() {
		super.onRestart();

		if (bFinish && WakeTask.timeout()) {
			this.tStop();
		} else
			this.tStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (bFinish && WakeTask.timeout()) {
			this.tStop();
		}else
			this.tStart();
	}

	private void tStart() {
		bRun = true;
		if (bThread == null) {
			ProcessThread pt = new ProcessThread();
			bThread = new Thread(pt);
			bThread.start();
		}
	}

	protected void tStop() {
		bRun = false;
		if (bThread != null) {
			bThread.interrupt();
			bThread = null;
		}
	}

}
