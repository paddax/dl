package org.fsd.servo.impl;

import org.fsd.servo.IPID;

/**
 * Created by Peter Davis on 02/01/2017.
 */
public class PID implements IPID {
    private double kP;
    private double kI;
    private double kD;
    private double force;
    private double error;
    private long dt;
    private double integral;

    @Override
    public void setProportional(double p) {
        kP = p;
    }

    @Override
    public double getProportional() {
        return kP;
    }

    @Override
    public void setDerivative(double d) {
        kD = d;
    }

    @Override
    public double getDerivative() {
        return kD;
    }

    @Override
    public void setIntegral(double i) {
        kI = i;
    }

    @Override
    public double getIntegral() {
        return kI;
    }

    @Override
    public double getForce(long dt, double e) {
        double time = (dt - this.dt) / 1000.0;
        this.dt = dt;
        double proportional = kP * e;
        integral += (e * kI * time);
        double derivative = kD * (e - error) / time;
        error = e;
        force = proportional + integral + derivative;
        return force;
    }
}
