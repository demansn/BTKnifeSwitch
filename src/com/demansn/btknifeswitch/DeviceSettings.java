package com.demansn.btknifeswitch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceSettings extends Activity {
	private TextView deviceInfo;
	private EditText newDevicePin;
	private EditText newDeviceName;
	private Button btnOk;
	private Button btnCancel;
	private BluetoothDevice bluetoothDevice;
	private ConnectedThread connectedThread;
	
	private OnClickListener btnOkOnClickListener = new OnClickListener(){		 
		@Override
		public void onClick(View arg0) {
			
			String newName = (String) newDeviceName.toString();
			String newPin = (String) newDevicePin.toString();
			
			/*if(newName.length() > 0){
				
			}*/
			
			connectedThread.write("4");
		}
	};
	
	private OnClickListener btnCancelOnClickListener = new OnClickListener(){		 
		@Override
		public void onClick(View arg0) {			
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_settings);
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_management, menu);
		return true;
	}
	
	public void init(){
		deviceInfo = (TextView) findViewById(R.id.textDeviceInfo);
		newDevicePin = (EditText) findViewById(R.id.editDevicePin);
		newDeviceName = (EditText) findViewById(R.id.editDeviceName);
		btnOk = (Button) findViewById(R.id.btnSaveNewInfo);		
		btnOk.setOnClickListener(btnOkOnClickListener);		
		btnCancel = (Button) findViewById(R.id.btnCancelNewInfo);
		btnCancel.setOnClickListener(btnCancelOnClickListener);
		bluetoothDevice = DeviceManagement.bluetoothDevice;
		connectedThread = DeviceManagement.connectedThread;
		deviceInfo.setText(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
		
		//connectThread = new ConnectThread(bluetoothDevice, connectHandler);
		//connectThread.start();
	}
}
