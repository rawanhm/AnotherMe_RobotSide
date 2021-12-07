package another.me.com.segway.remote.robot.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.segway.robot.algo.Pose2D;
// import com.segway.robot.algo.PoseVLS;
//import com.segway.robot.algo.VLSPoseListener;
//import com.segway.robot.algo.minicontroller.CheckPoint;
//import com.segway.robot.algo.minicontroller.CheckPointStateListener;
//import com.segway.robot.algo.minicontroller.ObstacleStateChangedListener;
import com.segway.robot.sdk.base.bind.ServiceBinder;
import com.segway.robot.sdk.locomotion.sbv.Base;
//import com.segway.robot.sdk.locomotion.sbv.StartVLSListener;

public class BaseService {

    private static final String TAG = "BaseService";

    private Base base = null;
    private Context context;
    private Handler timehandler;
    private Runnable lastStop = null;
   // private RobotCheckpointListener checkpointListener = null;
  //  private RobotVLSListener vlsListener = null;

    private float lastXPosition = 0f;
    private float lastYPosition = 0f;

    public static BaseService instance;

    public static BaseService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LoomoBaseService instance not initialized yet");
        }
        return instance;
    }

    public BaseService(Context context) {
        timehandler = new Handler();
        this.context = context;
       initBase();
        this.instance = this;
    }

    public void restartService() {
        initBase();
   }


/*
    private void setupNavigationVLS() {
        setNavControlMode();
        if (checkpointListener == null) {
            checkpointListener = new RobotCheckpointListener();
            base.setOnCheckPointArrivedListener(checkpointListener);
        }
        Log.d(TAG, "is vls started?" + base.isVLSStarted());

        if (!base.isVLSStarted()) {
            Log.d(TAG, "starting VLS");
            if (vlsListener == null) {
                vlsListener = new RobotVLSListener();
                base.setVLSPoseListener(vlsPoseListener);
            }

            base.startVLS(true, true, vlsListener);
            // Wait for VLS listener to finish, otherwise our moves will throw exceptions
            try {
                while (!base.isVLSStarted()) {
                    Log.d(TAG, "Waiting for VLS to get ready...");
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // enable obstacle avoidance
            Log.d(TAG, "is obstacle avoidance on? " + base.isUltrasonicObstacleAvoidanceEnabled() + " with distance " + base.getUltrasonicObstacleAvoidanceDistance());
            base.setUltrasonicObstacleAvoidanceEnabled(true);
            base.setUltrasonicObstacleAvoidanceDistance(0.5f);
            base.setObstacleStateChangeListener(obstacleStateChangedListener);
        }
    }*/

    // this method call in raw command take two parameter and set it to both  linearVelocity,angularVelocity
    public void move(float linearVelocity, float angularVelocity) {
        setRawControlMode();// call method make sure it is in raw control move
        base.setLinearVelocity(linearVelocity);// set linearVelocity value
        base.setAngularVelocity(angularVelocity);// set angularVelocity value
        //chack last stop
        if (lastStop != null) {
            timehandler.removeCallbacks(lastStop);
            Log.d(TAG, "removed callback to stop");
        }
        // if last stop null set it and make both  linearVelocity,angularVelocity zeros
        lastStop = new Runnable() {
            @Override
            public void run() {
                base.setLinearVelocity(0);
                base.setAngularVelocity(0);
            }
        };

        timehandler.postDelayed(lastStop, 1000);
        Log.d(TAG, "added callback to stop");
    }

    // This method check if it in " Visual Localization System 'VLS' " stop it  then set it to  raw control mode
    private void setRawControlMode() {
        if (base.isVLSStarted()) {
            Log.d(TAG, "Stopping VLS");
            base.stopVLS();
        }

        if (base.getControlMode() != Base.CONTROL_MODE_RAW) {
            Log.d(TAG, "Setting control mode to: RAW");//This is the default base control mode.
            base.setControlMode(Base.CONTROL_MODE_RAW);
        }
    }
/*
    private void setNavControlMode() {
        if (base.getControlMode() != Base.CONTROL_MODE_NAVIGATION) {
            Log.d(TAG, "Setting control mode to: NAVIGATION");
            base.setControlMode(Base.CONTROL_MODE_NAVIGATION);
        }
    }
    */
private void initBase() {
    base = Base.getInstance();
    base.bindService(context, new ServiceBinder.BindStateListener() {
        @Override
        public void onBind() {
            Log.d(TAG, "Base bind successful");
            base.setControlMode(Base.CONTROL_MODE_NAVIGATION);
        }

        @Override
        public void onUnbind(String reason) {
            Log.d(TAG, "Base bind failed");
        }
    });
}
    // calling disconnect()  to stop Base service

    public void disconnect() {
        this.base.unbindService();
    }
}

