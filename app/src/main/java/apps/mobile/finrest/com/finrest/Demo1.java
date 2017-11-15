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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Demo1 extends AppCompatActivity {


         LinkedHashMap<String, GroupInfo> subjects = new LinkedHashMap<String, GroupInfo>();
         ArrayList<GroupInfo> deptList = new ArrayList<GroupInfo>();

         CustomAdapter listAdapter;
         ExpandableListView simpleExpandableListView;


    ConnectionClass connectionClass;
    ArrayList<CustomerDetailsFunction> users;
    String customerid;
    ArrayList<HashMap<String, String>> profileList1 = new ArrayList<HashMap<String, String>>();
    public ArrayList<String> acc_type;

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_demo1);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#217cc3")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Accounts");
            Window window = this.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Demo1.this.getResources().getColor(R.color.light_blue));
            }
            acc_type = new ArrayList<String>();
        GetAccountDetailsFunction getAccountDetailsFunction = new GetAccountDetailsFunction();
        getAccountDetailsFunction.execute();

        //get reference of the ExpandableListView
        simpleExpandableListView = (ExpandableListView) findViewById(R.id.simpleExpandableListView);
        // create the adapter by passing your ArrayList data
        listAdapter = new CustomAdapter(Demo1.this, deptList);
        // attach the adapter to the expandable list view
        simpleExpandableListView.setAdapter(listAdapter);


            // setOnChildClickListener listener for child row click
            simpleExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    //get the group header
                    GroupInfo headerInfo = deptList.get(groupPosition);
                    //get the child info
                    ChildInfo detailInfo =  headerInfo.getProductList().get(childPosition);
                    //display it or do something with it
                    Toast.makeText(getBaseContext(), " Clicked on :: " + headerInfo.getName()
                            + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });
            // setOnGroupClickListener listener for group heading click
            simpleExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    //get the group header
                    GroupInfo headerInfo = deptList.get(groupPosition);
                    //display it or do something with it
                    Toast.makeText(getBaseContext(), " Header is :: " + headerInfo.getName(),
                            Toast.LENGTH_LONG).show();

                    return false;
                }
            });

        }




    public class GetAccountDetailsFunction extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(Demo1.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = Demo1.this.getSharedPreferences("database_details", MODE_PRIVATE);
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

                    String querycmd = "select ca.account_number, ca.account_type from CIS_FINREST_ACCOUNT ca join CIS_FINREST_ACCOUNTHEAD cah on cah.account_head=ca.account_type where customer_id='0010000002' AND status='Active'";

                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(querycmd);

                    while (rs.next()) {
                        HashMap<String, String> datanum = new HashMap<String, String>();
                        datanum.put("account_number1", rs.getString("account_number"));
                        datanum.put("account_type1", rs.getString("account_type"));

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


            ArrayList<String> acc_no = new ArrayList<String>();
            ArrayList<String> acc_type = new ArrayList<String>();
            int i=0;
            for (HashMap<String, String> map : profileList1) {
                String account_number = map.get("account_number1");
                String account_type = map.get("account_type1");
                acc_no.add(account_number);
                acc_type.add(account_type);
                System.out.println("fname---"+account_number);
                i++;

            }
            for(int j=0;j<acc_no.size();j++){
                System.out.println(j+" =="+acc_no.get(j));

            }

            loadData();



        }
    }


        //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.collapseGroup(i);
        }
    }

    //load some initial data into out list
    private void loadData(){

//        for(int i=0;i<acc_type.size();i++)
//        {
//            addProduct(acc_type.get(i),"ListView");
//
//        }

        addProduct("Android","ListView");
        addProduct("Android","ExpandableListView");
        addProduct("Android","GridView");

        addProduct("Java","PolyMorphism");
        addProduct("Java","Collections");

    }



    //here we maintain our products in various departments
    private int addProduct(String department, String product){

        int groupPosition = 0;

        //check the hash map if the group already exists
        GroupInfo headerInfo = subjects.get(department);
        //add the group if doesn't exists
        if(headerInfo == null){
            headerInfo = new GroupInfo();
            headerInfo.setName(department);
            subjects.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<ChildInfo> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        ChildInfo detailInfo = new ChildInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(product);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }

    public class GroupInfo {

        private String name;
        private ArrayList<ChildInfo> list = new ArrayList<ChildInfo>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<ChildInfo> getProductList() {
            return list;
        }

        public void setProductList(ArrayList<ChildInfo> productList) {
            this.list = productList;
        }

    }

    public class ChildInfo {

        private String sequence = "";
        private String name = "";

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


     class CustomAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<GroupInfo> deptList;

        public CustomAdapter(Context context, ArrayList<GroupInfo> deptList) {
            this.context = context;
            this.deptList = deptList;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
            return productList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View view, ViewGroup parent) {

            ChildInfo detailInfo = (ChildInfo) getChild(groupPosition, childPosition);
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.demo1_list_items, null);
            }

            TextView sequence = (TextView) view.findViewById(R.id.sequence);
            sequence.setText(detailInfo.getSequence().trim() + ". ");
            TextView childItem = (TextView) view.findViewById(R.id.childItem);
            childItem.setText(detailInfo.getName().trim());

            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
            return productList.size();

        }

        @Override
        public Object getGroup(int groupPosition) {
            return deptList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return deptList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isLastChild, View view,
                                 ViewGroup parent) {

            GroupInfo headerInfo = (GroupInfo) getGroup(groupPosition);
            if (view == null) {
                LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.accounts_list_group, null);
            }

            TextView heading = (TextView) view.findViewById(R.id.heading);
            heading.setText(headerInfo.getName().trim());

            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
