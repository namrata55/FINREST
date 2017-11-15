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
import android.os.StrictMode;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Register extends AppCompatActivity {

    ConnectionClass connectionClass;
    EditText customerId_edittext;
    TextView societyCode_edittext;
    public String customerid,societycode;
    Button Proceed;
    String random_otp;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    String mobile_no;
    static final int REQUEST_CODE = 0;

    String society_name,society_database_name,society_code,society_ip_address,society_db_username,society_db_password,society_db_instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Window window = Register.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Register.this.getResources().getColor(R.color.light_blue));
        }
        connectionClass = new ConnectionClass();
        customerId_edittext = (EditText) findViewById(R.id.customer_id);
        societyCode_edittext = (TextView) findViewById(R.id.society_code);

        InputMethodManager imm1 = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm1.hideSoftInputFromWindow(customerId_edittext.getWindowToken(), 0);

        societyCode_edittext.requestFocus();
        Proceed = (Button) findViewById(R.id.proceed);
        random_otp = ""+((int)(Math.random()*9000)+1000);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.GINGERBREAD){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(customerId_edittext.hasFocus())
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(customerId_edittext, 0);
        }

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(customerId_edittext.getWindowToken(), 0);

                customerid = customerId_edittext.getText().toString();
                societycode = societyCode_edittext.getText().toString();

                String space = "";
                if (customerid.equals(space) && societycode.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Customer Id and Society Code", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                 else if (customerid.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Customer Id", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    customerId_edittext.requestFocus();
                }
                else if(societycode.equals(space)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Select Society Code", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                else {
                    DoRegister doRegister = new DoRegister();
                    doRegister.execute("");
                }
            }
        });

        societyCode_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(customerId_edittext.getWindowToken(), 0);

                startActivityForResult(new Intent(Register.this,SocietiesList.class), REQUEST_CODE);
            }
        });

    }

    public class DoRegister extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String userid = customerId_edittext.getText().toString();

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(Register.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

                try {

                    SharedPreferences Database_Details = Register.this.getSharedPreferences("database_details", MODE_PRIVATE);
                    String _DB=Database_Details.getString("database_name","");
                    String _server=Database_Details.getString("database_ip_address","");
                    String _user=Database_Details.getString("database_username","");
                    String _pass=Database_Details.getString("database_password","");
                    Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "select * from CIS_FINREST_CUSTOMER where customer_id='" + userid + "'";
                        System.out.println("query"+query);
                        System.out.println("Connection:----"+con);
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                            while (rs.next()) {
                                z = "Login successfull";
                                isSuccess=true;
                                HashMap<String, String> datanum = new HashMap<String, String>();
                                datanum.put("mob_no", rs.getString("mob_no"));
                                profileList1.add(datanum);
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

                SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
                SharedPreferences.Editor editor = Customer_Details.edit();
                editor.putString("customer_id", customerid);
                editor.commit();

                for (HashMap<String, String> map : profileList1) {
                    mobile_no = map.get("mob_no");
                }
                UpdateOTP uotp = new UpdateOTP();
                uotp.execute();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Invalid Customer Id", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                customerId_edittext.setText("");
                customerId_edittext.requestFocus();
            }
        }
    }

    public class UpdateOTP extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String userid = customerId_edittext.getText().toString();
        String password = societyCode_edittext.getText().toString();

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(Register.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = Register.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB = Database_Details.getString("database_name","");
                String _server = Database_Details.getString("database_ip_address","");
                String _user = Database_Details.getString("database_username","");
                String _pass = Database_Details.getString("database_password","");
                String _instance = Database_Details.getString("database_instance","");

                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,_instance);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "update CIS_FINREST_CUSTOMER set otp='"+random_otp+"' where customer_id='" + userid + "'";
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
                SendSms();
                Toast toast = Toast.makeText(getApplicationContext(),"OTP has been sent to your registered mobile no.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                System.out.println("OTP---"+random_otp);
                Intent i = new Intent(Register.this, OTPConfirmation.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(i);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Invalid Customer Id", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                customerId_edittext.setText("");
                customerId_edittext.requestFocus();
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

    void SendSms()
    {
        try {
            String recipient = "91"+mobile_no;
            String message = "OTP to apply New Registration for Finrest android app is:"+random_otp;
            String username = "smartt";
            String password = "8800755655";
            String originator = "SMARTT";
            String requestUrl  = "http://193.105.74.159/api/v3/sendsms/plain?" +
                    "user=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8") +
                    "&sender=" + URLEncoder.encode(originator, "UTF-8") +
                    "&SMSText=" + URLEncoder.encode(message, "UTF-8") +
                    "&type=longsms"+
                    "&GSM=" + URLEncoder.encode(recipient, "UTF-8") +
                    "&output=json";

            System.out.println("Message:"+requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection uc = (HttpURLConnection)url.openConnection();
            System.out.println("result:"+uc.getResponseMessage());
            uc.disconnect();
        } catch(Exception ex) {
            System.out.println("error:"+ex.getMessage());
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

            if (resultCode == RESULT_OK) {

                society_code = data.getStringExtra("Society-Code");
                society_name = data.getStringExtra("Society-Name");
                society_database_name = data.getStringExtra("Society-Database-Name");
                society_ip_address = data.getStringExtra("Society-Ip-Address");
                society_db_username = data.getStringExtra("Society-Db-Username");
                society_db_password = data.getStringExtra("Society-Db-Password");
                society_db_instance = data.getStringExtra("Society-Db-Instance");

                System.out.println("name---------" + society_ip_address);
                System.out.println("name---------" + society_database_name);
                System.out.println("name---------" + society_name);
                System.out.println("name---------" + society_code);
                System.out.println("name---------" + society_db_password);
                System.out.println("name---------" + society_db_username);
                System.out.println("instance---------" + society_db_instance);


                societyCode_edittext.setText("" + society_code + "-" + society_name + "");
                SharedPreferences DatabaseDetails = getApplicationContext().getSharedPreferences("database_details", MODE_PRIVATE);
                SharedPreferences.Editor editor = DatabaseDetails.edit();
                editor.putString("database_name", society_database_name);
                editor.putString("database_ip_address", society_ip_address);
                editor.putString("database_username", society_db_username);
                editor.putString("database_password", society_db_password);
                editor.putString("database_instance", society_db_instance);
                editor.commit();

            }
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
                final Dialog dialog = new Dialog(Register.this);

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