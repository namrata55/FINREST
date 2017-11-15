package apps.mobile.finrest.com.finrest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Locale;
import java.util.TimeZone;

public class PassbookDemo extends AppCompatActivity {

    ConnectionClass connectionClass;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    public TableLayout tl;
    TableRow tr;

    String customerid;
    ArrayList<CustomerDetailsFunction> users;
    TextView date_textview,particulars_textview,debit_textview,credit_textview,balance_textview;
    public String ClosingBalance;
    Button nextButton,prvButton;
    public int totalrowperpage=10;

    public int indexRowStart = 0;
    public int indexRowEnd;
    public int TotalRows = 0;

    public int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook_demo);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Passbook");

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(PassbookDemo.this.getResources().getColor(R.color.light_blue));
        }

        tl = (TableLayout) findViewById(R.id.maintable);

        users = new ArrayList<CustomerDetailsFunction>();
        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");

        nextButton = (Button) findViewById(R.id.btnNext);
        prvButton = (Button) findViewById(R.id.btnPre);

        indexRowEnd = totalrowperpage;

        GetPassbook getPassbook = new GetPassbook();
        getPassbook.execute();


        // Disabled Button Next

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tl.removeAllViews();
//                indexRowStart = indexRowEnd;
//                indexRowEnd = indexRowStart+totalrowperpage;
                currentPage =currentPage+1;
                indexRowStart = ((totalrowperpage*currentPage)-totalrowperpage);
                indexRowEnd = indexRowStart + totalrowperpage;

                if(indexRowEnd>=users.size()){
                    indexRowEnd = users.size() ;

                }
                else{
                    indexRowEnd = indexRowStart + totalrowperpage;
                }
                System.out.println("NEXT");
                System.out.println("size"+users.size());
                System.out.println("start index"+indexRowStart);
                System.out.println("end index"+indexRowEnd);
                addData();

            }
        });

        prvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tl.removeAllViews();
                currentPage = currentPage - 1;

                indexRowStart = ((totalrowperpage*currentPage)-totalrowperpage);
                indexRowEnd = indexRowStart + totalrowperpage;

                if(indexRowEnd>=users.size()){
                    indexRowEnd = users.size() ;
                }
                else{
                    indexRowEnd = indexRowStart + totalrowperpage;
                }
                System.out.println("PREVIOUS");
                System.out.println("size"+users.size());
                System.out.println("start index"+indexRowStart);
                System.out.println("end index"+indexRowEnd);
//                indexRowEnd = indexRowStart;
//                indexRowStart = indexRowEnd-totalrowperpage;
                addData();
            }
        });

    }

    public class GetPassbook extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(PassbookDemo.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = PassbookDemo.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
//                String _server = "192.168.1.19";
//                String _server = "10.0.2.2";
//                String _DB = "CIS_FINREST_TEST2v5.7.1";
//                String _user = "sa";
//                String _pass = "Pa$$w0rd";

                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "Select * from CIS_FINREST_TRANSECTION where customer_id='"+customerid+"' and GLCode='7' ORDER BY tran_date DESC";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("date", rs.getString("tran_date"));
                        datanum.put("particular", rs.getString("naration"));
                        datanum.put("transaction_type", rs.getString("transection_type"));
                        datanum.put("final_balance", rs.getString("balance"));
                        datanum.put("debit_amount", rs.getString("debitamount"));
                        datanum.put("credit_amount", rs.getString("creditamount"));
                        profileList1.add(datanum);
                    }

                    String querycmd1 = "Select * from CIS_FINREST_TRANSECTION where customer_id='" + customerid + "' and GLCode='7' ORDER BY tran_date ASC";

                    Statement statement1 = con.createStatement();
                    ResultSet rs1 = statement1.executeQuery(querycmd1);

                    while (rs1.next()) {
                        ClosingBalance = rs1.getString("balance");
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
                user.setParticular(map.get("particular"));
                user.setDebit_amount(map.get("debit_amount"));
                user.setCredit_amount(map.get("credit_amount"));
                user.setBalance(map.get("final_balance"));
                users.add(user);
            }
            TotalRows = users.size();
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

        TotalRows = users.size();

        int TotalPage = 0;
        if(TotalRows<=totalrowperpage)
        {
            TotalPage =1;
        }
        else if((TotalRows % totalrowperpage)==0)
        {
            TotalPage =(TotalRows/totalrowperpage) ;
        }
        else
        {
            TotalPage =(TotalRows/totalrowperpage)+1;
            TotalPage = (int)TotalPage;
        }

        // Disabled Button Next
        if(currentPage >= TotalPage)
        {
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        }
        else
        {
            nextButton.setEnabled(true);
            nextButton.setBackgroundResource(R.drawable.button_blue_rounded_green_border);

        }

        // Disabled Button Previos
        if(currentPage <= 1)
        {
            prvButton.setEnabled(false);
            prvButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        }
        else
        {
            prvButton.setEnabled(true);
            prvButton.setBackgroundResource(R.drawable.button_blue_rounded_green_border);
        }


        if(indexRowStart <= 0)
        {
            prvButton.setEnabled(false);
        }
        else
        {
            prvButton.setEnabled(true);
        }

        if(indexRowEnd>=users.size())
        {
            nextButton.setEnabled(false);
        }
        else
        {
            nextButton.setEnabled(true);
        }
        addHeader();
        addClosingBalance();
