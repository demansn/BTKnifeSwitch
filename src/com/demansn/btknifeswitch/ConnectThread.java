package com.demansn.btknifeswitch;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ConnectThread extends Thread {
	
	public final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final Handler handler;
    private final String debugTeg = "demansn";
    
    protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 
    
 
    public ConnectThread(BluetoothDevice device, Handler mHandler) {

        BluetoothSocket tempBluetoothSocket = null;
        bluetoothDevice = device;
        handler = mHandler;

        try {          
        	tempBluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { 
        	Log.e(debugTeg, "get socket failed");        	
        }
        
        bluetoothSocket = tempBluetoothSocket;
    }
 
    public void run() {
       
        try {    
        	
        	bluetoothSocket.connect();
         
        } catch (IOException connectException) {
        	
        	Log.e(debugTeg, "connect failed");
           
            try {
            	bluetoothSocket.close();
            } catch (IOException closeException) {
            	Log.e(debugTeg, "bluetoothSocket.close() failed");
            }
            return;
        }
   
        handler.obtainMessage(R.bluetoothActions.SUCCESS_CONNECT, bluetoothSocket).sendToTarget();
    }
 
    public boolean isConnected(){    	
    	return bluetoothSocket.isConnected();
    }  

	
    public void cancel() {
        try {
        	bluetoothSocket.close();
        } catch (IOException e) {
        	Log.e(debugTeg, "bluetoothSocket.close() failed");
        }
    }
}
