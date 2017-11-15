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
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MiniStatement extends AppCompatActivity {
    ConnectionClass connectionClass;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    public TableLayout tl;
    TableRow tr;

    String customerid,firstname,lastname,fullname,sbaccountno;
    ArrayList<CustomerDetailsFunction> users;
    String AvailableBalanace;
    TextView date_textview,transaction_remark_textview,sb_acc_no_textview,balance_textview,serial_num_textview;

    TextView availableBalance,customerId,custname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mini_statement);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mini Statement");
        Window window = MiniStatement.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(MiniStatement.this.getResources().getColor(R.color.light_blue));
        }
        tl = (TableLayout) findViewById(R.id.maintable);
        availableBalance = (TextView) findViewById(R.id.available_balance);
        customerId = (TextView) findViewById(R.id.cust_id);
        custname = (TextView) findViewById(R.id.name);
        sb_acc_no_textview = (TextView) findViewById(R.id.sb_acc_no);

        users = new ArrayList<CustomerDetailsFunction>();
        connectionClass = new ConnectionClass();

        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");
        firstname = Customer_Details.getString("FirstName","");
        lastname = Customer_Details.getString("LastName","");
        sbaccountno = Customer_Details.getString("sbaccno","");
        System.out.println("Account no. in mini statement"+sbaccountno);

        fullname = firstname+" "+lastname;
        custname.setText(fullname);
        sb_acc_no_textview.setText(sbaccountno);
        customerId.setText(customerid);
        GetMiniStatement getMiniStatement = new GetMiniStatement();
        getMiniStatement.execute();
    }

    public class GetMiniStatement extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(MiniStatement.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = MiniStatement.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "select TOP 5 * from CIS_FINREST_TRANSECTION where account_number='"+sbaccountno+"' ORDER BY tran_date DESC";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("date", rs.getString("tran_date"));
                        datanum.put("transaction_remark", rs.getString("naration"));
                        datanum.put("transaction_type", rs.getString("transection_type"));
                        datanum.put("dramount", rs.getString("debitamount"));
                        datanum.put("cramount", rs.getString("creditamount"));
                        datanum.put("final_balance", rs.getString("balance"));

                        profileList1.add(datanum);
                    }

                    String querycmd1 = "Select * from CIS_FINREST_TRANSECTION where account_number='"+sbaccountno+"' ORDER BY tran_date ASC";

                    Statement statement1 = con.createStatement();
                    ResultSet rs1 = statement1.executeQuery(querycmd1);

                    while (rs1.next()) {
                         AvailableBalanace = rs1.getString("balance");
                         System.out.println("Available balance"+AvailableBalanace);
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
                CustomerDetailsFunction user = new CustomerDetailsFunction();
                user.setTransaction_date(map.get("date"));
                user.setTransaction_type(map.get("transaction_type"));
                user.setParticular(map.get("transaction_remark"));
                user.setDebit_amount(map.get("dramount"));
                user.setCredit_amount(map.get("cramount"));
                user.setBalance(map.get("final_balance"));
                users.add(user);
            }
            Double price_double=Double.parseDouble(AvailableBalanace);
            Format formatter = NumberFormat.getCurrencyInstance(new Locale("", "in"));
            String moneyString = formatter.format(price_double);
            availableBalance.setText(moneyString);
            addData();
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

    public void addData() {
        addHeader();

        for (Iterator i = users.iterator(); i.hasNext(); ) {

            CustomerDetailsFunction p = (CustomerDetailsFunction) i.next();

            /** Create a TableRow dynamically **/
            tr = new TableRow(this);

            /** Creating a TextView to add to the row **/
            date_textview = new TextView(this);

            String t_date = getconvertdate1(p.getTransaction_date());
            date_textview.setText(t_date);

            date_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            date_textview.setBackground(getResources().getDrawable(
                    R.drawable.rectangular_edittext));
            // InvNumTextView.setPadding(3, 3, 3, 3);
            date_textview.setGravity(Gravity.CENTER);
            LinearLayout L2 = new LinearLayout(this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            L2.addView(date_textview, params2);
            tr.addView((View) L2); // Adding textView to tablerow.

            /** Creating Qty Button **/
            transaction_remark_textview = new TextView(this);
            transaction_remark_textview.setSingleLine(false);
            transaction_remark_textview.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            transaction_remark_textview.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
            String s;

            if(p.getParticular().toString().length()>25){
                String upToNCharacters = p.getParticular().toString().substring(0, Math.min(p.getParticular().toString().length(), 22));
                s = upToNCharacters+"...";
            }
            else {
                s = p.getParticular();
            }
            transaction_remark_textview.setText(s);

            transaction_remark_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            transaction_remark_textview.setBackground(getResources().getDrawable(
                    R.drawable.rectangular_edittext));
            transaction_remark_textview.setGravity(Gravity.CENTER);
            LinearLayout Ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            Ll.addView(transaction_remark_textview, params);
            tr.addView((View) Ll); // Adding textview to tablerow.


            if(p.getTransaction_type().equals("Deposit")){
                balance_textview = new TextView(this);

                Double price_double=Double.parseDouble(p.getCredit_amount());
                Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                String moneyString = formatter.format(price_double);

                balance_textview.setText(moneyString+" Cr.");
                balance_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                balance_textview.setBackground(getResources().getDrawable(
                        R.drawable.rectangular_edittext));
                balance_textview.setGravity(Gravity.RIGHT);
                Ll = new LinearLayout(this);
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(balance_textview, params);
                tr.addView((View) Ll); // Adding textview to tablerow.

                // Add the TableRow to the TableLayout
            }
            else{
                balance_textview = new TextView(this);
                Double price_double=Double.parseDouble(p.getDebit_amount());
                Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                String moneyString = formatter.format(price_double);

                balance_textview.setText(moneyString+" Dr.");
                balance_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                balance_textview.setBackground(getResources().getDrawable(
                        R.drawable.rectangular_edittext));
                balance_textview.setGravity(Gravity.RIGHT);
                Ll = new LinearLayout(this);
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(balance_textview, params);
                tr.addView((View) Ll); // Adding textview to tablerow.
                // Add the TableRow to the TableLayout
            }

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
        //addFooter(TotalBill);
    }

    void addHeader(){
        /** Create a TableRow dynamically **/

        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        date_textview = new TextView(this);
        date_textview.setText("Date");
        date_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
        date_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        date_textview.setTextColor(Color.parseColor("#FFFFFF"));
        date_textview.setGravity(Gravity.CENTER);
        date_textview.setPadding(7,7,7,7);
        date_textview.setGravity(Gravity.CENTER);
        LinearLayout L2 = new LinearLayout(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        L2.addView(date_textview,params2);
        tr.addView((View)L2); // Adding textView to tablerow.

        transaction_remark_textview = new TextView(this);
        transaction_remark_textview.setText("Transaction Remark");
        transaction_remark_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        transaction_remark_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        transaction_remark_textview.setTextColor(Color.parseColor("#FFFFFF"));

        transaction_remark_textview.setGravity(Gravity.CENTER);

        transaction_remark_textview.setPadding(7,7,7,7);;
        transaction_remark_textview.setGravity(Gravity.CENTER);

        LinearLayout L3 = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(transaction_remark_textview,params);
        tr.addView((View)L3); // Adding textview to tablerow.

        /** Creating Qty Button **/
        balance_textview = new TextView(this);
        balance_textview.setText("Amount");
        balance_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        balance_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        balance_textview.setTextColor(Color.parseColor("#FFFFFF"));

        balance_textview.setPadding(7,7,7,7);
        balance_textview.setGravity(Gravity.CENTER);

        LinearLayout L4 = new LinearLayout(this);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L4.addView(balance_textview,params4);
        tr.addView((View)L4); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public String getconvertdate1(String date)
    {
        DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
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
                final Dialog dialog = new Dialog(MiniStatement.this);

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
