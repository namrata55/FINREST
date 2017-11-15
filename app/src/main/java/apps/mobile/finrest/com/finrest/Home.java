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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    String customerid;
    ConnectionClass connectionClass;
    String firstname,lastname,accounttype,amount,status,openingdate,sbaccountno;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    RememberMPIN mpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));

        Window window = Home.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Home.this.getResources().getColor(R.color.light_blue));
        }

        mpin = new RememberMPIN();
        mpin.setMPINPreferences(Home.this, "status", "1");

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

                Intent i = new Intent(Home.this,MiniStatement.class);
                startActivity(i);
            }
        });

        LinearLayout menu_passbook = (LinearLayout) findViewById (R.id.passbook_menu);
        menu_passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,DemoPassbook.class);
                startActivity(i);
            }
        });


        LinearLayout menu_accounts = (LinearLayout) findViewById (R.id.accounts_menu);
        menu_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,Accounts.class);
                startActivity(i);
            }
        });


        LinearLayout menu_user_profile = (LinearLayout) findViewById (R.id.user_profile_menu);
        menu_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,UserProfile.class);
                startActivity(i);
            }
        });


        LinearLayout menu_reset_mpin = (LinearLayout) findViewById (R.id.reset_mpin_menu);
        menu_reset_mpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,ResetMPIN.class);
                startActivity(i);
            }
        });


        LinearLayout menu_branch_details = (LinearLayout) findViewById (R.id.branch_details_menu);
        menu_branch_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,BranchDetails.class);
                startActivity(i);
            }
        });
    }


    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, requesrUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    SliderUtils slideUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        slideUtils.setSliderImageUrl(jsonObject.getString("image_url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sliderImg.add(slideUtils);
                }
                Log.e("URL---", String.valueOf(response));
//                System.out.print("URL---"+String.valueOf(slideUtils));

                viewPageAdapter = new ViewPageAdapter(sliderImg,HomeInternetStatus.this);
                viewPager.setAdapter(viewPageAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        rq.add(jsonArrayRequest);
    }



    public class GetCustomerDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;


        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(Home.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = Home.this.getSharedPreferences("database_details", MODE_PRIVATE);
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

//                    String querycmd = "Select * from CIS_FINREST_CUSTOMER where customer_id='" + customerid + "'";
                    String querycmd = "Select * from CIS_FINREST_CUSTOMER CUST JOIN CIS_FINREST_ACCOUNT ACC ON ACC.customer_id=CUST.customer_id WHERE CUST.customer_id='" + customerid + "' AND ACC.account_type='SD'";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("first_name1", rs.getString("first_name"));
                        datanum.put("last_name1", rs.getString("last_name"));
                        datanum.put("sb_account_no", rs.getString("account_number"));

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
                firstname = map.get("first_name1");
                lastname = map.get("last_name1");
                sbaccountno = map.get("sb_account_no");
            }

            SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
            SharedPreferences.Editor editor = Customer_Details.edit();
            editor.putString("FirstName", firstname);
            editor.putString("LastName", lastname);
            editor.putString("sbaccno", sbaccountno);
            editor.commit();
            System.out.println("Saving Account No==="+sbaccountno);
            getSupportActionBar().setTitle("Welcome "+firstname);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(Home.this);

            View header = navigationView.getHeaderView(0);
            TextView nav_username = (TextView) header.findViewById(R.id.nav_username);
            nav_username.setText("    Hello! "+firstname);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            mpin.setMPINPreferences(Home.this, "status", "0");
            Intent i = new Intent(Home.this,LoginDemo.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

        } else if (id == R.id.contact_us) {
            Intent i1 = new Intent(Home.this,ContactUs.class);
            startActivity(i1);

        } else if (id == R.id.about) {
            Intent i = new Intent(Home.this,AboutUs.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            } else {
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
                final Dialog dialog = new Dialog(Home.this);

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
