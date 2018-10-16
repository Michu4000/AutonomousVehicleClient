package com.michu.AutonomousVehicleClient;

import static java.lang.Math.pow;

public class Calculator {

    public static double calculateDistance(double volts) { // calculate distance from volts for IR sensor
        return 13.0d * pow(volts, -1.0d);
    }

    public static double calculateDirection(double x, double y) { // calculate direction using data from magnetometer
        double direction, degreeDirection; // computed direction

        direction = Math.atan2(x, y); // compute magnetic direction using arcustangens2 function
        if(direction < 0.0d)
            direction += 2.0d * Math.PI;
        degreeDirection = direction * 180.0d / Math.PI; // radians to degrees conversion

        //corrections: Waveshare is heading right side of vehicle so -90* and -5.87* for magnetic declination
        if(degreeDirection >= 95.87d)
            degreeDirection -= 95.87d;
        else
            degreeDirection += 264.13d; // 360-(95.87-degreeDirection)

        return degreeDirection;
    }
}