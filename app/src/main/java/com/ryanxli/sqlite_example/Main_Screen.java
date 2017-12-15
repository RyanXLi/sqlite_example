package com.ryanxli.sqlite_example;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;


public class Main_Screen extends Activity implements SalutDataCallback {
	static final String TAG = "DbSync";
	public static final int REQUEST_ADD = 1;
	public static final int REQUEST_UPDATE = 2;
    boolean isHost = false;
    boolean inClientScreen = false;
    Button add_btn;
    ListView Contact_listview;
    ArrayList<Contact> contact_data = new ArrayList<Contact>();
    Contact_Adapter cAdapter;
    DatabaseHandler db;
    String Toast_msg;
    Intent intent;

    public ListView deviceListView;
    public ArrayAdapter deviceArrayAdapter;
    public ArrayList<String> deviceArrayList;

    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut salut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        int identity = intent.getIntExtra("Identity", StartActivity.IDENTITY_DEFAULT);

        if (identity == StartActivity.IDENTITY_DEFAULT) {
            Log.e(TAG, "No valid identity (host or client).");

        } else if (identity == StartActivity.IDENTITY_HOST) {
            isHost = true;
            dataReceiver = new SalutDataReceiver(this, this);
            serviceData = new SalutServiceData("myService", 6666, android.os.Build.MODEL);

            salut = new Salut(dataReceiver, serviceData, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
                }
            });

            salut.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                }
            });
            changeContentView();

        } else if (identity == StartActivity.IDENTITY_CLIENT){
            inClientScreen = true;
            setContentView(R.layout.activity_client);

            deviceListView = (ListView) findViewById(R.id.client_listView);


            dataReceiver = new SalutDataReceiver(this, this);
            serviceData = new SalutServiceData("myService", 6666, android.os.Build.MODEL);

            salut = new Salut(dataReceiver, serviceData, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
                }
            });

            deviceArrayList = new ArrayList<>();

            deviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.device_adapter, deviceArrayList);
            deviceListView.setAdapter(deviceArrayAdapter);

            salut.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    deviceArrayList.add(0, salut.foundDevices.get(0).instanceName);
                    deviceArrayAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Device: " + salut.foundDevices.get(0).instanceName + " found.");
                    Toast.makeText(getApplicationContext(), "Device: " + salut.foundDevices.get(0).instanceName +
                            " found.", Toast.LENGTH_SHORT).show();
                }
            }, true);


            deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String deviceName = deviceArrayList.get(position);
                    Boolean found = false;

                    for (SalutDevice device : salut.foundDevices) {
                        if (device.instanceName.equals(deviceName)) {
                            found = true;
                            salut.registerWithHost(device, new SalutCallback() {
                                @Override
                                public void call() {
                                    Log.d(TAG, "We're now registered.");

                                }
                            }, new SalutCallback() {
                                @Override
                                public void call() {
                                    Log.d(TAG, "We failed to register.");
                                }
                            });
                            inClientScreen = false;
                            changeContentView();
                        }
                        if (!found) {
                            Toast.makeText(getApplicationContext(), "Device" + deviceName + "is not not available.", Toast.LENGTH_SHORT).show();
                            deviceArrayList.remove(position);
                        }
                    }

                }
            });
        }
    }

    public void changeContentView() {
        setContentView(R.layout.main);
        try {
            Contact_listview = (ListView) findViewById(R.id.list);
            Contact_listview.setItemsCanFocus(false);
            add_btn = (Button) findViewById(R.id.add_btn);

            Set_Referash_Data();

        } catch (Exception e) {
            Log.e("some error", "" + e);
        }
        add_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent add_user = new Intent(Main_Screen.this,
                        Add_Update_User.class);
                add_user.putExtra("called", "add");
                startActivityForResult(add_user, REQUEST_ADD);
                //		finish();
            }
        });
    }

    public void Set_Referash_Data() {
        contact_data.clear();
        db = new DatabaseHandler(this);
        ArrayList<Contact> contact_array_from_db = db.Get_Contacts();

        for (int i = 0; i < contact_array_from_db.size(); i++) {

            int tidno = contact_array_from_db.get(i).getID();
            String name = contact_array_from_db.get(i).getName();
            String mobile = contact_array_from_db.get(i).getPhoneNumber();
            String email = contact_array_from_db.get(i).getEmail();
            Contact cnt = new Contact();
            cnt.setID(tidno);
            cnt.setName(name);
            cnt.setEmail(email);
            cnt.setPhoneNumber(mobile);

            contact_data.add(cnt);
        }
        db.close();
        cAdapter = new Contact_Adapter(Main_Screen.this, R.layout.listview_row,
            contact_data);
        Contact_listview.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
    }

    public void Show_Toast(String msg) {
	    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
		super.onResume();
        if (!inClientScreen) {
            Set_Referash_Data();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isHost) {
            salut.stopNetworkService(true);
        } else {
            salut.unregisterClient(true);
        }
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == Add_Update_User.RESULT_CANCELED) {
            Log.i(TAG, "Canceled.");
            return;
        }

        Contact newContact = intent.getExtras().getParcelable("result");



        //send result to peer
        Message newMessage = new Message();

        if (requestCode == REQUEST_ADD) {newMessage.myAction = Message.ADD;}
        else //if (requestCode == REQUEST_UPDATE)
            {newMessage.myAction = Message.UPDATE;}

        newMessage.newContact = newContact.toString();
        newMessage.sender = android.os.Build.MODEL+ " (host)";
        newMessage.sendTime = System.currentTimeMillis();

        if (isHost) {
            salut.sendToAllDevices(newMessage, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "The data failed to send.");
                }
            });
        } else {
            salut.sendToHost(newMessage, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "The data failed to send.");
                }
            });
        }

        Log.i(TAG, "Add/update.");
	}





    @Override
    public void onDataReceived(Object data) {
        Log.d(TAG, "Received data.");
        db = new DatabaseHandler(this);
        String myAction = Message.DEFAULT;
        Message newMessage = new Message();

        try
        {
            newMessage = LoganSquare.parse((String) data, Message.class);
            Log.d(TAG, newMessage.sender);
            Log.d(TAG, newMessage.myAction);
            myAction = newMessage.myAction;
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }

        switch (myAction) {
            case Message.DEFAULT:
                Log.e(TAG, "No valid action in message.");
                break;

            case Message.ADD:
                if (newMessage.newContact == null) {
                    Log.e(TAG, "No valid contact in message.");
                } else {
                    db.Add_Contact(Contact.fromString(newMessage.newContact));
                }
                break;

            case Message.UPDATE:
                if (newMessage.newContact == null) {
                    Log.e(TAG, "No valid contact in message.");
                } else {
                    db.Update_Contact(Contact.fromString(newMessage.newContact));
                }
                break;

            case Message.DELETE:
                db.Delete_Contact(newMessage.contactID);
                break;
        }
        db.close();
        Set_Referash_Data();
    }





    public class Contact_Adapter extends ArrayAdapter<Contact> {
		Activity activity;
		int layoutResourceId;
		Contact user;
		ArrayList<Contact> data = new ArrayList<Contact>();

		public Contact_Adapter(Activity act, int layoutResourceId,
			ArrayList<Contact> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			UserHolder holder = null;
	
			if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(activity);
	
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new UserHolder();
			holder.name = (TextView) row.findViewById(R.id.user_name_txt);
			holder.email = (TextView) row.findViewById(R.id.user_email_txt);
			holder.number = (TextView) row.findViewById(R.id.user_mob_txt);
			holder.edit = (Button) row.findViewById(R.id.btn_update);
			holder.delete = (Button) row.findViewById(R.id.btn_delete);
			row.setTag(holder);
			} else {
			holder = (UserHolder) row.getTag();
			}
			user = data.get(position);
			holder.edit.setTag(user.getID());
			holder.delete.setTag(user.getID());
			holder.name.setText(user.getName());
			holder.email.setText(user.getEmail());
			holder.number.setText(user.getPhoneNumber());
	
			holder.edit.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				
				Log.i("Edit Button Clicked", "**********");
				Intent update_user = new Intent(activity,
					Add_Update_User.class);
				update_user.putExtra("called", "update");
				update_user.putExtra("USER_ID", v.getTag().toString());
				activity.startActivityForResult(update_user, REQUEST_UPDATE);
	
			}
			});
			holder.delete.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(final View v) {
				// show a message while loader is loading
				AlertDialog.Builder adb = new AlertDialog.Builder(activity);
				adb.setTitle("Delete?");
				adb.setMessage("Are you sure you want to delete ");
				final int user_id = Integer.parseInt(v.getTag().toString());
				adb.setNegativeButton("Cancel", null);
				adb.setPositiveButton("Ok",
					new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
						int which) {
						// MyDataObject.remove(positionToRemove);
						DatabaseHandler dBHandler = new DatabaseHandler(
							activity.getApplicationContext());
						dBHandler.Delete_Contact(user_id);



                        //send result to peer
                        Message newMessage = new Message();
                        newMessage.myAction = Message.DELETE;
                        newMessage.contactID = user_id;
                        newMessage.sender = android.os.Build.MODEL+ " (host)";
                        newMessage.sendTime = System.currentTimeMillis();

                        if (isHost) {
                            salut.sendToAllDevices(newMessage, new SalutCallback() {
                                @Override
                                public void call() {
                                    Log.e(TAG, "The data failed to send.");
                                }
                            });
                        } else {
                            salut.sendToHost(newMessage, new SalutCallback() {
                                @Override
                                public void call() {
                                    Log.e(TAG, "The data failed to send.");
                                }
                            });
                        }
                        Log.i(TAG, "Delete.");


						Main_Screen.this.onResume();
	
					}
					});
				adb.show();
			}
	
			});
			return row;
	
		}
	
		class UserHolder {
			TextView name;
			TextView email;
			TextView number;
			Button edit;
			Button delete;
		}

    }

}
