package coilsim;

/**
 * Class coilsim.CopperWire -
 * This is a container object, that stores everything
 * we ever need to know about one size of a copper wire:
 * size, weight, resistance, current rating.
 * All stored in metric, of course, and all the work is done in
 * the class factory and the object's properties cannot be modified.
 */

public class CopperWire {
	// description of the wire
	private String m_sName;			    // Size as string format, e.g. "6 AWG"
	private float m_fCircularMils;		// cross-sectional area (mm sq)
	private float m_fDiameter;			// nominal size (mm)
    private static int m_nType = 1;     // 1=awg, 2=swg, 3=metric
    public static final int NumberOfGauges = 3;

    public static int NumberOfSizes;    // How many wire sizes do we know about?
                                        // This tells the caller how times they
                                        // need to iterate our list to fetch them all.

    /**
     * constructor
     * Made "protected" to force you to use the class factory method "initialize"
     *
     * @param sSize string representing the wire size, e.g. "6 AWG" or "1.0mm"
     * @param fCircularMils (circular mils)
     * @param fDiameter (mm)
     */
    protected CopperWire(String sSize, float fCircularMils, float fDiameter) {
        m_sName = sSize;
        m_fCircularMils = fCircularMils;
        m_fDiameter = fDiameter;
    }

    private static CopperWire[] initAWG() {
        NumberOfSizes = 29;
        m_nType = 1;
        CopperWire[] wire = new CopperWire[NumberOfSizes];
        // Data from WireTron Corp., http://www.wiretron.com, June 2003
        //                                  (circ mils)      (mm)
        //                         sSize,   fCircularMils, fDiameter
        wire[ 0] = new CopperWire( "4  AWG", 41740.F,       5.2083F);
        wire[ 1] = new CopperWire( "5  AWG", 33090.F,       4.6393F);
        wire[ 2] = new CopperWire( "6  AWG", 26240.F,       4.1339F);
        wire[ 3] = new CopperWire( "7  AWG", 20820.F,       3.6589F);
        wire[ 4] = new CopperWire( "8  AWG", 16510.F,       3.2830F);
        wire[ 5] = new CopperWire( "9  AWG", 13090.F,       2.9248F);
        wire[ 6] = new CopperWire("10  AWG", 10383.F,       2.6073F);
        wire[ 7] = new CopperWire("11  AWG",  8226.F,       2.3228F);
        wire[ 8] = new CopperWire("12  AWG",  6529.F,       2.1006F);
        wire[ 9] = new CopperWire("13  AWG",  5184.F,       1.8771F);
        wire[10] = new CopperWire("14  AWG",  4109.F,       1.6739F);
        wire[11] = new CopperWire("15  AWG",  3260.F,       1.4910F);
        wire[12] = new CopperWire("16  AWG",  2581.F,       1.3310F);
        wire[13] = new CopperWire("17  AWG",  2052.F,       1.1913F);
        wire[14] = new CopperWire("18  AWG",  1624.F,       1.0617F);
        wire[15] = new CopperWire("19  AWG",  1289.F,       0.9474F);
        wire[16] = new CopperWire("20  AWG",  1024.F,       0.8484F);
        wire[17] = new CopperWire("21  AWG",   812.3F,      0.7569F);
        wire[18] = new CopperWire("22  AWG",   640.1F,      0.6756F);
        wire[19] = new CopperWire("23  AWG",   510.8F,      0.6071F);
        wire[20] = new CopperWire("24  AWG",   404.0F,      0.5410F);
        wire[21] = new CopperWire("25  AWG",   320.4F,      0.4826F);
        wire[22] = new CopperWire("26  AWG",   252.8F,      0.4318F);
        wire[23] = new CopperWire("27  AWG",   201.6F,      0.3886F);
        wire[24] = new CopperWire("28  AWG",   158.8F,      0.3480F);
        wire[25] = new CopperWire("29  AWG",   127.7F,      0.3124F);
        wire[26] = new CopperWire("30  AWG",   100.0F,      0.2769F);
        wire[27] = new CopperWire("31  AWG",    79.2F,      0.2464F);
        wire[28] = new CopperWire("32  AWG",    64.0F,      0.2235F);
        return wire;
    }

