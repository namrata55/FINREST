package apps.mobile.finrest.com.finrest;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class OTPConfirmation extends AppCompatActivity {

    ConnectionClass connectionClass;
    TextView societyCodeList;
    EditText otp_edittext;
    public String customerid;
    Button Proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpconfirmation);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Window window = OTPConfirmation.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(OTPConfirmation.this.getResources().getColor(R.color.light_blue));
        }
        connectionClass = new ConnectionClass();
        otp_edittext = (EditText) findViewById(R.id.otp);
        Proceed = (Button) findViewById(R.id.proceed);
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(otp_edittext.getWindowToken(), 0);
               String otp = otp_edittext.getText().toString();
                String space="";
                if(otp.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter OTP", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                System.out.print("Customer id"+customerid);
                VerifyOtp verifyotp = new VerifyOtp();
                verifyotp.execute("");
            }
        });

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }



    public class VerifyOtp extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String otp = otp_edittext.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(OTPConfirmation.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = OTPConfirmation.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select * from CIS_FINREST_CUSTOMER where customer_id='"+ customerid +"' and otp='" + otp + "'";
                    System.out.print("query"+query);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(rs.next())
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

                Toast toast = Toast.makeText(getApplicationContext(),"Your OTP verification is successful. Kindly set your MPIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent i = new Intent(OTPConfirmation.this, SetMPIN.class);
                startActivity(i);
                finish();
            }
            else{

                Toast toast = Toast.makeText(getApplicationContext(),"Invalid OTP", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                otp_edittext.setText("");
                otp_edittext.requestFocus();
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
                final Dialog dialog = new Dialog(OTPConfirmation.this);

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