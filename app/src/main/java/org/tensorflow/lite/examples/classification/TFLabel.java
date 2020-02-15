package org.tensorflow.lite.examples.classification;

/**
 * 2020-01-12
 * 新增一个类，用于临时存储识别的标签
 */
public class TFLabel {

    private String label;
    private float value;

    public TFLabel(String label, float value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
