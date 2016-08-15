package com.example.gaurav.game2;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import java.util.Arrays;
import java.util.List;

import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    private TextView infor;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    //BluetoothService bs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

     //   setContentView(new GameView(this));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
       setContentView(R.layout.activity_main);
        infor = (TextView)findViewById(R.id.info);
       try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.gaurav.game2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
               // infor.setText(Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


      loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
              /*  infor.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +loginResult.getAccessToken().getPermissions()+"\n"+
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
*/
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Log.v("LoginActivity", response.toString());

                                // Application code
                                infor.setText("hghghf");
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                    infor.setText(name + "   " + email + "   " + birthday);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                infor.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                infor.setText("Login attempt failed." + e);
            }
        });


        Button btn1=(Button)findViewById(R.id.button2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent asp=new Intent(getApplication(),BluetoothActivity.class);
               startActivity(asp);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}