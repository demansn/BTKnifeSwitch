package com.demansn.btknifeswitch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectedThread extends Thread {
	    private final BluetoothSocket bluetoothSocket;
	    private final InputStream inputStream;
	    private final OutputStream outputStream;
	    private final Handler handler;
	 
	    public ConnectedThread(BluetoothSocket socket, Handler mHandler) {
	    	
	    	bluetoothSocket = socket;
	    	handler = mHandler;
	    	
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;	 
	        
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { 
	        	
	        }
	 
	        inputStream = tmpIn;
	        outputStream = tmpOut;
	    }
	 
	    public void run() {
	    	
	        byte[] buffer; 
	        int bytes; 
	        
	        while (true) {
	            try {
	            
	            	buffer = new byte[1024];
	                bytes = inputStream.read(buffer);
	               
	                handler.obtainMessage(R.bluetoothActions.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
	            
	            } catch (IOException e) {
	            
	                break;
	            }
	        }
	    }
	 
	   
	    public void write(byte[] bytes) {
	        try {
	        	outputStream.write(bytes);
	        } catch (IOException e) {
	        	
	        }
	    }
	    
	    public void write(String string) {
	        try {
	        	outputStream.write(string.getBytes());
	        } catch (IOException e) {
	        	
	        }	    
	    
	    }	 
	    
	    public void write(Integer integer) {
	        try {
	        	outputStream.write(integer);
	        } catch (IOException e) {
	        	
	        }
	    }
	    
	   public boolean isConnected(){
	    	
		   boolean conected = !bluetoothSocket.isConnected();
		   
		   return conected;
	    }	 
	    
	    public void cancel() {
	        try {
	        	bluetoothSocket.close();
	        } catch (IOException e) {
	        	
	        }
	    }
}
    


