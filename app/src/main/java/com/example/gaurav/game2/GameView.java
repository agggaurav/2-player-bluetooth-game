package com.example.gaurav.game2;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameView extends SurfaceView{
    private static final int RESULT_OK = 1;
    private static final int RESULT_CANCELED = 0;
    private Bitmap bmp_slider,bmp_ball,obj2,obj1,bmp_slider2,left,right;
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    Context mContext;
     int x;
    int y=400;
    int[] cpos;
    int[] lpos;
    int xSpeed = 2;
    int ySpeed=1;
    int x2Speed=0;
    SensorManager sm = null;
    List list;
    float[] values;

    public int[][]a;
    public int[][]b;
   int last;

    int player=0;

    float X,Y;
int player2_speed_x,player2_speed_y;


    SensorEventListener sel = new SensorEventListener(){
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
			/* Isn't required for this example */

        }
        public void onSensorChanged(SensorEvent event) {
			/* Write the accelerometer values to the TextView */
            values = event.values;
            if (values[1] > 0) {
                xSpeed = (int)X/220;
                float gap=0.0f;

                if(player==1) {
                    gap = (x / X) * 100;
                }
                else if(player==2) {

                    gap = (slider2_x / X) * 100;

                }
                String q="r  "+Float.toString(gap);
                btservice.write(q.getBytes());

            } else if (values[1] <= 0) {
                xSpeed = (int)(X/220)*-1;
                float gap=0.0f;
                if(player==1) {
                    gap = (x/ X) * 100;
                }
                if(player==2)
                {
                    gap= (slider2_x/ X) * 100;
                }

                String q="l "+Float.toString(gap);
                btservice.write(q.getBytes());

            }
           // accText.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);
        Log.e("vvv",String.valueOf(values[1]));
        }

    };

    BluetoothService btservice;
    Point size;
         @TargetApi(Build.VERSION_CODES.KITKAT)

         public GameView(Context context, BluetoothService mService) {
        super(context);
             gameLoopThread = new GameLoopThread(this);
        mContext=context;
             btservice=mService;
             btservice.start=false;

X=btservice.screensize_x;
             Y=btservice.screensize_y;
             ratio=(int)(X/Y);
             unit_dist=(int)X/14;

if(mService.isThisDeviceServer==true)
{
 player=1;
}
             else if(mService.isThisDeviceServer==false)
{
    player=2;
}
             Bitmap bmp_slider1 = BitmapFactory.decodeResource(getResources(), R.mipmap.rect1);
             Bitmap bmp_ball1=BitmapFactory.decodeResource(getResources(),R.mipmap.ball5);
             left=BitmapFactory.decodeResource(getResources(), R.mipmap.left_dir);
             right=BitmapFactory.decodeResource(getResources(), R.mipmap.right_dir);
             obj1=BitmapFactory.decodeResource(getResources(), R.mipmap.obj);
             bmp_ball = Bitmap.createScaledBitmap(bmp_ball1,(int)(X/20),(int)X/20, true);

             bmp_slider = Bitmap.createScaledBitmap(bmp_slider1,(int)X/5,15, true);
             bmp_slider2=Bitmap.createScaledBitmap(bmp_slider1,(int)X/5,10,true);
             obj2= Bitmap.createScaledBitmap(obj1,(int)(X/20),(int)X/20, true);


             cpos=new int[2];
             lpos=new int[2];
             last=15;
             obj_pos sample[] = new obj_pos[last];
int start_x=(int)X/2;
           int start_y=(int)Y/2;
            int step_x=(int)obj2.getWidth()+2;
           int step_y=(int)obj2.getHeight()+2;

             if(player==2)
             {
                 player2_speed_x=btservice.speed_x;
                 player2_speed_y=btservice.speed_y;
             }

             a=new int[][]{
                     {start_x,start_y},{start_x+step_x,start_y},{start_x,start_y-step_y},{start_x+step_x,start_y-step_y},{start_x-step_x,start_y},{start_x-step_x,start_y-step_y},{start_x-2*step_x,start_y},{start_x-2*step_x,start_y-step_y},{start_x-2*step_x,start_y-2*step_y}
                     ,{start_x+2*step_x,step_y},{start_x+2*step_x,start_y-step_y},{start_x+2*step_x,start_y-2*step_y},{start_x-3*step_x,start_y},{start_x-3*step_x,start_y-step_y},{start_x-3*step_x,start_y-2*step_y}
             };

           /*  a=new int[][]{
                     {start_x,start_y},{start_x+step_x,start_y},{start_x,start_y+step_y},{start_x+start_y,start_y+step_y}
             };
*/

             int temp1,temp2;
             for(int k=0;k<last;k++)
             {
                 for(int l=k+1;l<last;l++)
                 {
                     if(a[k][0]>a[l][0])
                     {
                       temp1=a[k][0];
                         temp2=a[k][1];
                         a[k][0]=a[l][0];
                         a[k][1]=a[l][1];
                         a[l][0]=temp1;
                         a[l][1]=temp2;
                     }
                 }
             }

            /*for(int k=0;k<15;k++) {
                sample[k]=new obj_pos();
                sample[k].set(a[k][0],a[k][1]);
                al.add(k,sample[k]);
}*/
             sm = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);

        /* This corresponds to a TextView element in main.xml with android:id="@+id/accText" */
       // accText = (TextView)findViewById(R.id.textView1);

        /* Get list of accelerometers */
        list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

        /* If there are any accelerometers register a listener to the first else
           print a little error message */
        if(list.size()>0){
            sm.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(getContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG);
        }
            // y=getHeight()-bmp_ball.getHeight();
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
              //  gameLoopThread = new GameLoopThread(getContext());
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

         }

