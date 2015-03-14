package com.eiskaltdcpp.control;

public interface AsyncTaskResultListener<T>
{
	void onCompleted(T result);
	void onError(Throwable error);
}