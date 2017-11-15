package apps.mobile.finrest.com.finrest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class LoanAccount extends AppCompatActivity {

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
        setContentView(R.layout.loan_account);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Details");
        Window window = LoanAccount.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(LoanAccount.this.getResources().getColor(R.color.light_blue));
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
            loadingDialog = createProgressDialog(LoanAccount.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = LoanAccount.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "Select first_name,last_name,fah.account_type,fc.status,ammount,openingdate,account_number from CIS_FINREST_CUSTOMER fc inner join CIS_FINREST_ACCOUNT fa on fc.customer_id=fa.customer_id inner join CIS_FINREST_ACCOUNTHEAD fah on fah.account_head=fa.account_type where fc.customer_id='0010000001' and account_number='"+ acc_no +"'";

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
            full_name.setText(firstname+" "+lastname);
            accountnum.setText(accountno);
            atype.setText(accounttype);
            aamount.setText(amount);
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

}