package com.example.exchangediary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowUser.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowUser extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    Button addBtn;
    String saveNAME;
    TextView showNAME;
    ImageView showProfile;
    String myID;
    String type;
    String friendID;
    String photoURI;
    int sendCheck = 0;
    int receiveCheck = 0;
    int friendCheck = 0;
    ProgressBar pbLoding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShowUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowUser.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowUser newInstance(String param1, String param2) {
        ShowUser fragment = new ShowUser();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_show_user, container, false);
        showNAME = (TextView)view.findViewById(R.id.showAddName);
        showProfile = (ImageView)view.findViewById(R.id.showImage);
        pbLoding = (ProgressBar) view.findViewById(R.id.pbLoding);
        addBtn = (Button)view.findViewById(R.id.addBtn);

        Bundle name = getArguments();

        type = name.getString("type");
        if(type.equals("search"))
            addBtn.setText("일기쓰기");
        else if(type.equals("add"))
            addBtn.setText("친구추가");

        if(name != null) {
            myID =  name.getString("myID");
            friendID = name.getString("friendID");
            checkURI(friendID);
            saveNAME = name.getString("name");
            showNAME.setText(saveNAME);
        }
        else if(name == null) {
            showNAME.setText("NULL");
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("add"))
                    GotoAddFriend(friendID, myID);
                else if(type.equals("search"))
                    GotoNewBookShelf(myID, friendID);
            }
        });

        return view;
    }

    public void GotoNewBookShelf(final String myID, final String friendID){
        Intent intent = new Intent(getContext(), NewBookshelf.class);
        intent.putExtra("myID", myID);
        intent.putExtra("friendID", friendID);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void checkURI(final String friendID){
        joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (friendID.equals(postSnapShot.getKey())) {
                            if (postSnapShot.child("userProfile").getChildrenCount() != 0) {
                                photoURI = postSnapShot.child("userProfile").child("FileURL").getValue().toString();
                                pbLoding.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity())
                                        .load(photoURI)
                                        .fit()
                                        .centerCrop()
                                        .into(showProfile, new Callback.EmptyCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "SUCCESS");
                                                pbLoding.setVisibility(View.GONE);
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

    public void requetFriend(String FriendID, String myID) {
        RequestFriend request = new RequestFriend(FriendID, myID);
        joinDatabase.child("request").push().setValue(request);

        Toast.makeText(getContext(), "친구신청 완료", Toast.LENGTH_SHORT).show();
    }


    public void GotoAddFriend(final String FriendID, final String MyID){
        joinDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sendCheck = 0;
                receiveCheck = 0;
                if(dataSnapshot.getChildrenCount() != 0) {
                    // 내가 친구신청을 보낸 적이 있는지 검사하는 기능
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if(MyID.equals(postSnapShot.child("Sender").getValue())) {
                            if (FriendID.equals(postSnapShot.child("Receiver").getValue())){
                                sendCheck = 100;
                                break;
                            }
                        }
                    }
                    // 상대방이 나에게 친구신청을 보낸 적이 있는지 검사하는 기능
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if(MyID.equals(postSnapShot.child("Receiver").getValue())){
                            if (FriendID.equals(postSnapShot.child("Sender").getValue())){
                                receiveCheck = 100;
                                break;
                            }
                        }
                    }
                    if(sendCheck != 100 & receiveCheck != 100)
                        GotoAddFriend2(friendID, myID);
                    else if(sendCheck == 100)
                        Toast.makeText(getContext(), "이미 친구신청한 사용자입니다.", Toast.LENGTH_SHORT).show();
                    else if(receiveCheck == 100)
                        Toast.makeText(getContext(), "사용자가 이미 친구신청을 보내왔습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(dataSnapshot.getChildrenCount() == 0)
                    GotoAddFriend2(friendID, myID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void GotoAddFriend2(final String FriendID, final String MyID){
        joinDatabase.child("users").child(MyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendCheck = 0;
                String poName = "userFriend";
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    if (poName.equals(postSnapShot.getKey())) {
                        if(postSnapShot.hasChild(FriendID)) {
                            friendCheck = 100;
                            break;
                        }
                    }
                }
                if(friendCheck != 100)
                    requetFriend(FriendID, MyID);
                else if(friendCheck == 100)
                    Toast.makeText(getContext(), "친구한테 친구신청 하지마!", Toast.LENGTH_SHORT).show();
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
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
