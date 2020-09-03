package com.example.exchangediary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewBookshelf extends AppCompatActivity {
    GridView gridView;
    DiaryAdapter adapter;
    Button btnNewDiary;
    DatabaseReference joinDatabase;
    String myID;
    String friendID;
    Bitmap img;
    int buttonCheck;
    String name;
    private final int AddItem = 1004;
    ImageView image;
    int order;
    String diaryName;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bookshelf);

        joinDatabase = FirebaseDatabase.getInstance().getReference();
        img = BitmapFactory.decodeResource(this.getResources(), R.id.viewDiary);
        image = (ImageView) findViewById(R.id.viewDiary);

        // 로그인한 나의 ID를 받아와서 저장
        final Intent intent = getIntent();
        myID = intent.getStringExtra("myID");
        //내 친구들 ID를 받아옴
        friendID = intent.getStringExtra("friendID");
        checkAnswer(myID, friendID);
        checkInvitation(myID, friendID);

        gridView = (GridView) findViewById(R.id.gridDiary);
        adapter = new DiaryAdapter(NewBookshelf.this);
        checkSendDiary(myID, friendID);
        gridView.setAdapter(adapter);

        btnNewDiary = (Button) findViewById(R.id.btnNewDiary);
        btnNewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoADD(myID, friendID);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //다이어리 이름 가져오기
                diaryName = adapter.items.get(position).getDiaryName();
                //데이터베이스에서 Contents가 존재하면(일기 쓴게 존재하면) 책화면으로 이동하고 아니면 선택 다이얼로그 띄우기
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("diaryContents")) {
                            if(dataSnapshot.child("diaryContents").getChildrenCount() != 0) {
                                Intent intent5 = new Intent(getApplicationContext(), OpenBookActivity.class);
                                intent5.putExtra("diaryNAME", diaryName);
                                intent5.putExtra("myID", myID);
                                intent5.putExtra("friendID", friendID);
                                startActivity(intent5);
                                joinDatabase.removeEventListener(this);
                                finish();
                            }
                        }
                        else {
                            goToSelectDiary();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void goToSelectDiary() {
        AlertDialog.Builder alt_builder = new AlertDialog.Builder(NewBookshelf.this);
        alt_builder.setCancelable(false);
        alt_builder.setTitle("다이어리 종류 결정").setCancelable(false).setPositiveButton("그림일기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.v("알림", "다이얼로그 > 그림일기 선택");
                type = "picture";
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).child("diaryType").setValue(type);
                joinDatabase.child("BookShelf").child(friendID).child("Friends").child(myID).child(diaryName).child("diaryType").setValue(type);
                Intent intent0 = new Intent(getApplicationContext(), PictureDiary.class);
                intent0.putExtra("friendID", friendID);
                intent0.putExtra("myID", myID);
                intent0.putExtra("diaryNAME", diaryName);
                startActivity(intent0);
            }
        }).setNeutralButton("사진일기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("알림", "다이얼로그 > 사진일기 선택");
                type = "photo";
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).child("diaryType").setValue(type);
                joinDatabase.child("BookShelf").child(friendID).child("Friends").child(myID).child(diaryName).child("diaryType").setValue(type);
                Intent intent1 = new Intent(getApplicationContext(), PhotoDiary.class);
                intent1.putExtra("friendID", friendID);
                intent1.putExtra("myID", myID);
                intent1.putExtra("diaryNAME", diaryName);
                startActivity(intent1);
            }
        }).setNegativeButton("취소 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("알림", "다이얼로그 > 취소 선택");
                dialog.cancel();
            }
        });
        AlertDialog alert = alt_builder.create();
        alert.show();
    }

    public void GotoADD(final String myID, final String friendID){
        //일기장 생성 엑티비티로 이동
        Intent intent2 = new Intent(getApplicationContext(), CreateDiary.class);
        intent2.putExtra("myID", myID);
        intent2.putExtra("friendID", friendID);
        startActivityForResult(intent2, AddItem);
    }

    protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == AddItem){
            Toast.makeText(getApplicationContext(), "상대방의 답장을 기다리세요!", Toast.LENGTH_SHORT).show();
        }

    }

    // 나한테 온 일기신청을 확인해서 팝업창을 띄우는 기능
    public void checkInvitation(final String myID, final String friendID){
        joinDatabase.child("Invitation").child(friendID).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    AlertDialog.Builder alt_invitation =  new AlertDialog.Builder(NewBookshelf.this);
                    alt_invitation.setCancelable(false);
                    final String diaryName = postSnapShot.getKey().toString();
                    String invi_message = postSnapShot.child("invitation_MESSAGE").getValue().toString();
                    final String diaryURI = postSnapShot.child("PhotoURI").getValue().toString();
                    alt_invitation.setTitle(friendID + "님에게 초대장이 왔습니다:)");
                    alt_invitation.setMessage("다이어리 제목 : " + diaryName + "\n" + invi_message);
                    alt_invitation.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            joinDatabase.child("Answer").child(friendID).child(myID).setValue("Accept");
                            joinDatabase.child("sendDiary").child(friendID).child("Friends").child(myID).child(diaryName).child("diaryURI").setValue(diaryURI);
                            joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(diaryName).child("diaryURI").setValue(diaryURI);
                            joinDatabase.child("Invitation").child(friendID).child(myID).child(diaryName).removeValue();
                        }
                    });
                    alt_invitation.setNegativeButton("거부", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            joinDatabase.child("Answer").child(friendID).child(myID).setValue("Refusal");
                            joinDatabase.child("BookShelf").child(friendID).child("Friends").child(myID).child(diaryName).removeValue();
                            joinDatabase.child("Invitation").child(friendID).child(myID).child(diaryName).removeValue();
                        }
                    });
                    alt_invitation.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkSendDiary(final String myID, final String friendID) {
        joinDatabase.child("sendDiary").child(myID).child("Friends").child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    String diaryName = postSnapShot.getKey().toString();
                    adapter.addItem(new DiaryItem(myID, friendID, diaryName));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkAnswer(final String myID, final String friendID) {
        joinDatabase.child("Answer").child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    if(friendID.equals(postSnapShot.getKey())) {
                        if (postSnapShot.getValue().equals("Accept")) {
                            AlertDialog.Builder alt_answer = new AlertDialog.Builder(NewBookshelf.this);
                            alt_answer.setCancelable(false);
                            alt_answer.setTitle("초대장 답장");
                            alt_answer.setMessage(friendID + "님이 초대를 수락하셨습니다.");
                            alt_answer.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    joinDatabase.child("Answer").child(myID).child(friendID).removeValue();
                                }
                            });
                            alt_answer.show();
                        }
                        else if (postSnapShot.getValue().equals("Refusal")) {
                            AlertDialog.Builder alt_answer = new AlertDialog.Builder(NewBookshelf.this);
                            alt_answer.setTitle("초대장 답장");
                            alt_answer.setMessage(friendID + "님이 초대를 거부하셨습니다.");
                            alt_answer.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    joinDatabase.child("Answer").child(myID).child(friendID).removeValue();
                                }
                            });
                            alt_answer.show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}