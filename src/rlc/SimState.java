package rlc;/*
*/

/**
 * SimState contains the state of dynamic elements in a RLC circuit.
 * This includes:
 * - charge on the capacitor (volts)
 * - current in the inductor (amps)
 * - optional protection diode
 *
 * The circuit diagram looks like this:
 *
 *    +----------+---/\/\/\/-----+
 *    |          |     R         |_
 *  -----      -----              _)
 *  ----- C    _/_\_ D         L  _)
 *    |          |                _)
 *    |          |               |
 *    +----------+---------------+
 * 
 * Copyright Barry Hansen, (c)2007, barry@coilgun.info
 * All rights reserved.
 *
 * @author Barry Hansen
 * @version 2.0
 */

        
 
//----------------------------------------------------------------------
//           SimState
//----------------------------------------------------------------------
public class SimState
{

    //----------------------------------------------------------------------
    //           Slope
    //----------------------------------------------------------------------
    /**
    * helper class contains the slope (derivative) of the state variables
    */
    private class Slope {
        public Slope(double dv, double di) {
            dV_dt = dv;
            dI_dt = di;
        }
        public double dV_dt;
        public double dI_dt;
    }

    /**
     * state variable: voltage on the capacitor
     */
    public double m_Cvoltage;           // volts

    /**
     * state variable: current in the inductor
     */
    public double m_Lcurrent;           // amps
    
    /**
     * cached value of the maximum current (helps the graphing)
     */
    static public double m_fMax = 0;    // amps
    
    // represents the physical components
    private float m_R;                  // ohms
    private float m_L;                  // henries
    private float m_C;                  // farads
    private boolean m_diode;             // true=diode, false=no diode

    /**
     * ctor (must have NO return value! not even void!)
     * 
     * @param old = previous state of the system
     * @param dV = derivative of voltage on capacitor
     * @param dI = derivative of current in inductor
     */
    public SimState(SimState old, double dV, double dI) {
        // apply incremental changes to previous object
        m_Cvoltage = old.m_Cvoltage + dV;
        m_Lcurrent = old.m_Lcurrent + dI;
        
        // copy the old component values, they do not change over time
        m_R = old.m_R;
        m_L = old.m_L;
        m_C = old.m_C;
        m_diode = old.m_diode;
    }
    
    public SimState(double volts, double amps) {
        m_Cvoltage = volts;
        m_Lcurrent = amps;
    }
    
    /**
     * initialize the component values for the RLC circuit
     *
     * @param R = resistance (ohms)
     * @param L = inductance (henries)
     * @param C = capacitance (farads)
     * @param diode = true for protection diode, false for no diode
     */
    public void init_comp(float R, float L, float C, boolean diode) {
        m_R = R;
        m_L = L;
        m_C = C;
        m_diode = diode;
    }

    /**
     * Return an object containing the *next* state of this system.
     * This is the primary usage of the SimState object, and these
     * calculations embody the electrical simulation of the circuit.
     *
     * @param dt = incremental unit of time (seconds)
     * @return an object with the new state of the RLC circuit
     */
    public SimState NextState( double dt ) {
        // implementation of physical model goes here!
        //-------(1) linear model----------------------------
        //double dV = dV_dt() * dt;
        //double dI = dI_dt() * dt;
        
        //-------(2) Runge-Kutta 4th order model-------------
        // m0 is derivative at the current time
        // s0 is system state after one-half the time increment
        Slope m0 = new Slope( this.dV_dt(), this.dI_dt() );
        SimState s0 = new SimState( this, m0.dV_dt * dt / 2, m0.dI_dt * dt / 2);
                                    
        // m1 is our first estimate of derivative after one-half a time increment
        // s1 is our refined system state after one-half a time increment
        Slope m1 = new Slope( s0.dV_dt(), s0.dI_dt() );
        SimState s1 = new SimState( this, m1.dV_dt * dt / 2, m1.dI_dt * dt / 2);
        
        // m2 is our second estimate of derivative after one-half a time increment
        // s2 is system state after one full time increment
        Slope m2 = new Slope( s1.dV_dt(), s1.dI_dt() );
        SimState s2 = new SimState( this, m2.dV_dt * dt, m2.dI_dt * dt );

        // m3 is derivative after one complete time increment
        Slope m3 = new Slope( s2.dV_dt(), s2.dI_dt() );
        
        // m (finally!) is the derivative obtained by combining m0 - m3
        // according to the Runge-Kutta rules
        Slope m4 = new Slope( (m0.dV_dt + 2*m1.dV_dt + 2*m2.dV_dt + m3.dV_dt)/6,
                              (m0.dI_dt + 2*m1.dI_dt + 2*m2.dI_dt + m3.dI_dt)/6);
        
        double dV = m4.dV_dt * dt;
        double dI = m4.dI_dt * dt;
        SimState result = new SimState( this, dV, dI );
        
        // a little bbookkeepping to help the graphing
        if (s1.m_Lcurrent > m_fMax) {
            m_fMax = s1.m_Lcurrent;
        }
        return result;
    }
    
    /**
     * Calculate the instantaneous derivative of capacitor voltage
     * with respect to time.
     * @return derivative of the capacitor voltage
     */
    private double dV_dt() {
        if (m_diode) {
            // note: diode is connected in REVERSE across the capacitor to protect it
            // hence we give the diode the NEGATIVE of Vc
            return ((-1.) * diodeCurrent(this.m_Cvoltage* (-1.)) - this.m_Lcurrent) / m_C;
        }
        else
            return (0 - this.m_Lcurrent) / m_C;
    }
    
    /**
     * Calculate the instaneous derivative of inductor current
     * with respect to time.
     * @return derivative of the inductor current
     */
    private double dI_dt() {
        return (this.m_Cvoltage - this.m_Lcurrent * m_R) / m_L;
    }

    /**
     * Calculate diode current, based on its terminal voltage.
     * Since it's connected across the capacitor, Vc == Vd.
     *
     * We approximate a diode using the function Id = A e^(kV)
     * If we assume Vd=1.2 has 100A, and Vd=1.4 has 1000A:
     * Id = (1E-15) e^(1.6V)
     * @return diode current
     */
    public static double diodeCurrent(double voltage) {
        double Id = 0;
        if (voltage > 0)
            Id = Math.exp( 11.51292547 * voltage ) / 100000.;

        return Id;
    }
} // end class SimState


