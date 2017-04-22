package coilsim;/*
 * coilsim.InductorSim9.java - Calculates air-core inductor values, given coil dimensions.
 * Provides extravagent graphical user interface for a simple program.
 *
 * Permission is granted to modify this program provided you:
 * a) cite the author, Barry (coilgun@oz.net) in at least source code form, and
 * b) do not charge for distribution of derivative works.
 *
 * Created on June 9, 2002, 6:54 PM
 * Written using NetBeans IDE 3.3.1
 * Copyright Barry Hansen, (c)1999, 2003
 * All rights reserved.
 *
 * @author Barry (coilgun@oz.net)
 * @version 1.9
 * @see http://www.coilgun.info/home.htm
 */

import java.awt.*;
import java.awt.event.ItemEvent;

public class InductorSim9 extends java.applet.Applet {

    public String sVersion = "v1.9";
    static private final Rectangle appletSize = new Rectangle(0,0, 540, 430);

    // state variables: complete description of the user input
    private float m_fOD = MAX_OD_SIZE_SMALL*75/100; // coil outside diameter (mm)
    private float m_fID = MAX_OD_SIZE_SMALL*5/10;   // coil inside diameter (mm)
    private float m_fLength = 35;                   // coil length (mm)

    // array of wire sizes that we know about (AWG 6...28)
    CopperWire[] wire = CopperWire.initialize();

    // helper class for formatting numbers
    private Nearest nearest = new Nearest();

    // conversion factors for painting
    private float fScaleX;              // units are "canvas pixels per mm"
    private float fScaleY;
    private int m_nOffsetX;             // units are "canvas pixels"
    private int m_nOffsetY;

    // constants for coil dimensions
    static private final int MAX_ID_SIZE_SMALL =  90;
    static private final int MAX_OD_SIZE_SMALL = 100;

    static private final int MAX_ID_SIZE_LARGE = 490;
    static private final int MAX_OD_SIZE_LARGE = 500;

    static private final int MAX_COIL_LENGTH_SMALL = 150;   // mm
    static private final int MAX_COIL_LENGTH_LARGE = 600;

    static private final int SLIDER_WIDTH = 10;             // mm

    static private final Point UPLEFT = new Point(90,90);      // degrees, x=start, y=arc angle
    static private final Point UPRIGHT = new Point(0,90);      // x = 0 = 3 o'clock
    static private final Point LOWLEFT = new Point(180,90);    // y = +ve = ccw
    static private final Point LOWRIGHT = new Point(270,90);

    // constants for dimension conversions
    static private final float MM_PER_INCH = 25.4F;
    static private final float INCHES_PER_MM = 1.F/25.4F;
    static private final float FEET_PER_MM = INCHES_PER_MM / 12.F;
    static private final float FEET_PER_METER = FEET_PER_MM * 1000.F;
    static private final float POUNDS_PER_KG = 2.205F;

    /** getParameterInfo is a bean pattern
     */
    public String[][] getParameterInfo() {
        return null;
    }

    /**
     * getAppletInfo is a bean pattern the browser can use to get
     * information about this applet. It can also be used to fill
     * in an About box.
     * @return String - short description of the applet.
     */
    public String getAppletInfo() {
        return "Name: coilsim.InductorSim9 " + sVersion + "\r\n"
             + "Author: Barry (barry@coilgun.info)\r\n"
             + "Calculates air-core inductor values, given coil dimensions.\r\n";
    }

