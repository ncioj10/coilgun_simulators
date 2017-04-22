package rlc;/*
 * A simulation of a classic RLC circuit in Java.
 * Written using Symantec Visual Cafe Professional Edition, v3
 * also using IntelliJ IDEA 3.0.5
 * also using NetBeans (I just can't make up my mind!)
 * also using IntelliJ IDEA 4.5
 * and again with IntelliJ IDEA 5.0
 * Copyright Barry Hansen, (c)2001,2008, barry@coilgun.info
 * All rights reserved.
*/

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

//----------------------------------------------------------------------
//           CoilSim20
//----------------------------------------------------------------------
public class CoilSim20 extends javax.swing.JApplet
                      implements ChangeListener
{
    public String sVersion = "v2.0";    // last modified: 06 January 2008
    static private final Rectangle appletSize = new Rectangle(0,0, 740, 400);

    // physical limits on real world parameters
    static final float MAX_VOLT = 500;
    static final float MAX_OHM  = 0.5F;
    static final float MAX_FARAD = 0.050F;
    static final float MAX_HENRY = 0.005F;

    // starting values for *our* initial state, assuming typical hobbyist values
    static final float INIT_VOLTAGE = 100.F;        // initial capacitor charge (volts)
    static final float INIT_CAPACITANCE = 0.010F;   // initial capacitor size (farads)
    static final float INIT_RESISTANCE = 0.10F;     // initial lumped resistance (ohms)
    static final float INIT_INDUCTANCE = 0.001F;    // initial coil size with NO projectile (henrys)

    // physical parameters of the real world, in standard units
    public float m_voltage;             // capacitor charge (volts)
    public float m_capacitance;         // capacitor size   (farads)
    public float m_resistance;          // lumped resistance (ohms)
    public float m_inductance;          // coil size with NO projectile (henrys)

    // things that control simulation
    static final int NUM_STEPS = 500;   // total number of divisions in simulation
    public float m_elapsed_time;        // total simulation time (sec)
    private boolean inInit;             // TRUE = init() is active, don't run simulation
    
    // simulation results
    SimState m_state[];                 // an array of references to circuit state objects
    
    // helper class for formatting numbers
    private Nearest nearest = new Nearest();
    static final Color BEIGE = new java.awt.Color(255,255,221);        // input background color
    static final Color GREENPASTEL = new java.awt.Color(229,255,238);  // output background color

    static private final Point UPLEFT = new Point(90,90);      // degrees, x=start, y=arc angle
    static private final Point UPRIGHT = new Point(0,90);      // x = 0 = 3 o'clock
    static private final Point LOWLEFT = new Point(180,90);    // y = +ve = ccw
    static private final Point LOWRIGHT = new Point(270,90);


    // Declare GUI controls
    Label textTitle = new Label();
    JSliderFloat sliderVoltage;         JLabel textVoltValue;
    JSliderFloat sliderResistance;      JLabel textOhmValue;
    JSliderFloat sliderCapacitance;     JLabel textFaradValue;
    JSliderFloat sliderInductance;      JLabel textHenryValue;

    java.awt.Canvas canvas_input = new java.awt.Canvas();   // region with all input controls - beige background and rounded corners
    java.awt.Canvas canvas_output = new java.awt.Canvas();  // region with all output data - pastel green background and rounded corners
    java.awt.Canvas canvas_graph = new java.awt.Canvas();   // region where output graph is plotted
    java.awt.Button buttonReset = new java.awt.Button();
    java.awt.Checkbox checkboxVoltage = new java.awt.Checkbox();
    java.awt.Checkbox checkboxCurrent = new java.awt.Checkbox();
    java.awt.Checkbox checkboxDiode = new java.awt.Checkbox();

    /**
     * Initialize our java application by filling in all the user interface
     * elements, and choosing start-up values for sliders that select the
     * values of electrical components. Run the simulation once to display
     * a graph of something to get them started.
     */
    public void init()
    {
        // prevent simulation from running until initialization is complete
        inInit = true;

        // init array with 400 references, all null
        m_state = new SimState[NUM_STEPS];
        SimState.m_fMax = 0.F;

        // set initial state to default values
        m_voltage = INIT_VOLTAGE;
        m_resistance = INIT_RESISTANCE;
        m_capacitance = INIT_CAPACITANCE;
        m_inductance = INIT_INDUCTANCE;

        // set the time duration to show a couple cycles at this resonant frequency
        m_elapsed_time = getTotalSimulationTime(m_inductance, m_capacitance);

        // begin creating the user interface
        Container content = getContentPane();
        content.setLayout(null);
        content.setBackground(BEIGE);
        setSize(appletSize.width, appletSize.height);

        // tile the applet with input/output canvas areas
        canvas_input.setBounds(0,0, 344,appletSize.height);
        canvas_input.setVisible(false);
        content.add(canvas_input);

        canvas_output.setBounds(344,0, appletSize.width-344,appletSize.height);
        canvas_output.setVisible(false);
        content.add(canvas_input);

        // define where the output graph will appear, just inside the output area
        Rectangle rGraph = canvas_output.getBounds();
        rGraph.x += 40;
        rGraph.y += 20;
        rGraph.width -= (40 + 16);
        rGraph.height -= (16 + 20);
        canvas_graph.setBounds(rGraph);
        canvas_graph.setVisible(false);
        content.add(canvas_graph);

        // draw title for the graph
        textTitle.setText("Barry's RLC Simulator    " + sVersion);
        textTitle.setAlignment(Label.CENTER);
        textTitle.setBounds(canvas_input.getBounds().x+10,6, canvas_input.getBounds().width-20,18);
        content.add(textTitle);

        sliderVoltage = new JSliderFloat(MAX_VOLT, m_voltage, 1.0F);
        textVoltValue = createSlider(8, 32, "Volts", "v", sliderVoltage);

        sliderResistance = new JSliderFloat(MAX_OHM, m_resistance, 1.0F);
        textOhmValue = createSlider(88, 32, "R", "ohm", sliderResistance);

        sliderCapacitance = new JSliderFloat(MAX_FARAD, m_capacitance, 1000000F);
        textFaradValue = createSlider(168, 32, "C", "uF", sliderCapacitance);

        sliderInductance = new JSliderFloat(MAX_HENRY, m_inductance, 1000F);
        textHenryValue = createSlider(248, 32, "L", "mH", sliderInductance);

        updateAllValueLabels();

        checkboxDiode.setLabel("Use antiparallel diode");
        checkboxDiode.setBounds(12,316, 186,24);
        checkboxDiode.setVisible(false);        // TODO - fix the simulator, and re-enable this option
        content.add(checkboxDiode);

        checkboxCurrent.setState(true);
        checkboxCurrent.setLabel("Show inductor current");
        checkboxCurrent.setForeground(java.awt.Color.blue);
        checkboxCurrent.setBounds(12,338, 186,24);
        content.add(checkboxCurrent);

        checkboxVoltage.setLabel("Show capacitor voltage");
        checkboxVoltage.setForeground(java.awt.Color.red);
        checkboxVoltage.setBounds(12,360, 186,24);
        content.add(checkboxVoltage);

        buttonReset.setLabel("Reset");
        buttonReset.setBounds(198,340, 80,48);
        content.add(buttonReset);

        //{{REGISTER_LISTENERS
        SymAction lSymAction = new SymAction();
        buttonReset.addActionListener(lSymAction);
        SymItem lSymItem = new SymItem();
        checkboxVoltage.addItemListener(lSymItem);
        checkboxCurrent.addItemListener(lSymItem);
        checkboxDiode.addItemListener(lSymItem);
        //}}

        // init is complete, run simulation for the first time
        // this prevents it from running while init'ing the slider controls
        inInit = false;
        runSim();
    }

    /** Helper function to compute time (seconds) for at least TWO cycles at natural frequency
     * @param inductance in henrys
     * @param capacitance in farads
     * @return seconds
     */
    private float getTotalSimulationTime(float inductance, float capacitance) {
        final float NUMBER_OF_CYCLES = 2.F;
        double natural_freq = 1.F / (2 * Math.PI * Math.sqrt(inductance * capacitance));
        double expected_et = NUMBER_OF_CYCLES / natural_freq;
        double roundup_et = Nearest.ThirdDecade(expected_et);
        return (float)roundup_et;
    }

    /**
     * Re-Initialize our java controls, after the user clicks 'Reset'
     */
    public void reset()
    {
        // set initial state to default values
        inInit = true;
        SimState.m_fMax = 0.F;
        sliderVoltage.setMaximum(MAX_VOLT);
        sliderVoltage.setValueFloat(INIT_VOLTAGE);

        sliderResistance.setMaximum(MAX_OHM);
        sliderResistance.setValueFloat(INIT_RESISTANCE);

        sliderCapacitance.setMaximum(MAX_FARAD);
        sliderCapacitance.setValueFloat(INIT_CAPACITANCE);

        sliderInductance.setMaximum(MAX_HENRY);
        sliderInductance.setValueFloat(INIT_INDUCTANCE);

        checkboxDiode.setState(false);

        inInit = false;
        runSim();
    }

    /**
     * This applet uses four identical sliders and all their UI elements.
     * 'createSlider' does all the work to create them identically,
     * passing parameters to handle the different names and ranges.
     * @param sTitle = displayed name above the controls, e.g. "C"
     * @param sUnits = displayed units above the controls, e.g. "uF"
     * @param jSlider = slider control itself is connected to notification controls
     */
    private JLabel createSlider(int x, int y,
                                String sTitle, String sUnits,
                                JSliderFloat jSlider) {
        Container content = getContentPane();   // the contentPane object for this applet
        final int h = 190;          // slider pixel height
        final int w = 80;           // slider pixel width
        final int y1 = y;           // title
        final int y2 = y + 20;      // units    was y + 24
        final int y3 = y + 40;      // buttons  was y + 38
        final int y4 = y + 58;      // slider   was y + 48
        final int y5 = y + 248;     // value    was y + 238

        // When you move the slider's knob, the stateChanged method of the slider's ChangeListeners are called.
        // We use it to (1) change the text field, and (2) run the simulation
        jSlider.addChangeListener( this );
        jSlider.setBackground(BEIGE);

        // first - static top row of labels (V, R, C, L)
        JLabel jTitle = new JLabel(sTitle, JLabel.CENTER);
        jTitle.setBounds(x,y1, w*2/3,12);
        content.add(jTitle);

        // second - static row of labels (volt, ohm, uF, uH)
        JLabel jUnits = new JLabel(sUnits, JLabel.CENTER);
        jUnits.setBounds(x,y2, w*2/3,12);
        content.add(jUnits);

        // third - small +/- buttons to adjust slider's range
        ImageIcon imageDecrease = createImageIcon("decrease.gif");
        ImageIcon imageIncrease = createImageIcon("increase.gif");

        // icon image should include one-pixel blank border
        int iconWidth = imageDecrease.getIconWidth() + 8;
        int iconHeight = imageDecrease.getIconHeight() + 6;

        JButton jDecrease = new JButton(imageDecrease);
        jDecrease.setActionCommand("decrease");
        jDecrease.setBounds(x+8,y3, iconWidth, iconHeight);
        content.add(jDecrease);

        JButton jIncrease = new JButton(imageIncrease);
        jIncrease.setActionCommand("increase");
        jIncrease.setBounds(x+8+iconWidth+4,y3, iconWidth, iconHeight);
        content.add(jIncrease);

        // fourth - slider control itself
        jSlider.setBounds(x,y4, w,h);
        content.add(jSlider);

        // fifth - text label that shows the numeric value of slider
        JLabel jTextValue = new JLabel("3.14 "+sUnits);
        jTextValue.setLabelFor(jSlider);    // for possible assistive UI technologies
        jTextValue.setBounds(x+4,y5, w*8/10, 20);

        Font oldFont = jTextValue.getFont();      // I don't like default 'bold' so make it plain
        Font newFont = oldFont.deriveFont(Font.PLAIN, oldFont.getSize());
        jTextValue.setFont(newFont);

        content.add(jTextValue);

        // register listeners
        jIncrease.addActionListener(jSlider);
        jDecrease.addActionListener(jSlider);

        return jTextValue;
    }

    /**
     * Helper function to show pictures
     * @param path is the complete name of the image to load
     * @return returns an ImageIcon, or null if the path was invalid
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = CoilSim20.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Every time a slider changes, re-run the simulation
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();

        // update the value displayed in the edit box for this slider control
        //setLabelVolts( source.getValueFloat() );
        if (source == sliderVoltage) {
            // someone moved the voltage slider, so update the label beneath it
            updateValueLabel(sliderVoltage, textVoltValue, "v");
        }
        else if (source == sliderResistance) {
            updateValueLabel(sliderResistance, textOhmValue, "ohm");
        }
        else if (source == sliderCapacitance) {
            updateValueLabel(sliderCapacitance, textFaradValue, "F");
        }
        else if (source == sliderInductance) {
            updateValueLabel(sliderInductance, textHenryValue, "H");
        }
        else {
            // should not happen -- an event occurred we didn't expect!
        }

        runSim();
    }

    private void updateValueLabel(JSliderFloat slider, JLabel label, String units) {
        float fValue = slider.getValueFloat();
        String s = nearest.toStringEng(fValue, 3, units);
        label.setText(s);
    }

    private void updateAllValueLabels() {
        updateValueLabel(sliderVoltage, textVoltValue, "v");
        updateValueLabel(sliderResistance, textOhmValue, "ohm");
        updateValueLabel(sliderCapacitance, textFaradValue, "F");
        updateValueLabel(sliderInductance, textHenryValue, "H");
    }

    /**
     * run the simulation by reading the slider values, and
     * looping through all the itty bitty increments of time.
     * Oh yeah, plot the results when we're done.
     */
    public void runSim() {
        // don't run if we got called while initializing slider controls
        if (inInit)
            return;

        // query control settings for starting point
        m_voltage     = sliderVoltage.getValueFloat();      // volts
        m_resistance  = sliderResistance.getValueFloat();   // ohms
        m_capacitance = sliderCapacitance.getValueFloat();  // farads
        m_inductance  = sliderInductance.getValueFloat();   // henrys

        // set the time duration to show a couple cycles at this resonant frequency
        m_elapsed_time = getTotalSimulationTime(m_inductance, m_capacitance);

        // set the very first initial state
        m_state[0] = new SimState( m_voltage, 0);
        m_state[0].init_comp( m_resistance, m_inductance, m_capacitance, checkboxDiode.getState() );

        // run simulation and show results
        float steptime = m_elapsed_time / NUM_STEPS;    // time between each clock tick
        for (int ii=1; ii<NUM_STEPS; ii++) {
            m_state[ii] = m_state[ii-1].NextState( steptime );
        }
        repaint();
    } // end runSim()

    /**
     * Debug: highlight a canvas by drawing a red border
     * @param g graphics context
     * @param c canvas
     */
    private void debugCanvasBorder(Graphics g, java.awt.Component c, Color bordercolor) {
        Color savedColor = g.getColor();
        g.setColor(bordercolor);

        Rectangle rCanvas = c.getBounds();
        g.drawRect(rCanvas.x,rCanvas.y,rCanvas.width,rCanvas.height);

        g.setColor(savedColor);
    }

    /**
     * Draw the axis and tick marks, then draw the graph
     */
    public void paint(Graphics g) {
        super.paint(g); // clear the canvas
        paintRoundedBorder(g, canvas_input.getBounds());
        paintRoundedBorder(g, canvas_output.getBounds());
        PaintFrame(g);
        paintCurrentGraph(g);
        paintVoltageGraph(g);
    }

    //------------------------------------------------------------------
    private void paintRoundedBorder(Graphics g, Rectangle area)
    {
        // draw border around perimeter
        int nRadius = 16;

        int left = area.x;
        int right = area.x + area.width;
        int top = area.y;
        int bottom = area.y + area.height;
        drawRoundCorner(g, UPLEFT,   left,top);                         //  upper left corner
        drawRoundCorner(g, UPRIGHT,  right-2*nRadius,top);              //  upper right corner
        drawRoundCorner(g, LOWLEFT,  left,bottom-2*nRadius);            //  lower left corner
        drawRoundCorner(g, LOWRIGHT, right-2*nRadius, bottom-2*nRadius);//  bottomostest rightest corner

        g.drawLine(left+nRadius,top, right-nRadius,top);      // top border
        g.drawLine(right,nRadius,    right,bottom-nRadius);   // right border
        g.drawLine(left+nRadius,     bottom, right-nRadius,bottom);   // bottom border
        g.drawLine(left,nRadius,     left,bottom-nRadius);    // left border
    }

    private void drawRoundCorner(Graphics g, Point arc_angle, int x, int y) {
        final int nRadius = 16;
        //        origin of rectangle containing arc
        //        |    size of rectangle containing arc
        //        |    |                    arc angles
        //        |    |                    |
        g.drawArc(x,y, 2*nRadius,2*nRadius, arc_angle.x,arc_angle.y);
    }

    //------------------------------------------------------------------
    private void PaintFrame(Graphics g) {
        // get the dimensions of the window
        //  +--------------------------+ (10  20  50) ...
        //  |                          |
        //  +   -   -   -   -  -   -   +  8   16  40
        //  |                          |
        //  +   -   -   -   -  -   -   +  6   12  30
        //  |                          |
        //  +   -  -   -   -   -   -   +  4    8  20
        //  |                          |
        //  +   -  -   -   -   -   -   +  2    4  10
        //  |                          |
        //  +--------------------------+ 0
        //  |                          |
        //  +   -   -   -  -   -   -   + -2   -4  -10
        //  |                          |
        //  +--------------------------+ -4   -8  -20
        //
        Rectangle bou = canvas_graph.getBounds();
        final int nWidth = bou.width;
        final int nHeight = bou.height;
        final int nX = bou.x;
        final int nY = bou.y;

        // Debug: draw a red rectangle around the output results area
        //debugCanvasBorder(g, canvas_input, Color.RED);
        //debugCanvasBorder(g, canvas_graph, Color.GREEN);

        // prepare to measure width of strings on the window
        Font f = getFont();
        FontMetrics fm = getFontMetrics(f);

        int saveIndex = findCurrentMax();
        double fMaxAmps = m_state[saveIndex].m_Lcurrent;
        float y1_axis_maximum = Nearest.FractionOfDecade(fMaxAmps);      // 1,2,5, 10,20,50, ...
        float y2_axis_maximum = Nearest.FractionOfDecade(m_voltage);     // 1,2,5, 10,20,50, ...

        g.setColor(Color.gray);

        // label X-axis tic marks with "Time"
        final int XLABELS = 5;
        for (int ii=1; ii<=XLABELS; ii++) {
            String sTime = nearest.toStringEng(m_elapsed_time * ii/XLABELS, 2, "s");
            g.drawString(sTime, nX+nWidth*ii/XLABELS-fm.stringWidth(sTime)/2, nY+nHeight*50/70+fm.getHeight() );
        }

        // draw horizonal axises
        draw_X_axis_major(g, nY + nHeight* 0/70,  3, 0);
        draw_X_axis_minor(g, nY + nHeight*10/70);
        draw_X_axis_minor(g, nY + nHeight*20/70);
        draw_X_axis_minor(g, nY + nHeight*30/70);
        draw_X_axis_minor(g, nY + nHeight*40/70);
        draw_X_axis_major(g, nY + nHeight*50/70,  3,-3);
        draw_X_axis_minor(g, nY + nHeight*60/70);
        draw_X_axis_major(g, nY + nHeight*70/70,  0,-3);

        // draw vertical axises
        draw_Y_axis_major(g, nX, 0);
        draw_Y_axis_major(g, nX+nWidth, 0);
        if (checkboxCurrent.getState()) {
            draw_Y_axis_label(g, fm, y1_axis_maximum, nX,        nY, Color.BLUE, "A");
        }
        if (checkboxVoltage.getState()) {
            draw_Y_axis_label(g, fm, y2_axis_maximum, nX+nWidth, nY, Color.RED,  "v");
        }

        // draw settings
        String s = "V=" + textVoltValue.getText() + "     "
                 + "R=" + textOhmValue.getText() + "     "
                 + "C=" + textFaradValue.getText() + "     "
                 + "L=" + textHenryValue.getText();
        g.setColor(Color.BLACK);
        g.drawString(s, nX+nWidth/2-fm.stringWidth(s)/2, nY-4 );
    }

    private void draw_X_axis_major(Graphics g, int nY, int below, int above) {
        Rectangle bou = canvas_graph.getBounds();
        final int left = bou.x;
        final int nWidth = bou.width;

        g.drawLine(left,nY, left+nWidth,nY);
        for (int ii=0; ii<=10; ii++) {
            int nXoffset = nWidth * ii / 10;
            g.drawLine( left+nXoffset, nY+below, left+nXoffset, nY+above);
        }
    }

    private void draw_X_axis_minor(Graphics g, int nY) {
        Rectangle bou = canvas_graph.getBounds();
        final int left = bou.x;
        final int nWidth = bou.width;

        Color oldcolor = g.getColor();
        g.setColor(Color.lightGray);
        g.drawLine(left,nY, left+nWidth,nY);
        g.setColor(oldcolor);
    }

    private void draw_Y_axis_major(Graphics g, int x, int nudge) {
        Rectangle bou = canvas_graph.getBounds();
        final int top = bou.y;
        final int nHeight = bou.height;

        g.drawLine(x, top, x, top+nHeight);
        if (nudge != 0) {
            for (int ii=0; ii<=7; ii++) {
                int nYoffset = nHeight * ii / 7;
                g.drawLine( x,top+nYoffset,   x+nudge, top+nYoffset );
            }
        }
    }

    private void draw_Y_axis_label(Graphics g, FontMetrics fm, float maxval, int left, int top, Color color, String sUnit) {
        // nudge text toward left of vertical axis
        left -= 2;
        top += fm.getHeight()/3;

        Rectangle bou = canvas_graph.getBounds();
        final int nHeight = bou.height;
        g.setColor(color);

        String label = nearest.toStringEng(maxval, 2, sUnit);      g.drawString(label, left-fm.stringWidth(label), top );
        label = nearest.toStringEng(maxval*0.80, 2, sUnit);        g.drawString(label, left-fm.stringWidth(label), top+nHeight*10/70 );
        label = nearest.toStringEng(maxval*0.60, 2, sUnit);        g.drawString(label, left-fm.stringWidth(label), top+nHeight*20/70 );
        label = nearest.toStringEng(maxval*0.40, 2, sUnit);        g.drawString(label, left-fm.stringWidth(label), top+nHeight*30/70 );
        label = nearest.toStringEng(maxval*0.20, 2, sUnit);        g.drawString(label, left-fm.stringWidth(label), top+nHeight*40/70 );
        label = nearest.toStringEng(maxval*0.00, 2, "");           g.drawString(label, left-fm.stringWidth(label), top+nHeight*50/70 );
        label = "-" + nearest.toStringEng(maxval*0.20, 2, sUnit);  g.drawString(label, left-fm.stringWidth(label), top+nHeight*60/70 );
        label = "-" + nearest.toStringEng(maxval*0.40, 2, sUnit);  g.drawString(label, left-fm.stringWidth(label), top+nHeight );
    }

    //--------------------------------------------------------------
    // paint graph of inductor current
    //--------------------------------------------------------------
    private void paintCurrentGraph(Graphics g) {
        // find the point of maximum current (defines Y-axis scales)
        int indexCurrentMax = findCurrentMax();

        double fMaxAmps = m_state[indexCurrentMax].m_Lcurrent;
        float y1_axis_maximum = Nearest.FractionOfDecade(fMaxAmps);

        int nXmax = IndexToX( indexCurrentMax, NUM_STEPS );
        int nYmax = ValueToY( fMaxAmps, y1_axis_maximum );

        // examine checkbox to see if we should draw the graph of current
        if (checkboxCurrent.getState()) {
            // get coords of the first point
            int nX0 = IndexToX( 0, NUM_STEPS);
            int nY0 = ValueToY( m_state[0].m_Lcurrent, y1_axis_maximum );
            g.setColor(Color.blue);

            // repaint the data for inductor current
            for (int ii=1; ii<NUM_STEPS; ii++) {
                int x = IndexToX(ii, NUM_STEPS);
                int y = ValueToY(m_state[ii].m_Lcurrent, y1_axis_maximum );
                g.drawLine(nX0, nY0, x, y);

                // this point becomes the start of the next line segment
                nX0 = x;
                nY0 = y;
            }

            // label point of maximum current, using most convenient units
            String sMax;
            if (fMaxAmps < 1E-1)
                sMax = nearest.toStringEng( fMaxAmps, 3, "A");
            else if (fMaxAmps < 10.F)
                sMax = nearest.Tenth(fMaxAmps) + " amps";
            else if (fMaxAmps < 10000.F)
                sMax = nearest.Unit(fMaxAmps) + " amps";
            else
                sMax = nearest.toStringEng( fMaxAmps, 3, "A");
            g.drawString(sMax, nXmax, nYmax-1 );

            // label time of first zero-crossing
            int indexZeroCurrent = findCurrentZeroCrossing();
            if (indexZeroCurrent > 0) {
                float timeZeroCurrent = m_elapsed_time * indexZeroCurrent / NUM_STEPS;
                String sZeroCrossing = nearest.toStringEng( timeZeroCurrent, 3, "s");
                int x0 = IndexToX(indexZeroCurrent, NUM_STEPS);
                int y0 = ValueToY(0, y1_axis_maximum);
                g.drawString(sZeroCrossing, x0+16,   y0-26  );
                g.drawLine( x0+4, y0-4,     x0+16-2, y0-26-2);
                
                Font f = getFont();
                FontMetrics fm = getFontMetrics(f);

                // add Time/Frequency tips
                String sTime = "T = " + sZeroCrossing + "\r\n";
                int xTime = IndexToX( 75, 100 );    // 75% from the left edge
                int yTime = ValueToY( 90., 100. );  // 10% from the top
                g.drawString(sTime, xTime, yTime);

                float freq = 0.5F / timeZeroCurrent;
                String sFreq = "f = " + nearest.toStringEng( freq, 3, "Hz");
                g.drawString(sFreq, xTime, yTime+fm.getHeight());
            }
        }
    }

    /**
     * Helper function for scaling and graphing.
     * This is used to auto-scale the vertical axis, and label point of maximum.
     * @return returns array index for the point of maximum inductor current
     */
    private int findCurrentMax() {
        int saveIndex = 0;
        double fMax = 0;
        for (int ii=0; ii<NUM_STEPS; ii++) {
            // find the point of maximum current
            if (m_state[ii].m_Lcurrent > fMax){
                fMax = m_state[ii].m_Lcurrent;
                saveIndex = ii;
            }
        }
        return saveIndex;
    }

    /**
     * Helper function for showing useful information about current graph.
     * @return returns array index for the point of first zero-cross of inductor current
     * if no zero-crossing, it returns zero
     */
    private int findCurrentZeroCrossing() {
        int saveIndex = 0;
        for (int ii=1; ii<NUM_STEPS; ii++) {
            // find the first *negative* current
            if (m_state[ii].m_Lcurrent < 0){
                saveIndex = ii-1;
                break;
            }
        }
        return saveIndex;
    }

    //--------------------------------------------------------------
    // paint graph of capacitor voltage (same as inductor voltage!)
    //--------------------------------------------------------------
    private void paintVoltageGraph(Graphics g) {
        int nXmax;
        int nYmax;
        // Note: the maximum voltage is at time t=0, and then it can only
        // lose energy as time goes by. Voltage cannot peak higher than it
        // started.
        double fMaxVolts = m_state[0].m_Cvoltage;
        float y2_axis_maximum = Nearest.FractionOfDecade(fMaxVolts);
        nXmax = IndexToX( 0, NUM_STEPS);
        nYmax = ValueToY( fMaxVolts, y2_axis_maximum );

        // examine checkbox to see if we should draw the graph of capacitor voltage
        if (checkboxVoltage.getState()) {
            // get screen coords of the first point
            int nX0 = IndexToX( 0, NUM_STEPS);
            int nY0 = ValueToY( m_state[0].m_Cvoltage, y2_axis_maximum );
            g.setColor(Color.red);

            // repaint the data for capacitor voltage
            for (int ii=1; ii<NUM_STEPS; ii++) {
                int x = IndexToX(ii, NUM_STEPS);
                int y = ValueToY(m_state[ii].m_Cvoltage, y2_axis_maximum );
                g.drawLine(nX0, nY0, x, y);

                // this point becomes the start of the next line segment
                nX0 = x;
                nY0 = y;
            }
        }
    }

    //------------------------------------------------------------------
    // IndexToX - convert the array index to the window x offset
    private int IndexToX(int nIndex, int nMaxIndex) {
        Rectangle panel = canvas_graph.getBounds();
        int nOffset =  panel.width * nIndex / nMaxIndex;
        return panel.x + nOffset;
    }

    //------------------------------------------------------------------
    // ValueToY - convert the value to the y offset in the
    // window to plot the point
    // To make it look somewhat like an oscilloscope we plot from
    // +ve fMaxValue to -ve 40% of fMaxValue
    // Therefore, the +ve plot area is 5/7 of the total canvas for graphs.
    private int ValueToY(double fValue, double fMaxValue) {
        Rectangle panel = canvas_graph.getBounds();
        int nHeight = (int)((panel.height *5/7) * fValue / fMaxValue);
        return panel.y + ((panel.height *5/7) - nHeight);
    }

    //------------------------------------------------------------------
    class SymAction implements java.awt.event.ActionListener
    {
        public SymAction() { }
        public void actionPerformed(java.awt.event.ActionEvent event)
        {
            Object object = event.getSource();
            if (object == buttonReset)
                reset();
        }
    }

    //------------------------------------------------------------------
    class SymItem implements java.awt.event.ItemListener
    {
        public SymItem() { }
        public void itemStateChanged(java.awt.event.ItemEvent event)
        {
            Object object = event.getSource();
            if (object == checkboxVoltage)
                checkboxVoltage_ItemStateChanged(event);
            else if (object == checkboxCurrent)
                checkboxCurrent_ItemStateChanged(event);
            else if (object == checkboxDiode)
                checkboxDiode_ItemStateChanged(event);
        }
    }

    /**
     * When they click on the "show voltage" checkbox, just re-run the
     * simulation. The PaintData() routine will examine the current state
     * of the checkbox and draw/erase the graph of the voltage.
     * @param event
     */
    void checkboxVoltage_ItemStateChanged(java.awt.event.ItemEvent event) {
        runSim();
    }

    /**
     * When they click on the "show current" checkbox, just re-run the
     * simulation. The PaintData() routine will examine the current state
     * of the checkbox and draw/erase the graph of the current.
     * @param event
     */
    void checkboxCurrent_ItemStateChanged(java.awt.event.ItemEvent event) {
        runSim();
    }

    void checkboxDiode_ItemStateChanged(java.awt.event.ItemEvent event) {
        // re-running the simulation will include reading this new checkbox state
        runSim();
    }
}
