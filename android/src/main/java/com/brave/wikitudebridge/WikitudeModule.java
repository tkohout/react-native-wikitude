package com.brave.wikitudebridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import java.io.File;
import java.util.Arrays;

public class WikitudeModule extends ReactContextBaseJavaModule {
    private MyBroadcastReceiver receiver;

    public WikitudeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(reactContext);
        receiver = new MyBroadcastReceiver();
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(MyBroadcastReceiver.ACTION));

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.brave.wikitudebridge.jsonsent";
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonObject = intent.getStringExtra("data");
            System.out.println(jsonObject);
        }
    }

  @Override
  public String getName() {
    return "RNWikitude";
  }

  /*@ReactMethod
  public void alert(String message) {
    Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
  }*/

  @ReactMethod
  public void startAR(String architectWorldURL, boolean hasGeolocation, boolean hasImageRecognition, boolean hasInstantTracking, String wikitudeSDKKey)
  {


      final Activity currentActivity = getCurrentActivity();

	  final Intent intent = new Intent(currentActivity, WikitudePrecheck.class);

	  intent.putExtra(WikitudeActivity.EXTRAS_KEY_AR_URL, architectWorldURL);
	  intent.putExtra(WikitudeActivity.EXTRAS_KEY_HAS_GEO, hasGeolocation);
	  intent.putExtra(WikitudeActivity.EXTRAS_KEY_HAS_IR, hasImageRecognition);
	  intent.putExtra(WikitudeActivity.EXTRAS_KEY_HAS_INSTANT, hasInstantTracking);
	  intent.putExtra(WikitudeActivity.EXTRAS_KEY_SDK_KEY, wikitudeSDKKey);


	  //launch activity
	  currentActivity.startActivity(intent);
  }

}