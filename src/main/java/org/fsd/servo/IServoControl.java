package org.fsd.servo;

/**
 * Created by Peter Davis on 01/01/2017.
 */
public interface IServoControl {

    IActuator getActuator();

    IMotionSensor getSensor();

    void update(long ts);

}
