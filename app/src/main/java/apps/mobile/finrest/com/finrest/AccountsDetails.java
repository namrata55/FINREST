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
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AccountsDetails extends AppCompatActivity {

    String customerid;
    ConnectionClass connectionClass;
    String firstname,lastname,accounttype,amount,status,openingdate,accountno;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    RememberMPIN mpin;
    TextView full_name,atype,astatus,aamount,aopeningdate,accountnum;
    String acc_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_details);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Details");
        Window window = AccountsDetails.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(AccountsDetails.this.getResources().getColor(R.color.light_blue));
        }

        Intent i = getIntent();
        acc_no = i.getStringExtra("Account_no");
        System.out.println("Account no "+acc_no);
        full_name = (TextView) findViewById(R.id.account_name);
        atype = (TextView) findViewById(R.id.account_type);
        astatus = (TextView) findViewById(R.id.account_status);
        aamount = (TextView) findViewById(R.id.amount);
        aopeningdate = (TextView) findViewById(R.id.account_opening_date);
        accountnum = (TextView) findViewById(R.id.account_no);

        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");
        GetAccountDetails getAccountDetails =  new GetAccountDetails();
        getAccountDetails.execute();

    }



    public class GetAccountDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;


        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(AccountsDetails.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = AccountsDetails.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
//                String _server = "10.0.2.2";
//                String _DB = "CIS_FINREST_KRCMigrate";
//                String _user = "sa";
//                String _pass = "Pa$$w0rd";
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "Select fc.first_name,fc.last_name,fah.account_type,fa.status,fa.balance,openingdate,fa.account_number from CIS_FINREST_CUSTOMER fc inner join CIS_FINREST_ACCOUNT fa on fc.customer_id=fa.customer_id inner join CIS_FINREST_ACCOUNTHEAD fah on fah.account_head=fa.account_type where fc.customer_id='"+customerid+"' and account_number='"+ acc_no +"'";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("first_name", rs.getString("first_name"));
                        datanum.put("last_name",rs.getString("last_name"));
                        datanum.put("account_type",rs.getString("account_type"));
                        datanum.put("account_status",rs.getString("status"));
                        datanum.put("account_opening_date",rs.getString("openingdate"));
                        datanum.put("available_ammount",rs.getString("balance"));
                        datanum.put("account_num",rs.getString("account_number"));
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
                firstname = map.get("first_name");
                lastname = map.get("last_name");
                accounttype = map.get("account_type");
                status = map.get("account_status");
                openingdate = map.get("account_opening_date");
                amount = map.get("available_ammount");
                accountno = map.get("account_num");
            }
            full_name.setText(firstname+" "+lastname);
            accountnum.setText(accountno);
            atype.setText(accounttype);
            Double price_double=Double.parseDouble(amount);
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("", "in"));
            String moneyString = formatter.format(price_double);
            aamount.setText(moneyString);
            astatus.setText(status);
            aopeningdate.setText(openingdate);

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
                final Dialog dialog = new Dialog(AccountsDetails.this);

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