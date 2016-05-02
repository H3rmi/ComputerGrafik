package LWJGL;

/**
 * Created by Rene on 21.04.2016.
 */
public enum Slider{
    RED("Rot:"), GREEN("Grün:"),BLUE("Blau:"),
    XPOS("Position X:"),YPOS("Position Y:"),
    X_AXIS("Rotation X-Achse:"),
    Y_AXIS("Rotation Y-Achse:"),
    Z_AXIS("Rotation Z-Achse:"),
    ;
    private String LabelText;
    Slider(String s) {
        this.LabelText = s;
    }

    public String getLabelText(){
        return LabelText;
    }
}
