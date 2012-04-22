package csc594.SemesterProject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class RunningService extends Service {
	 
   String tag="TestService";
 
   private NotificationManager mNM;

   // Unique Identification Number for the Notification.
   // We use it on Notification start, and to cancel it.
   private int NOTIFICATION = R.string.local_service_started;
   
   // This is the object that receives interactions from clients.  See
   // RemoteService for a more complete example.
   private final IBinder mBinder = new LocalBinder();
   
   @Override
   public void onStart(Intent intent, int startId) {      
       super.onStart(intent, startId);  
       Toast.makeText(this, "Service started...", Toast.LENGTH_LONG).show();  
   }
       
//    * Class for clients to access.  Because we know this service always
//    * runs in the same process as its clients, we don't need to deal with
//    * IPC.
    
   public class LocalBinder extends Binder {
	   RunningService getService() {
           return null;
       }
   }

   @Override
   public void onCreate() {
	   //Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show(); 
       mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       // Display a notification about us starting.  We put an icon in the status bar.
       showNotification();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
       //Log.i("LocalService", "Received start id " + startId + ": " + intent);
       // We want this service to continue running until it is explicitly
       // stopped, so return sticky.
   	Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
       return START_STICKY;
   }

   @Override
   public void onDestroy() {
       // Cancel the persistent notification.
       mNM.cancel(NOTIFICATION);

       // Tell the user we stopped.
       Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
   }

   @Override
   public IBinder onBind(Intent intent) {
       return null;
   }

//   *
//    * Show a notification while this service is running.
//    
   private void showNotification() {
       // In this sample, we'll use the same text for the ticker and the expanded notification
       CharSequence text = getText(R.string.local_service_started);

       // Set the icon, scrolling text and timestamp
       Notification notification = new Notification(R.drawable.start_marker, text,
               System.currentTimeMillis());

       Intent intent = new Intent(this, MainActivity.class); 
       intent.setAction("android.intent.action.MAIN"); 
       intent.addCategory("android.intent.category.LAUNCHER"); 
       
       // The PendingIntent to launch our activity if the user selects this notification
       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
       		//new Intent(this, MainActivity.class), 0);//BAD - relaunched app!!
    		   intent,0);//much better!
       
       // Set the info for the views that show in the notification panel.
       notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                      text, contentIntent);

       // Send the notification.
       mNM.notify(NOTIFICATION, notification);
   }
}