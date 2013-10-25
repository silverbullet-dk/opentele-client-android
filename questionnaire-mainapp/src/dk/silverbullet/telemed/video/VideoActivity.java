package dk.silverbullet.telemed.video;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.video.measurement.TakeMeasurementFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VideoActivity extends Activity implements VideoCallbacks {
	private String guestName;
	private String roomKey;
	private String userPassword;
	private String serverUrl;

	private static final String TAG = "VideoActivity";
	private Fragment videoFragment;
	private String serviceUrl;
    private Ringtone ringtone;
    private TakeMeasurementFragment takeMeasurementFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_parent_layout);

		Intent startingIntent = getIntent();

		guestName = startingIntent.getStringExtra("guestName");
		roomKey = startingIntent.getStringExtra("roomKey");
		userPassword = startingIntent.getStringExtra("userPassword");
		serverUrl = startingIntent.getStringExtra("serverUrl");
		serviceUrl = startingIntent.getStringExtra("serviceUrl");

        playNotificationSound();
    }

    private Fragment getVideoFragment() {
        try {
            Class videoProviderClass = this.getClassLoader().loadClass("dk.silverbullet.telemed.video.VideoProvider");
            Method createVideoFragmentMethod = videoProviderClass.getMethod("createVideoFragment", String.class, String.class, String.class, Activity.class, VideoCallbacks.class);

            return (Fragment) createVideoFragmentMethod.invoke(null, guestName, roomKey, serviceUrl, this, this);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not create video fragment", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create video fragment", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not create video fragment", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not create video fragment", e);
        }
    }

    private void showJoiningProgressUI() {
        ViewGroup parentView = (ViewGroup) findViewById(R.id.video_top_viewGroup);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View joiningCallView = inflater.inflate(R.layout.video_joining_call, parentView, false);

        parentView.removeAllViews();
        parentView.addView(joiningCallView);
    }

    private void stopNotificationSound() {
        ringtone.stop();
    }

    private void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(this, notification);
            ringtone.play();
        } catch (Exception e) {}
    }

	@Override
	public void onBackPressed() {
		//Deliberately empty method. We want to disable the back button.
	}

    public void callAccepted(View v) {
        stopNotificationSound();

        videoFragment = getVideoFragment();
        showJoiningProgressUI();
    }

    public void dismissError(View v) {
        onConferenceEnded();
    }

    public String getServerURL() {
        return serverUrl;
    }

    public String getUsername() {
        return guestName;
    }

    public String getPassword() {
        return userPassword;
    }

    @Override
	public void onConferenceStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup parentView = (ViewGroup) findViewById(R.id.video_top_viewGroup);
                LayoutInflater inflater = (LayoutInflater)VideoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View conferenceInProgressView = inflater.inflate(R.layout.video_conference_in_progress, parentView, false);

                parentView.removeAllViews();
                parentView.addView(conferenceInProgressView);

                FragmentManager fragmentManager = getFragmentManager();

                takeMeasurementFragment = new TakeMeasurementFragment();

                fragmentManager.beginTransaction()
                        .add(R.id.measurements_section, takeMeasurementFragment)
                        .add(R.id.video_section, videoFragment)
                        .addToBackStack(null).commit();
            }
        });
	}

    @Override
    public void onError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater)VideoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View errorView = inflater.inflate(R.layout.video_error_joining, null);
                ViewGroup parentView = (ViewGroup) findViewById(R.id.video_top_viewGroup);

                parentView.removeAllViews();
                parentView.addView(errorView);
            }
        });
    }

	@Override
	public void onConferenceEnded() {
		this.setResult(RESULT_OK);
		finish();
	}
}
