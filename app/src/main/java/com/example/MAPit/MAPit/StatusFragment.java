package com.example.MAPit.MAPit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.MAPit.Commands_and_Properties.Commands;
import com.example.MAPit.Commands_and_Properties.DatastoreKindNames;
import com.example.MAPit.Commands_and_Properties.PropertyNames;
import com.example.MAPit.Data_and_Return_Data.Data;
import com.example.MAPit.Volley.adapter.StatusListAdapter;
import com.example.MAPit.Volley.data.StatusListItem;
import com.google.android.gms.maps.model.LatLng;
import com.mapit.backend.statusApi.model.StatusData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SETU on 1/20/2015.
 */
public class StatusFragment extends Fragment {

    public StatusFragment() {
        setHasOptionsMenu(true);
    }

    private ListView listView;
    private StatusListAdapter statuslistAdapter;
    private List<StatusListItem> statusListItems;
    public String command;
    public ArrayList<StatusData> passThisData;
    public ArrayList <String> loc;
    public Bundle bundle;
    public Bundle data;
    StatusListItem item;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friend_status, null, false);
        listView = (ListView) v.findViewById(R.id.list_frnd_status);
        loc = new ArrayList<>();
        statusListItems = new ArrayList<StatusListItem>();
        statuslistAdapter = new StatusListAdapter(getActivity(), statusListItems);
        listView.setAdapter(statuslistAdapter);
        data = getArguments();
        command = data.getString(Commands.Fragment_Caller.getCommand());

        if (command.equals(Commands.Called_From_Home.getCommand()))
            populateFriendsLatestStatus();
        else if (command.equals(Commands.Called_From_Info.getCommand()))
            populatePersonStatus();
        else if (command.equals(Commands.Called_From_Group.getCommand()))
            populateGroupStatus();
        else if(command.equals(Commands.Called_From_MyWall.getCommand()))
            populatePersonStatus();


        //listener for each listitem of friend status
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new Friends_Status_Comment_Fragment();
                StatusData st = passThisData.get(position);

                bundle = new Bundle();
                String groupKey = data.getString(PropertyNames.Status_groupKey.getProperty());
                if(groupKey != null) {
                    st.setGroupKey(groupKey);
                }



                ArrayList<StatusData> passData = new ArrayList<StatusData>();
                passData.add(st);

                bundle.putString(Commands.Fragment_Caller.getCommand(), Commands.Called_From_Status.getCommand());
                bundle.putSerializable(Commands.Arraylist_Values.getCommand(), passData);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        return v;
    }

    public void populateGroupStatus(){
        data = getArguments();
        String groupKey = data.getString(PropertyNames.Status_groupKey.getProperty());

        Data d = new Data();
        d.setCommand(Commands.Status_showGroupStatus.getCommand());

        StatusData s= new StatusData();
        s.setKind(DatastoreKindNames.StatusInGroup.getKind());
        s.setGroupKey(groupKey);

        new StatusEndpointCommunicator() {
            @Override
            protected void onPostExecute(ArrayList<StatusData> result) throws NullPointerException{

                super.onPostExecute(result);
                passThisData = result;
                try {
                    populate(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.execute(new Pair<Data, StatusData>(d, s));
    }

    public void populatePersonStatus() {
        data = getArguments();

        String personMail = data.getString(PropertyNames.Userinfo_Mail.getProperty());

        Data d = new Data();
        d.setCommand(Commands.Status_showIndividualStatus.getCommand());


        StatusData s = new StatusData();
        s.setKind(DatastoreKindNames.StatusbyIndividual.getKind());
        s.setPersonMail(personMail);


        new StatusEndpointCommunicator() {
            @Override
            protected void onPostExecute(ArrayList<StatusData> result) {

                super.onPostExecute(result);
                passThisData = result;
                try {
                    populate(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(new Pair<Data, StatusData>(d, s));
    }


    public void populateFriendsLatestStatus() {
        Bundle data = getArguments();
        ArrayList<StatusData> result = (ArrayList<StatusData>) data.getSerializable(Commands.Arraylist_Values.getCommand());
        passThisData = result;
        try {
            populate(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populate(ArrayList<StatusData> result) throws Exception {

        statusListItems.clear();
        statuslistAdapter.notifyDataSetChanged();


        for (int i = 0; i < result.size(); i++) {
            StatusData statusData = result.get(i);

            item = new StatusListItem();
            item.setName(statusData.getPersonName());
            item.setStatus(statusData.getStatus());
            item.setLocation(statusData.getLocation());


            if (statusData.getStatusPhoto() != null) {
                Log.v("pop", "called");
                item.setImge(statusData.getStatusPhoto());
            }
            if (statusData.getProfilePic() != null) {
                item.setProfilePic(statusData.getProfilePic());
            } else {

            }

            statusListItems.add(item);

        }

        statuslistAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home_fragment, menu);
        if(command.equals(Commands.Called_From_Group.getCommand())) {
            menu.findItem(R.id.switch_view_to_list).setTitle("Add New Information");
            menu.add(0,1,1,"Switch to Map");
        }
        else if(command.equals(Commands.Called_From_MyWall.getCommand())){
            menu.clear();
        }
        else {

            menu.findItem(R.id.switch_view_to_list).setTitle("Switch Back to Map");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.switch_view_to_list:
                Fragment fragment = null;
                if (command.equals(Commands.Called_From_Home.getCommand())) {
                    fragment = new HomeFragment();

                } else if (command.equals(Commands.Called_From_Info.getCommand())) {
                    bundle = new Bundle();
                    bundle.putString(Commands.ForMarkerView.getCommand(), Commands.Called_From_Status.getCommand());
                    bundle.putSerializable(Commands.Arraylist_Values.getCommand(), passThisData);
                    fragment = new Marker_MapView();
                    fragment.setArguments(bundle);

                }
                else if(command.equals(Commands.Called_From_Group.getCommand())) {
                    Boolean logged = data.getBoolean(PropertyNames.Group_logged.getProperty());
                    if (logged) {
                        fragment = new OnlyGoogleMap();
                        Bundle d = new Bundle();
                        String groupKey = data.getString(PropertyNames.Status_groupKey.getProperty());
                        d.putString(Commands.Group_Key.getCommand(), groupKey);
                        d.putString(Commands.SearchAndADD.getCommand(), Commands.Status_add.getCommand());
                        d.putBoolean(PropertyNames.Group_logged.getProperty(), true);
                        fragment.setArguments(d);

                    } else {
                        Toast.makeText(getActivity(), "Sorry, You haven't joined this group yet!", Toast.LENGTH_LONG).show();
                        return true;
                    }

                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            case 1:
                Fragment fragment1 = new Marker_MapView();
                bundle = new Bundle();
                bundle.putString(Commands.ForMarkerView.getCommand(), Commands.Grp_Status_Info.getCommand());
                bundle.putSerializable(Commands.Arraylist_Values.getCommand(), passThisData);
                fragment1.setArguments(bundle);
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.replace(R.id.frame_container, fragment1);
                transaction1.addToBackStack(null);
                transaction1.commit();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
