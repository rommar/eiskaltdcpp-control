package com.eiskaltdcpp.control;


public abstract class AbstractView <DataModelType extends AbstractPublisher<ListenerType>, ListenerType>
{
	protected DataModelType model;
	public void setModel(DataModelType newModel)
	{
		ListenerType listener = getListener();
		if (model != null)
		{
			model.unsubscribe(listener);
		}
		model = newModel;
		if (model != null)
		{
			model.subscribe(listener);
		}
	}
	
	protected abstract ListenerType getListener();
	
	
}