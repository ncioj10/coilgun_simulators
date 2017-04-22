package rlc;

import javax.swing.*;
import java.util.Hashtable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * JSliderFloat
 * is exactly like a Swing JSlider, except you get/set a 'float' instead of an 'int'.
 * This helps us deal with fractional values of voltage, resistance, capacitance, etc.
 *
 * Also, it listens for action commands to increase/decrease its maximum value.
 * This lets it model both tiny and large RCL components in the circuit.
 *
 * Presentation is exactly what is needed for CoilSim:
 * - Divided into 200 units
 * - 5 major tick marks, each with 5 minor ticks
 * - always vertical
 *
 * JSlider                    JSliderFloat
 * +---+ max = RANGE = 200   +---+ max = fMaximum
 * |   |                     |   |
 * |   |                     |   |
 * |===| x = (y-b)/m         |===| y = mx + b
 * |   |                     |   |
 * |   |                     |   |
 * +---+ min = 0             +---+ min = fMinimum = 0
 *
 * Edited by Barry (barry@coilgun.info) at Sept 16, 2005
 */
public class JSliderFloat
        extends JSlider
        implements ActionListener {
    final static int RANGE = 200;       // number of steps in slider
    final static int MAJOR_TICKS = 5;   // number of major ticks in slider
    final static int MINOR_TICKS = 25;  // number of minor ticks in slider

    final static float fMinimum = 0F;   // minimum allowed slider value
    protected float fMaximum;           // maximum allowed slider value
    protected float fValue;             // current slider value
    protected float labelScaleFactor;   //

    // helper class for formatting numbers
    private Nearest nearest = new Nearest();

    // ctor
    JSliderFloat(float fMax, float fInit, float fLabelScaleFactor) {
        super(JSlider.VERTICAL, 0, RANGE, 0);

        int iMajorSpacing = RANGE / MAJOR_TICKS;
        int iMinorSpacing = RANGE / MINOR_TICKS;
        setMajorTickSpacing(iMajorSpacing);
        setMinorTickSpacing(iMinorSpacing);
        setPaintTicks(true);

        // Create the custom label table
        createCustomLabels(fMax, fLabelScaleFactor);

        fMaximum = fMax;
        labelScaleFactor = fLabelScaleFactor;
        setValueFloat(fInit);
    }

    private void createCustomLabels(float fMax, float fLabelScaleFactor) {
        // the first entry is special because we want to show "0" instead of "0.0"
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0") );

        for (int ii=1; ii<=MAJOR_TICKS; ii++) {
            String sLabel = nearest.toStringEng(fMax*ii/MAJOR_TICKS*fLabelScaleFactor, 2, "");
            labelTable.put( new Integer( RANGE*ii/MAJOR_TICKS ), new JLabel(sLabel) );
        }
        setLabelTable( labelTable );
        setPaintLabels(true);
    }

    // Provide new interface that returns 'float'
    public float getMin() {
        return fMinimum;
    }
    public float getMax() {
        return fMaximum;
    }

    public void  setValueFloat(float fSetValue) {
        fValue = fSetValue;
        int iSliderValue = (int)((fSetValue - fMinimum)/(fMaximum - fMinimum)*RANGE);
        super.setValue(iSliderValue);
    }

    public float getValueFloat() {
        fValue = (super.getValue() * (fMaximum - fMinimum) / RANGE) + fMinimum;
        return fValue;
    }

    // Need a function for "reset" to restore original scale and appearance
    public void setMaximum(float fMax) {
        fMaximum = fMax;
        createCustomLabels(fMax, labelScaleFactor);
        setValueFloat(fValue);
    }

    // Listen for action commands to adjust the range of values
    public void actionPerformed(ActionEvent e) {
    if ("increase".equals(e.getActionCommand())) {
        // if possible, INCREASE the maximum slider range by factor of 10
        if (fMaximum < 1E9) {
            fMaximum *= 10F;
            fValue *= 10F;
            createCustomLabels(fMaximum, labelScaleFactor);
            fireStateChanged();     // notify all dependent components, e.g. text label
        }
    } else {
        // if possible, DECREASE the maximum slider range by factor of 10
        if (fMaximum > 1E-9) {
            fMaximum /= 10F;
            fValue /= 10F;
            createCustomLabels(fMaximum, labelScaleFactor);
            fireStateChanged();     // notify all dependent components, e.g. text label
        }
    }
}
}
