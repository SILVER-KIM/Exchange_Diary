package com.example.exchangediary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CreateDiary extends AppCompatActivity {
    DatabaseReference joinDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage;
    private final static int COLOR_ACTIVITY = 1;
    private final static int GOTOALBUM = 2;
    ImageView book;
    Button colorBTN, pictureBTN, okBTN, okNAME;
    TextView txtINPUT;
    EditText diaryNAME;
    Intent intent;
    int color;
    int buttonCheck=0;
    String name, profileURI, myID, friendID;
    int flag = 0;
    Bitmap bitmap;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);
        storage = FirebaseStorage.getInstance();

        Intent intent0 = getIntent();
        myID = intent0.getStringExtra("myID");
        friendID = intent0.getStringExtra("friendID");

        book = (ImageView)findViewById(R.id.book);
        colorBTN = (Button)findViewById(R.id.colorBTN);
        pictureBTN = (Button)findViewById(R.id.pictureBTN);
        okBTN = (Button)findViewById(R.id.okBTN);
        txtINPUT = (TextView)findViewById(R.id.txtINPUT);
        diaryNAME = (EditText)findViewById(R.id.diaryNAME);
        okNAME = (Button)findViewById(R.id.okNAME);

        // 다이어리 제목 적용하는 리스너
        okNAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = diaryNAME.getText().toString();
                if(name != null) {
                    txtINPUT.setText(name);
                    joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(name);
                }
            }
        });

        // 색 설정 버튼을 클릭했을때 리스너
        colorBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name == null) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), MyColorPicker.class);
                    if (color != 0) {
                        intent.putExtra("oldColor", color);
                    }
                    startActivityForResult(intent, COLOR_ACTIVITY);
                }
            }
        });

        // 표지를 완성한 후 나가기 위한 리스너
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1){
                    book.setDrawingCacheEnabled(true);
                    book.buildDrawingCache();
                    bitmap = Bitmap.createBitmap(book.getWidth(), book.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(color);
                    goToStorage(getImageUri(getApplicationContext(), bitmap));
                }
                else if(flag == 2) {
                    goToStorage(photoURI);
                }
            }
        });

        pictureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name == null) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    selectAlbum();
                }
            }
        });
    }

    public void onBackPressed() {
        final AlertDialog.Builder alt_cancle =  new AlertDialog.Builder(CreateDiary.this);
        alt_cancle.setCancelable(false);
        alt_cancle.setTitle("종료");
        alt_cancle.setMessage("일기를 만들지 않고 종료하시겠습니까?(｡•́︿•̀｡)");
        alt_cancle.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alt_cancle.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(name).child("diaryURI").removeValue();
                dialog.dismiss();
                finish();
            }
        });
        alt_cancle.show();
    }

    public void endActivity(){
        final AlertDialog.Builder alt_invitation =  new AlertDialog.Builder(CreateDiary.this);
        alt_invitation.setCancelable(false);
        alt_invitation.setTitle(friendID + "님에게 초대 메세지를 발송");
        alt_invitation.setMessage("초대 메세지를 입력하세요!");

        final EditText invitation = new EditText(CreateDiary.this);
        alt_invitation.setView(invitation);

        alt_invitation.setPositiveButton("전송", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonCheck = 1;
                Intent intent3 = new Intent(getApplicationContext(),NewBookshelf.class);
                intent3.putExtra("CHECK",buttonCheck);
                intent3.putExtra("diaryNAME", name);
                setResult(RESULT_OK, intent3);
                String invi_message = invitation.getText().toString();
                joinDatabase.child("Invitation").child(myID).child(friendID).child(name);
                joinDatabase.child("Invitation").child(myID).child(friendID).child(name).child("invitation_MESSAGE").setValue(invi_message);
                joinDatabase.child("Invitation").child(myID).child(friendID).child(name).child("PhotoURI").setValue(profileURI);
                dialog.dismiss();
                finish();
            }
        });
        alt_invitation.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alt_invitation.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == COLOR_ACTIVITY){
            flag = 1;
            color = data.getIntExtra("color", 0);
            book.setImageResource(0);
            book.setBackgroundColor(color);
        }
        else if(requestCode == GOTOALBUM){
            if(data.getData()!=null) {
                try{
                    flag = 2;
                    photoURI = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoURI);
                    book.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void selectAlbum(){
        Intent intent4 = new Intent(Intent.ACTION_PICK);
        intent4.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent4.setType("image/*");
        startActivityForResult(intent4, GOTOALBUM);
    }

    private Uri getImageUri(Context context, Bitmap inImgae){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImgae.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImgae, "Title", null);
        return Uri.parse(path);
    }

    public void goToStorage(Uri uri){
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("BookShelf").child(myID).child(friendID).child(name);

        UploadTask uploadTask;
        uploadTask = exchangeStorage.putFile(uri);

        final ProgressDialog progressDialog = new ProgressDialog(CreateDiary.this);
        progressDialog.setMessage("다이어리를 만들고 있습니다:)");
        progressDialog.show();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
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
        StorageReference exchangeStorage = storage.getReferenceFromUrl("gs://exchangediary-8c131.appspot.com").child("BookShelf").child(myID).child(friendID).child(name);
        exchangeStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileURI = String.valueOf(uri);
                joinDatabase.child("BookShelf").child(myID).child("Friends").child(friendID).child(name).child("diaryURI").setValue(profileURI);
                endActivity();
            }
        });
    }

}