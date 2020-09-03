package com.example.exchangediary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.support.constraint.Constraints.TAG;


public class PictureDiary extends AppCompatActivity {
    ProgressDialog progressDialog;
    private Spinner spinner_weather;
    private Spinner spinner_feeling;
    private ImageButton camera_image;
    String[] spinnerNames;
    String[] spinnerNames2;
    int[] spinnerImages;
    int[] spinnerImages2;
    int selected_weather_idx = 0;
    int selected_feeling_idx = 0;
    int flag;
    int mYear, mMonth, mDay;
    TextView mTxtDate;
    ProgressBar progress;
    MyView_2 vw;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int CROP_FROM_IMAGE = 2;

    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    String storagePermission[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    Uri uri;
    private Uri imgUri, photoURI;
    private ImageView profile;
    private String mCurrentPhotoPath;
    private static final int GOTOIMAGE = 0;
    private static final int FROM_ALBUM = 1;
    String saveURI;
    String profileURI;
    String idValue, friendID, diaryName, contentsOrder;
    String writeContent, writeTitle, writeToday;
    EditText textDiary, title;
    Button send;
    ImageButton plus;
    RelativeLayout background;
    private FirebaseStorage storage;

    private MyProfile.OnFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_picture_diary);
        storage = FirebaseStorage.getInstance();

        Intent intent0 = getIntent();
        idValue = intent0.getStringExtra("myID");
        friendID = intent0.getStringExtra("friendID");
        diaryName = intent0.getStringExtra("diaryNAME");

        textDiary = (EditText)findViewById(R.id.textDiary);
        title =  (EditText)findViewById(R.id.title);
        plus = (ImageButton) findViewById(R.id.plus);
        send = (Button)findViewById(R.id.send);
        mTxtDate = (TextView) findViewById(R.id.txtdate);

        //현재 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언.

        Calendar cal = new GregorianCalendar();

        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        //화면에 텍스트뷰에 업데이트 해줌.
        UpdateNow();


        //스피너 코드
        spinner_weather = (Spinner) findViewById(R.id.spinner_main_weather);
        spinner_feeling = (Spinner) findViewById(R.id.spinner_main_feeling);
        camera_image = (ImageButton) findViewById(R.id.camera_image);
        profile = (ImageView) findViewById(R.id.profile);
        background = (RelativeLayout)findViewById(R.id.background);


        // 스피너에 보여줄 문자열과 이미지 목록을 작성.
        spinnerNames = new String[]{"화창함", "비", "눈", "바람", "안개", "번개", "밤"};
        spinnerImages = new int[]{
                R.drawable.sun
                , R.drawable.rain
                , R.drawable.snow
                , R.drawable.wind
                , R.drawable.fog
                , R.drawable.storm
                , R.drawable.night};

        spinnerNames2 = new String[]{"미소", "웃음", "사랑", "뽀뽀", "화남", "눈물", "정색", "아픔", "침묵", "놀람"};
        spinnerImages2 = new int[]{
                R.drawable.happy
                , R.drawable.sohappy
                , R.drawable.love
                , R.drawable.kissing
                , R.drawable.angry
                , R.drawable.unhappy
                , R.drawable.confused
                , R.drawable.ill
                , R.drawable.quiet
                , R.drawable.surprised};