    /**
    * Recalculate and display results for the current coil setting.
    * This assumes all our state variables are already up to date from the UI.
    */
    public void recalculateEverything() {
        // Look at UI to see which type of wire is selected
        int index = listBoxWireSize.getSelectedIndex();
        //System.out.println( "Selected index is " + index + " for wire size " + wire[index].Name() );
        CopperWire w = wire[index];

        // Calculate physical characteristics (number of turns, length)
        int nTurnsPerLayer = (int)(m_fLength / w.Diameter());
        int nNumberOfLayers = (int)((m_fOD - m_fID) / 2.F / w.Diameter());
        int nTotalTurns = nTurnsPerLayer * nNumberOfLayers;
        float fWireLength = (float)(Math.PI * (m_fOD + m_fID)/2.F * nTotalTurns / 1000.F); // meters

        // Calculate electrical characteristics (mH, ohms)
        // Use standard approximation for multilayer coil
        //            0.8(N * A)^2
        //    L = --------------------- (microhenries)
        //         (6A) + (9B) + (10C)
        // where:
        //    N = number of turns
        //    A = avg coil radius = tube radius + half of coil thickness (inches)
        //    B = coil length (inches)
        //    C = coil thickness (inches)
        // (Don't use Wheeler's Formula for Inductance because it's for a spiral wound coil)
        // @see http://www.icorp.net/users/kev/tesla/form.txt
        //    L(uH) = (r^2) * (N^2) / (9*r + 10*h)
        float A = (m_fOD + m_fID) / 2.F / 2.F * INCHES_PER_MM;	// inches
        float B = m_fLength * INCHES_PER_MM;                    // inches
        float C = (m_fOD - m_fID) / 2.F * INCHES_PER_MM;        // inches
        float N = nTotalTurns;                                  // turns
        float fInductance = 0.8F * N * N * A * A
                          / (6.F*A + 9.F*B +10.F*C) / 1000.F;   // mH

        float fResistance = w.Resistance(fWireLength);		// ohms
        float fWireWeight = w.Weight(fWireLength);		// kg

        // Update output section of UI with these computed values
        labelWindingDensityValue.setText( nearest.Tenth(w.WindingDensity()) + " turns/cm");
        labelTurnsPerLayerValue.setText( Integer.toString(nTurnsPerLayer) + " turns" );
        if (nNumberOfLayers == 1)
            labelNumberOfLayersValue.setText( Integer.toString(nNumberOfLayers) + " layer" );
        else
            labelNumberOfLayersValue.setText( Integer.toString(nNumberOfLayers) + " layers" );
        labelNumberOfTurnsValue.setText( Integer.toString(nTotalTurns) + " turns" );
        labelWireLengthValue.setText( nearest.Hundredth(fWireLength) + " m" );
        labelInductanceValue.setText( nearest.Thousandth(fInductance) + " mH" );
        labelResistanceValue.setText( nearest.Thousandth(fResistance) + " ohms" );
        labelWeightValue.setText( nearest.Hundredth(fWireWeight) + " kg" );
        labelWireDiameterValue.setText( nearest.Hundredth(w.Diameter()) + " mm" );

        // tell the coil picture to repaint itself
        repaint();
    }

    /**
     * Update data labels for each control, according to slider position
     */
    void setLabelOD() {
        // read slider value in mm
        m_fOD = VScrollBarOD.getValue();

        // ensure OD > ID by reducing ID if needed
        if (m_fOD <= m_fID) {
            VScrollBarID.setValue( (int)(m_fOD - 1) );
            setLabelID();
        }

        String s;
        // create metric OD string "99 mm" or english string "1.23 in"
        s = Integer.toString( Math.round(m_fOD) ) + " mm";
        labelODvalue.setText(s);
    }

    void setLabelID() {
        // read slider value in mm
        m_fID = VScrollBarID.getValue();

        // ensure OD > ID by increasing OD if needed
        if (m_fID >= m_fOD) {
            VScrollBarOD.setValue( (int)(m_fID + 1) );
            setLabelOD();
        }

        String s;
        // create metric ID string "99 mm" or english string "1.23 in"
        s = Integer.toString( Math.round(m_fID) ) + " mm";
        labelIDvalue.setText(s);
    }

    void setLabelLength() {
        // read slider value in mm
        m_fLength = HScrollBarLength.getValue();

        String s;
        // create metric string "99 mm" or english string "1.23 in"
        s = Integer.toString( Math.round(m_fLength) ) + " mm";
        labelLengthValue.setText(s);
    }

    /**
     * Draw a stylized coil, with the current dimensions
     *
     * @param g handle to graphics
     */
    public void paint(Graphics g) {
        paintBackground(g);
        paintCoil(g);
    }

