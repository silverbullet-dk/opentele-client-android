package dk.silverbullet.telemed.questionnaire.element;

import android.content.Context;
import android.graphics.*;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.rest.tasks.RetrieveImageTask;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;


public class HelpTextElement extends Element {

    private static final String TAG = Util.getTag(HelpTextElement.class);

    @Expose private String text;
    @Expose private String imageFile;

    private Button button;
    private RelativeLayout layout;

    public HelpTextElement(final IONode node) {
        super(node);
    }

    public HelpTextElement(final IONode node, String text) {
        this(node);
        setText(text);
    }

    public HelpTextElement(final IONode node, String text, String imageFile) {
        this(node);
        setText(text);
        setImageFile(imageFile);
    }

    public void hideButton() {
        button.setVisibility(View.GONE);
    }

    public void showButton() {
        button.setVisibility(View.VISIBLE);
    }

    @Override
    public View getView() {
        if (layout == null) {
            final Context context = getQuestionnaire().getContext();
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Create the button through which help window can be launched:
            layout = (RelativeLayout) inflater.inflate(R.layout.help_button_element, null);
            button = (Button) layout.findViewById(R.id.button);
            button.setGravity(Gravity.RIGHT);
            button.setGravity(Gravity.TOP);

            final Point displaySize = new Point();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getSize(displaySize);

            final View popupView = inflater.inflate(R.layout.help_popup, null);

            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.setFocusable(true);

            TextView tvHelpText = (TextView)popupView.findViewById(R.id.help_text);
            tvHelpText.setText(text);
            tvHelpText.setMovementMethod(new ScrollingMovementMethod());
            //avoid dimming of text during scroll:
            tvHelpText.setClickable(false);
            tvHelpText.setLongClickable(false);

            final ImageView ivHelpImage = (ImageView)popupView.findViewById(R.id.help_image);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "*CLICK HELP BUTTON*");

                    //Setup onClick on ImageView to have ability to show image full screen
                    ivHelpImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Object tagObject = ivHelpImage.getTag();
                            if (tagObject != null && tagObject instanceof Bitmap) {
                                Log.d(TAG, " click: there is an image");

                                Bitmap fullImage = (Bitmap)tagObject;
                                View imgPopupView = inflater.inflate(R.layout.help_image_popup, null);
                                final PopupWindow imgPopupWindow = new PopupWindow(
                                        imgPopupView,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                imgPopupWindow.setFocusable(true);

                                Button btnImgDismiss = (Button)imgPopupView.findViewById(R.id.dismiss);
                                btnImgDismiss.setOnClickListener(new Button.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        System.gc(); //Generally not good practice, BUT Android is notoriously bad at doing necessary GC in connection w. bitmaps
                                        imgPopupWindow.dismiss();
                                    }
                                });

                                final ImageView ivFullHelpImage = (ImageView)imgPopupView.findViewById(R.id.full_help_image);
                                ivFullHelpImage.setImageBitmap(fullImage);

                                imgPopupWindow.showAtLocation(layout, Gravity.CENTER, 20, 20);

                                //Make sure we are full screen:
                                imgPopupWindow.update(displaySize.x, displaySize.y);
                            }
                            else
                                Log.d(TAG, " click: there is no image");
                        }
                    });

                    Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                    btnDismiss.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ivHelpImage.setTag(null); //Avoid mem. leak (see http://blog.mobile-j.de/2011/07/weird-problem-using-viewsettagintobject.html)
                            System.gc(); //Generally not good practice, BUT Android is notoriously bad at doing necessary GC in connection w. bitmaps
                            popupWindow.dismiss();
                        }
                    });

                    popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

                    if (imageFile != null && !imageFile.isEmpty()) {
                        Bitmap retrievingImg;
                        retrievingImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.retrieving_image);

                        ivHelpImage.setImageBitmap(retrievingImg);

                        RetrieveImageTask task = new RetrieveImageTask(getQuestionnaire(), ivHelpImage);
                        task.execute("rest/helpImage/downloadimage/" + imageFile);
                    }
                };
            });
        }

        return layout;
    }

    @Override
    public void leave() {
        System.gc(); //Generally not good practice, BUT Android is notoriously bad at doing necessary GC in connection w. bitmaps
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done!
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public boolean validates() {
        return true;
    }
}
