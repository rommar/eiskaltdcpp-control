package com.eiskaltdcpp.control;

import android.os.Handler;

public class PeriodicTask
{
	private long time = 0;
	private final long maxTime;
	private final long period;
	private Handler timerHandler = new Handler();
	private Runnable targetRunnable;
	
	private Runnable timerRunnable = new Runnable()
	    {
	    	public void run()
	    	{
	    		if (time >= maxTime)
	    			return;
	    		
	    		
	    		targetRunnable.run();
	    		
				timerHandler.postDelayed(this, period);
				time += period;
	    	}
	    };
	
	public PeriodicTask(long durationMs, long periodMs, Runnable r)
	{
		maxTime = durationMs;
		period = periodMs;
		targetRunnable = r;
		
	}
	
	public void start(long delayMs)
	{
		time = 0;
		timerHandler.postDelayed(timerRunnable, delayMs);
	}
	
	public void stop()
	{
		time = maxTime;
	}
}