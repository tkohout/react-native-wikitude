package com.brave.wikitudebridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

public class WikitudeModule extends ReactContextBaseJavaModule {
    private ModuleBroadcastReceiver receiver;

    public WikitudeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(reactContext);
        receiver = new ModuleBroadcastReceiver();
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(ModuleBroadcastReceiver.ACTION));

    }

    public class ModuleBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.brave.wikitudebridge.jsonsent";
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonString = intent.getStringExtra("data");

            ReactContext reactContext = getReactApplicationContext();

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                WritableMap params = JsonConvert.jsonToReact(jsonObject);
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("json-sent", params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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


    @ReactMethod
    public void stopAR() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getReactApplicationContext());

        Intent intent = new Intent();
        intent.setAction(WikitudeActivity.ActivityBroadcastReceiver.ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }


}