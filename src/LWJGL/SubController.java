package LWJGL;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene on 20.04.2016.
 */

public class SubController extends Observable implements ChangeListener {
    private List<Pair<FloatSlider,JLabel>> sliders;
    private int ID;

    JPanel getFrame() {
        return frame;
    }

    private JPanel frame;

    /**
     * @param ID
     * @param obs
     */
    public SubController(int ID, Observer obs) {
        this.ID = ID;
        frame = new JPanel();
        frame.setLayout(new GridLayout(1,0));
        sliders = new ArrayList<>();
        addObserver(obs);
        DreieckData data = ((DreieckUebung)obs).getPunktData(ID);
        AddSlider(-1.0f,1.0f,1.0f,0.5f,data.y,1,Slider.YPOS);
        AddSlider(-1.0f,1.0f,0.1f,0.5f,data.x,2,Slider.XPOS);
        AddSlider(0.0f,1.0f,0.1f,0.25f,data.red,2,Slider.RED);
        AddSlider(0f,255f,25f,50f,data.green * 255,0,Slider.GREEN);
        AddSlider(0,255,25,50,data.blue * 255,1,Slider.BLUE);
        frame.add(GetAll());
    }
    private JPanel GetAll(){
        JPanel panel = new JPanel(new GridLayout(0,1));
        sliders.forEach(x ->panel.add(GetPanel(x.getValue(),x.getKey())));
    	return panel;
    }
    private JPanel GetPanel(JLabel label, JSlider slider){
        JPanel panel = new JPanel(new GridLayout(2,0));
        panel.add(label);
        panel.add(slider);
        return panel;
    }

    private void AddSlider(float min, float max, float minor, float major, float value, int genauigkeit, Slider name){
        FloatSlider slider;
        if (genauigkeit == 0){
            slider = new FloatSlider<>((int) min, (int) max, (int) value, genauigkeit,name);
        }else{
            slider = new FloatSlider<>(min, max, value, genauigkeit,name);
        }
        JLabel label = new JLabel(name.getLabelText() + (genauigkeit == 0 ? String.valueOf((int) value) : String.valueOf(value)));
        slider.addChangeListener(this);
        slider.setMajorTickSpacing(major);
        slider.setMinorTickSpacing(minor);
        sliders.add(new Pair<>(slider,label));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        sliders.forEach(x -> x.getValue().setText(x.getKey().getSName().getLabelText()+ x.getKey().getSliderValue()));
        setChanged();
        notifyObservers();
    }

    public float GetSliderValue(Slider name){
        final float[] bla = {0};
        sliders.forEach(x -> bla[0] = x.getKey().getSName().equals(name)
                ? x.getKey().getSliderValue().floatValue()
                : bla[0]);
        return bla[0];
    }

    public int getID() {
        return ID;
    }
}
