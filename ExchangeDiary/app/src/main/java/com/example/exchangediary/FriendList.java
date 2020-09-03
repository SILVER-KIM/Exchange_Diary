package com.example.exchangediary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendList extends Fragment {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Search searchFragment;
    String saveNAME;
    TextView showName;
    TextView miniMESSAGE;
    ImageView profile;
    String saveMESSAGE;
    String saveID;
    ListView mFriendList;
    MyFriendAdapter mMyFriendAdapter;
    Toolbar toolbar;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    String friendID, friendNAME, friendMESSAGE;
    String photoURI;
    ProgressBar pbLogin;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyProfile.OnFragmentInteractionListener mListener;

    public FriendList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendList.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendList newInstance(String param1, String param2) {
        FriendList fragment = new FriendList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        showName = (TextView) view.findViewById(R.id.name);
        miniMESSAGE = (TextView) view.findViewById(R.id.miniMESSAGE);
        profile = (ImageView) view.findViewById(R.id.image);
        searchFragment = new Search();
        pbLogin = (ProgressBar) view.findViewById(R.id.pbLogin);


        //상단바 툴바 셋팅
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        FriendListActivity activity = (FriendListActivity) getActivity();
        //현재 액션바가 없으니 툴바를 액션바로 대체(이 activity에서 툴바를 사용한다 선언)
        activity.setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        //툴바 설정(꾸미기)
        //제목 색깔
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        //툴바 이름 바꾸기
        actionBar.setTitle("친구");
        actionBar.setDisplayShowCustomEnabled(true);

        Bundle name = getArguments();
        if(name != null) {
            saveID = name.getString("id");
            checkURI(saveID);
            saveNAME = name.getString("name");
            saveMESSAGE = name.getString("message");
            showName.setText(saveNAME);
            miniMESSAGE.setText(saveMESSAGE);
        }
        else if(name == null) {
            showName.setText("NULL");
            miniMESSAGE.setText("NULL");
        }

        mFriendList = (ListView)view.findViewById(R.id.friendList);
        mMyFriendAdapter = new MyFriendAdapter();
        mMyFriendAdapter.MyFriendAdapter(getContext());
        checkFriendID(saveID);
        mFriendList.setAdapter(mMyFriendAdapter);

        return view;
    }


    public void checkFriendID(final String myID){
        joinDatabase.child("users").child(myID).child("userFriend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    friendID = postSnapShot.getKey();
                    checkFriendINFO(friendID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkFriendINFO(final String friendID){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (friendID.equals(postSnapShot.getKey())) {
                            friendNAME = postSnapShot.child("userINFO").child("userNAME").getValue().toString();
                            friendMESSAGE = postSnapShot.child("userINFO").child("userMESSAGE").getValue().toString();
                            mMyFriendAdapter.addItem(new MyFriendItem(friendNAME, friendMESSAGE, friendID,saveID));
                            mMyFriendAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkURI(final String myID){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pbLogin.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (myID.equals(postSnapShot.getKey())) {
                            if (postSnapShot.child("userProfile").getChildrenCount() != 0) {
                                photoURI = postSnapShot.child("userProfile").child("FileURL").getValue().toString();
                                pbLogin.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity())
                                        .load(photoURI)
                                        .fit()
                                        .centerCrop()
                                        .into(profile, new Callback.EmptyCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "SUCCESS");
                                                pbLogin.setVisibility(View.GONE);
                                            }
                                        });
                                joinDatabase.removeEventListener(this);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //툴바 버튼들 넣기
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_toolbar_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    //버튼을 눌렸을 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //친구추가 버튼 눌렀을때 화면전환하기 코드 넣기
        switch(item.getItemId()) {
            case R.id.toolbar_addFriend:
                Intent intent = new Intent(getActivity(), Add.class);
                intent.putExtra("id", saveID);
                startActivity(intent);
                return true;
            case R.id.toolbar_search:
                Intent intent2 = new Intent(getActivity(), Search.class);
                startActivity(intent2);
                return true;
            case R.id.toolbar_Message:
                Intent intent3 = new Intent(getActivity(), Message.class);
                intent3.putExtra("id", saveID);
                startActivity(intent3);
                return true;
        }
        return false;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}