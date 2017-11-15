package apps.mobile.finrest.com.finrest;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class SetMPIN extends AppCompatActivity {

    EditText newMpin,cnfrmMpin;
    Button proceed;
    String customerid;
    ConnectionClass connectionClass;
    SessionManager registration_completed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_mpin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Window window = SetMPIN.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(SetMPIN.this.getResources().getColor(R.color.light_blue));
        }

        registration_completed = new SessionManager();
        connectionClass = new ConnectionClass();
        newMpin = (EditText) findViewById(R.id.new_mpin);
        cnfrmMpin = (EditText) findViewById(R.id.cnfrm_mpin);
        proceed = (Button) findViewById(R.id.proceed);


        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String space = "";
                String newmpin = newMpin.getText().toString();
                String cnfrmmpin = cnfrmMpin.getText().toString();
                if(newmpin.equals(space) && cnfrmmpin.equals(space))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter New MPIN and Confirm New MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newMpin.requestFocus();
                }
                else if(newmpin.equals(space) && !cnfrmmpin.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter New MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newMpin.requestFocus();
                }
                else if(cnfrmmpin.equals("") && !newmpin.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Confirm MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    cnfrmMpin.setText("");
                }
                else if (!newmpin.equals(cnfrmmpin)){
                    Toast toast = Toast.makeText(getApplicationContext(),"New MPIN and Confirm MPIN are not matching", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newMpin.setText("");
                    cnfrmMpin.setText("");
                    newMpin.requestFocus();
                }
                else if(newmpin.length()<4 && cnfrmmpin.length()<4){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter 4 digit MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newMpin.setText("");
                    cnfrmMpin.setText("");
                    newMpin.requestFocus();

                }

                else {
                    SetMpin setMPIN = new SetMpin();
                    setMPIN.execute("");
                }
            }
        });
    }

    public class SetMpin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String newmpin = newMpin.getText().toString();
        String cnfrmmpin = cnfrmMpin.getText().toString();
        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(SetMPIN.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = SetMPIN.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "update CIS_FINREST_CUSTOMER set mpin='"+ newmpin +"' where customer_id='"+ customerid +"'";
                    System.out.print("query"+query);
                    Statement stmt = con.createStatement();
                    int i = stmt.executeUpdate(query);
                    if(i>0)
                    {

                        z = "Login successfull";
                        isSuccess=true;
                    }
                    else
                    {
                        z = "Invalid Credentials";
                        isSuccess = false;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }

        @Override
        protected void onPostExecute(String r) {
            loadingDialog.dismiss();

            if(isSuccess) {
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cnfrmMpin.getWindowToken(), 0);

                registration_completed.setPreferences(SetMPIN.this,"status","1");
                Toast toast = Toast.makeText(getApplicationContext(),"MPIN set successfully", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                Intent i = new Intent(SetMPIN.this, Home.class);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"MPIN is not set successfully",Toast.LENGTH_SHORT).show();
                newMpin.setText("");
                cnfrmMpin.setText("");
                newMpin.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newMpin, InputMethodManager.SHOW_IMPLICIT);
            }

        }
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        // dialog.setMessage(Message);
        return dialog;
    }

    //-----------------------------BROADCAST RECEIVER----------------------------------
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (networkInfo != null && networkInfo.isConnected()) {

            } else {
                final Dialog dialog = new Dialog(SetMPIN.this);

                //setting custom layout to dialog
                dialog.setContentView(R.layout.no_internet_alert);
                dialog.setTitle("Custom Dialog");

                Button dismissButton = (Button) dialog.findViewById(R.id.button);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        registerReceiver(mConnReceiver,
                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    }
                });
                dialog.show();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
        if(isApplicationSentToBackground(this))
        {
            finishAffinity();
        }
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }



}
