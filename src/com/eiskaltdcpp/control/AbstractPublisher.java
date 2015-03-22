package com.eiskaltdcpp.control;

import java.util.ArrayList;

public class AbstractPublisher<ListenerType>
{
	protected interface Notificator<ListenerType>
	{
		abstract void notify(ListenerType listener); 
	}
	
	private ArrayList<ListenerType> listeners = new ArrayList<ListenerType>();
	
	public void subscribe(ListenerType listener)
	{
		listeners.add(listener);
	}
	
	public void unsubscribe(ListenerType listener)
	{
		listeners.remove(listener);
	}
	
	protected void notifyEach(Notificator<ListenerType> notificator)
	{
		for (ListenerType listener: listeners)
		{
			notificator.notify(listener);
		}
	}
}