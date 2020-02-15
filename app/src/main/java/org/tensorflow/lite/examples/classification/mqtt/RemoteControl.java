package org.tensorflow.lite.examples.classification.mqtt;

import android.content.Context;
import android.util.Log;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.tensorflow.lite.examples.classification.mqtt.MessageListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xiaxing on 16-6-7.
 */
public class RemoteControl {
    public static final String TAG = "RemoteControl";

    /**
     * 修改MQTT服务器的地址
     */
    public static final String URI_DEFAULT = "tcp://www.sjaiedu.com:1883";
    public static final String clientId = "paho-mqtt-clientid-002";

    private MqttAndroidClient mMqttAndroidClient;
    private CopyOnWriteArrayList<MessageListener> mMessageListeners = new CopyOnWriteArrayList<>();
    private Context mContext;
    private String requestUri;

    public RemoteControl(Context context) {
        mContext = context;
    }

    private static RemoteControl sInstance = null;

    public static RemoteControl createInstance(Context context) {
        if (sInstance == null)
            sInstance = new RemoteControl(context);

        return sInstance;
    }

    public static void destroyInstance() {
        if (sInstance == null)
            return;

        sInstance = null;
    }

    public static RemoteControl getInstance() {
        return sInstance;
    }

    public void registerResources(Context context) {
        if (mMqttAndroidClient == null)
            return;

        mMqttAndroidClient.registerResources(context);
    }

    public void unregisterResources() {
        if (mMqttAndroidClient == null)
            return;

        mMqttAndroidClient.unregisterResources();
    }

    public void addListener(MessageListener listener) {
        if (listener == null)
            return;

        if (!mMessageListeners.contains(listener))
            mMessageListeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        if (listener == null)
            return;

        if (mMessageListeners.contains(listener))
            mMessageListeners.remove(listener);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setKeepAliveInterval(1000);
//        mqttConnectOptions.setUserName("user-name:summer");
//        String msg = "hahaha";
//        mqttConnectOptions.setWill("topic:111", msg.getBytes(), 0, true);

        requestUri = URI_DEFAULT;

        mMqttAndroidClient = new MqttAndroidClient(mContext.getApplicationContext(), requestUri, clientId);
        mMqttAndroidClient.setTraceEnabled(true);
        mMqttAndroidClient.setTraceCallback(new TraceHandler());
        mMqttAndroidClient.setCallback(mMqttCallback);

        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, mIMqttActionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void myconnect(String uri){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setKeepAliveInterval(1000);

        mMqttAndroidClient = new MqttAndroidClient(mContext.getApplicationContext(), uri, clientId);
        mMqttAndroidClient.setTraceEnabled(true);
        mMqttAndroidClient.setTraceCallback(new TraceHandler());
        mMqttAndroidClient.setCallback(mMqttCallback);

        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, mIMqttActionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mMqttAndroidClient.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void publish(String message, String topic) {
        byte[] bMsg = message.getBytes();
        try {
            mMqttAndroidClient.publish(topic, bMsg, 0, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            mMqttAndroidClient.subscribe(topic, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void unsubscribe(String topic) {
        try {
            mMqttAndroidClient.unsubscribe(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            Log.v(TAG, "onSuccess");
            /**
             * 2020-01-12
             * 连接成功回调
             */
            for (MessageListener listener : mMessageListeners) {
                listener.onConnectSuccess();
            }
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            Log.v(TAG, "onFailure, " + throwable.toString());
            /**
             * 2020-01-12
             * 连接失败回调
             */
            for (MessageListener listener : mMessageListeners) {
                listener.onConnectFailure(throwable.toString());
            }
        }
    };

    private MqttCallback mMqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable throwable) {

            if (throwable != null) {
                Log.v(TAG, "mqtt callback connectionLost : " + throwable.toString());
                for (MessageListener listener : mMessageListeners) {
                    listener.connectionLost(throwable.toString());
                }
            } else {
                Log.v(TAG, "mqtt callback connectionLost" );
                for (MessageListener listener : mMessageListeners) {
                    listener.connectionLost("");
                }
            }
        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            Log.v(TAG, "mqtt callback messageArrived, s : " + s + ", message : " + mqttMessage.toString());
            fireMessageArrived(mqttMessage.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            Log.v(TAG, "mqtt callback deliveryComplete, s : " + iMqttDeliveryToken.toString());
        }
    };

    private void fireMessageArrived(final String message) {
        for (MessageListener listener : mMessageListeners) {
            listener.onMessageArrived(message);
        }
    }
}
