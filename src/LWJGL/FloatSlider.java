package LWJGL;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Created by Rene on 20.04.2016.
 */
class FloatSlider<T extends Number> extends JSlider {
    private int genauigkeitsFaktor;
    private T minimumValue;
    private boolean floatslider = true;
    private Slider Name;
    FloatSlider(T minValue, T maxValue, T Value, int genauigkeit, Slider name) {
        Name = name;
        minimumValue = minValue;
        if (genauigkeit == 0){
            floatslider = false;
        }
        if (floatslider){
            genauigkeitsFaktor = (int) Math.pow(10, genauigkeit);
            setMinimum((int)(minValue.floatValue() * genauigkeitsFaktor));
            setMaximum((int)(maxValue.floatValue() * genauigkeitsFaktor));
            setValue((int)(Value.floatValue() * genauigkeitsFaktor));
            return;
        }
        genauigkeitsFaktor = 1;
        setMinimum(minValue.intValue());
        setMaximum(maxValue.intValue());
        setValue(Value.intValue());
    }
    T getSliderValue(){
        if (floatslider){
             return (T) new Float(getValue() /(float)genauigkeitsFaktor);
        }
        return (T) new Integer(getValue());
    }
    void setMinorTickSpacing(T value){
        if (floatslider){
            setMinorTickSpacing((int)(value.floatValue()*genauigkeitsFaktor));
        }else {
            setMinorTickSpacing(value.intValue());
        }
        setPaintTicks(true);
    }
    void setMajorTickSpacing(T value){
        int countLabel = (int) ((Range()/genauigkeitsFaktor/ value.floatValue()) +1);
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        if (floatslider){
            float zahl = minimumValue.floatValue();
            for (int i = 0; i < countLabel;i++){
                JLabel label = new JLabel(""+zahl);
                table.put((int) (zahl*genauigkeitsFaktor),label);
                zahl += value.floatValue();
            }
        }else {
            int zahl = minimumValue.intValue();
            for (int i = 0; i < countLabel; i++) {
                JLabel label = new JLabel("" + zahl);
                table.put(zahl, label);
                zahl += value.floatValue();
            }
        }
        setLabelTable(table);
        setPaintLabels(true);
    }
    private int Range(){
        return Math.abs(getMinimum())+ getMaximum();
    }

    Slider getSName() {
        return Name;
    }
}
