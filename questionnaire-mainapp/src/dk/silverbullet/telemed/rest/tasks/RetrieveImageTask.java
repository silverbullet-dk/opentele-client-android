package dk.silverbullet.telemed.rest.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.content.Context;

import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import dk.silverbullet.telemed.utils.Util;

import java.lang.ref.WeakReference;

//Code heavily inspired by Gilles Debunnes example code at
//http://android-developers.blogspot.dk/2010/07/multithreading-for-performance.html
public class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = Util.getTag(RetrieveImageTask.class);

    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<ServerInformation> serverInformationWeakReference;
    private final Context context;

    public RetrieveImageTask(Context context, ServerInformation serverInformation, ImageView imageView) {
        this.context = context;
        imageViewReference = new WeakReference<ImageView>(imageView);
        serverInformationWeakReference = new WeakReference<ServerInformation>(serverInformation);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        return downloadBitmap(serverInformationWeakReference.get(), url);
    }

    protected Bitmap downloadBitmap(ServerInformation serverInformation, String url) {
        Bitmap bm = null;

        try {
            bm = RestClient.getImage(serverInformation, url);
        } catch (RestException e) {
            e.printStackTrace();
        }

        return bm;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (bitmap == null) {
            Log.d(TAG, "Bitmap was null");
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.retrieving_image_error);
        }

        ImageView imageView = imageViewReference.get();
        if (imageView == null) {
            Log.e(TAG, "ImageView was null");
            return;
        }

        //cache full version via tag:
        imageView.setTag(bitmap);
        //scale before showing:
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, 400, 400), Matrix.ScaleToFit.CENTER);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

        //show in imageView:
        imageView.setImageBitmap(scaledBitmap);
    }
}