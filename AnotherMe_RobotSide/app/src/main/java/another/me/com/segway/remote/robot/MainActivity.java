package another.me.com.segway.remote.robot;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;


import another.me.com.segway.remote.robot.service.BaseService;
import another.me.com.segway.remote.robot.service.ConnectivityService;
import another.me.com.segway.remote.robot.service.HeadService;
import another.me.com.segway.remote.robot.service.StreamVideoService;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private TextView displayIP;
    Button button;



    private StreamVideoService streamVideoService;
    private ConnectivityService connectivityService;
    private HeadService headService;
    private BaseService baseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayIP = (TextView) findViewById(R.id.text1);

        //exit button
        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);

            }//end onClick
        });//end ClickListner


        AssigIP();
        ServicesInitlaztion();


    }// end onCreate


    // Assign a text to the variable Text that contains the Loomo IP address
    private void AssigIP() {
        String ip = getDeviceIp();

        //If there is no WIFI connection
        if (ip.equalsIgnoreCase("0.0.0.0")) {
            Toast.makeText(getApplication(), "No Wi-Fi Connection", Toast.LENGTH_LONG).show();
        } else {
            //If there is WIFI connection
            displayIP.setText("Hello I am Loomo and here is my IP address: " + ip);
        }
    }






// Get the device (Loomo) IP address
    // use WifiManager for the WiFi connectivity
    private String getDeviceIp() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
        return ip;
    }


    // Create and initialize a new object of the class Connictivity Service and StreamViedo
    private void ServicesInitlaztion() {
        this.connectivityService = new ConnectivityService(this);
        this.streamVideoService = new StreamVideoService(getApplicationContext());
        this.headService = new HeadService(getApplicationContext());
        this.baseService = new BaseService(getApplicationContext());
    }


    // destroy the service when it dose not needed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.connectivityService.disconnect();
        this.streamVideoService.disconnect();
        this.headService.disconnect();
        this.baseService.disconnect();


    }
}// end class
