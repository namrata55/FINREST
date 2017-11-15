package apps.mobile.finrest.com.finrest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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

public class ForgotMPIN extends AppCompatActivity {

    Button proceed;
    String customerid;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    ConnectionClass connectionClass;
    String mpin,mobile_no,first_name;
    EditText cutomer_id_edittext;
    TextView fmpin_login_textview;
    String random_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_mpin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.GINGERBREAD){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Window window = ForgotMPIN.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ForgotMPIN.this.getResources().getColor(R.color.light_blue));
        }

        cutomer_id_edittext = (EditText) findViewById(R.id.fmpin_customer_id);
        fmpin_login_textview = (TextView) findViewById(R.id.fmpin_login);
        connectionClass = new ConnectionClass();

        proceed = (Button) findViewById(R.id.fmpin_proceed);

        fmpin_login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgotMPIN.this,Login.class);
                startActivity(i);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cutomer_id_edittext.getWindowToken(), 0);

                customerid = cutomer_id_edittext.getText().toString();
                if(customerid.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter Customer Id", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    random_otp = ""+((int)(Math.random()*9000)+1000);
                    VerifyCustomerId verifyCustomerId = new VerifyCustomerId();
                    verifyCustomerId.execute();
                }

            }
        });
    }


    public class VerifyCustomerId extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String userid = cutomer_id_edittext.getText().toString();

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(ForgotMPIN.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                SharedPreferences Database_Details = ForgotMPIN.this.getSharedPreferences("database_details", MODE_PRIVATE);
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
                        datanum.put("mpin1", rs.getString("mpin"));
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

                for (HashMap<String, String> map : profileList1) {
                    mobile_no = map.get("mob_no");
                    mpin = map.get("mpin1");
                }
                UpdateOTP uotp = new UpdateOTP();
                uotp.execute();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Invalid Customer Id", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                cutomer_id_edittext.setText("");
                cutomer_id_edittext.requestFocus();
            }
        }
    }

    public class UpdateOTP extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        String userid = cutomer_id_edittext.getText().toString();

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(ForgotMPIN.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = ForgotMPIN.this.getSharedPreferences("database_details", MODE_PRIVATE);
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
                Intent i = new Intent(ForgotMPIN.this, OTPConfirmationForMPIN.class);
                i.putExtra("mobile_no",mobile_no);
                i.putExtra("mpin",mpin);
                startActivity(i);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Invalid Customer Id", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                cutomer_id_edittext.setText("");
                cutomer_id_edittext.requestFocus();
            }
        }
    }

    void SendSms()
    {
        try {
            String recipient = "91"+mobile_no;
            String message = "OTP to get MPIN for your Finrest android app is:"+random_otp;
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

}
