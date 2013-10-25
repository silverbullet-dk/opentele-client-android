package dk.silverbullet.telemed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Dummy activity, required to handle reminders properly (i.e. not going to the log-in screen
 * if the user is in the middle of something).
 * 
 * See http://stackoverflow.com/questions/3356095/how-to-bring-android-existing-activity-to-front-via-notification
 */
public class ReminderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainActivity.hasBeenCreated) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
