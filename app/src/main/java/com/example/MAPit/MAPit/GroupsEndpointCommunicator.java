package com.example.MAPit.MAPit;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.example.MAPit.Commands_and_Properties.Commands;
import com.example.MAPit.Data_and_Return_Data.Data;
import com.example.MAPit.Data_and_Return_Data.GroupsEndpointReturnData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.mapit.backend.groupApi.GroupApi;
import com.mapit.backend.groupApi.model.Groups;
import com.mapit.backend.groupApi.model.ResponseMessages;
import com.mapit.backend.groupApi.model.SearchCollection;
import com.mapit.backend.groupApi.model.Search;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shubhashis on 2/9/2015.
 */
public class GroupsEndpointCommunicator extends AsyncTask <Pair<Data, Groups>, Void, GroupsEndpointReturnData>{
    GroupApi groupApi;
    private String usermail, command, pattern;
    @Override
    protected GroupsEndpointReturnData doInBackground(Pair<Data, Groups>... params) {
        if(groupApi == null){
            GroupApi.Builder builder = new GroupApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/") //Genymotion Config
                            //.setRootUrl("http://192.168.10.1:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            groupApi = builder.build();
        }

        usermail = params[0].first.getUsermail();
        command = params[0].first.getCommand();
        pattern = params[0].first.getExtra();

        Groups groupinfo = params[0].second;

        if(command.equals(Commands.Group_Create.getCommand())){
            try {
                ResponseMessages rm = groupApi.createGroup(groupinfo).execute();
                String response = rm.getResponseMessage();
                GroupsEndpointReturnData returnData = new GroupsEndpointReturnData();
                returnData.setResponseMessages(response);

                return returnData;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else if(command.equals(Commands.Group_Remove.getCommand())){
            try {
                ResponseMessages rm = groupApi.removeGroup(groupinfo).execute();
                String response = rm.getResponseMessage();
                GroupsEndpointReturnData returnData = new GroupsEndpointReturnData();
                returnData.setResponseMessages(response);

                return returnData;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else if(command.equals(Commands.Group_fetch_myGroups.getCommand())){
            try {
                SearchCollection myGroupCollection = groupApi.getMyGroups(usermail).execute();
                ArrayList<Search> myGroupList = (ArrayList <Search>) myGroupCollection.getItems();


                GroupsEndpointReturnData returnData = new GroupsEndpointReturnData();
                returnData.setDataList(myGroupList);

                return returnData;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else if(command.equals(Commands.Group_fetch_GroupsnotMine.getCommand())){
            try {
                SearchCollection myGroupCollection = groupApi.getGroupsNotMine(usermail, pattern).execute();
                ArrayList<Search> myGroupList = (ArrayList <Search>) myGroupCollection.getItems();


                GroupsEndpointReturnData returnData = new GroupsEndpointReturnData();
                returnData.setDataList(myGroupList);

                return returnData;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