        // 어댑터와 스피너를 연결합니다.

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, spinnerNames, spinnerImages);
        spinner_weather.setAdapter(customSpinnerAdapter);
        CustomSpinnerAdapter customSpinnerAdapter2 = new CustomSpinnerAdapter(this, spinnerNames2, spinnerImages2);
        spinner_feeling.setAdapter(customSpinnerAdapter2);

        // 스피너에서 아이템 선택시 호출하도록 함.
        spinner_weather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_weather_idx = spinner_weather.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_feeling.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_feeling_idx = spinner_feeling.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 일기에 그림 저장하는 버튼 클릭 시!
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PictureDiary.this, MainImageDraw_2.class);
                startActivityForResult(intent, GOTOIMAGE);
            }
        });

        //보내기 버튼
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().equals("") && !textDiary.getText().toString().equals("") && profile.getDrawable() != null) {
                    progressDialog = new ProgressDialog(PictureDiary.this);
                    progressDialog.setMessage("일기를 보내고 있습니다(◍•ᴗ•◍)\n");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    checkSequence();
                }
                else
                    Toast.makeText(getApplicationContext(), "항목을 다 채워주세요٩(๑´3｀๑)۶", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == GOTOIMAGE){
            uri = data.getParcelableExtra("image");
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .fit()
                        .into(profile, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "SUCCESS");
                                background.setBackgroundResource(R.drawable.background_edge);
                                plus.setVisibility(View.GONE);
                            }
                        });
                profile.setImageBitmap(bitmap);
            }catch(IOException e){
            }
        }
    }

    public void goToStorage(){
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("DiaryPicturesContents")
                .child(idValue).child(friendID).child(diaryName).child(contentsOrder);
        StorageReference exchangeStorage2 = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("DiaryPicturesContents")
                .child(friendID).child(idValue).child(diaryName).child(contentsOrder);
        UploadTask uploadTask, uploadTask2;

        Uri file = null;
        file = uri;

        uploadTask = exchangeStorage.putFile(file);
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
            }
        });
        uploadTask2 = exchangeStorage2.putFile(file);
    }

    private void goToWriteDiary() {
        writeContent = textDiary.getText().toString();
        writeTitle = title.getText().toString();
        writeToday = mTxtDate.getText().toString();

        // 내 다이어리에 넣는 코드
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Text").setValue(writeContent);          // 일기 내용
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Title").setValue(writeTitle);           // 일기 제목
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Date").setValue(writeToday);            // 날짜
        // 스피너 넣는 코드
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("spinner_weather").setValue(selected_weather_idx);
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("spinner_feeling").setValue(selected_feeling_idx);

        // 친구 다이어리에 넣는 코드
        joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Text").setValue(writeContent);          // 일기 내용
        joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Title").setValue(writeTitle);           // 일기 제목
        joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("Date").setValue(writeToday);            // 날짜
        // 스피너 넣는 코드
        joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("spinner_weather").setValue(selected_weather_idx);
        joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName)
                .child("diaryContents").child(contentsOrder).child("spinner_feeling").setValue(selected_feeling_idx);
        goToStorage();
    }

    // 자식노드의 개수를 가져와서 다음 페이지를 contentsOrder에 넣어주는 함수
    private void checkSequence() {
        joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("diaryContents")) {
                    long num = dataSnapshot.child("diaryContents").getChildrenCount() + 1;
                    contentsOrder = String.valueOf(num);
                    goToWriteDiary();
                    joinDatabase.removeEventListener(this);
                    return;
                }
                else {
                    contentsOrder = "1";
                    goToWriteDiary();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getURI(){
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("DiaryPicturesContents")
                .child(idValue).child(friendID).child(diaryName).child(contentsOrder);
        exchangeStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileURI = String.valueOf(uri);
                joinDatabase.child("BookShelf").child(idValue).child("Friends").child(friendID).child(diaryName).child("diaryContents").child(contentsOrder).child("pictureURI").setValue(profileURI);
                joinDatabase.child("BookShelf").child(friendID).child("Friends").child(idValue).child(diaryName).child("diaryContents").child(contentsOrder).child("pictureURI").setValue(profileURI);
                progressDialog.dismiss();
                sendDiary();
            }
        });
    }

    public void sendDiary(){
        joinDatabase.child("sendDiary").child(idValue).child("Friends").child(friendID).child(diaryName).child("diaryURI").removeValue();
        joinDatabase.child("sendDiary").child(friendID).child("Friends").child(idValue).child(diaryName).child("diaryURI").setValue(diaryName);
        Intent intent2 = new Intent(getApplicationContext(), NewBookshelf.class);
        intent2.putExtra("myID", idValue);
        intent2.putExtra("friendID", friendID);
        startActivity(intent2);
        finish();
    }


    //  여기 밑으론 달력 관련 코드임

    public void mOnClick(View v) {
        switch (v.getId()) {
            //날짜 대화상자 버튼이 눌리면 대화상자를 보여줌
            case R.id.date:
                //여기서 리스너도 등록함
                new DatePickerDialog(PictureDiary.this, mDateSetListener, mYear, mMonth, mDay).show();
                break;
        }
    }

    //날짜 대화상자 리스너 부분

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            //사용자가 입력한 값을 가져온뒤
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            //텍스트뷰의 값을 업데이트함

            UpdateNow();

        }
    };
    //텍스트뷰의 값을 업데이트 하는 메소드

    void UpdateNow(){
        mTxtDate.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));
    }



}