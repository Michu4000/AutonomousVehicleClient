package com.michu.AutonomousVehicleClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class Scan extends Activity {

    private Bitmap bmp;
    private ImageView img;
    private TextView dir;
    private Button svButton;
    private int saSeekBarValue = 1, mnSeekBarValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_tab);

        SeekBar saSeekBar, mnSeekBar;
        final TextView saSeekBarText, mnSeekBarText;

        // map some of components from xml
        img = findViewById(R.id.img);
        dir = findViewById(R.id.dir_txt);
        svButton = findViewById(R.id.save);

        saSeekBar = findViewById(R.id.sa_seekbar);
        saSeekBarText = findViewById(R.id.sa_seekbar_text);
        mnSeekBar = findViewById(R.id.mn_seekbar);
        mnSeekBarText = findViewById(R.id.mn_seekbar_text);

        // register seekbars listeners
        saSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i) {
                    case 0:
                        saSeekBarValue = 1;
                        saSeekBarText.setText("" + 1);
                        break;
                    case 1:
                        saSeekBarValue = 2;
                        saSeekBarText.setText("" + 2);
                        break;
                    case 2:
                        saSeekBarValue = 3;
                        saSeekBarText.setText("" + 3);
                        break;
                    case 3:
                        saSeekBarValue = 4;
                        saSeekBarText.setText("" + 4);
                        break;
                    case 4:
                        saSeekBarValue = 5;
                        saSeekBarText.setText("" + 5);
                        break;
                    case 5:
                        saSeekBarValue = 10;
                        saSeekBarText.setText("" + 10);
                        break;
                    case 6:
                        saSeekBarValue = 15;
                        saSeekBarText.setText("" + 15);
                        break;
                    case 7:
                        saSeekBarValue = 20;
                        saSeekBarText.setText("" + 20);
                        break;
                    case 8:
                        saSeekBarValue = 30;
                        saSeekBarText.setText("" + 30);
                        break;
                    case 9:
                        saSeekBarValue = 45;
                        saSeekBarText.setText("" + 45);
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mnSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mnSeekBarValue = i+1;
                mnSeekBarText.setText("" + (i+1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        bmp = getBlankBitmap(); // get blank bitmap (grid only)
        img.setImageBitmap(bmp); // apply bitmap to image view
    }

    public Bitmap getBlankBitmap() { // make blank bitmap (grid only)
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grid); // load grid from file
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true); // make bitmap mutable

        return bmp;
    }

    public Bitmap getFilledBitmap(double[] points) { // make bitmap with points
        Bitmap bmp = getBlankBitmap();

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.RED); // set drawing color

        // get starting point coordinates
        float startPointX = canvas.getWidth()/2.0f;
        float startPointY = canvas.getHeight();

        for(int i=0; i<=180; i++) {
            if(points[i] < 4.0d || points[i] > 30.0d) // skip measurements that are out of range
                continue;

            // scale point value
            points[i] *= canvas.getHeight() / 30.0d;

            // convert from polar coordinates system to Cartesian
            float PointX = (float)(points[i] * Math.cos(Math.toRadians(i)));
            float PointY = (float)(points[i] * Math.sin(Math.toRadians(i)));

            // draw circle in this point
            canvas.drawCircle(startPointX - PointX, startPointY - PointY, 30, paint);
        }

        return bmp;
    }

    public void start(View view) {
        bmp = getFilledBitmap(Commands.scan(saSeekBarValue, mnSeekBarValue)); // scan & draw points into bitmap
        img.setImageBitmap(bmp); // apply bitmap to image view

        // get direction from magnetometer
        double degreeDirection = Double.POSITIVE_INFINITY;
        double mag[] = Commands.checkMagnetometer(mnSeekBarValue);
        if(mag[1] != Double.POSITIVE_INFINITY && mag[2] != Double.POSITIVE_INFINITY) {
            degreeDirection = Calculator.calculateDirection(mag[1], mag[2]);
            degreeDirection = Math.round(degreeDirection * 100.0d) / 100.0d; // round up to 2 decimal places
        }
        if(degreeDirection != Double.POSITIVE_INFINITY)
            dir.setText("Direction: " + degreeDirection + '\u00B0'); // print direction from magnetometer
        else
            dir.setText("Direction: ?");

        svButton.setEnabled(true); // enable save button
        Toast.makeText(this, "Scan completed", Toast.LENGTH_SHORT).show(); // show notification
    }

    public void save(View view) { // save scan results to file
        String path = Environment.getExternalStorageDirectory().toString() + "/AutonomousVehicleClient/"; // save directory path

        Calendar c = Calendar.getInstance();
        String date = "" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH)
                + "_" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        File directory = new File(path); // directory
        File file = new File(path, "Scan" + date + ".png"); // file to save
        OutputStream fOut = null;

        try {
            if(!directory.exists()) // create directory if doesn;t exist
                directory.mkdirs();
            file.createNewFile(); // create file
            fOut = new FileOutputStream(file);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut); // save bitmap to PNG file with 100% quality

        try {
            fOut.flush();
            fOut.close();
            Toast.makeText(this, "Saved to file: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show(); // show notification
        } catch(IOException e) {
            Toast.makeText(this, "Failed to save data to file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}