    /**
     * Draw any graphical elements that are not alread pre-canned on the Form.
     * Right now, that consists merely of a separator line above the results.
     *
     * @param g handle to graphics
     */
    private void paintBackground(Graphics g) {
        // prepare to measure height of strings on the window
        Font font = getFont();
        FontMetrics fm = getFontMetrics(font);

        // Debug: draw a red rectangle around the output results area
        drawCanvasBorder(g,canvas2);

        // draw border around perimeter
        int nRight = appletSize.width;
        int nBottom = appletSize.height;

        int nRadius = 16;
        final Rectangle arc = new Rectangle(0,0,2*nRadius,2*nRadius);

        Rectangle outputArea = canvas2.getBounds();

        //      _______________
        // 1  /                \ 2
        //   |                  |
        //   |                  |
        //   |      ___________/ 3
        //   |    / 4          \ 9
        //   |    |             |
        // 6 \__5/\_7__________/ 8
        drawRoundCorner(g, UPLEFT,   0,0);                    // 1. upper left corner
        drawRoundCorner(g, UPRIGHT,  nRight-2*nRadius,0);     // 2. upper right corner
        drawRoundCorner(g, LOWRIGHT, nRight-2*nRadius,outputArea.y-2*nRadius); // 3. middle right corner
        drawRoundCorner(g, UPLEFT,   outputArea.x, outputArea.y);  // 4. middle left corner
        drawRoundCorner(g, LOWRIGHT, outputArea.x-2*nRadius,nBottom-2*nRadius); // 5. bottom middle corner
        drawRoundCorner(g, LOWLEFT,  0,nBottom-2*nRadius);    // 6. lower left corner
        drawRoundCorner(g, LOWLEFT,  outputArea.x, nBottom-2*nRadius); // 7. another lower left corner
        drawRoundCorner(g, LOWRIGHT, nRight-2*nRadius, nBottom-2*nRadius); // 8. bottomostest rightest corner
        drawRoundCorner(g, UPRIGHT,  nRight-2*nRadius, outputArea.y);  // 9. another corner

        g.drawLine(nRadius,0,      nRight-nRadius,0);        // top border
        g.drawLine(nRight,nRadius, nRight,outputArea.y-nRadius);   // right border
        g.drawLine(outputArea.x+nRadius,outputArea.y, nRight-nRadius, outputArea.y); // middle border
        g.drawLine(nRadius,nBottom,outputArea.x-nRadius,nBottom);  // bottom border
        g.drawLine(0,nRadius,      0,nBottom-nRadius);       // left border
        g.drawLine(outputArea.x+nRadius, outputArea.y, nRight-nRadius, outputArea.y);
        g.drawLine(outputArea.x,outputArea.y+nRadius, outputArea.x,nBottom-nRadius);
        g.drawLine(outputArea.x+nRadius, nBottom, nRight-nRadius, nBottom);
        g.drawLine(nRight,nBottom-nRadius, nRight, outputArea.y+nRadius);
    }

    private void drawRoundCorner(Graphics g, Point arc_angle, int x, int y) {
        final int nRadius = 16;
        //        origin of rectangle containing arc
        //        |    size of rectangle containing arc
        //        |    |                    arc angles
        //        |    |                    |
        g.drawArc(x,y, 2*nRadius,2*nRadius, arc_angle.x,arc_angle.y);
    }

    /**
     * Debug: highlight a canvas by drawing a red border
     * @param g graphics context
     * @param c canvas
     */
    private void drawCanvasBorder(Graphics g, java.awt.Component c) {
/*
        Color savedColor = g.getColor();
        g.setColor(Color.red);

        Rectangle rCanvas = c.getBounds();
        g.drawRect(rCanvas.x,rCanvas.y,rCanvas.width,rCanvas.height);

        g.setColor(savedColor);
*/
    }

