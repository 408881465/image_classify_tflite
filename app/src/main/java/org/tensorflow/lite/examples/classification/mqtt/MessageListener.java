package org.tensorflow.lite.examples.classification.mqtt;

/**
 * Created by xiaxing on 16-6-14.
 */
public interface MessageListener {
    void onMessageArrived(String message);

    /**
     * 2020-01-12
     * 新增以下三个连接状态回调
     */
    void onConnectSuccess();

    void onConnectFailure(String message);

    void connectionLost(String message);
}
