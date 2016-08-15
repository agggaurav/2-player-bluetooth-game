package com.example.gaurav.game2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;

public class GameLoopThread extends Thread {
    static final long FPS = 8;
    private GameView view;
    private boolean running = false;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    public GameLoopThread(Context context) {
        this.view=view;
    }

    public void setRunning(boolean run) {
        running = run;
        Intent a=new Intent(view.getContext(),BluetoothActivity.class);

    }

    @Override
    public void run() {
        long ticksPS = 150 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    if(isInterrupted()) {
                        break;
                    }
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(ticksPS);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }


}