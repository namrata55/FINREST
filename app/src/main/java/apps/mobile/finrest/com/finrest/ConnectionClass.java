package apps.mobile.finrest.com.finrest;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by h-pc on 16-Oct-15.
 */
public class ConnectionClass {

   // String ip = "10.0.2.2";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
//    String db = "CIS_FINREST_TEST2v5.7.1";
//    String un = "sa";
//    String password = "Pa$$w0rd";
        String db=null ;
    String ip=null;

    String un =null;
    String password=null;


    @SuppressLint("NewApi")
    public Connection CONN(String _server,String _DB,String _user,String _pass,String _instance) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        System.out.println("In Connection class");
        System.out.println("Server---------" + _server);
        System.out.println("Database---------" + _DB);
        System.out.println("username---------" + _user);
        System.out.println("password---------" + _pass);
        System.out.println("instance---------" + _instance);

        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ":1433/"
                    + _DB + ";instance="+ _instance.trim() +";user=" + _user + ";password="
                    + _pass + ";";


//            ConnURL = "jdbc:jtds:sqlserver://"+ _server +"\\"+_instance+":1433;databaseName="+_DB+";user="+_user+";password="+_pass+";";

//
//            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
//                    + "databaseName=" + _DB + ";user=" + _user + ";password="
//                    + _pass + ";";

            System.out.println("Connection Url---------" + ConnURL);

            conn = DriverManager.getConnection(ConnURL);
            System.out.println("Connection Url---------" + conn);

        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