    /**
     * Draw an outline of a coil, scaled and placed to fill the canvas.
     * The visible left end is an ellipse bounded by (x0,y0) and (x2,y2).
     * The right end is half an ellipse bounded by (x3,y0) and (x5,y2)
     * but only the right half of the ellipse is drawn.
     * All dimensions and sliders are in millimeters.
     *
     *     +-------+---------------------------+ y0 = 0
     *     : /   \ :                     /   \ :
     *     :/     \:                    /     \:
     *     (   o   )                   (   o   ) y1 (mm)
     *     :\     /:                    \     /:
     *     : \   / :                     \   / :
     *     +-------+---------------------------+ y2
     *     x0  x1  x2        (mm)      x3  x4 x5
     *     =0
     *
     * @param g ref to Graphics object
     */
    private void paintCoil(Graphics g) {
        // compute a coil, using u.l. corner as (0,0) and mm units
        float x0 = 0.f;
        float x1 = m_fOD/2.f;
        float x2 = m_fOD;

        float x3 = x0 + m_fLength;
        float x4 = x1 + m_fLength;
        float x5 = x2 + m_fLength;

        float y0 = 0.f;
        float y1 = m_fOD/2.f;
        float y2 = m_fOD;

        // Move the drawing origin to the u.l. corner of the canvas area.
        // We're not drawing on the canvas, the canvas itself is invisible and
        // we just use its coordinates to tell us where to draw on the applet.
        Rectangle rCanvas = canvas1.getBounds();

        // Debug: draw a red rectangle around the canvas
        drawCanvasBorder(g, canvas1);

        // make (0,0) to upper left corner of canvas
        g.translate(rCanvas.x, rCanvas.y);

        // Translate the entire coil drawing downward from the u.l., but just
        // far enough so the coil's centerline is in the middle of the canvas.
        m_nOffsetY = (int)((rCanvas.height - m_fOD*fScaleY) / 2.F);

        // Translate coil drawing rightward such that the center of the
        // left ellipse stays in one place.
        float fMaxOD = VScrollBarOD.getMaximum()         // max OD slider value (integer mm)
                     - VScrollBarOD.getVisibleAmount();  // width of slider itself
        m_nOffsetX = (int)((fMaxOD - m_fOD)/2.F * fScaleX);

        // DEBUG: draw green square at offset
        // g.setColor(Color.green);
        // g.drawRect(m_nOffsetX-5,m_nOffsetY-5, 10,10);
        // g.setColor(Color.black);

        // draw the coil outline (O.D.)
        drawArc (g, x0,y0, x2-x0,y2-y0, 0,360);
        drawArc (g, x3,y0, x2-x0,y2-y0, -90,180);
        drawLine(g, x1,y0, x4,y0);
        drawLine(g, x1,y2, x4,y2);

        // add small arcs to indicate the first few wires
        // compute actual pitch from wire size
        int wireIndex = listBoxWireSize.getSelectedIndex();
        float wireDiameter = wire[wireIndex].Diameter();

        // how many arcs to draw? it should be limited to the number that fit inside the coil's image
        int nTurnsPerLayer = (int)(m_fLength / wireDiameter);
        int nArcLimit = 4;
        if (nTurnsPerLayer < nArcLimit)
            nArcLimit = nTurnsPerLayer;

        double start_angle = 270.F;     // 270 degrees = six o'clock
        double draw_angle = 75.F;       // +ve degrees = draw ccw
        Rectangle arcBound = new Rectangle((int)x3,(int)y0, (int)(x5-x3),(int)(y2-y0));
        g.setColor(Color.gray);
        for (int loop=1; loop<=nArcLimit; loop++) {
            arcBound.x -= wireDiameter;
            drawArc (g, arcBound.x,arcBound.y, arcBound.width,arcBound.height, start_angle,draw_angle);
            start_angle += 0.F;
            draw_angle -= 8.F;
        }
        g.setColor(Color.black);

        // can we see a portion of the coil's back end through hole in the middle?
        if (m_fLength < m_fID) {

            // yes we can see a little bit of the back opening through the hole!
            // find the u.l. corner of a bounding box around the back ellipse for the
            // inside diameter which is centered around (x4, y1)
            float bx1 = x4 - m_fID/2.f;
            float by1 = y1 - m_fID/2.f;

            // find the half-angle of arc that is visible:
            //
            //               ( sqrt(2*2*b*b*k*k - len*len) )
            // angle = arctan( --------------------------- )
            //               (        k * len              )

            // I want to write this:
            // float arc_angle = Math.atan( Math.sqrt(m_fOD*m_fOD - len*len)/(len) );
            // but stupid old Java requires tons of float/double conversions!

            double arctan_arg = (float)Math.sqrt(m_fID*m_fID - m_fLength*m_fLength)/( m_fLength);
            double arc_angle = Math.toDegrees( Math.atan( arctan_arg ) );

            start_angle = 180.F - arc_angle;
            draw_angle = 2.F*arc_angle;
            drawArc (g, bx1,by1, m_fID,m_fID, start_angle, draw_angle);
            g.setColor(Color.black);
        }

        // draw the coil bore (I.D.)
        float fOriginX = (m_fOD/2.F - m_fID/2.F);
        float fOriginY = (m_fOD/2.F - m_fID/2.F);
        drawArc (g, fOriginX,fOriginY, m_fID,m_fID, 0,360);

        // tick marks as visual aid
        g.setColor(Color.blue);
        drawLine(g, x1,y0-3, x1,y0+0);
        drawLine(g, x1,y2-0, x1,y2+3);
        drawLine(g, x4,y0-3, x4,y0+0);
        drawLine(g, x4,y2-0, x4,y2+3);

        // draw the center mark  "+" at left end of coil
        drawLine(g, x1-3,y1, x1+3, y1);
        drawLine(g, x1,y1-3, x1,y1+3);
    }

