package game;

import java.util.Timer;
import java.util.TimerTask;

class TimerMinesweeper {
	
	private static int TIMER = 0;
	private static boolean timer_running;
	private static Timer timer;
	private static TimerTask task;
	
	TimerMinesweeper () {
		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				TIMER++;
				if (TIMER < 99999) FrameMinesweeper.setTimer(TIMER);
			}
		};
	}
	
	public static void startTimer() {
		timer.scheduleAtFixedRate(task, 1000, 1000);
		timer_running = true;
	}
	public static void stopTimer() {
		timer.cancel();
		timer_running = false;
	}
	
	// getter & setter
	public static int getTimer() {
		return TIMER;
	}
	public static void setTimer(int value) {
		TIMER = value;
	}
	public static boolean isTimeRunning() {
		return timer_running;
	}
	
	
	
}
