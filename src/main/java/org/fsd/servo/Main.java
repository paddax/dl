package org.fsd.servo;

import org.fsd.servo.impl.DummyServo;
import org.fsd.servo.impl.PID;

/**
 * Created by Peter Davis on 01/01/2017.
 */
public class Main {

    public static void main(String[] args) {
        DummyServo ds = new DummyServo();
        PID pid = new PID();
        pid.setProportional(100);
        pid.setIntegral(0);
        pid.setDerivative(10);

        Display d = new Display(ds, pid);

//        ds.reset(0);
//        ds.setForce(1);
//        ds.update(1000);
//        System.out.println(ds.toString());
//
//        ds.reset(0);
//        ds.setForce(1);
//
//        for(int i=1; i<=1000; i++) {
//            ds.update(i* 100);
//            System.out.println(ds.toString());
//        }
    }
}
