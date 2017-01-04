package org.fsd.servo;

/**
 * Created by Peter Davis on 02/01/2017.
 */
public interface IPID {

    void setProportional(double p);

    double getProportional();

    void setDerivative(double d);

    double getDerivative();

    void setIntegral(double i);

    double getIntegral();

    double getForce(long dt, double error);
}
