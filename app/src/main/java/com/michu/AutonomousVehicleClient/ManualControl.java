package com.michu.AutonomousVehicleClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Math.pow;

public class ManualControl extends Activity {

    private int goSeekBarValue = 1, rotateSeekBarValue = 1, servoSeekBarValue = 90, measurementsSeekBarValue = 1;
    private String stepResolutionSeekBarValue = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_tab);

        SeekBar goSeekBar, rotateSeekBar, servoSeekBar, measurementsSeekBar, stepResolutionSeekBar;
        final TextView goSeekBarText, rotateSeekBarText, servoSeekBarText, measurementsSeekBarText, stepResolutionSeekBarText;

        // map some of components from xml
        goSeekBar = findViewById(R.id.g_seekbar);
        goSeekBarText = findViewById(R.id.g_seekbar_text);
        rotateSeekBar = findViewById(R.id.rotate_seekbar);
        rotateSeekBarText = findViewById(R.id.rotate_seekbar_text);
        servoSeekBar = findViewById(R.id.servo_seekbar);
        servoSeekBarText = findViewById(R.id.servo_seekbar_text);
        measurementsSeekBar = findViewById(R.id.measurements_seekbar);
        measurementsSeekBarText = findViewById(R.id.measurements_seekbar_text);
        stepResolutionSeekBar = findViewById(R.id.step_resolution_seekbar);
        stepResolutionSeekBarText = findViewById(R.id.step_resolution_seekbar_text);

        // register seekbars listeners
        goSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                goSeekBarValue = i+1;
                goSeekBarText.setText("" + (i+1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rotateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rotateSeekBarValue = i+1;
                rotateSeekBarText.setText("" + (i+1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        servoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                servoSeekBarValue = i;
                servoSeekBarText.setText("" + i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        measurementsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                measurementsSeekBarValue = i+1;
                measurementsSeekBarText.setText("" + (i+1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        stepResolutionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i == 0) {
                    stepResolutionSeekBarValue = "" + 1;
                    stepResolutionSeekBarText.setText("" + 1);
                }
                else {
                    stepResolutionSeekBarValue = "" + 1 + "/" + (int) pow(2, i);
                    stepResolutionSeekBarText.setText("" + 1 + "/" + (int) pow(2, i));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void goForward(View view) { // go forward for x steps
        int stepsDone = Commands.goForward(goSeekBarValue);

        if(stepsDone < 0) {
            Connectivity.showErrors(stepsDone, this);
            return;
        }

        if(stepsDone == goSeekBarValue)
            Toast.makeText(this, "Moved forward for " + stepsDone + " step(s)", Toast.LENGTH_SHORT).show();
        else { // microswitch hit!
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Collision detected!")
                    .setMessage("Done " + stepsDone + "/" + goSeekBarValue + " step(s) before hit")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void goBackward(View view) { // go backward for x steps
        int status = Commands.goBackward(goSeekBarValue);

        if(status == 0)
            Toast.makeText(this, "Moved backward for " + goSeekBarValue + " step(s)", Toast.LENGTH_SHORT).show();
        else
            Connectivity.showErrors(status, this);
    }

    public void rotateLeft(View view) { // rotate left for x steps
        int stepsDone = Commands.rotateLeft(rotateSeekBarValue);

        if(stepsDone < 0) {
            Connectivity.showErrors(stepsDone, this);
            return;
        }

        if(stepsDone == rotateSeekBarValue)
            Toast.makeText(this, "Rotated left for " + stepsDone + " step(s)", Toast.LENGTH_SHORT).show();
        else { // microswitch hit!
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Collision detected!")
                    .setMessage("Done " + stepsDone + "/" + rotateSeekBarValue + " step(s) before hit")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void rotateRight(View view) { // rotate right for x steps
        int stepsDone = Commands.rotateRight(rotateSeekBarValue);

        if(stepsDone < 0) {
            Connectivity.showErrors(stepsDone, this);
            return;
        }

        if(stepsDone == rotateSeekBarValue)
            Toast.makeText(this, "Rotated right for " + stepsDone + " step(s)", Toast.LENGTH_SHORT).show();
        else { // microswitch hit!
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Collision detected!")
                    .setMessage("Done " + stepsDone + "/" + rotateSeekBarValue + " step(s) before hit")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void moveServo(View view) { // move servo to x angle
        int status = Commands.moveServo(servoSeekBarValue);

        if(status == 0)
            Toast.makeText(this, "Moved servo on " + servoSeekBarValue + '\u00B0', Toast.LENGTH_SHORT).show();
        else
            Connectivity.showErrors(status, this);
    }

    public void checkDistance(View view) { // single angle distance scan
        double distance; // output data calculated to cm
        double volts = Commands.checkDistance(measurementsSeekBarValue); // output data calculated to volts

        if(volts >= 0.0d) {
            distance = Calculator.calculateDistance(volts); // volts -> cm
            volts = Math.round(volts * 1000.0d) / 1000.0d; // round up to 3 decimal places
            distance = Math.round(distance * 100.0d) / 100.0d; // round up to 2 decimal places

            new AlertDialog.Builder(this) // popup window
                    .setTitle("Distance measure")
                    .setMessage("Obstacle at a distance of " + distance + " cm (" + volts + " V)\nbased on " + measurementsSeekBarValue + " measurement(s)")
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors((int) volts, this);
    }

    public void checkAcceleration(View view) { // check acceleration
        double accel[] = Commands.checkAcceleration(measurementsSeekBarValue);

        if(accel[1] != Double.POSITIVE_INFINITY && accel[2] != Double.POSITIVE_INFINITY) {
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Acceleration measure")
                    .setMessage("X = " + accel[0] + " G\nY = " + accel[1] + " G\nZ = " + accel[2] + " G\nbased on " + measurementsSeekBarValue + " measurement(s)")
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors((int) accel[0], this);
    }

    public void checkGyroscope(View view) { // check angular velocity
        double gyro[] = Commands.checkGyroscope(measurementsSeekBarValue);

        if(gyro[1] != Double.POSITIVE_INFINITY && gyro[2] != Double.POSITIVE_INFINITY) {
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Angular velocity measure")
                    .setMessage("X = " + gyro[0] + " " + '\u00B0' + "\\s\nY = " + gyro[1] + " " + '\u00B0' + "\\s\nZ = " + gyro[2] + " " + '\u00B0' + "\\s\nbased on " + measurementsSeekBarValue + " measurement(s)")
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors((int) gyro[0], this);
    }

    public void checkMagnetometer(View view) { // check magnetic field
        double mag[] = Commands.checkMagnetometer(measurementsSeekBarValue);

        if(mag[1] != Double.POSITIVE_INFINITY && mag[2] != Double.POSITIVE_INFINITY) {
            double degreeDirection = Calculator.calculateDirection(mag[1], mag[2]);
            degreeDirection = Math.round(degreeDirection * 100.0d) / 100.0d; // round up to 2 decimal places

            new AlertDialog.Builder(this) // popup window
                    .setTitle("Magnetic field measure")
                    .setMessage("X = " + mag[0] + " " + '\u00B5' + "T\nY = " + mag[1] + " " + '\u00B5' + "T\nZ = " + mag[2] + " " + '\u00B5'+ "T\nheading direction = " + degreeDirection + '\u00B0' +"\nbased on " + measurementsSeekBarValue + " measurement(s)")
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors((int) mag[0], this);
    }

    public void changeStepResolution(View view) { // change stepper motors step resolution
        int status = Commands.changeStepResolution(stepResolutionSeekBarValue);

        if(status == 0)
            Toast.makeText(this, "Changed motors step resolution to " + stepResolutionSeekBarValue + " step", Toast.LENGTH_SHORT).show();
        else
            Connectivity.showErrors(status, this);
    }

    public void checkEngines(View view) { // check engines drivers nFAULT pin
        String msg = "";
        int enginesStatus = Commands.checkEngines();

        if(enginesStatus >= 0) {
            if (enginesStatus == 1)
                msg = "Engines are working";

            if (enginesStatus == 0)
                msg = "Engines failure!";

            new AlertDialog.Builder(this) // popup window
                    .setTitle("Engines check")
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors(enginesStatus, this);
    }

    public void ping(View view) { // measure ping
        int ping = Commands.ping();

        if(ping >= 0) {
            new AlertDialog.Builder(this) // popup window
                    .setTitle("Ping")
                    .setMessage("Ping to vehicle: " + ping + " ms")
                    .setPositiveButton("OK", null)
                    .show();
        }
        else
            Connectivity.showErrors(ping, this);
    }

    public void restartArduino(View view) { // restart Arduino program (registers aren't wipe)
        new AlertDialog.Builder(this)
                .setTitle("Restart Arduino")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int status = Commands.restartArduino();

                                if(status == 0) {
                                    Toast.makeText(getBaseContext(), "Restarting Arduino...", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getBaseContext(), "Please wait few seconds", Toast.LENGTH_LONG).show();
                                }
                                else
                                    Connectivity.showErrors(status, getBaseContext());
                            }
                        })
                .setNegativeButton("No", null).show();
    }

    public void espFactorySettings(View view) { // ESP8266 factory settings
        new AlertDialog.Builder(this)
                .setTitle("Esp8266 factory settings")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int status = Commands.espFactorySettings();

                                if(status == 0) {
                                    Toast.makeText(getBaseContext(), "Restoring Esp8266 factory settings...", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getBaseContext(), "Now you must manually restart Arduino and there can be temporary connection problems", Toast.LENGTH_LONG).show();
                                }
                                else
                                    Connectivity.showErrors(status, getBaseContext());
                            }
                        })
                .setNegativeButton("No", null).show();
    }
}