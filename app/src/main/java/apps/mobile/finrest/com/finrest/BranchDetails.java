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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BranchDetails extends AppCompatActivity {

    ConnectionClass connectionClass;
    String customerid;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    String branchId,state,district,pincode,city,email,phone,address;
    TextView branch_textview,branch_id_textview,email_textview,phone_textview,pincode_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_details_demo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bank Details");
        Window window = BranchDetails.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(BranchDetails.this.getResources().getColor(R.color.light_blue));
        }
        branch_textview = (TextView) findViewById(R.id.branch_address);
        branch_id_textview = (TextView) findViewById(R.id.branch_id);
        email_textview = (TextView) findViewById(R.id.email);
        phone_textview = (TextView) findViewById(R.id.phone_no);
        pincode_textview = (TextView) findViewById(R.id.pincode);

        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        GetBranchDetails getBranchDetails = new GetBranchDetails();
        getBranchDetails.execute();
    }


    public class GetBranchDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(BranchDetails.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = BranchDetails.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
////                String _server = "192.168.1.19";
//                String _server = "10.0.2.2";
//                String _DB = "CIS_FINREST_TEST2v5.7.1";
//                String _user = "sa";
//                String _pass = "Pa$$w0rd";
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "Select cust.branch_id, sb.address, sb.telephone_no,sb.email_id, sb.branch,st.state_name, dist.district_name, sb.pin_code, city.city_name from CIS_FINREST_SOCIETY_BRANCH sb join CIS_FINREST_CUSTOMER cust on cust.branch_id=sb.branch_id join CIS_FINREST_STATEMASTER st on st.state_id=sb.state join CIS_FINREST_DISTRICTMASTER dist on dist.district_id=sb.district join CIS_FINREST_CITYMASTER city on city.city_id=sb.city where customer_id='" + customerid + "'";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("branch_id1", rs.getString("branch_id"));
                        datanum.put("state1", rs.getString("state_name"));
                        datanum.put("district1", rs.getString("district_name"));
                        datanum.put("pincode1", rs.getString("pin_code"));
                        datanum.put("city1", rs.getString("city_name"));
                        datanum.put("email1", rs.getString("email_id"));
                        datanum.put("phone_no1", rs.getString("telephone_no"));
                        datanum.put("address", rs.getString("address"));

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
            // TODO Auto-generated method stub
            for (HashMap<String, String> map : profileList1) {
                branchId = map.get("branch_id1");
                state = map.get("state1");
                district = map.get("district1");
                email = map.get("email1");
                phone = map.get("phone_no1");
                pincode = map.get("pincode1");
                city = map.get("city1");
                address = map.get("address");
                System.out.println("ffffff"+address);

            }
            pincode_textview.setText(pincode);
            branch_id_textview.setText(branchId);
            email_textview.setText(email);
            phone_textview.setText(phone);
            branch_textview.setText(address+" "+city+"\nDistrict-"+district+",State-Karnataka");

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
                final Dialog dialog = new Dialog(BranchDetails.this);

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
