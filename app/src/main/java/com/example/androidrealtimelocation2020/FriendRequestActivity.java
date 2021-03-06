package com.example.androidrealtimelocation2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.androidrealtimelocation2020.Interface.IFirebaseLoadDone;
import com.example.androidrealtimelocation2020.Interface.IRecyclerItemClickListener;
import com.example.androidrealtimelocation2020.Model.User;
import com.example.androidrealtimelocation2020.Utils.Common;
import com.example.androidrealtimelocation2020.ViewHolder.FriendRequestViewHolder;
import com.example.androidrealtimelocation2020.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, FriendRequestViewHolder> adapter,searchAdapter;
    RecyclerView recycler_all_users;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        //Init View
        searchBar = (MaterialSearchBar)findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                {
                    if(adapter != null)
                    {
                        //if close search
                        recycler_all_users.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_users = (RecyclerView)findViewById(R.id.recycler_all_people);
        recycler_all_users.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_users.setLayoutManager(layoutManager);
        recycler_all_users.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager)layoutManager).getOrientation()));

        firebaseLoadDone = this;

        loadFriendRequestList();
        loadSearchData();
    }

    private void startSearch(String search_value) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST)
                .orderByChild("email")
                .startAt(search_value);


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder friendRequestViewHolder, int i, @NonNull final User user) {
                friendRequestViewHolder.txt_user_email.setText(user.getEmail());
                friendRequestViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(user, false);
                        addToAcceptList(user);
                        addUserToFriendContact(user);
                    }
                });
                friendRequestViewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(user, true);

                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friend_request, parent, false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recycler_all_users.setAdapter(searchAdapter);

    }

    private void loadFriendRequestList() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder friendRequestViewHolder, int i, @NonNull final User user) {
                friendRequestViewHolder.txt_user_email.setText(user.getEmail());
                friendRequestViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(user, false);
                        addToAcceptList(user);
                        addUserToFriendContact(user);
                    }
                });
                friendRequestViewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(user, true);

                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friend_request, parent, false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        adapter.startListening();
        recycler_all_users.setAdapter(adapter);

    }

    private void addUserToFriendContact(User user) {
        //Friend add user
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(user.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(user.getUid()).setValue(Common.loggedUser);

    }

    private void addToAcceptList(User user) {
        //User add to friend
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(user.getUid()).setValue(user);
    }

    private void deleteFriendRequest(final User model, final boolean isShowMessage)
    {
        DatabaseReference friendRequest = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);

        friendRequest.child(model.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(isShowMessage)
                            Toast.makeText(FriendRequestActivity.this, "Remove!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        if(adapter != null)
        {
            adapter.stopListening();
        }
        if(searchAdapter != null)
        {
            searchAdapter.stopListening();
        }
        super.onStop();
    }

    private void loadSearchData() {

        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user = userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });

    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
