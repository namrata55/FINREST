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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePage extends AppCompatActivity {

    String customerid;
    ConnectionClass connectionClass;
    Button refresh;
    String firstname,lastname,accounttype,amount,status,openingdate,accountno;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    RememberMPIN mpin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));

        Window window = HomePage.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(HomePage.this.getResources().getColor(R.color.light_blue));
        }

        mpin = new RememberMPIN();
        mpin.setMPINPreferences(HomePage.this, "status", "1");

        final ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        final Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
        final Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);

        viewFlipper.setInAnimation(animFlipInNext);
        viewFlipper.setOutAnimation(animFlipOutNext);
        viewFlipper.showNext();
        viewFlipper.startFlipping();
        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        GetCustomerDetails getCustomerDetails = new GetCustomerDetails();
        getCustomerDetails.execute();

        LinearLayout menu_mini_statement = (LinearLayout) findViewById (R.id.mini_statement_menu);
        menu_mini_statement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,MiniStatement.class);
                startActivity(i);
            }
        });

        LinearLayout menu_passbook = (LinearLayout) findViewById (R.id.passbook_menu);
        menu_passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,Passbook.class);
                startActivity(i);
            }
        });


        LinearLayout menu_accounts = (LinearLayout) findViewById (R.id.accounts_menu);
        menu_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,Accounts.class);
                startActivity(i);
            }
        });


        LinearLayout menu_user_profile = (LinearLayout) findViewById (R.id.user_profile_menu);
        menu_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,UserProfile.class);
                startActivity(i);
            }
        });


        LinearLayout menu_reset_mpin = (LinearLayout) findViewById (R.id.reset_mpin_menu);
        menu_reset_mpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,ChangeMPIN.class);
                startActivity(i);
            }
        });


        LinearLayout menu_branch_details = (LinearLayout) findViewById (R.id.branch_details_menu);
        menu_branch_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePage.this,BranchDetails.class);
                startActivity(i);
            }
        });

    }

    public class GetCustomerDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;


        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(HomePage.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = HomePage.this.getSharedPreferences("database_details", MODE_PRIVATE);
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

                        String querycmd = "Select first_name,last_name,fah.account_type,fc.status,ammount,openingdate,account_number from CIS_FINREST_CUSTOMER fc inner join CIS_FINREST_ACCOUNT fa on fc.customer_id=fa.customer_id inner join CIS_FINREST_ACCOUNTHEAD fah on fah.account_head=fa.account_type where fc.customer_id='0010000001' and fah.account_type='SAVINGS DEPOSIT'";

                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(querycmd);

                        while (rs.next()) {
                            HashMap<String, String> datanum = new HashMap<String, String>();
                            datanum.put("first_name", rs.getString("first_name"));
                            datanum.put("last_name",rs.getString("last_name"));
                            datanum.put("account_type",rs.getString("account_type"));
                            datanum.put("account_status",rs.getString("status"));
                            datanum.put("account_opening_date",rs.getString("openingdate"));
                            datanum.put("available_ammount",rs.getString("ammount"));
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


            getSupportActionBar().setTitle("Welcome "+firstname);

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                mpin.setMPINPreferences(HomePage.this, "status", "0");
                Intent i = new Intent(HomePage.this,Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
                return true;
            default:
                onBackPressed();
                return  true;

        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {


            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            else{
                doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap once again to exit" +
                    "", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
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
                final Dialog dialog = new Dialog(HomePage.this);

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
