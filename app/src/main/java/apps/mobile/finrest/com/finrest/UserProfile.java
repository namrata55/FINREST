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
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class UserProfile extends AppCompatActivity {
    ConnectionClass connectionClass;
    String customerid;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    TextView fullnameTextView,emailTextView,panTextView,aadhaarTextView,mobileTextView,addressTextView,dobTextView;
    ImageView photoImageView;
    String firstname,midname,lastname,email,mobile,pan,aadhaar,address,photo,area,landmark,state,district,city,dateofbirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_demo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");

        Window window = UserProfile.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(UserProfile.this.getResources().getColor(R.color.light_blue));
        }

        fullnameTextView = (TextView) findViewById(R.id.fullname);
        emailTextView = (TextView) findViewById(R.id.email);
        panTextView = (TextView) findViewById(R.id.pan);
        aadhaarTextView = (TextView) findViewById(R.id.aadhaar);
        mobileTextView = (TextView) findViewById(R.id.mobile);
        addressTextView = (TextView) findViewById(R.id.address);
//        photoImageView = (ImageView) findViewById(R.id.customer_photo);
        dobTextView = (TextView) findViewById(R.id.dob);

        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        GetUserProfileDetails getUserProfileDetails = new GetUserProfileDetails();
        getUserProfileDetails.execute();

    }


    public class GetUserProfileDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(UserProfile.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = UserProfile.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
////                String _server = "192.168.1.19";
//                String _server = "10.0.2.2";
//                String _DB = "CIS_FINREST_TEST2v5.7.1";
//                String _user = "sa";
//                String _pass = "Pa$$w0rd";
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "Select * from CIS_FINREST_CUSTOMER where customer_id='"+ customerid +"'";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("first_name1", rs.getString("first_name"));
                        datanum.put("last_name1", rs.getString("last_name"));
                        datanum.put("mid_name1", rs.getString("middle_name"));
                        datanum.put("mobile_no1", rs.getString("mob_no"));
                        datanum.put("email_id1", rs.getString("email_id"));
                        datanum.put("pan_no1", rs.getString("pan"));
                        datanum.put("aadhaar_no1", rs.getString("uid"));
                        datanum.put("photo1", rs.getString("photo"));
                        datanum.put("date_of_birth", rs.getString("dob"));
                        datanum.put("current_address", rs.getString("cur_address"));
                        datanum.put("current_area", rs.getString("cadd_area"));
                        datanum.put("current_landmark", rs.getString("cadd_landmark"));
                        datanum.put("current_city", rs.getString("cadd_city"));
                        datanum.put("current_district", rs.getString("cadd_district"));
                        datanum.put("current_state", rs.getString("cadd_state"));
                        datanum.put("user_photo", rs.getString("photo"));
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
                midname = map.get("mid_name1");
                dateofbirth = map.get("date_of_birth");
                lastname = map.get("last_name1");
                mobile = map.get("mobile_no1");
                email = map.get("email_id1");
                pan = map.get("pan_no1");
                pan = map.get("pan_no1");
                aadhaar = map.get("aadhaar_no1");
                address = map.get("current_address");
                area = map.get("current_area");
                landmark = map.get("current_landmark");
                city = map.get("current_city");
                district = map.get("current_district");
                state = map.get("current_state");
                photo = map.get("user_photo");
            }

            String fullname = firstname+" "+midname+" "+lastname+"";
            fullnameTextView.setText(fullname);
            mobileTextView.setText(mobile);
            emailTextView.setText(email);
            panTextView.setText(pan);
            aadhaarTextView.setText(aadhaar);
            addressTextView.setText(address+","+area+","+landmark+","+city+"\n"+"District-"+district+",State-"+state);
            String con_dob = parseTodaysDate(dateofbirth);
            dobTextView.setText(con_dob);
//            System.out.println("Photo address"+photo);
//            byte[] decodeString = Base64.decode(photo, Base64.DEFAULT);
//            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
//            photoImageView.setImageBitmap(decodebitmap);

        }
    }

    public String getconvertdate1(String date)
    {
        DateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date parsed = new Date();
        try
        {
            parsed = inputFormat.parse(date);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String outputText = outputFormat.format(parsed);
        return outputText;
    }

    public static String parseTodaysDate(String time) {

        String inputPattern = "MM/dd/yyyy";

        String outputPattern = "dd/MM/yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
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
                final Dialog dialog = new Dialog(UserProfile.this);

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
