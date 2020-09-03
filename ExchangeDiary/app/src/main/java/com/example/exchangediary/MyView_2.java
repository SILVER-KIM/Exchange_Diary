package com.example.exchangediary;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;


//좌표 이동시 저장 클래스 (picture diary에서 쓰임)
class Point{
    float x; //x좌표
    float y; //y좌표
    int pValue; //마우스 상태(down,move)
    int color;
    int size;

    public Point(float x, float y, int value, int color, int size){
        this.x = x;
        this.y = y;
        pValue = value;
        this.color = color;
        this.size = size;
    }
}

class MyView_2 extends View{ // implements View.OnTouchListener{
    Paint pnt;
    ArrayList<Point> arrP; //Point 클래스 저장
    final int START = 0; //mouse의 상태(Down)
    final int MOVE = 1; // mouse의 상태(Move)
    int value, color, reStart, reEnd, size; // value(mouse 상태 저장 변수) color(Drawimage 색상)
    // reStart(Drawimage의 시작 값 대입 변수) reEnd(Drawimage의 끝 값 변수)


    Bitmap bit;


    //MyView의 콜백 메서드


    public MyView_2 (Context context){
        super(context);
        pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        pnt.setStrokeJoin(Paint.Join.ROUND);
        pnt.setStrokeCap(Paint.Cap.ROUND);
        arrP = new ArrayList<Point>();
        color = Color.BLACK;
        bit = null;
        size = 3;
    }


    protected  void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        if(bit!=null){
            canvas.drawBitmap(bit, 0, 0, null);
        }

        for(int i = 0; i< arrP.size(); i++) {
            pnt.setColor(arrP.get(i).color);
            pnt.setStrokeWidth(arrP.get(i).size);
            if (arrP.get(i).pValue == MOVE){
                canvas.drawLine(arrP.get(i - 1).x, arrP.get(i - 1).y, arrP.get(i).x, arrP.get(i).y, pnt);

            }
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            value = START;
            arrP.add(new Point(event.getX(), event.getY(), value, color, size));
            return true;
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            value = MOVE;
            arrP.add(new Point(event.getX(), event.getY(),value,color,size));
            invalidate();
            return true;
        }
        return false;
    }
}