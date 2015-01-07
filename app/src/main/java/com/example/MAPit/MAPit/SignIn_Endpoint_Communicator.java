package com.example.MAPit.MAPit;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.MAPit.Commands_and_Properties.Commands;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.mapit.backend.userinfoModelApi.model.UserinfoModel;
import com.mapit.backend.userinfoModelApi.model.ResponseMessages;
import com.mapit.backend.userinfoModelApi.UserinfoModelApi;
import com.mapit.backend.userinfoModelApi.model.UserinfoModelCollection;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shubhashis on 1/8/2015.
 */


public class SignIn_Endpoint_Communicator extends AsyncTask<Pair<Context, UserinfoModel>, Void, UserinfoModelCollection> {
    private Context maincontext;
    private UserinfoModelApi userinfo_api;
    private UserinfoModel userdata;
    private manipulate_Signin ms;
    @Override
    protected UserinfoModelCollection doInBackground(Pair<Context, UserinfoModel>... params) {
        if(userinfo_api == null) {  // Only do this once
            UserinfoModelApi.Builder builder = new UserinfoModelApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    //.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            userinfo_api = builder.build();
        }
        maincontext = params[0].first;
        userdata = params[0].second;
        Log.v("sign in", "1");
        try {
            UserinfoModelCollection UserinfoResult = userinfo_api.getUserinfo(Commands.Userinfo_getpass.getCommand(), userdata).execute();
            Log.v("sign in", "2");
            return UserinfoResult;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(UserinfoModelCollection result){
        ms = (manipulate_Signin) ((Activity) maincontext);

        ArrayList <UserinfoModel> result_list = (ArrayList<UserinfoModel>) result.getItems();
        UserinfoModel logininfo = result_list.get(0);

        Log.v("sign in", "3");
        ms.setResponseMessage(logininfo);
    }

    public interface manipulate_Signin{
        public void setResponseMessage(UserinfoModel logininfo);
    }

}