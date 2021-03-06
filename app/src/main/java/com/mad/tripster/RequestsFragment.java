package com.mad.tripster;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private ChildEventListener mUserListener;
    List<User> users,friends;
    private ListView mRequestListView;
    private RequestAdapter requestAdapter;
    private FirebaseAuth mAuth;
    String uid;
    User currUser;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        users = new ArrayList<User>();
        friends = new ArrayList<User>();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference().child("users");

        mRequestListView = (ListView) getActivity().findViewById(R.id.listView_requests);
        requestAdapter = new RequestAdapter(getContext(), R.layout.row_request_layout, friends);



        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user1 = dataSnapshot.getValue(User.class);
                users.add(user1);
                if(user1.getUser_id().equals(uid)){
                    currUser = user1;
                    Log.d("demo","user found in request fragment: "+user1.toString());
                }

                Log.d("demo","Inside on child added"+user1.toString());
                if(currUser!=null) {
                    if (currUser.getReceivedReq() != null) {
                        Log.d("demo", "user has requests" + currUser.getReceivedReq().toString());
                        for (String id : currUser.getReceivedReq()) {
                            Log.d("demo", "id: " + id + " users: " + users.toString());
                            for (User usr : users) {
                                Log.d("demo", "usr id:" + usr.getUser_id());
                                if (usr.getUser_id().equals(id)) {
                                    Log.d("demo", "ids matched, user added:" + usr.toString());
                                    if(!friends.contains(usr))
                                        friends.add(usr);
                                    //requestAdapter.add(usr);
                                }
                            }
                            Log.d("demo", "Friends: " + friends.toString());

                        }
                        requestAdapter = new RequestAdapter(getContext(), R.layout.row_request_layout, friends);
                        mRequestListView.setAdapter(requestAdapter);
                        requestAdapter.setNotifyOnChange(true);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUsersRef.addChildEventListener(mUserListener);


        Log.d("demo","Users list: "+users.toString());


    }
}
