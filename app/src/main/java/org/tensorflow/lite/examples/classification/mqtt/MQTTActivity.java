package org.tensorflow.lite.examples.classification.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.CameraActivity;
import org.tensorflow.lite.examples.classification.ClassifierActivity;
import org.tensorflow.lite.examples.classification.R;


public class MQTTActivity extends AppCompatActivity {
    private static final String TAG = "MQTT.Main";

    private RemoteControl mRemoteControl;
    private MainHandler mHandle;
    public static String TOPIC;
    private String uri;
    private String requestUriHeader = "tcp://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
//        initView();

        Button btn_connect = (Button) findViewById(R.id.btn_connect);
//        Button btn_subscribe = (Button) findViewById(R.id.btn_subscribe);
//        Button btn_unsubscribe = (Button) findViewById(R.id.btn_unsubscribe);
//        Button btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        EditText editText_broker = (EditText) findViewById(R.id.editText_broker);
        EditText editText_pub_topic = (EditText) findViewById(R.id.editText_pub_topic);

        mHandle = new MainHandler();

//        mRemoteControl = com.example.android.mqtt.RemoteControl.createInstance(getApplicationContext());
        mRemoteControl =RemoteControl.createInstance(getApplicationContext());
        mRemoteControl.addListener(mMessageListener);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = requestUriHeader + editText_broker.getText().toString();
                TOPIC = editText_pub_topic.getText().toString();
                mRemoteControl.myconnect(uri);
                Log.d(TAG, "onClick: connect mqtt broker");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteControl.registerResources(this);
    }

    @Override
    protected void onPause() {
        mRemoteControl.unregisterResources();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRemoteControl.removeListener(mMessageListener);
//        com.example.android.mqtt.RemoteControl.destroyInstance();
        RemoteControl.destroyInstance();
        super.onDestroy();
    }

    private MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onMessageArrived(String message) {
            Log.v(TAG, "onMessageArrived, message : " + message);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("info", message);
            msg.setData(bundle);
            mHandle.sendMessage(msg);
        }

        @Override
        public void onConnectSuccess() {

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("info", "onConnectSuccess");
            msg.setData(bundle);
            mHandle.sendMessage(msg);
            Toast.makeText(MQTTActivity.this, "MQTT服务器连接成功！", Toast.LENGTH_SHORT).show();
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //Do something after 100ms
//
//                }
//            }, 2000);
            startActivity(new Intent(MQTTActivity.this, ClassifierActivity.class));
        }

        @Override
        public void onConnectFailure(String message) {

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("info", "onConnectFailure:" + message);
            Toast.makeText(MQTTActivity.this, "MQTT服务器连接失败！", Toast.LENGTH_SHORT).show();
            msg.setData(bundle);
            mHandle.sendMessage(msg);

        }

        @Override
        public void connectionLost(String message) {

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("info", "connectionLost:" + message);
            Toast.makeText(MQTTActivity.this, "MQTT服务器连接断开！", Toast.LENGTH_SHORT).show();
            msg.setData(bundle);
            mHandle.sendMessage(msg);
        }
    };

    private class MainHandler extends Handler {

        public MainHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String info = bundle.getString("info");
            ((TextView) findViewById(R.id.text_receive_info)).setText("Received info : \n" + info);
        }
    }


    public void initView() {


    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_connect:
////                mRemoteControl.connect();
//
//                mRemoteControl.myconnect("");
//                break;
//            case R.id.btn_subscribe:
//                mRemoteControl.subscribe(TOPIC);
//                break;
//            case R.id.btn_unsubscribe:
//                mRemoteControl.unsubscribe(TOPIC);
//                break;
//            case R.id.btn_disconnect:
//                mRemoteControl.disconnect();
//                break;
//        }
//    }
}



