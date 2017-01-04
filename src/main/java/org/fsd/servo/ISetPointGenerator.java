package org.fsd.servo;

/**
 * Created by Peter Davis on 02/01/2017.
 */
public interface ISetPointGenerator {

    double getDesiredPosition(long dt);
}
