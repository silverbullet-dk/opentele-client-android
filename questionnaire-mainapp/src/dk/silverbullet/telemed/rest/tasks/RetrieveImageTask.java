package dk.silverbullet.telemed.rest.tasks;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ImageView;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.client.ServerInformation;

import java.lang.ref.WeakReference;

//Code heavily inspired by Gilles Debunnes example code at
//http://android-developers.blogspot.dk/2010/07/multithreading-for-performance.html
public class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<ServerInformation> serverInformationWeakReference;

    public RetrieveImageTask(ServerInformation serverInformation, ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        serverInformationWeakReference = new WeakReference<ServerInformation>(serverInformation);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(serverInformationWeakReference.get(), params[0]); // params comes from the execute() call: params[0] is the url.
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

        if (bitmap != null && imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                //cache full version via tag:
                imageView.setTag(bitmap);
                //scale before showing:
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, 400, 400), Matrix.ScaleToFit.CENTER);
                Bitmap scaledBitmap = bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                //show in imageView:
                imageView.setImageBitmap(scaledBitmap);
            }
        }
    }
}