    private static CopperWire[] initSWG() {
        NumberOfSizes = 29;
        m_nType = 2;            // 2 = SWG
        CopperWire[] wire = new CopperWire[NumberOfSizes];
        // Data from Standard Transformer Corp, Chicago, 1952
        //                                  (circ mils)    (mm)
        //                         sSize,   fCircularMils, fDiameter
        wire[ 0] = new CopperWire( "4  SWG", 53820.F,       6.5843F);
        wire[ 1] = new CopperWire( "5  SWG", 44940.F,       5.8649F);
        wire[ 2] = new CopperWire( "6  SWG", 36860.F,       5.2241F);
        wire[ 3] = new CopperWire( "7  SWG", 30980.F,       4.6533F);
        wire[ 4] = new CopperWire( "8  SWG", 25600.F,       4.1449F);
        wire[ 5] = new CopperWire( "9  SWG", 20740.F,       3.6921F);
        wire[ 6] = new CopperWire("10  SWG", 16380.F,       3.2887F);
        wire[ 7] = new CopperWire("11  SWG", 13460.F,       2.9294F);
        wire[ 8] = new CopperWire("12  SWG", 10820.F,       2.6093F);
        wire[ 9] = new CopperWire("13  SWG",  8464.F,       2.3242F);
        wire[10] = new CopperWire("14  SWG",  6400.F,       2.0703F);
        wire[11] = new CopperWire("15  SWG",  5184.F,       1.8441F);
        wire[12] = new CopperWire("16  SWG",  4096.F,       1.6426F);
        wire[13] = new CopperWire("17  SWG",  3136.F,       1.4631F);
        wire[14] = new CopperWire("18  SWG",  2304.F,       1.3033F);
        wire[15] = new CopperWire("19  SWG",  1600.F,       1.1609F);
        wire[16] = new CopperWire("20  SWG",  1296.F,       1.0340F);
        wire[17] = new CopperWire("21  SWG",  1024.F,       0.9211F);
        wire[18] = new CopperWire("22  SWG",   784.F,       0.8204F);
        wire[19] = new CopperWire("23  SWG",   576.F,       0.7308F);
        wire[20] = new CopperWire("24  SWG",   484.F,       0.6510F);
        wire[21] = new CopperWire("25  SWG",   400.F,       0.5798F);
        wire[22] = new CopperWire("26  SWG",   324.F,       0.5165F);
        wire[23] = new CopperWire("27  SWG",   269.F,       0.4601F);
        wire[24] = new CopperWire("28  SWG",   219.F,       0.4098F);
        wire[25] = new CopperWire("29  SWG",   185.F,       0.3650F);
        wire[26] = new CopperWire("30  SWG",   153.8F,      0.3251F);
        wire[27] = new CopperWire("31  SWG",   134.6F,      0.2896F);
        wire[28] = new CopperWire("32  SWG",   116.6F,      0.2580F);
        return wire;
    }

    private static CopperWire[] initMetric() {
        NumberOfSizes = 37;
        m_nType = 3;            // 3 = metric
        CopperWire[] wire = new CopperWire[NumberOfSizes];
        //                                  (circ mils)    (mm)
        //                         sSize,   fCircularMils, fDiameter
        wire[ 0] = new CopperWire( "3.35 mm", 17390.F,      3.4350F);
        wire[ 1] = new CopperWire( "3.15 mm", 15380.F,      3.2330F);
        wire[ 2] = new CopperWire( "3.00 mm", 13950.F,      3.0830F);
        wire[ 3] = new CopperWire( "2.80 mm", 12150.F,      2.8800F);
        wire[ 4] = new CopperWire( "2.65 mm", 10880.F,      2.7300F);
        wire[ 5] = new CopperWire( "2.50 mm",  9690.F,      2.5780F);
        wire[ 6] = new CopperWire( "2.36 mm",  8630.F,      2.4380F);
        wire[ 7] = new CopperWire( "2.24 mm",  7780.F,      2.3160F);
        wire[ 8] = new CopperWire( "2.12 mm",  6970.F,      2.1960F);
        wire[ 9] = new CopperWire( "2.00 mm",  6200.F,      2.0740F);
        wire[10] = new CopperWire( "1.90 mm",  5600.F,      1.9740F);
        wire[11] = new CopperWire( "1.80 mm",  5020.F,      1.8720F);
        wire[12] = new CopperWire( "1.70 mm",  4480.F,      1.7720F);
        wire[13] = new CopperWire( "1.60 mm",  3970.F,      1.6700F);
        wire[14] = new CopperWire( "1.50 mm",  3490.F,      1.5700F);
        wire[15] = new CopperWire( "1.40 mm",  3040.F,      1.4680F);
        wire[16] = new CopperWire( "1.32 mm",  2700.F,      1.3880F);
        wire[17] = new CopperWire( "1.25 mm",  2420.F,      1.3160F);
        wire[18] = new CopperWire( "1.20 mm",  2231.49F,    1.2471F);
        wire[19] = new CopperWire( "1.18 mm",  2160.F,      1.2395F);
        wire[20] = new CopperWire( "1.12 mm",  1940.F,      1.1840F);
        wire[21] = new CopperWire( "1.10 mm",  1875.18F,    1.1481F);
        wire[22] = new CopperWire( "1.06 mm",  1740.F,      1.1240F);
        wire[23] = new CopperWire( "1.00 mm",  1550.F,      1.0620F);
        wire[24] = new CopperWire( "0.95 mm",  1400.F,      1.0120F);
        wire[25] = new CopperWire( "0.90 mm",  1260.0F,     0.9591F);
        wire[26] = new CopperWire( "0.80 mm",   992.0F,     0.8550F);
        wire[27] = new CopperWire( "0.70 mm",   759.3F,     0.7620F);
        wire[28] = new CopperWire( "0.60 mm",   558.0F,     0.6490F);
        wire[29] = new CopperWire( "0.50 mm",   388.0F,     0.5440F);
        wire[30] = new CopperWire( "0.40 mm",   248.0F,     0.4390F);
        wire[31] = new CopperWire( "0.31 mm",   139.9F,     0.3277F);
        wire[32] = new CopperWire( "0.22 mm",    77.8F,     0.2520F);
        wire[33] = new CopperWire( "0.16 mm",    39.7F,     0.1820F);
        wire[34] = new CopperWire( "0.11 mm",    19.4F,     0.1300F);
        wire[35] = new CopperWire( "0.08 mm",     9.92F,    0.0940F);
        wire[36] = new CopperWire( "0.05 mm",     3.88F,    0.0600F);
        return wire;
    }

