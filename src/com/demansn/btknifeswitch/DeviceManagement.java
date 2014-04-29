package com.demansn.btknifeswitch;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.view.Menu;

public class DeviceManagement extends Activity {
	
	private BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_management);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_management, menu);
		return true;
	}
	
	public void init(){
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//��������� ������� �� ������, ���� �� ������� ����� �������� ���
		
		if(bluetoothAdapter.isEnabled()) {
			enebleBluetoothAdapter();
		}
		
		
		//��������� ���� �� ����������� ����������, ���� ���� ����� ������� � ������ ���������
	}
	
	public void enebleBluetoothAdapter(){
		
	}

}
