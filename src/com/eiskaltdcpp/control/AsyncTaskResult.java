package com.eiskaltdcpp.control;

public class AsyncTaskResult<T>
{
	private Throwable exception;
	private T value;
	
	public AsyncTaskResult(T value)
	{
		this.value = value;
	}
	
	public AsyncTaskResult(Throwable exception)
	{
		this.exception = exception; 
	}
	
	public Throwable getError()
	{
		return exception;
	}
	
	public T getValue()
	{
		return value;
	}		
}