    /**
     * coilsim.CopperWire factory - use this instead of ctor!
     * @return the wire size
     */
    public static CopperWire[] initialize() {
        return initAWG();
    }

    /**
     * coilsim.CopperWire factory method - Rotates through AWG -> SWG -> mm -> AWG
     * @return an object representing wire size
     */
    public static CopperWire[] nextGaugeType() {
        CopperWire[] newWire;
        switch (m_nType) {
            case 1:  newWire = initSWG();    break;
            case 2:  newWire = initMetric(); break;
            case 3:  newWire = initAWG();    break;
            default: newWire = initAWG();    break;
        }
        return newWire;
    }

	/**
	 * @return wire	size in "American Wire Gauge" (AWG) or as a String
	 */
	public String Name()			{ return m_sName; }

	/**
	 * @return maximum recommended steady-state	safe current rating in amps
	 */
	public float SafeCurrent()		{ return 0.00477109F * m_fCircularMils; }

	/**
	 * @return cross-sectional area	of wire	in circular	mils
	 */
	public float CircularMils()		{ return m_fCircularMils; }

	/**
	 * @return nominal diameter of wire in mm
	 */
	public float Diameter()			{ return m_fDiameter; }

    /**
     * @return the number of turns per cm that you can wind a practical coil
     * For now, we assume ideal winding (100% packing factor)
     */
    public float WindingDensity() {
        float CM_PER_MM = 0.1F;
        float fPackingFactor = 1.0F;

        float fDiameterCM = m_fDiameter * CM_PER_MM;
        return fPackingFactor / fDiameterCM;
    }

	/**
     * Find the total resistance (ohms) for a given length of wire at temperature of 20C.
     *
     * Note that resistance is inversely proportional to the cross-sectional area.
	 * By looking at WireTron data, you can prove:  CircMils * UnitResistance = constant
     * and some work with a spreadsheet can deduce: CircMils * UnitResistance = 34.020198
     *
	 * @param meters of wire
	 * @return resistance of the wire (ohms) for a given length
	 */
	public float Resistance(float meters)	{ return 34.020198F / m_fCircularMils * meters; }	// ohms

	/**
	 * Find the total weight (kg) for a given length of wire.
     *
     * Note that weight is directly proportional to the wire's cross-section.
     * More work with WireTron data found: UnitWeight = 4.547E-06 * CircMils
     *
	 * @param meters of wire
	 * @return resistance of the weight in kg for wire of a given length
	 */
	public float Weight(float meters)	{ return 0.000004547F * m_fCircularMils * meters; }	// kg

    // helper function
//    static public String NearestTenth(float f) {
//        int nNearestTenth = (int)(f * 10.F + 0.5F);
//        float fNearestTenth = nNearestTenth;
//        String s = Float.toString(fNearestTenth/10.F);
//        return s;
//    }
}
