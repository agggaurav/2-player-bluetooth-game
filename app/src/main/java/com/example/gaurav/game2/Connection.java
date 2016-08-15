
package com.example.gaurav.game2;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Gaurav on 20-07-2016.
 */

public class Connection extends Activity
{
    private static final String LOG_TAG = "PikachuVolleyball";
    public static final int TYPE_SCREEN_GAME = 0;
    public static final int TYPE_SCREEN_MENU = 1;
    private int currentSreentType;
    private boolean isHost = true;

    private BluetoothModule btModule;
    private BluetoothModule.BtListAdapter btDevicesListAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /* ========== Customized Members ========== */

    public void init() {
        // Set current screen type
        //setCurScreenType(TYPE_SCREEN_MENU);

        // Init Bluetooth
        Log.d(LOG_TAG, "Trying to init Bluetooth");
      //  btModule = new BluetoothModule(this, this);
        ListView btDevicesListView = new ListView(getApplicationContext());
        ListView btMsgListView = new ListView(getApplicationContext());
        btDevicesListAdapter =  btModule.bindBtDevicesAdapter(btDevicesListView);
        btModule.bindMsgAdapter(btMsgListView, android.R.layout.simple_list_item_1);
    }



    /**
     * Get the BluetoothModule object
     * @return BluetoothModule object
     */
    public BluetoothModule getBtModule() {
        return btModule;
    }

    /**
     * Get discovered Bluetooth devices
     * @return an ArrayList of BluetoothDevices
     */
   public ArrayList<BluetoothDevice> getFoundDevices() {
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        int size = btDevicesListAdapter.getCount();
        for (int i = 0; i < size; i++) {
            BluetoothDevice btd = (BluetoothDevice)btDevicesListAdapter.getItem(i);
            devices.add(btd);
        }
        return devices;
    }

    public ArrayList<BluetoothDevice> getKnownDevices() {
        btModule.btGetKnownDevices();
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        int size = btDevicesListAdapter.getCount();
        for (int i = 0; i < size; i++) {
            BluetoothDevice btd = (BluetoothDevice) btDevicesListAdapter.getItem(i);
            devices.add(btd);
        }
        return devices;
    }

    /**
     * Check if this device is the host of Bluetooth connection
     * @return
     */
    public boolean isHost() {
        return this.isHost;
    }

    /**
     * The callback function of Bluetooth messages receiver thread
     * @param msg Message passed by receiver thread
     */

    public void msgCallback(Message msg) {
       String strMsg;
       switch (msg.what) {
           case BluetoothModule.SERVERSOCK_THREAD_WHAT:
               strMsg = msg.getData().getString(BluetoothModule.SERVERSOCK_MSG_KEY);
               // Log.d(LOG_TAG, "Got message: " + strMsg);
               if (strMsg == BluetoothModule.RESUlT_CONN_OK) {
                   //if (getCurScreenType() == TYPE_SCREEN_MENU) {
                   isHost = true;
                   // ((MainMenuScreen) getCurrentScreen()).startGame();
                   //}
               }
               break;
           case BluetoothModule.CLIENTSOCK_THREAD_WHAT:
               strMsg = msg.getData().getString(BluetoothModule.CLIENTSOCK_MSG_KEY);
               // Log.d(LOG_TAG, "Got message: " + strMsg);
               if (strMsg == BluetoothModule.RESUlT_CONN_OK) {
                   //  if (getCurScreenType() == TYPE_SCREEN_MENU) {
                   isHost = false;
                   //    ((MainMenuScreen) getCurrentScreen()).startGame();
                   //}
               }
               break;
           case BluetoothModule.RECEIVER_THREAD_WHAT:
               strMsg = msg.getData().getString(BluetoothModule.RECEIVER_MSG_KEY);
               // Log.d(LOG_TAG, "Got message: " + strMsg);
              /*  if (getCurScreenType() == TYPE_SCREEN_GAME) {
                    try {
                        int controlCmd = Integer.parseInt(strMsg);

                        if (controlCmd == GameScreen.PAUSE_GAME) {
                            getCurrentScreen().pause();
                        } else if (controlCmd == GameScreen.YOU_GOOD_TO_GO) {
                            getCurrentScreen().resume();
                        } else if (controlCmd == GameScreen.START_THAT_FUKING_GAMEEEE) {
                            try {
                                ((GameScreen) getCurrentScreen()).stargGame();
                            } catch (ClassCastException e) {}
                        } else if (controlCmd == GameScreen.YOU_ARE_LOSE) {
                            ((GameScreen) getCurrentScreen()).endGame();
                        } else {
                            ((GameScreen) getCurrentScreen()).getEnemy().handleAction(controlCmd);
                        }
                    }
                    catch (NumberFormatException e) {
                        // "x y isjumped"
                        String[] tmp = strMsg.split(" ");
                        int x = Integer.parseInt(tmp[0]);
                        int y = Integer.parseInt(tmp[1]);
                        String msgType = tmp[2];
                        if (msgType.equals(GameScreen.VOLLEYBALL_MSG)) {
                            ((GameScreen) getCurrentScreen())
                                    .getVolleyball().setPosition(x, y);
                        }
                        else if (msgType.equals("true") || msgType.equals("false")) {
                            ((GameScreen) getCurrentScreen())
                                    .getEnemy().setPosition(x, y, Boolean.valueOf(msgType));
                        }
                        else if (msgType.equals(GameScreen.SCORE_SYNC)) {
                            ((GameScreen) getCurrentScreen())
                                    .setScores(y, x);
                        }
                        else if (msgType.equals(GameScreen.SCREEN_SIZE_KEY)) {
                            ((GameScreen) getCurrentScreen())
                                    .setOtherScreenSize(x, y);
                        }

                    }
                }*/
             /*   break;
            case BluetoothModule.SYS_MSG_WHAT:
                strMsg = msg.getData().getString(BluetoothModule.SYS_MSG_KEY);
                // Log.d(LOG_TAG, "Got message: " + strMsg);
                break;
            default:
                Log.d(LOG_TAG, "Message error");
                strMsg = "Message error";
                break;
        }
    }


*/
       }
   }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothModule.REQUEST_ENABLE_BIT:
                Log.d(LOG_TAG, "[onActivityResult] REQUEST_ENABLE_BIT");
                // If user has enabled the Bluetooth function
                if (resultCode == RESULT_OK) {
                    btModule.btOKCallback();
                }
                else if (resultCode == RESULT_CANCELED) {
                    new AlertDialog.Builder(this)
                            .setTitle("No Bluetooth Found")
                            .setMessage("Cannot running without Bluetooth. Please enable it.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // exit game
                                    finish();
                                }
                            })
                            .show();
                    btModule.btErrorCallback();
                }
                break;
        }
    }

}

