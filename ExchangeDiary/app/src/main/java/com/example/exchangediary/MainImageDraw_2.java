package com.example.exchangediary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainImageDraw_2 extends AppCompatActivity {
    private String mCurrentPhotoPath;
    Bitmap bit, image;
    Uri photoURI;
    MyView_2 vw;
    SeekBar seek;
    TextView text;
    RelativeLayout relative;
    Button apply_Image;
    File file;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image_draw_2);

        apply_Image = (Button)findViewById(R.id.apply_Image) ;
        vw = new MyView_2(getApplication());
        relative = (RelativeLayout)findViewById(R.id.subLayout);
        relative.addView(vw);


        findViewById(R.id.btn_1).setOnTouchListener(mButton);
        findViewById(R.id.btn_2).setOnTouchListener(mButton);
        findViewById(R.id.btn_3).setOnTouchListener(mButton);
        findViewById(R.id.btn_4).setOnTouchListener(mButton);
        findViewById(R.id.btn_5).setOnTouchListener(mButton);
        findViewById(R.id.btn_6).setOnTouchListener(mButton);
        findViewById(R.id.btn_new).setOnTouchListener(mButton);
        findViewById(R.id.btn_back).setOnTouchListener(mButton);
        findViewById(R.id.btn_save).setOnTouchListener(mButton);
        findViewById(R.id.btn_open).setOnTouchListener(mButton);
        findViewById(R.id.btn_eraser).setOnTouchListener(mButton);

        //seekBar 속성 설정

        text = (TextView)findViewById(R.id.textView1);

        seek = (SeekBar)findViewById(R.id.seekBar);
        seek.setOnSeekBarChangeListener(mSeekBar);
        seek.setMax(10);
        seek.setProgress(3);

        //그림을 PictureDiary로 보내는 코드, 여기에서 이미지뷰에 연결해줘야될거같음!
        apply_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoURI != null) {
                    Intent intent0 = new Intent(getApplicationContext(), PictureDiary.class);
                    intent0.putExtra("image", photoURI);
                    setResult(RESULT_OK, intent0);
                    finish();
                }
            }
        });



    }


    SeekBar.OnSeekBarChangeListener mSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override //seek값이 바뀔 때 마다
        public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
            vw.size = progress;
            text.setText("Line size" + progress);
            vw.invalidate();
        }

        @Override //터치했을 때
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override //터치에서 손을 뗐을 때
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // 버튼 리스너

    Button.OnTouchListener mButton = new View.OnTouchListener() {
        int saveCnt = 0;  //image를 저장할때의 순서를 지정하는 변수
        int mSelect = 0; // 대화상자를 선택하기 위해 필요한 변수
        String[] imgPath; //image를 저장할 임의의 배열

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_1:
                        vw.color = Color.WHITE;
                        break;
                    case R.id.btn_2:
                        vw.color = Color.RED;
                        break;
                    case R.id.btn_3:
                        vw.color = Color.YELLOW;
                        break;
                    case R.id.btn_4:
                        vw.color = Color.GREEN;
                        break;
                    case R.id.btn_5:
                        vw.color = Color.BLUE;
                        break;
                    case R.id.btn_6:
                        vw.color = Color.BLACK;
                        break;

                    case R.id.btn_new:
                        vw.bit = null;
                        vw.arrP.clear();
                        vw.color = Color.BLACK;
                        vw.bit = BitmapFactory.decodeResource(getResources(), Color.WHITE);
                        vw.invalidate();
                        break;

                    case R.id.btn_back:
                        if(vw.arrP.size()>0)
                            vw.arrP.remove(vw.arrP.size() - 1); //좌표값이 저장된 arrP를 뒤에서부터 remove
                        vw.invalidate();
                        break;

                    case R.id.btn_save:
                        vw.invalidate();
                        vw.setDrawingCacheEnabled(true);
                        bit = vw.getDrawingCache();
                        String imgFileName = "CustomViewSave" + saveCnt++ + ".png";

                        image = null;
                        file = null;
                        photoURI = null;

                        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Draw");
                        if (!storageDir.exists()) {
                            Log.v("알림", "storageDir 존재 x " + storageDir.toString());
                            storageDir.mkdirs();
                        }
                        file = new File(storageDir, imgFileName);
                        mCurrentPhotoPath = file.getAbsolutePath();

                        OutputStream out = null;
                        try{
                            out = new FileOutputStream(file);
                            if(out != null){
                                bit.compress(Bitmap.CompressFormat.PNG, 75, out);   //실제로 파일을 만드는 메소드
                                galleryAddPic();
                                image = Bitmap.createBitmap(bit);
                                photoURI = getImageUri(getApplicationContext(), image);
                                Toast.makeText(MainImageDraw_2.this, "Capture Success", 0).show();
                            }
                            out.close();
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                            Toast.makeText(MainImageDraw_2.this, "FileNotFounderr", 0).show();
                        } catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(MainImageDraw_2.this, "InOuterr", 0).show();
                        }
                        break;
                        //파일 오픈 기능
                    case R.id.btn_open:
                        imgPath = new String[5];
                        for(int i = 0; i<5; i++)    //imgPath배열에 경로명과 파일명으로 된 String을 저장. (Item 창)
                            imgPath[i] = Environment.getExternalStorageDirectory() + "/Pictures/Draw/CustomViewSave" + i + ".png";

                        new AlertDialog.Builder(MainImageDraw_2.this)
                                .setTitle("Open할 파일을 선택").setIcon(R.mipmap.ic_launcher)
                                .setSingleChoiceItems(new String[]{"CustomViewSave4", "CustomViewSave3","CustomViewSave2",
                                                "CustomViewSave1", "CustomViewSave0"}
                                        , mSelect, new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int which){
                                                mSelect = which;
                                            }
                                        }).setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                switch (mSelect) {     //mSelect와 index가 반대
                                    case 0:
                                        importCanvas(imgPath[4]); //importCanvas메소드 호출. 그림 저장 경로 전달.
                                        break;
                                    case 1:
                                    importCanvas(imgPath[3]);
                                    break;
                                    case 2:
                                    importCanvas(imgPath[2]);
                                    break;
                                    case 3:
                                    importCanvas(imgPath[1]);
                                    break;
                                    case 4:
                                    importCanvas(imgPath[0]);
                                    break;
                                }
                            }
                        }).setNegativeButton("Cancel", null).show();
                        break;

                    case R.id.btn_eraser:
                        vw.color = Color.WHITE;
                        break;
                }
            }
            return false;
        }
    };

    public void importCanvas(String path){
        Bitmap bit = BitmapFactory.decodeFile(path);
        vw.bit = bit;
        vw.invalidate();
    }


    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
        Toast.makeText(getApplicationContext(),"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();

    }

    private Uri getImageUri(Context context, Bitmap inImgae){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImgae.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImgae, "Title", null);
        return Uri.parse(path);
    }


}