//        for (Iterator i = users.iterator(); i.hasNext(); ) {
            for (int i1 = indexRowStart; i1 < indexRowEnd; i1++) {
                CustomerDetailsFunction p = (CustomerDetailsFunction) users.get(i1);

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
                date_textview.setPadding(3, 3, 3, 3);
                date_textview.setGravity(Gravity.CENTER);
                LinearLayout Ll = new LinearLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(date_textview, params);
                tr.addView((View) Ll); // Adding textView to tablerow.

                /** Creating Qty Button **/
                particulars_textview = new TextView(this);
                particulars_textview.setText(p.getParticular());
                particulars_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                particulars_textview.setBackground(getResources().getDrawable(
                        R.drawable.rectangular_edittext));
                particulars_textview.setGravity(Gravity.CENTER);
                particulars_textview.setPadding(3, 3, 3, 3);
                Ll = new LinearLayout(this);
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(particulars_textview, params);
                tr.addView((View) Ll); // Adding textview to tablerow.

                if (p.getTransaction_type().equals("Deposit")) {
                    credit_textview = new TextView(this);

                    Double price_double = Double.parseDouble(p.getCredit_amount());
                    Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String moneyString = formatter.format(price_double);

                    credit_textview.setText(moneyString);
                    credit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    credit_textview.setBackground(getResources().getDrawable(
                            R.drawable.rectangular_edittext));
                    credit_textview.setGravity(Gravity.RIGHT);
                    credit_textview.setPadding(3, 3, 3, 3);

                    Ll = new LinearLayout(this);
                    params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    Ll.addView(credit_textview, params);
                    tr.addView((View) Ll); // Adding textview to tablerow.

                    debit_textview = new TextView(this);
                    debit_textview.setText("");
                    debit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    debit_textview.setBackground(getResources().getDrawable(
                            R.drawable.rectangular_edittext));
                    debit_textview.setGravity(Gravity.CENTER);
                    Ll = new LinearLayout(this);
                    params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    Ll.addView(debit_textview, params);
                    tr.addView((View) Ll); // Adding textview to tablerow.
                } else {
                    credit_textview = new TextView(this);

                    credit_textview.setText("");
                    credit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    credit_textview.setBackground(getResources().getDrawable(
                            R.drawable.rectangular_edittext));
                    credit_textview.setGravity(Gravity.RIGHT);
                    credit_textview.setPadding(3, 3, 3, 3);
                    Ll = new LinearLayout(this);
                    params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    Ll.addView(credit_textview, params);
                    tr.addView((View) Ll); // Adding textview to tablerow.


                    Double price_double = Double.parseDouble(p.getDebit_amount());
                    Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String moneyString = formatter.format(price_double);

//                String moneyString = NumberFormat.getInstance().format(price_double);
//                textView2.setText(String.format("%.2f", result));

                    debit_textview = new TextView(this);
                    debit_textview.setText(moneyString);
                    debit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    debit_textview.setBackground(getResources().getDrawable(
                            R.drawable.rectangular_edittext));
                    debit_textview.setGravity(Gravity.RIGHT);
                    debit_textview.setPadding(3, 3, 3, 3);
                    Ll = new LinearLayout(this);
                    params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    Ll.addView(debit_textview, params);
                    tr.addView((View) Ll); // Adding textview to tablerow.
                }

                Double price_double = Double.parseDouble(p.getBalance());
                Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                String moneyString = formatter.format(price_double);

                balance_textview = new TextView(this);
                balance_textview.setText(moneyString);
                balance_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                balance_textview.setBackground(getResources().getDrawable(
                        R.drawable.rectangular_edittext));
                balance_textview.setGravity(Gravity.RIGHT);
                balance_textview.setPadding(3, 3, 3, 3);
                Ll = new LinearLayout(this);
                params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                Ll.addView(balance_textview, params);
                tr.addView((View) Ll); // Adding textview to tablerow.

                // Add the TableRow to the TableLayout
                tl.addView(tr, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
        }

        //addFooter(TotalBill);
    }

    public String getconvertdate1(String date)
    {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    void addClosingBalance(){
        /** Create a TableRow dynamically **/

        tr = new TableRow(this);

        /** Creating a TextView to add to the row **/
        date_textview = new TextView(this);
        date_textview.setText("");

        date_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        date_textview.setBackground(getResources().getDrawable(
                R.drawable.rectangular_edittext));
        // InvNumTextView.setPadding(3, 3, 3, 3);
        date_textview.setGravity(Gravity.CENTER);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(date_textview, params);
        tr.addView((View) Ll); // Adding textView to tablerow.

        /** Creating Qty Button **/
        particulars_textview = new TextView(this);
        particulars_textview.setText("Closing Balance");
        particulars_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        particulars_textview.setBackground(getResources().getDrawable(
                R.drawable.rectangular_edittext));
        particulars_textview.setGravity(Gravity.CENTER);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(particulars_textview, params);
        tr.addView((View) Ll); // Adding textview to tablerow.

        credit_textview = new TextView(this);
        credit_textview.setText("");
        credit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        credit_textview.setBackground(getResources().getDrawable(
                R.drawable.rectangular_edittext));
        credit_textview.setGravity(Gravity.RIGHT);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(credit_textview, params);
        tr.addView((View) Ll); // Adding textview to tablerow.

        debit_textview = new TextView(this);
        debit_textview.setText("");
        debit_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        debit_textview.setBackground(getResources().getDrawable(
                R.drawable.rectangular_edittext));
        debit_textview.setGravity(Gravity.CENTER);
        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(debit_textview, params);
        tr.addView((View) Ll); // Adding textview to tablerow.

        Double price_double=Double.parseDouble(ClosingBalance);
        Format formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        String moneyString = formatter.format(price_double);

        balance_textview = new TextView(this);
        balance_textview.setText(moneyString);
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
        tl.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

    }

    void addHeader(){
        /** Create a TableRow dynamically **/
        tr = new TableRow(this);
        /** Creating a TextView to add to the row **/
        date_textview = new TextView(this);
        date_textview.setText("Date");
        date_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        date_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        date_textview.setTextColor(Color.parseColor("#FFFFFF"));
        date_textview.setGravity(Gravity.CENTER);
        date_textview.setPadding(7,7,7,7);
        date_textview.setGravity(Gravity.CENTER);
        date_textview.setWidth(30);
        LinearLayout Ll = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        Ll.addView(date_textview,params);
        tr.addView((View)Ll); // Adding textView to tablerow.

        particulars_textview = new TextView(this);
        particulars_textview.setText("Particular");
        particulars_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        particulars_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        particulars_textview.setTextColor(Color.parseColor("#FFFFFF"));

        particulars_textview.setGravity(Gravity.CENTER);
        particulars_textview.setWidth(30);

        particulars_textview.setPadding(7,7,7,7);;
        particulars_textview.setGravity(Gravity.CENTER);

        LinearLayout L3 = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        L3.addView(particulars_textview,params);
        tr.addView((View)L3); // Adding textview to tablerow.

        debit_textview = new TextView(this);
        debit_textview.setText("Withdrawal");
        debit_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        debit_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        debit_textview.setTextColor(Color.parseColor("#FFFFFF"));

        debit_textview.setPadding(7,7,7,7);
        debit_textview.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(debit_textview,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout

        /** Creating Qty Button **/
        credit_textview = new TextView(this);
        credit_textview.setText("Deposit");
        credit_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        credit_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        credit_textview.setTextColor(Color.parseColor("#FFFFFF"));

        credit_textview.setPadding(7,7,7,7);
        credit_textview.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(credit_textview,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        /** Creating Qty Button **/
        balance_textview = new TextView(this);
        balance_textview.setText("Balance");
        balance_textview.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        balance_textview.setBackgroundColor(Color.parseColor("#6ec633"));
        balance_textview.setTextColor(Color.parseColor("#FFFFFF"));

        balance_textview.setPadding(7,7,7,7);
        balance_textview.setGravity(Gravity.CENTER);

        Ll = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        Ll.addView(balance_textview,params);
        tr.addView((View)Ll); // Adding textview to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }


}