int flag=0;
    int t_count=0;
    int ratio;

int unit_dist;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x_point = event.getX();

           if (x_point >= 0) {
               //Toast.makeText(getContext(), "touch", Toast.LENGTH_SHORT).show();
if(player==1) {
    String q = "a"+Float.toString(X);
    btservice.write(q.getBytes());
    t_count++;
}



           }

        float y_point = event.getY();

     /*   if (player == 2) {
            if (x_point >= 10 && x_point <= 10 + left.getWidth()) {
                if (y_point >= 10 && y_point <= 10 + left.getHeight()) {
                    slider2_x = slider2_x - 20;
                        String q="l";
                    btservice.write(q.getBytes());
                }
            }
            if (x_point >= getWidth() - right.getWidth() - 10 && x <= getWidth() - 10) {
                if (y_point >= 10 && y_point <= 10 + right.getHeight()) {
                    slider2_x = slider2_x + 20;
String q="r";
                btservice.write(q.getBytes());
                }
            }
        }*/
            return true;

    }
int slider2_x;
    int pop=0;
    int temp_x,temp_y;
    int speed_x,speed_y;
    int x_pos;
    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {

            if(btservice.start==true)
            {if(t_count<5)
                t_count++;
            }

           if(player==1){ x = x + xSpeed;}
            else if(player==2){slider2_x=slider2_x+xSpeed;}



        canvas.drawColor(Color.BLACK);

            Paint painting = new Paint();
            painting.setColor(Color.WHITE);

            if(player==1) {
                canvas.drawText("1", 0, 0, painting);
            }
                else {
                canvas.drawText("2", 0, 0, painting);
            }



        //canvas.drawBitmap(left, 10, 10, null);

        //canvas.drawBitmap(right, getWidth() - right.getWidth() - 10, 10, null);

            if(player==1) {

                if(btservice.right==true) {
                    slider2_x=slider2_x+unit_dist;
                    if(slider2_x +bmp_slider2.getWidth()>getWidth())
                    {
                        slider2_x=getWidth()-bmp_slider2.getWidth();
                    }
                    canvas.drawBitmap(bmp_slider2, slider2_x, getHeight()/2, null);
                btservice.right=false;
                }
                else if(btservice.left==true)
                {
                    slider2_x=slider2_x-unit_dist;
                    if(slider2_x<0)
                    {
                        slider2_x=0;
                    }
                    canvas.drawBitmap(bmp_slider2, slider2_x, getHeight() / 2, null);
                    btservice.left=false;
                }
                else {
                    slider2_x=(int) (btservice.reciever_x *(float) X)/100;
                    canvas.drawBitmap(bmp_slider2, slider2_x, getHeight() / 2, null);

                }




                if (x <= 0) {
                    x = 0;
                    canvas.drawBitmap(bmp_slider, 0, getHeight() - bmp_slider.getHeight(), null);
                }

                else if (x + bmp_slider.getWidth() >= getWidth()) {
                    x = getWidth() - bmp_slider.getWidth();
                    canvas.drawBitmap(bmp_slider, getWidth() - bmp_slider.getWidth(), getHeight() - bmp_slider.getHeight(), null);
                } else {
                    canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
                }

            }

            if(player==2)
            {
                if(btservice.right==true) {
                    x=x+unit_dist;
                  if(x+bmp_slider.getWidth()>getWidth()){x=getWidth()-bmp_slider.getWidth();}
                    canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
                    btservice.right=false;
                }
                else if(btservice.left==true)
                {
                    x=x-unit_dist;
                   if(x<0)
                   {
                       x=0;
                   }
                    canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
                    btservice.left=false;
                }
                else {
                    x=(int)(btservice.reciever_x * (float)X)/100;
                    canvas.drawBitmap(bmp_slider,x, getHeight() - bmp_slider.getHeight(), null);

                }



                if (slider2_x <= 0) {
                    slider2_x = 0;
                    canvas.drawBitmap(bmp_slider2, 0,getHeight()/2, null);
                }

                else if (slider2_x + bmp_slider2.getWidth() >= getWidth()) {
                    slider2_x = getWidth() - bmp_slider2.getWidth();
                    canvas.drawBitmap(bmp_slider2, getWidth() - bmp_slider2.getWidth(),getHeight()/2, null);
                } else {
                    canvas.drawBitmap(bmp_slider2, slider2_x, getHeight()/2, null);
                }

            }


        for (int i = 0; i < last; i++) {

            canvas.drawBitmap(obj2, a[i][0], a[i][1], null);
        }

        if (t_count == 0) {
            cpos[0] = x + bmp_slider.getWidth() / 2;
            lpos[0] = cpos[0];
            cpos[1] = getHeight() - bmp_ball.getHeight();
            lpos[1] = cpos[1];
            canvas.drawBitmap(bmp_ball, x + bmp_slider.getWidth() / 2, getHeight() - bmp_ball.getHeight(), null);
            temp_x=x+bmp_slider.getWidth();
            temp_y=getHeight()-bmp_ball.getHeight();

        }

        if (t_count >= 1) {

            if (flag == 0) {
                ySpeed= -1;
                x2Speed= 0;
                movedown(canvas);

                flag = 1;

            } else if (flag == 1) {
                //  Toast.makeText(getContext(),y,Toast.LENGTH_SHORT).show();

                if(pop!=1) {
                    speed_x = cpos[0] - temp_x;
                    speed_y = cpos[0] - temp_y;
                    pop=1;
                    String q="x "+Integer.toString(speed_x);
                    String c="y "+Integer.toString(speed_y);
                    btservice.write(q.getBytes());
                    btservice.write(c.getBytes());
                }
                movedown(canvas);
            }
        }
    }
    }


    int x2;
    int inverse=0;
    int m=1;
    int cslope;
    public void change()
    {
        if(m==1){
            m=-1;
        }
        else if(m==-1)
        {
            m=1;
        }
    }

    public void movedown(Canvas canvas) {

        if (canvas != null)
        {
            checkcollision(canvas);

        if (cpos[1] == 0) {

            ySpeed= 1;
            x2Speed= 0;
            if (inverse == 1) {
                change();
                inverse = 0;
            }
            //lpos[0]=cpos[0];
            //lpos[1]=cpos[1];
        }
            if(cpos[1]+bmp_ball.getHeight()/2==getHeight())
            {

                gameLoopThread.setRunning(false);

            }

        if (cpos[1] == getHeight() - bmp_slider.getHeight()) {
            ySpeed= -1;
            x2Speed= 0;
            if (inverse == 1) {
                change();
                inverse = 0;
            }

            //lpos[0]=cpos[0];
            //lpos[1]=cpos[1];
        }

        if (cpos[0] + bmp_ball.getWidth() == getWidth()) {
            ySpeed= 0;
            x2Speed= -1;

            if (inverse == 1) {
                change();
                inverse = 0;
            }

            //lpos[1]=cpos[1];
            //lpos[0]=cpos[0];
        }

        if (cpos[1] + bmp_ball.getHeight() == getHeight() - bmp_slider.getHeight() && cpos[0] >= x - bmp_slider.getWidth() && cpos[0] <= x + bmp_slider.getWidth()) {

            x2Speed= 0;
            ySpeed= -1;
            if (inverse == 1) {
                change();
                inverse = 0;
            }


        }


        if (cpos[1] + bmp_ball.getHeight() == getHeight() - bmp_slider.getHeight() - 200 && cpos[0] >= slider2_x - bmp_slider2.getWidth() && cpos[0] <= slider2_x + bmp_slider2.getWidth()) {

            x2Speed= 0;
            ySpeed= -1;
            if (inverse == 1) {
                change();
                inverse = 0;
            }


        }


        if (cpos[0] == 0) {
            x2Speed= 1;
            ySpeed= 0;
            if (inverse == 1) {
                change();
                inverse = 0;
            }

            //lpos[0]=cpos[0];
            //lpos[1]=cpos[1];
//            moveright(canvas);
        }

        cpos[1] = cpos[1] + ySpeed;
        cpos[0] = cpos[0] + x2Speed;

        if (ySpeed== 1 && x2Speed== 0) {
            //cpos[0] = cpos[1] - lpos[1] + lpos[0];


            if (m == 1) {
                cpos[0] = cpos[1] - lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[0] = -cpos[1] + lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }

            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);

            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (ySpeed== -1 && x2Speed== 0) {
            // cpos[0] = -cpos[1] + lpos[1] + lpos[0];


            if (m == -1) {
                cpos[0] = -cpos[1] + lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[0] = cpos[1] - lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (x2Speed== -1 && ySpeed== 0) {
            //cpos[1] = cpos[0] + lpos[1] - lpos[0];
            //if(lpos[0]>cpos[0]){inverse=1;}
            if (m == 1) {
                cpos[1] = cpos[0] - lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[1] = -cpos[0] + lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (x2Speed== 1 && ySpeed== 0) {
            if (m == 1) {
                cpos[1] = cpos[0] - lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[1] = -cpos[0] + lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);

            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        checkcollision(canvas);
    }
    }

int collide=0;
    int j=0;
    public void checkcollision(Canvas canvas) {
        if (canvas != null)
        {

            int j = 0;
        while (cpos[0] >= a[j][0] && j < last) {
//if(Math.sqrt(Math.pow(cpos[0]+bmp_ball.getWidth()/2-a[j][0]+30,2)+Math.pow(cpos[1]+bmp_ball.getHeight()/2-a[j][1]+30,2))<100){
            if (Math.sqrt(Math.pow(cpos[0] - a[j][0], 2) + Math.pow(cpos[1] - a[j][1], 2)) < 50 || Math.sqrt(Math.pow(cpos[0] + bmp_ball.getWidth() - a[j][0], 2) + Math.pow(cpos[1] + bmp_ball.getWidth() - a[j][1], 2)) < 50 || Math.sqrt(Math.pow(cpos[0] + bmp_ball.getWidth() - a[j][0], 2) + Math.pow(cpos[1] - a[j][1], 2)) < 50 || Math.sqrt(Math.pow(cpos[0] - a[j][0], 2) + Math.pow(cpos[1] + bmp_ball.getHeight() - a[j][1], 2)) < 50) {
                inverse = 1;
                change();
                lpos[0] = cpos[0];
                lpos[1] = cpos[1];
                for (int i = j; i < last - 1; i++) {
                    a[i][0] = a[i + 1][0];
                    a[i][1] = a[i + 1][1];
                }
                last--;
                int temp1, temp2;
                for (int k = 0; k < last; k++) {
                    for (int l = k + 1; l < last; l++) {
                        if (a[k][0] > a[l][0]) {
                            temp1 = a[k][0];
                            temp2 = a[k][1];
                            a[k][0] = a[l][0];
                            a[k][1] = a[l][1];
                            a[l][0] = temp1;
                            a[l][1] = temp2;
                        }
                    }
                }
            }

            j++;

        }


        if (ySpeed== 1 && x2Speed== 0) {
            //cpos[0] = cpos[1] - lpos[1] + lpos[0];


            if (m == 1) {
                cpos[0] = cpos[1] - lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[0] = -cpos[1] + lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }

            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);

            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (ySpeed== -1 && x2Speed== 0) {
            // cpos[0] = -cpos[1] + lpos[1] + lpos[0];


            if (m == -1) {
                cpos[0] = -cpos[1] + lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[0] = cpos[1] - lpos[1] + lpos[0];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (x2Speed== -1 && ySpeed== 0) {
            //cpos[1] = cpos[0] + lpos[1] - lpos[0];
            //if(lpos[0]>cpos[0]){inverse=1;}
            if (m == 1) {
                cpos[1] = cpos[0] - lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[1] = -cpos[0] + lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }

        if (x2Speed== 1 && ySpeed== 0) {
            if (m == 1) {
                cpos[1] = cpos[0] - lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);
            } else {
                cpos[1] = -cpos[0] + lpos[0] + lpos[1];
                {
                    inverse = 1;
                }
                canvas.drawBitmap(bmp_ball, cpos[0], cpos[1], null);

            }
            canvas.drawBitmap(bmp_slider, x, getHeight() - bmp_slider.getHeight(), null);
            lpos[0] = cpos[0];
            lpos[1] = cpos[1];
        }


    }

    }






}



