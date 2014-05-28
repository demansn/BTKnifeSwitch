package com.demansn.btknifeswitch;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DeviceManagement extends Activity {
	
	protected static final int REQUEST_CODE_DEVISE_SEARCH = 1;
    protected static final int REQUEST_CODE_DEVISE_PAIRING = 2;
    protected static final int REQUEST_CODE_BTADAPTER_ENABLE = 3;
    protected static final int REQUEST_CODE_DEVISE_SETTINGS = 4;
    protected static final int PAIRING_VARIANT_PIN = 0;
	protected static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";	       
	protected static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    protected static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
    protected static final String EXTRA_THREAD = "com.demansn.btknifeswitch.ConnectedThread";
   
	public static BluetoothDevice bluetoothDevice;
	private ConnectThread connectThread;
	public static ConnectedThread connectedThread;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private SharedPreferences sharedPreferences; 
	private String deviceMACAddress;
	public ToggleButton toggle;
	private String debugTeg = "demansn";
	private ImageView lampOn;
	private ImageView lampOff;
	private ProgressDialog progressDialog;
	private StringBuilder  readBufer = new StringBuilder();
	public Context  ct;
	
	@SuppressLint("HandlerLeak")
	public Handler connectHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {			
		
				super.handleMessage(msg);
				
				switch(msg.what){
				
				case R.bluetoothActions.SUCCESS_CONNECT:
			
					connectedThread = new ConnectedThread((BluetoothSocket)msg.obj, connectHandler);
					Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_SHORT).show();
					
					//connectedThread.write(getString(R.bluetoothActions.CONNECTED));
					connectedThread.write(getString(R.bluetoothActions.GET_STATE));
					connectedThread.start();
					
					progressDialog.hide();
			
					break;
					
				case R.bluetoothActions.MESSAGE_READ:
					byte[] readBuf = (byte[])msg.obj;					
			
					String strIncom = new String(readBuf, 0, msg.arg1); 
					
					readBufer.append(strIncom);
					
					Log.d(debugTeg, readBufer.toString());

	                int beginOfLineIndex = readBufer.indexOf("*");
	                int endOfLineIndex = readBufer.indexOf("#");
	                  
	                if ((endOfLineIndex > 0) && (beginOfLineIndex == 0)) {
	                	  
	                	  String sbprint = readBufer.substring(beginOfLineIndex + 3, endOfLineIndex - 2);
	                	  
	                	  Log.d(debugTeg, "message read = " + sbprint);
	                	  
	                	  try {	                
	                		
	                		  if(sbprint.equals("0") == true){
		                		  toggle.setChecked(false);
		                		  lampOn.setVisibility(ImageView.INVISIBLE);
		                		  lampOff.setVisibility(ImageView.VISIBLE);		                		  
		                	  } else if(sbprint.equals("1") == true){
		                		  toggle.setChecked(true);
		                		  lampOn.setVisibility(ImageView.VISIBLE);
		      		        	  lampOff.setVisibility(ImageView.INVISIBLE);
		                	  }
	                		  
	                	  } catch(Exception e){
	                		  Log.d(debugTeg, "Error" + e.getMessage());
	                	  } 
	                	  
	                	  readBufer.delete(0, readBufer.length());
	                }
				
					break;
				}
			}
	};	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_management);
		
		if(bluetoothAdapter == null){			
			Toast.makeText(this, R.string.message_not_btAdapter, Toast.LENGTH_LONG).show();
			
			this.finish();
		} else {
			this.init();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.device_management, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      
	  int itemID = item.getItemId();
	  
	  switch(itemID){
	  case R.id.action_settings:
		  
		  //requestSearchDevice();
		  Intent intent = new Intent(this, DeviceSettings.class);
			
		  startActivity(intent);
		  
		  break;
		  
	  case R.id.action_deviceSearch:
		  requestSearchDevice();
		  break;	  
	  }      
      return super.onOptionsItemSelected(item);
    }	

	
	public void init(){	
		
		if(!bluetoothAdapter.isEnabled()) {
			//enebleBluetoothAdapter();	
			bluetoothAdapter.enable();
		}
		
		ct = this;
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.progress_title);
		progressDialog.setMessage(getString(R.string.progress_message));
		progressDialog.hide();
		
		
		lampOn = (ImageView)findViewById(R.id.imageLampOn);
		lampOn.setVisibility(ImageView.INVISIBLE);
		
		lampOff = (ImageView)findViewById(R.id.imageLampOff);		
		
		toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	if(IsConnected()){
			        if (!isChecked && connectedThread != null) {
			        	connectedThread.write(getString(R.bluetoothActions.TURN_SWITCH_ON));
			        	lampOn.setVisibility(ImageView.INVISIBLE);
			        	lampOff.setVisibility(ImageView.VISIBLE);
			        } else {
			        	connectedThread.write(getString(R.bluetoothActions.TURN_SWITCH_OFF));
			        	lampOn.setVisibility(ImageView.VISIBLE);
			        	lampOff.setVisibility(ImageView.INVISIBLE);
			        }
		    	} else {
		    		Toast.makeText(ct, R.string.message_btDeviceNotPair, Toast.LENGTH_LONG).show();
		    	}
		    }
		});

		sharedPreferences = this.getPreferences(MODE_PRIVATE); 
		deviceMACAddress = sharedPreferences.getString("deviceMACAddress", "");		
		
		if(deviceMACAddress != ""){
			bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceMACAddress);    
		}
		
		if(bluetoothDevice == null){
    		requestSearchDevice();
    	} else {
    		if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE){
    			requestPairingDevice();
    		} else {
    			StartConnectToDevice();   			
    		}
    	}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);		
		
		switch(requestCode){
		
			case REQUEST_CODE_DEVISE_SEARCH:
				
				if(resultCode == RESULT_OK){
				
					bluetoothDevice = data.getParcelableExtra("device");
					
					Editor editor = this.sharedPreferences.edit();
					
					this.deviceMACAddress = bluetoothDevice.getAddress();
					
					editor.putString("deviceMACAddress", this.deviceMACAddress);
					editor.commit();
					
					StartConnectToDevice();	
				}
				
				break;
				
			case REQUEST_CODE_DEVISE_PAIRING:
				
					if(resultCode == RESULT_OK && bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE){
						//bluetoothAdapter.disable();
						Toast.makeText(this, R.string.message_btDeviceNotPair, Toast.LENGTH_LONG).show();
					}
					//Toast.makeText(this, R.string.message_btDeviceNotPair, Toast.LENGTH_LONG).show();
					//finish();				
								
				break;
				
			case REQUEST_CODE_BTADAPTER_ENABLE:
				
				break;
			
		}
	}
	
	public void enebleBluetoothAdapter(){
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		
		startActivityForResult(intent, REQUEST_CODE_BTADAPTER_ENABLE);
	}
	
	private void requestPairingDevice(){
		
		Intent intent = new Intent(ACTION_PAIRING_REQUEST);
		
        intent.putExtra(EXTRA_DEVICE, bluetoothDevice);	
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        
        startActivityForResult(intent, REQUEST_CODE_DEVISE_PAIRING);
	}
	
	private void requestSearchDevice(){
		
		Intent intent = new Intent(this, DeviceSearch.class);
		
	    startActivityForResult(intent, REQUEST_CODE_DEVISE_SEARCH);
	}
	
	private void requestSettingDevice(){
		
		Intent intent = new Intent(this, DeviceSettings.class);
		//intent.putExtra(EXTRA_DEVICE, bluetoothDevice);	
       // intent.putExtra(EXTRA_THREAD, connectedThread);
		
	    startActivityForResult(intent, REQUEST_CODE_DEVISE_SEARCH);
		
	}
	
	private void StartConnectToDevice(){
		connectThread = new ConnectThread(bluetoothDevice, connectHandler);
		connectThread.start();	
		progressDialog.show();
	}
	
	private boolean IsConnected(){
		boolean isConnected = false;
		
		if(bluetoothDevice != null && bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
			
			isConnected = true;
			
		}		
		
		return isConnected;		
		
	}

}
