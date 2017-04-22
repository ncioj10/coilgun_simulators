import coilsim.InductorSim9;
import rlc.CoilSim20;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicolas on 22.04.2017.
 */
public class StartMenu  extends JApplet{
    private JButton coilSimButton;
    private JButton RLCSimButton;
    private JPanel menuPanel;

    public StartMenu() {
        coilSimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCoilSimApplet();
            }
        });
        RLCSimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRlcApplet();
            }
        });
    }

    public static void main(String[] args) {
        JFrame menuFrame = new JFrame("Menu");
        menuFrame.setContentPane(new StartMenu().menuPanel);
        menuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        menuFrame.pack();
        menuFrame.setVisible(true);
    }

    public void startCoilSimApplet(){
        InductorSim9 sim9=new InductorSim9();
        JFrame myFrame=new JFrame("Inductor Sim");
        myFrame.setSize(800,600);
        myFrame.add(sim9);
        myFrame.setVisible(true);
        sim9.init();
    }

    public void startRlcApplet(){
        CoilSim20 rlc=new CoilSim20();
        JFrame myFrame=new JFrame("Coil Sim");
        myFrame.setSize(1000,600);
        myFrame.add(rlc);
        myFrame.setVisible(true);
        rlc.init();
    }
}
