package com.demansn.btknifeswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class DeviceSettings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_management, menu);
		return true;
	}
}
