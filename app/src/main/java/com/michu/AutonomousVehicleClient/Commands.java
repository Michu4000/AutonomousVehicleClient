package com.michu.AutonomousVehicleClient;

public class Commands {

    public static int goForward(int stepsTodo) { // go forward for x steps
        String[] response;
        int stepsDone = 0;
        int sendStatus = Connectivity.send("F" + stepsTodo + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive( String.valueOf(stepsTodo).length() + 2); // receive number of steps done
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'F') { // and if it's move forward confirmation
                        String stps = response[1].substring(1, response[1].length()-1); // get number of steps done from received confirmation
                        for(int i=0; i<stps.length(); i++) { // and parse them to int (they can be preceded by 0's)
                            if(stps.charAt(i) != '0') {
                                stepsDone = Integer.parseInt(stps.substring(i));
                                break;
                            }
                        }
                        return stepsDone;
                    }
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int goBackward(int stepsTodo) { // go backward for x steps
        String[] response;
        int sendStatus = Connectivity.send("B" + stepsTodo + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(2); // receive confirmation
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'B') // and if it's moved backward confirmation
                        return 0;
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int rotateLeft(int stepsTodo) { // rotate left for x steps
        String[] response;
        int stepsDone = 0;
        int sendStatus = Connectivity.send("L" + stepsTodo + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive( String.valueOf(stepsTodo).length() + 2); // receive number of steps done
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'L') { // and if it's rotate left confirmation
                        String stps = response[1].substring(1, response[1].length()-1); // get number of steps done from received confirmation
                        for(int i=0; i<stps.length(); i++) { // and parse them to int (they can be preceded by 0's)
                            if(stps.charAt(i) != '0') {
                                stepsDone = Integer.parseInt(stps.substring(i));
                                break;
                            }
                        }
                        return stepsDone;
                    }
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int rotateRight(int stepsTodo) { // rotate right for x steps
        String[] response;
        int stepsDone = 0;
        int sendStatus = Connectivity.send("R" + stepsTodo + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive( String.valueOf(stepsTodo).length() + 2); // receive number of steps done
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'R') { // and if it's rotate right confirmation
                        String stps = response[1].substring(1, response[1].length()-1); // get number of steps done from received confirmation
                        for(int i=0; i<stps.length(); i++) { // and parse them to int (they can be preceded by 0's)
                            if(stps.charAt(i) != '0') {
                                stepsDone = Integer.parseInt(stps.substring(i));
                                break;
                            }
                        }
                        return stepsDone;
                    }
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int moveServo(int angle) { // move servo to x angle
        String[] response;
        int sendStatus = Connectivity.send("S" + angle + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(2); // receive confirmation
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'S') // and if it's servo moved confirmation
                        return 0;
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static double checkDistance(int measurementsNumber) { // single angle distance scan with IR sensor
        String[] response;
        int rawAnswer; // raw output number
        double volts; // output data calculated to volts
        int sendStatus = Connectivity.send("D" + measurementsNumber + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(6); // receive distance data
            if(response != null) {
                if(response[0].equals("0")) { // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'D') { // and if it's distance response
                        // parse received distance value to int
                        if(response[1].charAt(1) != '0')
                            rawAnswer = Integer.parseInt(response[1].substring(1, 5));
                        else if(response[1].charAt(2) != '0')
                            rawAnswer = Integer.parseInt(response[1].substring(2, 5));
                        else if(response[1].charAt(3) != '0')
                            rawAnswer = Integer.parseInt(response[1].substring(3, 5));
                        else if(response[1].charAt(4) != '0')
                            rawAnswer = Integer.parseInt(response[1].substring(4, 5));
                        else rawAnswer = 0;

                        volts = rawAnswer * 0.00322265625d; // value from sensor * (3.3/1024) [V]
                        return volts;
                    }
                    else
                        return -6.0d;
                }
                else
                    return (double) Integer.parseInt(response[0]);
            }
            else
                return -5.0d;
        }
        else
            return (double) sendStatus;
    }

    public static double[] scan(int skipAngle, int measurementsNumber) { // perform full range distance scan with IR sensor
        double[] sc = new double[181]; // array for results (for each angle)

        for(int i=0; i<=180; i+=skipAngle) {
            if(Commands.moveServo(i) != 0) { // move servo error
                sc[i] = -1.0d;
                continue;
            }

            sc[i] = Commands.checkDistance(measurementsNumber); // check distance

            if(sc[i] < 0.0d) { // check distance error
                sc[i] = -2.0d;
                continue;
            }

            sc[i] = Calculator.calculateDistance(sc[i]); // volts -> cm
        }

        Commands.moveServo(90); // move servo back to front
        return sc;
    }

    public static double[] checkAcceleration(int measurementsNumber) { // check acceleration
        String[] response;
        double Ax, Ay = Double.POSITIVE_INFINITY, Az = Double.POSITIVE_INFINITY;
        int sendStatus = Connectivity.send("A" + measurementsNumber + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(14); // receive acceleration data
            if(response != null) {
                if(response[0].equals("0")) { // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'A') { // and if it's acceleration response
                        // parse received values to doubles
                        //Ax
                        if(response[1].charAt(2) != '0')
                            Ax = (double) Integer.parseInt(response[1].substring(2, 5)) / 100.0d;
                        else if(response[1].charAt(3) != '0')
                            Ax = (double) Integer.parseInt(response[1].substring(3, 5)) / 100.0d;
                        else if(response[1].charAt(4) != '0')
                            Ax = (double) Integer.parseInt(response[1].substring(4, 5)) / 100.0d;
                        else Ax = 0.0d;
                        if(response[1].charAt(1) == '-')
                            Ax *= -1.0d;
                        //Ay
                        if(response[1].charAt(6) != '0')
                            Ay = (double) Integer.parseInt(response[1].substring(6, 9)) / 100.0d;
                        else if(response[1].charAt(7) != '0')
                            Ay = (double) Integer.parseInt(response[1].substring(7, 9)) / 100.0d;
                        else if(response[1].charAt(8) != '0')
                            Ay = (double) Integer.parseInt(response[1].substring(8, 9)) / 100.0d;
                        else Ay = 0.0d;
                        if(response[1].charAt(5) == '-')
                            Ay *= -1.0d;
                        //Az
                        if(response[1].charAt(10) != '0')
                            Az = (double) Integer.parseInt(response[1].substring(10, 13)) / 100.0d;
                        else if(response[1].charAt(11) != '0')
                            Az = (double) Integer.parseInt(response[1].substring(11, 13)) / 100.0d;
                        else if(response[1].charAt(12) != '0')
                            Az = (double) Integer.parseInt(response[1].substring(12, 13)) / 100.0d;
                        else Az = 0.0d;
                        if(response[1].charAt(9) == '-')
                            Az *= -1.0d;
                    }
                    else
                        Ax = -6.0d;
                }
                else
                    Ax = (double) Integer.parseInt(response[0]);
            }
            else
                Ax = -5.0d;
        }
        else
            Ax = (double) sendStatus;

        return new double[]{Ax, Ay, Az};
    }

    public static double[] checkGyroscope(int measurementsNumber) { // check angular velocity
        String[] response;
        double Gx, Gy = Double.POSITIVE_INFINITY, Gz = Double.POSITIVE_INFINITY;
        int sendStatus = Connectivity.send("G" + measurementsNumber + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(20); // receive angular velocity data
            if(response != null) {
                if(response[0].equals("0")) { // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'G') { // and if it's angular velocity response
                        // parse received values to doubles
                        //Gx
                        if(response[1].charAt(2) != '0')
                            Gx = (double) Integer.parseInt(response[1].substring(2, 7)) / 100.0d;
                        else if(response[1].charAt(3) != '0')
                            Gx = (double) Integer.parseInt(response[1].substring(3, 7)) / 100.0d;
                        else if(response[1].charAt(4) != '0')
                            Gx = (double) Integer.parseInt(response[1].substring(4, 7)) / 100.0d;
                        else if(response[1].charAt(5) != '0')
                            Gx = (double) Integer.parseInt(response[1].substring(5, 7)) / 100.0d;
                        else if(response[1].charAt(6) != '0')
                            Gx = (double) Integer.parseInt(response[1].substring(6, 7)) / 100.0d;
                        else Gx = 0.0d;
                        if(response[1].charAt(1) == '-')
                            Gx *= -1.0d;
                        //Gy
                        if(response[1].charAt(8) != '0')
                            Gy = (double) Integer.parseInt(response[1].substring(8, 13)) / 100.0d;
                        else if(response[1].charAt(9) != '0')
                            Gy = (double) Integer.parseInt(response[1].substring(9, 13)) / 100.0d;
                        else if(response[1].charAt(10) != '0')
                            Gy = (double) Integer.parseInt(response[1].substring(10, 13)) / 100.0d;
                        else if(response[1].charAt(11) != '0')
                            Gy = (double) Integer.parseInt(response[1].substring(11, 13)) / 100.0d;
                        else if(response[1].charAt(12) != '0')
                            Gy = (double) Integer.parseInt(response[1].substring(12, 13)) / 100.0d;
                        else Gy = 0.0d;
                        if(response[1].charAt(7) == '-')
                            Gy *= -1.0d;
                        //Gz
                        if(response[1].charAt(14) != '0')
                            Gz = (double) Integer.parseInt(response[1].substring(14, 19)) / 100.0d;
                        else if(response[1].charAt(15) != '0')
                            Gz = (double) Integer.parseInt(response[1].substring(15, 19)) / 100.0d;
                        else if(response[1].charAt(16) != '0')
                            Gz = (double) Integer.parseInt(response[1].substring(16, 19)) / 100.0d;
                        else if(response[1].charAt(17) != '0')
                            Gz = (double) Integer.parseInt(response[1].substring(17, 19)) / 100.0d;
                        else if(response[1].charAt(18) != '0')
                            Gz = (double) Integer.parseInt(response[1].substring(18, 19)) / 100.0d;
                        else Gz = 0.0d;
                        if(response[1].charAt(13) == '-')
                            Gz *= -1.0d;
                    }
                    else
                        Gx = -6.0d;
                }
                else
                    Gx = (double) Integer.parseInt(response[0]);
            }
            else
                Gx = -5.0d;
        }
        else
            Gx = (double) sendStatus;

        return new double[]{Gx, Gy, Gz};
    }

    public static double[] checkMagnetometer(int measurementsNumber) { // check magnetic field
        String[] response;
        double Mx, My = Double.POSITIVE_INFINITY, Mz = Double.POSITIVE_INFINITY;
        int sendStatus = Connectivity.send("M" + measurementsNumber + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(20); // receive magnetic field data
            if(response != null) {
                if(response[0].equals("0")) { // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'M') { // and if it's magnetic field response
                        // parse received values to doubles
                        //Mx
                        if(response[1].charAt(2) != '0')
                            Mx = (double) Integer.parseInt(response[1].substring(2, 7)) / 100.0d;
                        else if(response[1].charAt(3) != '0')
                            Mx = (double) Integer.parseInt(response[1].substring(3, 7)) / 100.0d;
                        else if(response[1].charAt(4) != '0')
                            Mx = (double) Integer.parseInt(response[1].substring(4, 7)) / 100.0d;
                        else if(response[1].charAt(5) != '0')
                            Mx = (double) Integer.parseInt(response[1].substring(5, 7)) / 100.0d;
                        else if(response[1].charAt(6) != '0')
                            Mx = (double) Integer.parseInt(response[1].substring(6, 7)) / 100.0d;
                        else Mx = 0.0d;
                        if(response[1].charAt(1) == '-')
                            Mx *= -1.0d;
                        //My
                        if(response[1].charAt(8) != '0')
                            My = (double) Integer.parseInt(response[1].substring(8, 13)) / 100.0d;
                        else if(response[1].charAt(9) != '0')
                            My = (double) Integer.parseInt(response[1].substring(9, 13)) / 100.0d;
                        else if(response[1].charAt(10) != '0')
                            My = (double) Integer.parseInt(response[1].substring(10, 13)) / 100.0d;
                        else if(response[1].charAt(11) != '0')
                            My = (double) Integer.parseInt(response[1].substring(11, 13)) / 100.0d;
                        else if(response[1].charAt(12) != '0')
                            My = (double) Integer.parseInt(response[1].substring(12, 13)) / 100.0d;
                        else My = 0.0d;
                        if(response[1].charAt(7) == '-')
                            My *= -1.0d;
                        //Mz
                        if(response[1].charAt(14) != '0')
                            Mz = (double) Integer.parseInt(response[1].substring(14, 19)) / 100.0d;
                        else if(response[1].charAt(15) != '0')
                            Mz = (double) Integer.parseInt(response[1].substring(15, 19)) / 100.0d;
                        else if(response[1].charAt(16) != '0')
                            Mz = (double) Integer.parseInt(response[1].substring(16, 19)) / 100.0d;
                        else if(response[1].charAt(17) != '0')
                            Mz = (double) Integer.parseInt(response[1].substring(17, 19)) / 100.0d;
                        else if(response[1].charAt(18) != '0')
                            Mz = (double) Integer.parseInt(response[1].substring(18, 19)) / 100.0d;
                        else Mz = 0.0d;
                        if(response[1].charAt(13) == '-')
                            Mz *= -1.0d;
                    }
                    else
                        Mx = -6.0d;
                }
                else
                   Mx = (double) Integer.parseInt(response[0]);
            }
            else
                Mx = -5.0d;
        }
        else
            Mx = (double) sendStatus;

        return new double[]{Mx, My, Mz};
    }

    public static int changeStepResolution(String stepResolution) { // change stepper motors step resolution
        String[] response;
        String resolution;

        switch(stepResolution) {
            case "1":
                resolution = "000";
                break;
            case "1/2":
                resolution = "100";
                break;
            case "1/4":
                resolution = "010";
                break;
            case "1/8":
                resolution = "110";
                break;
            case "1/16":
                resolution = "001";
                break;
            case "1/32":
                resolution = "101";
                break;
            default:
                resolution = "000";
                break;
        }

        int sendStatus = Connectivity.send("E" + resolution + "\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(2); // receive confirmation
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'E') // and if it's change step resolution confirmation
                        return 0;
                    else
                        return -6;
                }
                else
                   return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int checkEngines() { // check engines drivers nFAULT pin
        String[] response;
        int sendStatus = Connectivity.send("N\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(3); // receive nFAULT state
            if(response != null) {
                if(response[0].equals("0")) { // // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'N') { // and if it's check engines response
                        if(response[1].charAt(1) == '1')
                            return 1;
                        else if(response[1].charAt(1) == '0')
                            return 0;
                        else
                            return -6;
                    }
                    else
                        return -6;
                }
                else
                   return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int ping() { // measure ping
        String[] response;
        long startTime = System.currentTimeMillis(); // get current time
        int sendStatus = Connectivity.send("H\n"); // send command

        if(sendStatus == 0) { // if send status == 0 -> send OK
            response = Connectivity.receive(2); // receive heartbeat
            if(response != null) {
                if(response[0].equals("0")) { // if receive status == 0 -> receive OK
                    if(response[1].charAt(0) == 'H') // and answer is valid
                        return (int) (System.currentTimeMillis() - startTime);
                    else
                        return -6;
                }
                else
                    return Integer.parseInt(response[0]);
            }
            else
                return -5;
        }
        else
            return sendStatus;
    }

    public static int restartArduino() { // restart Arduino program (registers aren't wipe)
        int sendStatus = Connectivity.send("X\n"); // send command
        return sendStatus;
    }

    public static int espFactorySettings() { // ESP8266 factory settings
        int sendStatus = Connectivity.send("Y\n"); // send command
        return sendStatus;
    }
}