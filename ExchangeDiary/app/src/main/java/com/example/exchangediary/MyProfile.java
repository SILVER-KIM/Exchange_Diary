package com.example.exchangediary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;



import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;
import static android.view.ViewGroup.PERSISTENT_ALL_CACHES;


public class MyProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int CROP_FROM_IMAGE = 2;

    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    String storagePermission[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    String saveNAME;
    TextView showID;
    EditText showNAME;
    EditText showMESSAGE;
    String idValue;
    String saveMESSAGE;
    int flag;
    ProgressBar pbLogin;

    private Uri imgUri, photoURI;
    String profileURI;
    private ImageView profile;
    private String mCurrentPhotoPath;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    String saveURI;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FirebaseStorage storage;

    public MyProfile() {
        // Required empty public constructor
    }

    public static MyProfile newInstance(String param1, String param2) {
        MyProfile fragment = new MyProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        Button renew = (Button) view.findViewById(R.id.Renew);
        final ImageButton cameraBtn = (ImageButton) view.findViewById(R.id.cameraBtn);

        profile = (ImageView) view.findViewById(R.id.profile);
        pbLogin = (ProgressBar) view.findViewById(R.id.pbLogin);


        showID = (TextView) view.findViewById(R.id.showID);
        showNAME = (EditText) view.findViewById(R.id.showNAME);
        showMESSAGE = (EditText) view.findViewById(R.id.showMESSAGE);

        // FriendListActivity에서 보낸 Bundle을 받음
        Bundle id = getArguments();
        if (id != null) {
            idValue = id.getString("id");
            checkURI(idValue);
            saveNAME = id.getString("name");
            saveMESSAGE = id.getString("message");
            showID.setText(idValue);
            showNAME.setText(saveNAME);
            showMESSAGE.setText(saveMESSAGE);
        } else if (id == null) {
            showID.setText("NULL");
            showNAME.setText("NULL");
            showMESSAGE.setText("NULL");
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPM();
            }
        });

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String newNAME = showNAME.getText().toString();
                        String newMESSAGE = showMESSAGE.getText().toString();

                        Bundle id = getArguments();
                        idValue = id.getString("id");
                        showID.setText(idValue);

                        // 프로필창에서 닉네임, 상태메세지를 변경하면 DB에 저장, 로드되도록 하는 기능
                        if (dataSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                if (idValue.equals(postSnapShot.getKey())) {
                                    joinDatabase.child("users").child(idValue).child("userINFO").child("userNAME").setValue(newNAME);
                                    joinDatabase.child("users").child(idValue).child("userINFO").child("userMESSAGE").setValue(newMESSAGE);
                                    Toast.makeText(getContext(), "적용되었습니다.", Toast.LENGTH_SHORT).show();
                                    joinDatabase.removeEventListener(this);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        return view;
    }

    private void makeDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setTitle("프로필 사진 설정").setCancelable(false).setPositiveButton("사진촬영", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.v("알림", "다이얼로그 > 사진촬영 선택");
                flag = 0;
                takePhoto();
            }
        }).setNeutralButton("앨범선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("알림", "다이얼로그 > 앨범선택 선택");
                flag = 1;
                selectAlbum();
            }
        }).setNegativeButton("취소 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("알림", "다이얼로그 > 취소 선택");
                dialog.cancel();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }


    public void checkPM() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            makeDialog();
        } else {
            makeDialog();
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
        }
    }

    public void selectAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }

    public void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(getContext(),"com.example.exchangediary", photoFile);
                    imgUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null) {
                    try{
                        photoURI = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
                        profile.setImageBitmap(bitmap);
                        goToStorage();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            }
            case FROM_CAMERA : {
                //촬영
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                    //이미지뷰에 이미지셋팅
                    profile.setImageURI(imgUri);
                    goToStorage();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public File createImageFile() throws IOException {
        String imgFileName = idValue + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ExchangeDiary");
        if (!storageDir.exists()) {
            Log.v("알림", "storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        Toast.makeText(getContext(),"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();

    }

    public void goToStorage(){
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("userProfile").child(idValue);
        UploadTask uploadTask;

        Uri file = null;
        if(flag == 0){
            file = Uri.fromFile(new File(mCurrentPhotoPath));
        }
        else if(flag == 1){
            file = photoURI;
        }
        uploadTask = exchangeStorage.putFile(file);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("프로필 사진을 저장중입니다.");
        progressDialog.show();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.v("알림", "사진 업로드 실패");
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getURI();
                progressDialog.dismiss();
            }
        });
    }

    public void getURI(){
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("userProfile").child(idValue);
        exchangeStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileURI = String.valueOf(uri);
                joinDatabase.child("users").child(idValue).child("userProfile").child("FileURL").setValue(profileURI);
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

                                saveURI = postSnapShot.child("userProfile").child("FileURL").getValue().toString();
                                pbLogin.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity())
                                        .load(saveURI)
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
