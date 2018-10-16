package com.michu.AutonomousVehicleClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutionException;

public class Connectivity {

    private static String			host = "192.168.1.1"; // destination address
    private static int 				port = 1307; // destination port
    private static Socket			clientSocket; // TCP socket
    private static DataInputStream 	in; // input stream for socket
    private static DataOutputStream	out; // output stream for socket

    static int connect() { // open connection using AsyncTask
        ConnectAsync task = new ConnectAsync();
        try {
            return task.execute().get(); // start task, wait for completion and return result
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -3;
        }
    }

    static void changeIP(String ip, int prt) { // change ip & port (if sth wrong reverse default values)
        if(ip != null) {
            if(ip.length() >= 7) // shortest possible address (ex. 1.1.1.1)
                host = ip;
            else
                host = "192.168.1.1";
        }
        else
            host = "192.168.1.1";

        if(prt > 0)
            port = prt;
        else
            port = 1307;
    }

    static int send(String msg) { // send data using AsyncTask
        String[] amsg = { msg };
        SendAsync task = new SendAsync();
        try {
            return task.execute(amsg).get(); // start task, wait for completion and return result
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -3;
        }
    }

    static int checkDataAvailable() { // check if is data available to read using AsyncTask
        CheckAsync task = new CheckAsync();
        try {
            return task.execute().get(); // start task, wait for completion and return result
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -3;
        }
    }

    static String[] receive(int len) { // receive data using AsyncTask
        String[] result = new String[2]; // first string in array is receive status, 2nd is message
        Integer[] alen = { len };
        ReceiveAsync task = new ReceiveAsync();
        try {
            result = task.execute(alen).get(); // start task, wait for completion and get result
        } catch (InterruptedException e) {
            e.printStackTrace();
            result[0] = "-2";
        } catch (ExecutionException e) {
            e.printStackTrace();
            result[0] = "-3";
        }
        return result;
    }

    static int disconnect() { // disconnect using AsyncTask
        DisconnectAsync task = new DisconnectAsync();
        try {
            return task.execute().get(); // start task, wait for completion and return result
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -3;
        }
    }

    private static class ConnectAsync extends AsyncTask<Void, Void, Integer> { // AsyncTask for connect
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                clientSocket = new Socket(host, port); // new TCP socket
                out = new DataOutputStream(clientSocket.getOutputStream()); // get output stream object
                out.flush();
                in = new DataInputStream(clientSocket.getInputStream()); // get input stream object
                return 0;
            } catch (UnknownHostException unknownHost) { // host not found
                System.err.println("Exception: UnknownHost");
                return -4;
            } catch (IOException e) { // IO error
                e.printStackTrace();
                return -1;
            }
        }
    }

    private static class SendAsync extends AsyncTask<String, Void, Integer> { // AsyncTask for send data
        @Override
        protected Integer doInBackground(String... params) {
            try {
                // send data
                byte[] data = params[0].getBytes();
                out.write(data);
                out.flush();
                return 0;
            }
            catch(IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    private static class CheckAsync extends AsyncTask<Void, Void, Integer> { // AsyncTask for checking data availability
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                if(in.available() > 0) // if there is something in input stream buffer
                    return 1;
                else
                    return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    private static class ReceiveAsync extends AsyncTask<Integer, Void, String[]> { // AsyncTask for receiving data
        @Override
        protected String[] doInBackground(Integer... params) {
            String[] result = new String[2];
            try {
                //receive data
                byte[] data = new byte[params[0]];
                in.read(data);
                result[1] = new String(data);
                result[0] = "0";

            }
            catch(IOException e) {
                e.printStackTrace();
                result[0] = "-1";
            }
            return result;
        }
    }

    private static class DisconnectAsync extends AsyncTask<Void, Void, Integer> { // AsyncTask for disconnect
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                if(in != null && out != null && clientSocket != null) {
                    in.close(); // close input stream
                    out.close(); // close output stream
                    clientSocket.close(); // close socket
                    return 0;
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

    public static void showErrors(int status, Context context) { // display connection errors
        if(status == -1)
            Toast.makeText(context, "Connection failure! IO Exception", Toast.LENGTH_LONG).show();

        if(status == -2)
            Toast.makeText(context, "Connection failure! Task Interrupted Exception", Toast.LENGTH_LONG).show();

        if(status == -3)
            Toast.makeText(context, "Connection failure! Task Execution Exception", Toast.LENGTH_LONG).show();

        if(status == -4)
            Toast.makeText(context, "Connection error! Exception: UnknownHost", Toast.LENGTH_LONG).show();

        if(status == -5)
            Toast.makeText(context, "Connection failure! Receive data error", Toast.LENGTH_LONG).show();

        if(status == -6)
            Toast.makeText(context, "Connection failure! Received bad response", Toast.LENGTH_LONG).show();
    }
}