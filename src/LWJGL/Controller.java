package LWJGL;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rene on 21.04.2016.
 *
 *
 */
public class Controller {
    private JFrame frame;
    public Controller() {
        frame = new JFrame("Controller");
        frame.setDefaultCloseOperation(3);
        frame.setLayout(new GridLayout(1,0));
        frame.getContentPane().setPreferredSize(new Dimension(700,530));
        frame.setLocation(600, 0);
        frame.pack();
    }

    public void addSubController(JPanel cont){
        frame.add(cont);
    }
    public void SetVisible(){
        frame.setVisible(true);
    }
}
