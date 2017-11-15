package apps.mobile.finrest.com.finrest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SocietiesList extends AppCompatActivity {
    ConnectionClass connectionClass;
    ArrayList<CustomerDetailsFunction> users;
    String customerid;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    ListView lv;
    String society_name;
    String society_code;
    String society_database_name;
    String society_ip_address;
    String society_db_username;
    String society_db_password;
    String society_db_instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.societies_list);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Society List");
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(SocietiesList.this.getResources().getColor(R.color.light_blue));
        }
        users = new ArrayList<CustomerDetailsFunction>();
        connectionClass = new ConnectionClass();

        GetSocoetiesFunction getSocoetiesFunction = new GetSocoetiesFunction();
        getSocoetiesFunction.execute();

        lv=(ListView) findViewById(R.id.societies);
    }

    public class GetSocoetiesFunction extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(SocietiesList.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

//                String _server = "103.219.60.220";
//                String _DB = "CIS_SOCIETY_DETAILS";
//                String _user = "sa";
//                String _pass = "all@thebest123";
//                String _instance = "LICENSE";
                String _server = "192.168.1.19";
                String _DB = "CIS_SOCIETY_DETAILS";
                String _user = "sa";
                String _pass = "Pa$$w0rd";
                String _instance = "SQLEXPRESS";
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,_instance);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String querycmd = "select * from SOCIETY_INFORMATION";

                    Statement statement = con.createStatement();
                      ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("societyname", rs.getString("SOCIETY_NAME"));
                        datanum.put("societynameinlist", rs.getString("SOCIETY_CODE")+"-"+rs.getString("SOCIETY_NAME"));
                        datanum.put("societydatabasename", rs.getString("SOCIETY_DATABASE_NAME"));
                        datanum.put("societyipaddress", rs.getString("SOCIETY_IP_ADDRESS"));
                        datanum.put("societycode", rs.getString("SOCIETY_CODE"));
                        datanum.put("societydbusername", rs.getString("SOCIETY_DB_USERNAME"));
                        datanum.put("societydbpassword", rs.getString("SOCIETY_DB_PASSWORD"));
                        datanum.put("societydbinstance", rs.getString("SOCIETY_DB_INSTANCE"));
                        System.out.println("Name==" + rs.getString("SOCIETY_DB_INSTANCE"));

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


            ArrayList<String> societies_names = new ArrayList<String>();
            int i = 0;
            for (HashMap<String, String> map : profileList1) {
                String account_number = map.get("societynameinlist");
                societies_names.add(account_number);

                society_name = (String) map.get("societyname");
                society_code = (String) map.get("societycode");
                society_database_name = (String) map.get("societydatabasename");
                society_ip_address = (String) map.get("societyipaddress");
                society_db_username = (String) map.get("societydbusername");
                society_db_password = (String) map.get("societydbpassword");
                society_db_instance = (String) map.get("societydbinstance");
            }

            lv.setAdapter(new CustomAdapter(SocietiesList.this, societies_names));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    HashMap<String, String> map = profileList1.get(position);

                    String society_name1 = map.get("societyname");
                    String society_code1 = map.get("societycode");
                    String society_database_name1 = map.get("societydatabasename");
                    String society_ip_address1 = map.get("societyipaddress");
                    String society_db_username1 = map.get("societydbusername");
                    String society_db_password1 = map.get("societydbpassword");
                    String society_db_instance1 = map.get("societydbinstance");

                    System.out.println("DB Instance "+society_db_instance1);
                    Intent intent = new Intent();
                    intent.putExtra("Society-Name", society_name1);
                    intent.putExtra("Society-Code", society_code1);
                    intent.putExtra("Society-Database-Name", society_database_name1);
                    intent.putExtra("Society-Ip-Address", society_ip_address1);
                    intent.putExtra("Society-Db-Username", society_db_username1);
                    intent.putExtra("Society-Db-Password", society_db_password1);
                    intent.putExtra("Society-Db-Instance", society_db_instance1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<String> societies_results;
        Context context;
        private  LayoutInflater inflater=null;

        public CustomAdapter(SocietiesList mainActivity, ArrayList<String> societies) {
            // TODO Auto-generated constructor stub
            societies_results=societies;
            context=mainActivity;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return societies_results.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tv_acc_no;
            TextView tv_acc_type;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            CustomAdapter.Holder holder=new CustomAdapter.Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.society_list_items, null);
            holder.tv_acc_no=(TextView) rowView.findViewById(R.id.society_name);

            holder.tv_acc_no.setText(societies_results.get(position));

//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            return rowView;
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
