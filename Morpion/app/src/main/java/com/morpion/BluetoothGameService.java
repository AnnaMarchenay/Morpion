package com.morpion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by annam on 27/03/2017.
 */

class BluetoothGameService {

    private static final String TAG = "BluetoothGameService";

    private static final String mName = "Morpion";

    private static final UUID mUUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private String marker = "";

    private int mState;

    static final int STATE_NONE = 0;
    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_DISCONNECTED = 4;
    static final int STATE_NEW_GAME = 5;

    BluetoothGameService(Handler handler){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setState(int state){
        mState = state;
        mHandler.obtainMessage(TwoPlayerActivityBluetooth.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    synchronized int getState(){
        return mState;
    }

    synchronized void start(){

        if(mConnectThread != null){mConnectThread.cancel(); mConnectThread = null;}
        if(mConnectedThread != null){mConnectedThread.cancel(); mConnectedThread = null;}

        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        setState(STATE_LISTEN);
    }

    void setNewGame(){
        if(mState == STATE_CONNECTED) {
            setState(STATE_NEW_GAME);
        }
        setState(STATE_CONNECTED);
    }
    synchronized void connect(BluetoothDevice device, String marker) {

        this.marker = marker;

        if(mState == STATE_CONNECTING){
            if(mConnectThread != null){mConnectThread.cancel(); mConnectThread = null;}
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);

    }


    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        if (mConnectThread != null) {
            mConnectThread.cancel(); mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel(); mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel(); mAcceptThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        Message msg = mHandler.obtainMessage(TwoPlayerActivityBluetooth.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(TwoPlayerActivityBluetooth.DEVICE_NAME, device.getName());
        bundle.putString(TwoPlayerActivityBluetooth.MARKER, marker);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel(); mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel(); mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel(); mAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    void writeLocation(byte[] out){
        ConnectedThread r;
        synchronized (this){
            if(mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.writeLocation(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);

        Message msg = mHandler.obtainMessage(TwoPlayerActivityBluetooth.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TwoPlayerActivityBluetooth.TOAST, "Impossible de se connecter à un appareil");
        msg.setData(bundle);
        mHandler.sendMessage(msg);


    }

    private void connectionLost() {
        setState(STATE_DISCONNECTED);
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(TwoPlayerActivityBluetooth.MESSAGE_DISCONNECTED);
        Bundle bundle = new Bundle();
        bundle.putString(TwoPlayerActivityBluetooth.TOAST, "La connexion a été perdue");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mServerSocket;


        AcceptThread(){
            BluetoothServerSocket temp = null;
            try{
                temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mName, mUUID);
            }catch (IOException e){
                Log.e(TAG, "listen échoué", e);
            }


            mServerSocket = temp;
        }

        public void run(){
            setName("AcceptThread");

            BluetoothSocket socket;

            while(mState != STATE_CONNECTED){
                try{
                    socket = mServerSocket.accept();
                }catch(IOException e){
                    Log.e(TAG, "accept échoué", e);
                    break;
                }


                if(socket != null){
                    synchronized (BluetoothGameService.this){
                        switch (mState){
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try{
                                    socket.close();
                                }catch(IOException e){
                                    Log.e(TAG, "Impossible de close socket", e);
                                }
                                break;
                        }
                    }
                }
            }

        }

        void cancel(){
            try{
                mServerSocket.close();
            }catch(IOException e){
                Log.e(TAG, "close server échoué", e);
            }
        }
    }

    private class ConnectThread extends Thread{

        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        ConnectThread(BluetoothDevice device){

            BluetoothSocket temp = null;
            mDevice = device;

            try{
                temp = device.createRfcommSocketToServiceRecord(mUUID);
            }catch(IOException e){
                Log.e(TAG, "create échoué", e);
            }
            mSocket = temp;
        }

        public void run(){
            setName("ConnectThread");

            mBluetoothAdapter.cancelDiscovery();

            try{
                mSocket.connect();
            }catch(IOException e){
                connectionFailed();

                try{
                    mSocket.close();
                }catch(IOException closeException){
                    Log.e(TAG, "impossible de close socket", closeException);
                }
                BluetoothGameService.this.start();
                return;
            }

            synchronized (BluetoothGameService.this){
                mConnectThread = null;
            }

            connected(mSocket, mDevice);

        }

        void cancel(){
            try{
                mSocket.close();
            }catch(IOException e){
                Log.e(TAG, "close client échoué", e);
            }
        }

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        ConnectedThread(BluetoothSocket socket){

            mSocket = socket;
            InputStream tempInput = null;
            OutputStream tempOutput = null;

            try{
                tempInput = socket.getInputStream();
                tempOutput = socket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG, "sockets temporaires échoués", e);
            }

            mInStream = tempInput;
            mOutStream = tempOutput;
        }

        public void run(){
            readLocation();
        }

        void readLocation(){

            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = mInStream.read(buffer);
                    mHandler.obtainMessage(TwoPlayerActivityBluetooth.PLAYER_LOCATION_READ, bytes, -1, buffer).sendToTarget();
                }catch(IOException e){
                    Log.e(TAG, "déconnecté", e);
                    connectionLost();
                    break;
                }
            }
        }

        void writeLocation(byte[] buffer){
            try{
                mOutStream.write(buffer);
                mHandler.obtainMessage(TwoPlayerActivityBluetooth.PLAYER_LOCATION_WRITE, -1 , -1 , buffer).sendToTarget();
            }catch(IOException e){
                Log.e(TAG, "write échoué", e);
            }
        }

        void cancel(){
            try{
                mSocket.close();
            }catch(IOException e){
                Log.e(TAG, "close socket échoué", e);
            }
        }
    }




}