    /**
     * drawArc - same as g.drawArc() but input is a 'float' dimension
     * in mm units and it moves and resizes everything to fit the canvas.
     * This means scaling and translating:  y=mx+b
     * The factors and offsets are set in our init() routine.
     *
     * Draws the outline of a circular or elliptical arc covering the specified rectangle.
     * The center of the arc is the center of the rectangle whose origin is (x, y) and
     * whose size is specified by the fWidth and fHeight arguments.
     *
     * @param g - ref to graphics object
     * @param x - the x coordinate of the upper-left corner of the arc to be drawn
     * @param y - the y coordinate of the upper-left corner of the arc to be drawn
     * @param fWidth - the fWidth of a box bounding the ellipse
     * @param fHeight - the fHeight of a box bounding the ellipse
     * @param startAngle - degrees, where 0 is the 3 o'clock position
     * @param arcAngle - degrees
     */
    private void drawArc( Graphics g,
                          float x, float y,
                          float fWidth, float fHeight,
                          double startAngle, double arcAngle) {
        int nWidth  = (int)(fWidth * fScaleX);
        int nHeight = (int)(fHeight * fScaleY);
        g.drawArc( valueToX(x), valueToY(y), nWidth, nHeight, (int)startAngle, (int)arcAngle );
    }

    /**
     * drawLine - same as g.drawLine() but input is a 'float' dimension
     * in mm units and it moves and resizes everything to fit the canvas.
     * This means scaling and translating:  y=mx+b
     * The factors and offsets are set in our init() routine.
     *
     * @param g ref to Graphics object
     * @param x1 - first x endpoint, mm
     * @param y1 - first y endpoint, mm
     * @param x2 - second x endpoint, mm
     * @param y2 - second y endpoint, mm
     */
    private void drawLine( Graphics g,
                           float x1, float y1,
                           float x2, float y2) {
        g.drawLine( valueToX(x1), valueToY(y1), valueToX(x2), valueToY(y2));
    }

    /**
     * valueToX - converts dimension (mm) to canvas coordinate (pixel)
     *
     * @param fValueX horizontal distance in mm from u.l.
     * @return canvas coordinate.
     */
    private int valueToX(float fValueX) {
        return (int)(fValueX * fScaleX) + m_nOffsetX;
    }

    /**
     * valueToY - converts dimension (mm) to canvas coordinate (pixel)
     *
     * @param fValueY vertical distance downward in mm from u.l.
     * @return canvas coordinate.
     */
    private int valueToY(float fValueY) {
        return (int)(fValueY * fScaleY) + m_nOffsetY;
    }

    /** Initializes the applet */
    public void init() {
        initComponents();
        canvas1.setVisible(false);
        canvas2.setVisible(false);

        initWireListbox();

        // Initialize some middle-of-the-range values for a starting coil
        listBoxWireSize.select(CopperWire.NumberOfSizes / 2);
        VScrollBarOD.setValue( MAX_OD_SIZE_SMALL*75/100 );
        VScrollBarID.setValue( MAX_OD_SIZE_SMALL*5/10 );
        HScrollBarLength.setValues( 35,     // initial position (pixels)
                                    10,     // visible size (pixels)
                                    1,      // minimum value (mm)
                                    150+SLIDER_WIDTH);// maximum value = (maximum mm) + (visible pixels)

        // Update slider readout text to match the slider settings
        setLabelOD();
        setLabelID();
        setLabelLength();

        // compute conversion rates from mm dimensions onto canvas coordinates
        initScaleFactors();

        // Make first calculations of starting coil
        recalculateEverything();
    }

    private void initScaleFactors() {
        // Compute scale factors that will convert coil dimensions (mm)
        // into canvas coordinates (i.e. pixels)
        float fMaxOD = VScrollBarOD.getMaximum()        // max OD slider value
                     - VScrollBarOD.getVisibleAmount(); // width of slider itself

        float fMaxLen = HScrollBarLength.getMaximum()           // max Length slider value
                      - HScrollBarLength.getVisibleAmount();    // width of slider itself

        float fMaxX = fMaxOD/2.F       // left half ellipse
                    + fMaxLen          // body of coil
                    + fMaxOD/2.F;      // right half ellipse
        float fMaxY = fMaxOD;

        Rectangle rCanvas = canvas1.getBounds();
        fScaleX = rCanvas.width/fMaxX;
        fScaleY = rCanvas.height/fMaxY;
    }

    /**
     * Take care of GUI items that change when you swap AWG - SWG - Metric
     */
    private void initWireListbox() {
        // Populate the Wire Size (AWG,SWG,metric) listbox
        listBoxWireSize.removeAll();
        for (int ii=0; ii<CopperWire.NumberOfSizes; ii++) {
            listBoxWireSize.add( wire[ii].Name());
        }
        // Select the middle entry in the list
        listBoxWireSize.select(CopperWire.NumberOfSizes/2);
    }

