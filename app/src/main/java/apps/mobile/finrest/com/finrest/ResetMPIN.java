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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
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

public class ResetMPIN extends AppCompatActivity {

    EditText oldmpin_edittext,newmpin_edittext,cnfrmnewmpin_edittext;
    Button submit;
    String oldmpin;
    String newmpin;
    String newcnfrmmpin,customerid;
    ConnectionClass connectionClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_mpin);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change MPIN");

        Window window = ResetMPIN.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ResetMPIN.this.getResources().getColor(R.color.light_blue));
        }

        oldmpin_edittext = (EditText) findViewById(R.id.old_mpin);
        newmpin_edittext = (EditText) findViewById(R.id.new_mpin);
        cnfrmnewmpin_edittext = (EditText) findViewById(R.id.cnfrmnewmpin);
        submit = (Button) findViewById(R.id.reset_mpin_submit);
        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cnfrmnewmpin_edittext.getWindowToken(), 0);

                oldmpin = oldmpin_edittext.getText().toString();
                 newmpin = newmpin_edittext.getText().toString();
                 newcnfrmmpin = cnfrmnewmpin_edittext.getText().toString();
                String space = "";

                if(oldmpin.equals(space) && newmpin.equals(space) && newcnfrmmpin.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Old MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    oldmpin_edittext.requestFocus();
                }
                else if(oldmpin.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Old MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    oldmpin_edittext.requestFocus();

                }
                else if(newmpin.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter New MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newmpin_edittext.requestFocus();

                }
                else if(newcnfrmmpin.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Confirm New MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    cnfrmnewmpin_edittext.requestFocus();

                }
                else if(!newmpin.equals(newcnfrmmpin)){
                    Toast toast = Toast.makeText(getApplicationContext(),"New MPIN and Confirm MPIN are not matching", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newmpin_edittext.setText("");
                    cnfrmnewmpin_edittext.setText("");
                    newmpin_edittext.requestFocus();
                }
                else if(oldmpin.equals(newcnfrmmpin) && oldmpin.equals(newmpin)){
                    Toast toast = Toast.makeText(getApplicationContext(),"New MPIN and Old MPIN are same", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newmpin_edittext.setText("");
                    cnfrmnewmpin_edittext.setText("");
                    oldmpin_edittext.setText("");
                    oldmpin_edittext.requestFocus();
                }
                else if(newmpin.length()<4 && newcnfrmmpin.length()<4){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter your 4 digit MPIN", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    newmpin_edittext.setText("");
                    cnfrmnewmpin_edittext.setText("");
                    newmpin_edittext.requestFocus();
                }
                else{
                    UpdateMPIN updatempn = new UpdateMPIN();
                    updatempn.execute();
                }
            }
        });
    }


    public class UpdateMPIN extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(ResetMPIN.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = ResetMPIN.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "update CIS_FINREST_CUSTOMER set mpin='"+newmpin+"'where customer_id='" + customerid + "' and mpin='" + oldmpin + "'";
                    System.out.println("query"+query);
                    Statement stmt = con.createStatement();
                    int i = stmt.executeUpdate(query);
                    if(i>0){

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
                Toast toast = Toast.makeText(getApplicationContext(),"MPIN has been reset successfully", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent i = new Intent(ResetMPIN.this, LoginDemo.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(i);

            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Invalid old MPIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                oldmpin_edittext.setText("");
                newmpin_edittext.setText("");
                cnfrmnewmpin_edittext.setText("");
                oldmpin_edittext.requestFocus();

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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        // dialog.setMessage(Message);
        return dialog;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
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
                final Dialog dialog = new Dialog(ResetMPIN.this);

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
