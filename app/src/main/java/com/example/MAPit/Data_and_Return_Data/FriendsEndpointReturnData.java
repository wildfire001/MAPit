package com.example.MAPit.Data_and_Return_Data;

import com.mapit.backend.friendsApi.model.Search;

import java.util.ArrayList;

/**
 * Created by shubhashis on 2/5/2015.
 */
public class FriendsEndpointReturnData {
    private String responseMessages;
    private ArrayList <Search> dataList;

    public String getResponseMessages() {
        return responseMessages;
    }

    public void setResponseMessages(String responseMessages) {
        this.responseMessages = responseMessages;
    }

    public ArrayList<Search> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<Search> dataList) {
        this.dataList = dataList;
    }
}
