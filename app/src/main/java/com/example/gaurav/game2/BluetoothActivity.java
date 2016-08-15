package com.example.gaurav.game2;

/**
 * Created by Gaurav on 23-07-2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class BluetoothActivity extends Activity
{
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Name of the connected device
    public static String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the bluetooth services
    private BluetoothService mService = null;
    public static String message;
    private static final int STATUS_NOT_CONNECTED = 0;
    private static final int STATUS_CONNECTED = 1;
    private static final int STATUS_CONNECTING = 2;
    private int status;
    public player ply;// used to communicate
    Point size;
  private boolean isGameViewActive;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        message = "";
        status = STATUS_NOT_CONNECTED;
        isGameViewActive = false;

     try {
         size = new Point();
         getWindowManager().getDefaultDisplay().getSize(size);
      //  Toast.makeText(getApplication(),(Integer.toString(size.x)),Toast.LENGTH_SHORT).show();



     }
     catch (NullPointerException e)
     {
        // Toast.makeText(getApplicationContext(), (CharSequence) e,Toast.LENGTH_SHORT).show();
     }
      //  mService.screensize=size;
        ply=new player();
        // Set up the window layout
       /* MyView view = new MyView(this);
        view.setKeepScreenOn(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_bluetoothconn);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ensureDiscoverable();
       Button btn=(Button)findViewById(R.id.button5);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
               startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
           }
       });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (mService == null)
                setupGame();
        }
    }

    private void setupGame()
    {
        // Initialize the BluetoothService to perform bluetooth connections
        mService = new BluetoothService(this, mHandler);
        mService.screensize_x=(size.x);
         mService.screensize_y=size.y;
    }

    @Override
    public synchronized void onResume()
    {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE)
            {
                // Start the Bluetooth services
                mService.start();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
    }

    private void ensureDiscoverable()
    {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
        else
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

            dlgAlert.setMessage("You are discoverable.");
            dlgAlert.setTitle("Make discoverable");
            dlgAlert.setPositiveButton("Ok", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    // Bluetooth is now enabled, so set up a session
                    setupGame();
                } else
                {
                    // User did not enable Bluetooth or an error occured
                    //Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case BluetoothService.STATE_CONNECTED:
                            status = STATUS_CONNECTED;
                            GameView gview = new GameView(getApplicationContext(), mService);
                            gview.setKeepScreenOn(true);
                           setContentView(gview);
                            isGameViewActive = true;
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            status = STATUS_CONNECTING;
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            status = STATUS_NOT_CONNECTED;
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    message = readMessage;

                   //Toast.makeText(getApplication(),substr,Toast.LENGTH_SHORT).show();
                    if(readMessage.equals("exit"))
                    {Toast.makeText(getApplicationContext(),"exit",Toast.LENGTH_SHORT).show();
                        mService.write("exit".getBytes());
                      Intent sa=new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(sa);
                    }
                    if(readMessage.substring(0,1).equals("a"))
                    {
                        mService.start=true;
                        Toast.makeText(getApplication(),readMessage,Toast.LENGTH_SHORT).show();
                    }
                    else if(readMessage.substring(0,1).equals("x"))
                    {String substr=readMessage.substring(readMessage.indexOf(" ")+1);
                        if(substr!=null)
                        {

                             mService.speed_x=Integer.parseInt(substr);
                        }
                    }
                    else if(readMessage.substring(0,1).equals("y"))
                    {String substr=readMessage.substring(readMessage.indexOf(" ")+1);
                        if(substr!=null)
                        {
                            mService.speed_y=Integer.parseInt(substr);
                        }
                    }
                   else {
                        String substr=readMessage.substring(readMessage.indexOf(" ")+1);
                        if(substr!=null) {
                            float position = Float.parseFloat(substr);
                            mService.reciever_x=position;

                        }
                    }
                  //  Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                    Log.e("readddddd",message);

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    if (msg.getData().getString(TOAST).equals("Device connection was lost"))
                    {
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed()
    {
        if (isGameViewActive)
        {
            doExit();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void doExit()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Exit to main menu?");
        alertDialog.setTitle("Exit?");
        alertDialog.show();
    }

    private class MyView extends View
    {
        private Bitmap connectButtonUp;
        private Bitmap connectButtonDown;
        private Bitmap discoverableButtonUp;
        private Bitmap discoverableButtonDown;
        private int screenW;
        private int screenH;
        private boolean connectButtonPressed;
        private boolean discoverableButtonPressed;
        private Context myContext;
        private Bitmap paperGraphic;
        private Bitmap statusNotConnectedGraphic;
        private Bitmap statusConnectedGraphic;
        private Bitmap statusConnectingGraphic;

        public MyView(Context context)
        {
            super(context);
            myContext = context;
            // Load connect button
            //connectButtonUp = BitmapFactory.decodeResource(getResources(),
                //    R.drawable.connect_device_up);
            //connectButtonDown = BitmapFactory.decodeResource(getResources(),
                  //  R.drawable.connect_device_down);
            // Load discoverable button
          //  discoverableButtonUp = BitmapFactory.decodeResource(getResources(),
                   // R.drawable.make_discoverable_up);
          //  discoverableButtonDown = BitmapFactory.decodeResource(getResources(),
                   // R.drawable.make_discoverable_down);
            // Load paper graphic
          //  paperGraphic = BitmapFactory.decodeResource(getResources(),
           //         //R.drawable.paper);
            // Load status graphics
           // statusNotConnectedGraphic = BitmapFactory.decodeResource(getResources(),
                   // R.drawable.status_not_connected);
           // statusConnectedGraphic = BitmapFactory.decodeResource(getResources(),
                    //R.drawable.status_connected);
           // statusConnectingGraphic = BitmapFactory.decodeResource(getResources(),R.drawable.status_connecting);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            // Draw paper
            canvas.drawBitmap(paperGraphic, (screenW - paperGraphic.getWidth()) / 2,
                    (screenH - paperGraphic.getHeight()) / 2, null);
            // Draw connect button
            if(connectButtonPressed)
            {
                canvas.drawBitmap(connectButtonDown,
                        (screenW - connectButtonDown.getWidth()) / 2,
                        (int)(screenH * 0.3), null);
            }
            else
            {
                canvas.drawBitmap(connectButtonUp,
                        (screenW - connectButtonUp.getWidth()) / 2,
                        (int)(screenH * 0.3), null);
            }
            // Draw discoverable button
            if (discoverableButtonPressed)
            {
                canvas.drawBitmap(discoverableButtonDown,
                        (screenW - discoverableButtonDown.getWidth()) / 2,
                        (int)(screenH * 0.45), null);
            }
            else
            {
                canvas.drawBitmap(discoverableButtonUp,
                        (screenW - discoverableButtonUp.getWidth()) / 2,
                        (int)(screenH * 0.45), null);
            }
            // Draw status graphic
            if (status == STATUS_NOT_CONNECTED)
            {
                canvas.drawBitmap(statusNotConnectedGraphic,
                        (screenW - statusNotConnectedGraphic.getWidth()) / 2,
                        (int)(screenH * 0.75), null);
            }
            else if (status == STATUS_CONNECTED)
            {
                canvas.drawBitmap(statusConnectedGraphic,
                        (screenW - statusConnectedGraphic.getWidth()) / 2,
                        (int)(screenH * 0.75), null);
            }
            else  if (status == STATUS_CONNECTING)
            {
                canvas.drawBitmap(statusConnectingGraphic,
                        (screenW - statusConnectingGraphic.getWidth()) / 2,
                        (int)(screenH * 0.75), null);
            }
            invalidate();
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
            screenW = w;
            screenH = h;
        }

        public boolean onTouchEvent(MotionEvent event)
        {
            int eventaction = event.getAction();
            int x = (int)event.getX();
            int y = (int)event.getY();
            switch(eventaction)
            {
                case MotionEvent.ACTION_DOWN:
                    // Check if connect button is pressed
                    if (x > (screenW - connectButtonUp.getWidth()) / 2 &&
                            x < ((screenW + connectButtonUp.getWidth()) / 2) &&
                            y > (int)(screenH * 0.3) &&
                            y < (int)(screenH * 0.3) + connectButtonUp.getHeight())
                    {
                        connectButtonPressed = true;
                        //MySoundPlayer.playSound(myContext, MySoundPlayer.buttonClick);
                    }
                    // Check if discoverable button is pressed
                    if (x > (screenW - discoverableButtonUp.getWidth()) / 2 &&
                            x < ((screenW + discoverableButtonUp.getWidth()) / 2) &&
                            y > (int)(screenH * 0.45) &&
                            y < (int)(screenH * 0.45) + discoverableButtonUp.getHeight())
                    {
                        discoverableButtonPressed = true;
                        //MySoundPlayer.playSound(myContext, MySoundPlayer.buttonClick);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (connectButtonPressed)
                    {
                        // Launch the DeviceListActivity to see devices and do scan
                        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    }
                    if (discoverableButtonPressed)
                    {
                        // Ensure this device is discoverable by others
                        ensureDiscoverable();
                    }
                    connectButtonPressed = false;
                    discoverableButtonPressed = false;
                    break;
            }
            invalidate();
            return true;
        }
    }
}
