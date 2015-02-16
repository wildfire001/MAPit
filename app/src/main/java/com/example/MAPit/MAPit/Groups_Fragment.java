package com.example.MAPit.MAPit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.MAPit.Commands_and_Properties.Commands;
import com.example.MAPit.Commands_and_Properties.PropertyNames;
import com.example.MAPit.Data_and_Return_Data.Data;
import com.example.MAPit.Data_and_Return_Data.GroupsEndpointReturnData;
import com.example.MAPit.Volley.adapter.SearchListAdapter;
import com.example.MAPit.Volley.data.SearchListItem;
import com.mapit.backend.groupApi.model.Groups;
import com.mapit.backend.groupApi.model.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SETU on 1/24/2015.
 */
public class Groups_Fragment extends Fragment {
    String usermail;
    private EditText searchBox;
    private ListView listview;

    private SearchListAdapter searchListAdapter;
    private List<SearchListItem> listItems;

    public Groups_Fragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_list_adapter_layout, null, false);

        searchBox = (EditText) v.findViewById(R.id.searchBox);
        listview = (ListView) v.findViewById(R.id.listview);
        listItems = new ArrayList<SearchListItem>();
        searchListAdapter = new SearchListAdapter(getActivity(), listItems);
        listview.setAdapter(searchListAdapter);

        showMyGroups();
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pattern = searchBox.getText().toString().toLowerCase(Locale.getDefault());

                if(pattern.length() != 0)
                    searchGroups(pattern);
                else
                    showMyGroups();

            }
        });

        return v;
    }

    public String getmail(){
        Bundle mailBundle = ((SlidingDrawerActivity)getActivity()).getEmail();
        String mail = mailBundle.getString(PropertyNames.Userinfo_Mail.getProperty());
        return mail;
    }


    public void showMyGroups(){
        Data info = new Data();
        info.setContext(getActivity());
        info.setCommand(Commands.Group_fetch_myGroups.getCommand());
        info.setUsermail(getmail());

        Groups g = new Groups();

        new GroupsEndpointCommunicator(){
            @Override
            protected void onPostExecute(GroupsEndpointReturnData result){

                super.onPostExecute(result);

                ArrayList <Search> res = result.getDataList();
                PopulateMyGroups(res);

            }
        }.execute(new Pair<Data, Groups>(info, g));
    }

    public void PopulateMyGroups(ArrayList<Search> a){
        listItems.clear();
        searchListAdapter.notifyDataSetChanged();

        for (int i = 0; i < a.size(); i++) {
            Search s = a.get(i);

            SearchListItem item = new SearchListItem();
            item.setName(s.getData());
            item.setLocation("Khulna");
            item.setKey(s.getKey());
            item.setButton(Commands.Group_Remove.getCommand());
            item.setExtra(getmail());
            listItems.add(item);
        }

        // notify data changes to list adapter
        searchListAdapter.notifyDataSetChanged();

    }

    public void searchGroups(String pattern){
        Search searchProperty = new Search();
        searchProperty.setData(pattern);

        Data info = new Data();
        info.setContext(getActivity());
        info.setCommand(Commands.Group_fetch_GroupsnotMine.getCommand());
        info.setUsermail(getmail());
        info.setExtra(pattern);

        Groups g = new Groups();

        new GroupsEndpointCommunicator(){
            @Override
            protected void onPostExecute(GroupsEndpointReturnData result){

                super.onPostExecute(result);

                ArrayList <Search> res = result.getDataList();
                PopulateSearchGroup(res);

            }
        }.execute(new Pair<Data, Groups>(info, g));
    }

    public void PopulateSearchGroup(ArrayList<Search> a){
        listItems.clear();
        searchListAdapter.notifyDataSetChanged();

        for (int i = 0; i < a.size(); i++) {
            Search s = a.get(i);

            SearchListItem item = new SearchListItem();
            item.setName(s.getData());
            item.setLocation("Khulna");
            item.setButton(Commands.Group_Join_Group.getCommand());
            item.setKey(s.getKey());
            item.setExtra(getmail());
            listItems.add(item);
        }

        // notify data changes to list adapter
        searchListAdapter.notifyDataSetChanged();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_group_adding, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager;
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.create_group:

                fragment = new OnlyGoogleMap();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container,fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            case R.id.my_groups:

                fragment = new MyOwnGroupsFragment();
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.replace(R.id.frame_container,fragment);
                transaction1.addToBackStack(null);
                transaction1.commit();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}