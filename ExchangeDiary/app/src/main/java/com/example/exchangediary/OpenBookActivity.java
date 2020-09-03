package com.example.exchangediary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.support.constraint.Constraints.TAG;

public class OpenBookActivity extends AppCompatActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();

    private Spinner spinner_weather;
    private Spinner spinner_feeling;
    private Spinner spinner_stemp;
    String[] spinnerNames;
    String[] spinnerNames2;
    String[] spinnerNames3;
    int[] spinnerImages;
    int[] spinnerImages2;
    int[] spinnerImages3;

    EditText edit_comment;
    Button previous, next, plus, commentSave;
    TextView diaryTitle, diaryText, diaryDate, diaryPage;
    ImageView diaryPic;
    String myID, friendID, diaryName;
    String title, text, date, photo, page, comment;
    String saveComment;
    int saveStamp;
    int weather, feeling;

    String number = "0";
    int allNumber;

    int selected_stamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        Intent intent0 = getIntent();
        myID = intent0.getStringExtra("myID");
        friendID = intent0.getStringExtra("friendID");
        diaryName = intent0.getStringExtra("diaryNAME");

        diaryTitle = (TextView)findViewById(R.id.title);
        diaryText = (TextView)findViewById(R.id.textDiary);
        diaryDate = (TextView)findViewById(R.id.txtdate);
        diaryPage = (TextView)findViewById(R.id.page);
        diaryPic = (ImageView)findViewById(R.id.profile);

        commentSave = (Button)findViewById(R.id.commentSave);
        previous = (Button)findViewById(R.id.previous);
        next = (Button)findViewById(R.id.next);
        plus = (Button)findViewById(R.id.plus);

        edit_comment = (EditText)findViewById(R.id.edit_comment);

        spinner_weather = (Spinner) findViewById(R.id.spinner_main_weather);
        spinner_feeling = (Spinner) findViewById(R.id.spinner_main_feeling);
        spinner_stemp = (Spinner) findViewById(R.id.spinner_stemp);

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

        spinnerNames3 = new String[]{"기쁨","속상"};
        spinnerImages3 = new int[]{
                R.drawable.good
                , R.drawable.bad};


        // 어댑터와 스피너를 연결합니다.

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, spinnerNames, spinnerImages);
        spinner_weather.setAdapter(customSpinnerAdapter);
        CustomSpinnerAdapter customSpinnerAdapter2 = new CustomSpinnerAdapter(this, spinnerNames2, spinnerImages2);
        spinner_feeling.setAdapter(customSpinnerAdapter2);
        CustomSpinnerAdapter customSpinnerAdapter3 = new CustomSpinnerAdapter(this,spinnerNames3, spinnerImages3);
        spinner_stemp.setAdapter(customSpinnerAdapter3);

        // 스피너에서 아이템 선택시 호출하도록 함.
        spinner_stemp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_stamp = spinner_stemp.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(number.equals("0")) {
            checkFirst();
        }

        // 이전장으로
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(number) - 1;
                plus.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                if(num == 1){
                    number = String.valueOf(num);
                    checkDiary(number);
                    previous.setVisibility(View.GONE);
                }
                else {
                    number = String.valueOf(num);
                    checkDiary(number);
                }
            }
        });

        // 다음장으로
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setVisibility(View.VISIBLE);
                int num = Integer.parseInt(number) + 1;
                if(num <= allNumber){
                    number = String.valueOf(num);
                    checkDiary(number);
                }
                else if(num > allNumber){
                    number = String.valueOf(num);
                    next.setVisibility(View.GONE);
                    plus.setVisibility(View.VISIBLE);
                    spinner_weather.setSelection(0);
                    spinner_feeling.setSelection(0);
                    diaryTitle.setText("");
                    diaryText.setText("");
                    diaryDate.setText("");
                    diaryPage.setText(num + "/" + (allNumber +1));
                    diaryPic.setImageResource(0);
                    edit_comment.setText("");
                }
            }
        });

        commentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = edit_comment.getText().toString();
                if(!comment.isEmpty()) {
                    joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName)
                            .child("diaryContents").child(number).child("spinner_stemp").setValue(selected_stamp);
                    joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName)
                            .child("diaryContents").child(number).child("comment").setValue(comment);
                    joinDatabase.child("BookShelf").child(friendID).child("Friends").child(myID).child(diaryName)
                            .child("diaryContents").child(number).child("spinner_stemp").setValue(selected_stamp);
                    joinDatabase.child("BookShelf").child(friendID).child("Friends").child(myID).child(diaryName)
                            .child("diaryContents").child(number).child("comment").setValue(comment);
                    Toast.makeText(getApplicationContext(), "코멘트가 저장되었습니다ヾ(´∀｀)ﾉﾞ", Toast.LENGTH_SHORT).show();
                }
                else if(comment.isEmpty())
                    Toast.makeText(getApplicationContext(), "코멘트를 작성해주세요ಠ_ಠ", Toast.LENGTH_SHORT).show();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String type = dataSnapshot.child("diaryType").getValue().toString();
                        if (type.equals("picture")) {
                            Intent intent0 = new Intent(getApplicationContext(), PictureDiary.class);
                            intent0.putExtra("diaryNAME", diaryName);
                            intent0.putExtra("myID", myID);
                            intent0.putExtra("friendID", friendID);
                            startActivity(intent0);
                            finish();
                        }
                        else if(type.equals("photo")) {
                            Intent intent1 = new Intent(getApplicationContext(), PhotoDiary.class);
                            intent1.putExtra("diaryNAME", diaryName);
                            intent1.putExtra("myID", myID);
                            intent1.putExtra("friendID", friendID);
                            startActivity(intent1);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void checkFirst(){
        joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).child("diaryContents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number = String.valueOf(dataSnapshot.getChildrenCount());
                allNumber = Integer.parseInt(number);
                checkDiary(number);
                if(allNumber == 1){
                    previous.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void checkDiary(final String diaryNumber){
        joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).child("diaryContents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    if(postSnapShot.getKey().equals(diaryNumber)) {
                        title = postSnapShot.child("Title").getValue().toString();                                  // 제목
                        text = postSnapShot.child("Text").getValue().toString();                                    // 내용
                        date = postSnapShot.child("Date").getValue().toString();                                    // 날짜
                        photo = postSnapShot.child("pictureURI").getValue().toString();                             // 사진
                        page = postSnapShot.getKey().toString();                                                     // 페이지
                        weather = Integer.parseInt(postSnapShot.child("spinner_weather").getValue().toString());    // 날씨 스피너
                        feeling = Integer.parseInt(postSnapShot.child("spinner_feeling").getValue().toString());    // 기분 스피너
                        spinner_weather.setSelection(weather);
                        spinner_feeling.setSelection(feeling);
                        diaryTitle.setText(title);
                        diaryText.setText(text);
                        diaryDate.setText(date);
                        diaryPage.setText(page + "/" + dataSnapshot.getChildrenCount());
                        Picasso.with(getApplicationContext())
                                .load(photo)
                                .fit()
                                .into(diaryPic);
                        if(postSnapShot.hasChild("comment")){
                            saveComment = postSnapShot.child("comment").getValue().toString();
                            saveStamp = Integer.parseInt(postSnapShot.child("spinner_stemp").getValue().toString());
                            edit_comment.setText(saveComment);
                            spinner_stemp.setSelection(saveStamp);
                        }
                        else if(!postSnapShot.hasChild("comment"))
                            edit_comment.setText("");
                        joinDatabase.removeEventListener(this);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}