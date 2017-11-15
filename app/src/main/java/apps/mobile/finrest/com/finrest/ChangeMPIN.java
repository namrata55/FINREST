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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class ChangeMPIN extends AppCompatActivity {

    EditText oldmpin_edittext1,oldmpin_edittext2,oldmpin_edittext3,oldmpin_edittext4;
    EditText newmpin_edittext1,newmpin_edittext2,newmpin_edittext3,newmpin_edittext4;
    EditText cnfrmnewmpin_edittext1,cnfrmnewmpin_edittext2,cnfrmnewmpin_edittext3,cnfrmnewmpin_edittext4;
    Button submit;
    String oldmpin;
    String newmpin;
    String newcnfrmmpin,customerid;
    ConnectionClass connectionClass;

    String OLDMPIN,NEWMPIN,CNFRMNEWMPIN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_mpin);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change MPIN");
        Window window = ChangeMPIN.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ChangeMPIN.this.getResources().getColor(R.color.light_blue));
        }


        oldmpin_edittext1 = (EditText) findViewById(R.id.old_mpin1);
        oldmpin_edittext2 = (EditText) findViewById(R.id.old_mpin2);
        oldmpin_edittext3 = (EditText) findViewById(R.id.old_mpin3);
        oldmpin_edittext4 = (EditText) findViewById(R.id.old_mpin4);

        newmpin_edittext1 = (EditText) findViewById(R.id.new_mpin1);
        newmpin_edittext2 = (EditText) findViewById(R.id.new_mpin2);
        newmpin_edittext3 = (EditText) findViewById(R.id.new_mpin3);
        newmpin_edittext4 = (EditText) findViewById(R.id.new_mpin4);

        cnfrmnewmpin_edittext1 = (EditText) findViewById(R.id.cnfrm_mpin1);
        cnfrmnewmpin_edittext2 = (EditText) findViewById(R.id.cnfrm_mpin2);
        cnfrmnewmpin_edittext3 = (EditText) findViewById(R.id.cnfrm_mpin3);
        cnfrmnewmpin_edittext4 = (EditText) findViewById(R.id.cnfrm_mpin4);

        oldmpin_edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    oldmpin_edittext2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                oldmpin_edittext1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        oldmpin_edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    oldmpin_edittext3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                oldmpin_edittext2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        oldmpin_edittext3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    oldmpin_edittext4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        oldmpin_edittext4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    newmpin_edittext1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        newmpin_edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    newmpin_edittext2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newmpin_edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    newmpin_edittext3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newmpin_edittext3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    newmpin_edittext4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newmpin_edittext4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    cnfrmnewmpin_edittext1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cnfrmnewmpin_edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    cnfrmnewmpin_edittext2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cnfrmnewmpin_edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    cnfrmnewmpin_edittext3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cnfrmnewmpin_edittext3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    cnfrmnewmpin_edittext4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        cnfrmnewmpin_edittext4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cnfrmnewmpin_edittext4.getWindowToken(), 0);

                    validation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id","");
    }
    void validation()
    {
        String oldmpin1 = oldmpin_edittext1.getText().toString();
        String oldmpin2 = oldmpin_edittext2.getText().toString();
        String oldmpin3 = oldmpin_edittext3.getText().toString();
        String oldmpin4 = oldmpin_edittext4.getText().toString();

        OLDMPIN = "" + oldmpin1 + "" + oldmpin2 + "" + oldmpin3 + "" + oldmpin4 + "";

        String newmpin1 = newmpin_edittext1.getText().toString();
        String newmpin2 = newmpin_edittext2.getText().toString();
        String newmpin3 = newmpin_edittext3.getText().toString();
        String newmpin4 = newmpin_edittext4.getText().toString();

        NEWMPIN = "" + newmpin1 + "" + newmpin2 + "" + newmpin3 + "" + newmpin4 + "";

        String cnfrmmpin1 = cnfrmnewmpin_edittext1.getText().toString();
        String cnfrmmpin2 = cnfrmnewmpin_edittext2.getText().toString();
        String cnfrmmpin3 = cnfrmnewmpin_edittext3.getText().toString();
        String cnfrmmpin4 = cnfrmnewmpin_edittext4.getText().toString();

        CNFRMNEWMPIN = "" + cnfrmmpin1 + "" + cnfrmmpin2 + "" + cnfrmmpin3 + "" + cnfrmmpin4 + "";
        System.out.print("oldmpin====="+OLDMPIN+"   newMpin======"+NEWMPIN+"   cnfrmmpin====="+CNFRMNEWMPIN);

        if(NEWMPIN.equals(OLDMPIN)){
            Toast toast = Toast.makeText(getApplicationContext(),"New MPIN and old MPIN are same ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            oldmpin_edittext1.setText("");
            oldmpin_edittext2.setText("");
            oldmpin_edittext3.setText("");
            oldmpin_edittext4.setText("");

            newmpin_edittext1.setText("");
            newmpin_edittext2.setText("");
            newmpin_edittext3.setText("");
            newmpin_edittext4.setText("");

            cnfrmnewmpin_edittext1.setText("");
            cnfrmnewmpin_edittext2.setText("");
            cnfrmnewmpin_edittext3.setText("");
            cnfrmnewmpin_edittext4.setText("");

            oldmpin_edittext1.requestFocus();
        }
        else if(!NEWMPIN.equals(CNFRMNEWMPIN))
        {
            Toast toast = Toast.makeText(getApplicationContext(),"New MPIN and cnfirm MPIN are not same ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            newmpin_edittext1.setText("");
            newmpin_edittext2.setText("");
            newmpin_edittext3.setText("");
            newmpin_edittext4.setText("");

            cnfrmnewmpin_edittext1.setText("");
            cnfrmnewmpin_edittext2.setText("");
            cnfrmnewmpin_edittext3.setText("");
            cnfrmnewmpin_edittext4.setText("");

            newmpin_edittext1.requestFocus();
        }
        else if(NEWMPIN.equals(CNFRMNEWMPIN))
        {
            UpdateMPIN updatempn = new UpdateMPIN();
            updatempn.execute();
        }
    }

    public class UpdateMPIN extends AsyncTask<String,String,String>
    {

        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(ChangeMPIN.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = ChangeMPIN.this.getSharedPreferences("database_details", MODE_PRIVATE);
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
                    String query = "update CIS_FINREST_CUSTOMER set mpin='"+ NEWMPIN +"'where customer_id='" + customerid + "' and mpin='" + OLDMPIN + "'";
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
                Toast toast = Toast.makeText(getApplicationContext(),"MPIN has been reset successfully", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent i = new Intent(ChangeMPIN.this, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);

            }
            else{

                Toast toast = Toast.makeText(getApplicationContext(),"Invalid old MPIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                oldmpin_edittext1.setText("");
                oldmpin_edittext2.setText("");
                oldmpin_edittext3.setText("");
                oldmpin_edittext4.setText("");

                newmpin_edittext1.setText("");
                newmpin_edittext2.setText("");
                newmpin_edittext3.setText("");
                newmpin_edittext4.setText("");

                cnfrmnewmpin_edittext1.setText("");
                cnfrmnewmpin_edittext2.setText("");
                cnfrmnewmpin_edittext3.setText("");
                cnfrmnewmpin_edittext4.setText("");

                oldmpin_edittext1.requestFocus();

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


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK ) {

            if(getCurrentFocus()== cnfrmnewmpin_edittext4){
                cnfrmnewmpin_edittext4.setText("");
                cnfrmnewmpin_edittext3.requestFocus();
            }
            else   if(getCurrentFocus()==cnfrmnewmpin_edittext3){
                cnfrmnewmpin_edittext3.setText("");
                cnfrmnewmpin_edittext2.requestFocus();
            }
            else   if(getCurrentFocus()==cnfrmnewmpin_edittext2){
                cnfrmnewmpin_edittext2.setText("");
                cnfrmnewmpin_edittext1.requestFocus();
            }
            else if(getCurrentFocus()==cnfrmnewmpin_edittext1)
            {
                cnfrmnewmpin_edittext1.setText("");
                newmpin_edittext4.requestFocus();
            }
        }
        return super.onKeyUp(keyCode, event);
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
                final Dialog dialog = new Dialog(ChangeMPIN.this);

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
