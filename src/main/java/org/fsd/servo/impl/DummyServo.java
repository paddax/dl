package org.fsd.servo.impl;

import org.fsd.servo.IActuator;
import org.fsd.servo.ILoad;
import org.fsd.servo.IMotionSensor;
import org.fsd.servo.IServoControl;

import java.text.DecimalFormat;

/**
 * Created by Peter Davis on 01/01/2017.
 */
public class DummyServo implements IServoControl, IActuator, IMotionSensor, ILoad {

    private long ts;
    private double force;
    private double position;
    private double mass;
    private double friction = 0;
    private double acceleration;
    private double speed;

    private DecimalFormat df = new DecimalFormat("#.###");

    public DummyServo() {
        this.force = 0;
        this.position = 0;
        this.mass = 1;
        this.speed = 0;
        this.ts = System.currentTimeMillis();
    }

    @Override
    public void setForce(double f) {
        this.force = f;
    }

    @Override
    public double getForce() {
        return force;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public IActuator getActuator() {
        return this;
    }

    @Override
    public IMotionSensor getSensor() {
        return this;
    }

    @Override
    public void update(long ts) {
        // determine this time slice
        double dt = (ts - this.ts) / 1000.0;
        this.ts = ts;

        // set the current acceleration based on torque
        acceleration = force / mass;
        // update speed based on the current acceleration
        double oldspeed = speed;
        speed += acceleration * dt;
        // Linear friction component that is proportional to speed
        speed -= speed * friction * dt;
        // Update the position based on the current speed
        position += (oldspeed + speed) / 2 * dt;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Position:")
                .append(df.format(position))
                .append(" Speed:")
                .append(df.format(speed));
        return sb.toString();
    }

    public void reset(long ts) {
        speed = 0;
        acceleration = 0;
        position = 0;
        this.ts = ts;
    }
}
