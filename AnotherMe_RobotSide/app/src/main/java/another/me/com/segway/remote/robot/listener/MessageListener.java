package another.me.com.segway.remote.robot.listener;

import android.util.Log;
import android.widget.Toast;

import com.segway.robot.sdk.baseconnectivity.Message;
import com.segway.robot.sdk.baseconnectivity.MessageConnection;

import java.util.Arrays;

import another.me.com.segway.remote.robot.MainActivity;

public class MessageListener implements MessageConnection.MessageListener {

    private static String TAG = "MessageListener";
    private final MainActivity activity;

    private Toast messageToast = null;

    public MessageListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    // if an error occured during message sent
    public void onMessageSentError(Message message, String error) {
        Log.d(TAG, "onMessageSentError: " + error + " during message: " + message);
    }

    @Override
    // if message was sent successfully
    public void onMessageSent(Message message) {
        Log.d(TAG, "onMessageSent: " + message + " was sent successfully!");
    }
    // split message to know what type of service is needed
    private String[] splitMessage(Message message){
        return message.getContent().toString().split(";");
    }

    @Override
    public void onMessageReceived(final Message message) {
        Log.d(TAG, "onMessageReceived: " + message);

        long startTime = System.currentTimeMillis();
        // create command from class MessageCommand
        MessageCommand command = null;
        String[] splitMessage = splitMessage(message);
        String prefix = splitMessage[0];

        Log.i(TAG,  prefix +  "Received");
        try {
            switch (prefix){

                //if the recived service of type vision
                case "vision":
                    command = new StreamVideoCommand(splitMessage);
                    break;

                default:
                    Log.w(TAG, "Unknown message " + Arrays.toString(splitMessage));
            }



            if (command != null) {
                command.execute();
                Log.i(TAG,   command.getClass().toString()+"Executed");
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (messageToast != null) {
                        messageToast.cancel();
                    }
                    messageToast = Toast.makeText(activity, "Got message: " + message.getContent().toString(), Toast.LENGTH_SHORT);
                    messageToast.show();
                }
            });


        } catch (Exception e) {
            Log.w(TAG, "An exception occurred", e);
        }

    }// end message recevied

}// end calss
