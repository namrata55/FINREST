package apps.mobile.finrest.com.finrest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class Splash extends AppCompatActivity {

    RememberMPIN rmpin;
    SessionManager registration_completed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Window window = Splash.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Splash.this.getResources().getColor(R.color.light_blue));
        }
        registration_completed = new SessionManager();
        rmpin = new RememberMPIN();

        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(2000);

                    String registration_status = registration_completed.getPreferences(Splash.this,"status");
                    String status_rmpin = rmpin.getMPINPreferences(Splash.this,"status");

                    if (registration_status.equals("1")){
//                        if (status_rmpin.equals("1")){
//                            Intent i = new Intent(Splash.this,Home.class);
//                            startActivity(i);
//                            finish();
//                        }
//                        else{
                            Intent i = new Intent(Splash.this,LoginDemo.class);
                            startActivity(i);
                            finish();
//                        }

                    }else{
                        Intent i = new Intent(Splash.this,Register.class);
                        startActivity(i);
                        finish();
                    }

                } catch (Exception e) {

                }
            }
        };
        // start thread
        background.start();
    }

}
