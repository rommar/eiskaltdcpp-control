package com.eiskaltdcpp.control;

import android.os.AsyncTask;


abstract class AsyncAction<ResultT> extends AsyncTask<Void, Void, AsyncTaskResult<ResultT> >
{

	protected abstract ResultT execAction();
	protected void onError(Throwable error)
	{
		if (listener != null)
			listener.onError(error);
	}
	protected void onCancelled() {}
	protected void onCompleted(ResultT result)
	{
		if (listener != null)
			listener.onCompleted(result);
	}
	
	private AsyncTaskResultListener<ResultT> listener;
	
	public AsyncAction(AsyncTaskResultListener<ResultT> resultListener)
	{
		super();
		listener = resultListener;
	}
	
	@Override
	protected final AsyncTaskResult<ResultT> doInBackground(Void... params)
	{
		try
		{
			ResultT result = execAction();
			return new AsyncTaskResult<ResultT>(result);
		}
		catch(Throwable error)
		{
			return new AsyncTaskResult<ResultT>(error);
		}
	}
	
	@Override
	protected final void onPostExecute(AsyncTaskResult<ResultT> result)
	{
		Throwable error = result.getError();
		if (error != null)
		{
			onError(error);
		}
		else if (isCancelled())
		{
			onCancelled();
		}
		else
		{	
			onCompleted(result.getValue());
		}
	}

	
}