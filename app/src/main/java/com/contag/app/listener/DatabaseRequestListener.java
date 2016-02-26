package com.contag.app.listener;

/**
 * Created by tanaytandon on 27/01/16.
 */
public interface DatabaseRequestListener {

    public void onPreExecute();

    public Object onRequestExecute();

    public void onPostExecute(Object responseObject);
}
