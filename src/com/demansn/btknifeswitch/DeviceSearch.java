package com.demansn.btknifeswitch;

import java.util.ArrayList;
import java.util.Set;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceSearch extends Activity implements OnItemClickListener {
	
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private ArrayList<BluetoothDevice> bluetoothDevices;
	private ListView listView;
	private BroadcastReceiver receiver;
    private IntentFilter filter;
    private ArrayAdapter<String> listAdapter;
    private Button btnSearch;
    private ProgressDialog progressDialog;
    private ArrayList<String> pairedDevices;
    private Set<BluetoothDevice> bondedDevicesArray;
    private BluetoothDevice selectedBluetoothDevice;
    
    protected static final int REQUEST_CODE_DEVISE_PAIRING = 2;
    protected static final int PAIRING_VARIANT_PIN = 0;
   	protected static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";	       
   	protected static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    protected static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_search);
		this.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_management, menu);
		return true;
	}
	
	public void init(){		

		progressDialog = new ProgressDialog(this);
		listView = (ListView)findViewById(R.id.listView1);
		btnSearch = (Button)findViewById(R.id.btSearch);
		bluetoothDevices = new ArrayList<BluetoothDevice>();
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
		pairedDevices = new ArrayList<String>();
		
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener((OnItemClickListener) this);
		
		 OnClickListener oclBtnSearch = new OnClickListener() {
		       @Override
		       public void onClick(View v) { 
		    		   startDiscovery();		    	  
		       }
		     };
		     
		     
		btnSearch.setOnClickListener(oclBtnSearch);
		
		receiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {				
				
				String action = intent.getAction();				
				
				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);				
					
					
					String s = "";
					for(int a = 0; a < pairedDevices.size(); a++){
						if(device.getName().equals(pairedDevices.get(a))){
							//append 
							s = " (Paired)";
							break;
						}
					}
			
					bluetoothDevices.add(device);					
					listAdapter.add(device.getName() + s + "\n"+device.getAddress());
					
				} else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
					
					bluetoothDevices.clear();
					listAdapter.clear();
			
					progressDialog.setTitle("Поиск");
					progressDialog.setMessage("Идет поиск устройств...");				    
					progressDialog.show();
					
				} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){		
					
					progressDialog.hide();					
				
				} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
					
				}
		  
			}
		};
		
		getPairedDevices();
		
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);		
		
		if(requestCode == REQUEST_CODE_DEVISE_PAIRING){
			if(resultCode == RESULT_OK){
				returnToDeviceManagement();
			}
			if(resultCode == RESULT_CANCELED){
				Toast.makeText(this, R.string.message_pairCanceled, Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void startDiscovery() {
		
		bluetoothAdapter.cancelDiscovery();
		bluetoothAdapter.startDiscovery();
		
	}
	
	private void getPairedDevices() {	
		
		bondedDevicesArray = bluetoothAdapter.getBondedDevices();
			
		if(bondedDevicesArray.size()>0){
			for(BluetoothDevice bluetoothDevice:bondedDevicesArray){
				pairedDevices.add(bluetoothDevice.getName());				
			}
		}
		
	}
	
	private void requestPairingDevice(){
		
		Intent intent = new Intent(ACTION_PAIRING_REQUEST);
		
        intent.putExtra(EXTRA_DEVICE, selectedBluetoothDevice);	
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        startActivityForResult(intent, REQUEST_CODE_DEVISE_PAIRING);	
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	
		
		if(bluetoothAdapter.isDiscovering()){
			bluetoothAdapter.cancelDiscovery();
		}		
	
		selectedBluetoothDevice = bluetoothDevices.get(arg2);		
		
		if(selectedBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
			returnToDeviceManagement();
		} else {
			requestPairingDevice();
		}
	}
	
	public void returnToDeviceManagement(){
		
		Intent intent = new Intent();
		intent.putExtra("device", selectedBluetoothDevice);
		
		setResult(RESULT_OK, intent);
		
		finish();	
	}
}
