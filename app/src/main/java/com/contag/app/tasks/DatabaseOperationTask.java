package com.contag.app.tasks;

import android.os.AsyncTask;

import com.contag.app.listener.DatabaseRequestListener;

public class DatabaseOperationTask extends AsyncTask<Object, Void, Object> {

    private DatabaseRequestListener mDatabaseRequestListener;

    public DatabaseOperationTask(DatabaseRequestListener mDatabaseRequestListener) {
        this.mDatabaseRequestListener = mDatabaseRequestListener;
    }

    @Override
    protected void onPreExecute() {
        this.mDatabaseRequestListener.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... params) {
        return mDatabaseRequestListener.onRequestExecute();
    }

    @Override
    protected void onPostExecute(Object result) {
        mDatabaseRequestListener.onPostExecute(result);
    }
}
