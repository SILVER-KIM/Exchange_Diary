package com.example.exchangediary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFriend.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFriend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriend extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();

    // 로그 필터 변수
    public static final String TAG = "Test_Alert_Dialog";


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String idValue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;

    private OnFragmentInteractionListener mListener;

    public AddFriend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriend.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriend newInstance(String param1, String param2) {
        AddFriend fragment = new AddFriend();
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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        Bundle id = getArguments();
        if(id != null) {
            idValue = id.getString("id");
        }

        mListView = (ListView)view.findViewById(R.id.listView);

        ArrayList<String> data = new ArrayList<>();
        data.add("비밀번호 변경");
        data.add("로그아웃");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, data);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //비밀번호 변경을 눌렀을 때
                    case 0:
                        Intent intent = new Intent(getActivity(), ChangePwActivity.class);
                        intent.putExtra("id", idValue);
                        startActivity(intent);
                        break;
                    case 1:
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                        ad.setTitle("환경설정");       // 제목 설정
                        ad.setMessage("로그아웃 하시겠습니까?");   // 내용 설정

                        // 확인 버튼 설정
                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"Yes Btn Click");
                                dialog.dismiss();
                                getActivity().finish();

                                //닫기
                                // Event
                            }
                        });

                        // 취소 버튼 설정
                        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"No Btn Click");
                                dialog.dismiss();     //닫기
                                // Event
                            }
                        });

                        // 창 띄우기
                        ad.show();


                }
            }
        });
        return view;
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