    /** This method is called from within the init() method to
     * initialize the form.
     */
    private void initComponents() {
        listBoxWireSize = new java.awt.List();
        labelID = new java.awt.Label();
        labelIDvalue = new java.awt.Label();
        VScrollBarID = new java.awt.Scrollbar();
        labelTitle = new java.awt.Label();
        VScrollBarOD = new java.awt.Scrollbar();
        labelOD = new java.awt.Label();
        labelODvalue = new java.awt.Label();
        HScrollBarLength = new java.awt.Scrollbar();
        labelLength = new java.awt.Label();
        labelLengthValue = new java.awt.Label();
        labelTurnsPerLayer = new java.awt.Label();
        labelNumberOfTurns = new java.awt.Label();
        labelWireLength = new java.awt.Label();
        labelNumberOfTurnsValue = new java.awt.Label();
        labelTurnsPerLayerValue = new java.awt.Label();
        labelNumberOfLayersValue = new java.awt.Label();
        labelWireLengthValue = new java.awt.Label();
        labelInductance = new java.awt.Label();
        labelResistance = new java.awt.Label();
        labelWeight = new java.awt.Label();
        labelInductanceValue = new java.awt.Label();
        labelResistanceValue = new java.awt.Label();
        labelWeightValue = new java.awt.Label();
        labelWireDiameter = new java.awt.Label();
        labelWireDiameterValue = new java.awt.Label();
        labelNumberOfLayers = new java.awt.Label();
        canvas1 = new java.awt.Canvas();
        canvas2 = new java.awt.Canvas();

        setLayout(null);

        Color beige = new java.awt.Color(255,255,221);      // set background color
        setBackground(beige);

        // application title bar
        labelTitle.setAlignment(java.awt.Label.CENTER);
        labelTitle.setFont(new java.awt.Font("SansSerif", 1, 18));
        labelTitle.setText("Air Core Inductor");
        add(labelTitle);
        Rectangle titleArea = new Rectangle(200,10, 164,20);
        labelTitle.setBounds(titleArea);

        // put the version number to the right of application title
        labelVersion = new java.awt.Label(sVersion);
        add(labelVersion);
        labelVersion.setBounds(titleArea.x+titleArea.width+4, titleArea.y+3, 26, 20);

        listBoxWireSize.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listBoxWireSizeItemStateChanged(evt);
            }
        });

        // canvas1 contains the coil diagram
        canvas1.setEnabled(false);  //
        add(canvas1);
        canvas1.setBounds(MAX_OD_SIZE_SMALL+46, 60, 324, 190);  // to the right of ID slider bar

        // canvas2 contains the output results area
        canvas2.setEnabled(false);
        add(canvas2);
        Point out = new Point(120,308); // TODO: should be 120,300
        canvas2.setBounds(out.x,out.y, appletSize.width-out.x,appletSize.height-out.y);

        add(listBoxWireSize);
        listBoxWireSize.setBounds(16, 58, 90, 260);

        // Inside Diameter controls
        labelID.setAlignment(java.awt.Label.CENTER);
        labelID.setText("Inner Diam.");
        add(labelID);
        labelID.setBounds(90, 10, 90, 16);

        labelIDvalue.setAlignment(java.awt.Label.CENTER);
        labelIDvalue.setText("99 mm");
        add(labelIDvalue);
        labelIDvalue.setBounds(100, 30, 60, 16);

        VScrollBarID.setMaximum(MAX_ID_SIZE_SMALL);
        VScrollBarID.setMinimum(1);
        VScrollBarID.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                VScrollBarIDAdjustmentValueChanged(evt);
            }
        });

        add(VScrollBarID);
        VScrollBarID.setBounds(120, 60, 16, 180);

        // Outside Diameter controls
        labelOD.setAlignment(java.awt.Label.CENTER);
        labelOD.setText("Outer Diam.");
        add(labelOD);
        labelOD.setBounds(445, 10, 90, 16);

        labelODvalue.setAlignment(java.awt.Label.CENTER);
        labelODvalue.setText("99 mm");
        add(labelODvalue);
        labelODvalue.setBounds(470, 30, 60, 16);

        VScrollBarOD.setMaximum(MAX_OD_SIZE_SMALL + SLIDER_WIDTH);
        VScrollBarOD.setMinimum(2);
        VScrollBarOD.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                VScrollBarODAdjustmentValueChanged(evt);
            }
        });

        add(VScrollBarOD);
        VScrollBarOD.setBounds(484, 50, 16, 200);

        // Coil Length controls
        labelLength.setAlignment(java.awt.Label.CENTER);
        labelLength.setText("Length =");
        add(labelLength);
        labelLength.setBounds(230, 280, 60, 16);

        labelLengthValue.setAlignment(java.awt.Label.CENTER);
        labelLengthValue.setText("100 mm");
        add(labelLengthValue);
        labelLengthValue.setBounds(300, 280, 60, 16);

        HScrollBarLength.setMinimum(1);
        HScrollBarLength.setOrientation(java.awt.Scrollbar.HORIZONTAL);
        HScrollBarLength.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                HScrollBarLengthAdjustmentValueChanged(evt);
            }
        });

        add(HScrollBarLength);
        HScrollBarLength.setBounds(120, 260, 380, 16);

        // output area (results) controls
        // ----- Column 1: labels
        int nColumn1 = out.x + 16;
        labelNumberOfTurns.setText("Number of turns =");
        add(labelNumberOfTurns);
        labelNumberOfTurns.setBounds(nColumn1, 320, 116, 16);

        labelWindingDensity = new java.awt.Label();
        labelWindingDensity.setName("labelWindingDensity");
        labelWindingDensity.setText("Winding density =");
        add(labelWindingDensity);
        labelWindingDensity.setBounds(nColumn1, 340, 101, 20);

        labelTurnsPerLayer.setText("Turns per layer =");
        add(labelTurnsPerLayer);
        labelTurnsPerLayer.setBounds(nColumn1, 360, 116, 16);

        labelNumberOfLayers.setText("Number of layers =");
        add(labelNumberOfLayers);
        labelNumberOfLayers.setBounds(nColumn1, 380, 116, 16);

        labelWireDiameter.setName("labelWireDiameter");
        labelWireDiameter.setText("Wire diameter =");
        add(labelWireDiameter);
        labelWireDiameter.setBounds(nColumn1, 400, 116, 16);

        // ----- Column 2: values
        int nColumn2 = out.x + 130;
        labelNumberOfTurnsValue.setFont(new java.awt.Font("Dialog", 1, 12));
        labelNumberOfTurnsValue.setText("9999 turns");
        add(labelNumberOfTurnsValue);
        labelNumberOfTurnsValue.setBounds(nColumn2, 320, 90, 16);

        labelWindingDensityValue = new java.awt.Label();
        labelWindingDensityValue.setName("labelWindingDensityValue");
        labelWindingDensityValue.setText("99 turns/cm");
        add(labelWindingDensityValue);
        labelWindingDensityValue.setBounds(nColumn2, 340, 90, 20);

        labelTurnsPerLayerValue.setText("999 turns");
        add(labelTurnsPerLayerValue);
        labelTurnsPerLayerValue.setBounds(nColumn2, 360, 90, 16);

        labelNumberOfLayersValue.setText("99 layers");
        add(labelNumberOfLayersValue);
        labelNumberOfLayersValue.setBounds(nColumn2, 380, 90, 16);

        labelWireDiameterValue.setText("1.23 mm");
        add(labelWireDiameterValue);
        labelWireDiameterValue.setBounds(nColumn2, 400, 90, 16);

        // ----- Column 3: more labels
        int nColumn3 = out.x + 220;
        labelInductance.setText("Inductance =");
        add(labelInductance);
        labelInductance.setBounds(nColumn3, 320, 90, 16);

        labelResistance.setText("Resistance =");
        add(labelResistance);
        labelResistance.setBounds(340, 340, 90, 16);

        labelWireLength.setText("Wire length =");
        add(labelWireLength);
        labelWireLength.setBounds(340, 360, 90, 16);

        labelWireLengthValue.setText("99.9 m");
        add(labelWireLengthValue);
        labelWireLengthValue.setBounds(440, 360, 90, 16);

        labelWeight.setText("Wire weight =");
        add(labelWeight);
        labelWeight.setBounds(340, 380, 90, 16);

        // ----- Column 4: more values
        int nColumn4 = out.x + 320;
        labelInductanceValue.setFont(new java.awt.Font("Dialog", 1, 12));
        labelInductanceValue.setText("999 mH");
        add(labelInductanceValue);
        labelInductanceValue.setBounds(nColumn4, 320, 90, 16);

        labelResistanceValue.setText("9.99 ohms");
        add(labelResistanceValue);
        labelResistanceValue.setBounds(nColumn4, 340, 90, 16);

        labelWeightValue.setText("9.99 kg");
        add(labelWeightValue);
        labelWeightValue.setBounds(nColumn4, 380, 90, 16);

        buttonNextWire = new java.awt.Button();
        buttonNextWire.setLabel("Next Wire");
        buttonNextWire.setName("buttonWireSize");
        buttonNextWire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextWireActionPerformed(evt);
            }
        });

        add(buttonNextWire);
        buttonNextWire.setBounds(20, 20, 70, 24);

        // ----- Radio Button Group -----
        labelMaximumSize1 = new java.awt.Label("Maximum");
        labelMaximumSize2 = new java.awt.Label("diameter:");
        add(labelMaximumSize1);
        add(labelMaximumSize2);

        sizeButtonGroup = new java.awt.CheckboxGroup();
        jrbSizeSmall = new Checkbox("100 mm", sizeButtonGroup, true);         // select top radio button
        add(jrbSizeSmall);
        jrbSizeLarge = new Checkbox("500 mm", sizeButtonGroup, false);
        add(jrbSizeLarge);

        labelMaximumSize1.setBounds(16, 340, 90, 16);
        labelMaximumSize2.setBounds(16, 360, 90, 16);
        jrbSizeSmall.setBounds(20, 380, 90, 16);
        jrbSizeLarge.setBounds(20, 400, 90, 16);
        jrbSizeSmall.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                actionSizeSmall(evt);
            }
        });

        jrbSizeLarge.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                actionSizeLarge(evt);
            }
        });

    } // end initComponents()

    private void actionSizeSmall(java.awt.event.ItemEvent evt) {
        VScrollBarID.setMaximum(MAX_ID_SIZE_SMALL);
        VScrollBarOD.setMaximum(MAX_OD_SIZE_SMALL + SLIDER_WIDTH);
        HScrollBarLength.setMaximum(MAX_COIL_LENGTH_SMALL + SLIDER_WIDTH);

        if (m_fOD > MAX_OD_SIZE_SMALL)
            m_fOD = MAX_OD_SIZE_SMALL;
        if (m_fID > MAX_ID_SIZE_SMALL)
            m_fID = MAX_ID_SIZE_SMALL;
        if (m_fLength > MAX_COIL_LENGTH_SMALL)
            m_fLength = MAX_COIL_LENGTH_SMALL;
        initScaleFactors();
        recalculateEverything();
    }

    private void actionSizeLarge(java.awt.event.ItemEvent evt) {
        VScrollBarID.setMaximum(MAX_ID_SIZE_LARGE);
        VScrollBarOD.setMaximum(MAX_OD_SIZE_LARGE + SLIDER_WIDTH);
        HScrollBarLength.setMaximum(MAX_COIL_LENGTH_LARGE + SLIDER_WIDTH);
        initScaleFactors();
        recalculateEverything();
    }

    private void buttonNextWireActionPerformed(java.awt.event.ActionEvent evt) {
        // Cycle through AWG - SWG - metric wire sizes
        wire = CopperWire.nextGaugeType();
        initWireListbox();
        recalculateEverything();
    }

    private void listBoxWireSizeItemStateChanged(java.awt.event.ItemEvent evt) {
        recalculateEverything();
    }

    private void HScrollBarLengthAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
        setLabelLength();
        recalculateEverything();
    }

    private void VScrollBarODAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
        setLabelOD();
        recalculateEverything();
    }

    private void VScrollBarIDAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
        setLabelID();
        recalculateEverything();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label labelNumberOfLayers;
    private java.awt.Label labelVersion;
    private java.awt.Canvas canvas1;
    private java.awt.Canvas canvas2;
    private java.awt.Label labelIDvalue;
    private java.awt.Label labelID;
    private java.awt.Label labelWeight;
    private java.awt.Scrollbar VScrollBarOD;
    private java.awt.Label labelInductance;
    private java.awt.Label labelWireDiameter;
    private java.awt.Label labelNumberOfTurns;
    private java.awt.Label labelOD;
    private java.awt.Label labelLengthValue;
    private java.awt.Label labelWireDiameterValue;
    private java.awt.Label labelNumberOfTurnsValue;
    private java.awt.Scrollbar HScrollBarLength;
    private java.awt.Label labelODvalue;
    private java.awt.Label labelWindingDensityValue;
    private java.awt.List listBoxWireSize;
    private java.awt.Label labelWeightValue;
    private java.awt.Label labelLength;
    private java.awt.Label labelTitle;
    private java.awt.Label labelWireLengthValue;
    private java.awt.Scrollbar VScrollBarID;
    private java.awt.Label labelResistance;
    private java.awt.Label labelInductanceValue;
    private java.awt.Label labelTurnsPerLayerValue;
    private java.awt.Button buttonNextWire;
    private java.awt.Label labelWindingDensity;
    private java.awt.Label labelResistanceValue;
    private java.awt.Label labelWireLength;
    private java.awt.Label labelTurnsPerLayer;
    private java.awt.Label labelNumberOfLayersValue;
    private java.awt.Label labelMaximumSize1;
    private java.awt.Label labelMaximumSize2;
    private java.awt.CheckboxGroup sizeButtonGroup;
    private java.awt.Checkbox jrbSizeSmall;
    private java.awt.Checkbox jrbSizeLarge;

}
