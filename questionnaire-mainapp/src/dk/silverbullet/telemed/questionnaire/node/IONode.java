package dk.silverbullet.telemed.questionnaire.node;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.*;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IONode extends Node {
    private boolean enterCalled = false;
    private boolean hideTopPanel;
    private boolean hideBackButton;
    private boolean hideMenuButton;

    private static final String TAG = Util.getTag(IONode.class);

    @Expose
    private List<Element> elements = new ArrayList<Element>();

    private Activity activity;

    private View topPanel;
    private LinearLayout outerLayout;
    private LinearLayout innerLayout;
    private LinearLayout headerLayout;
    private FrameLayout menuFrame;

    public IONode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    public void addElement(Element element) {
        if (null == elements) {
            elements = new ArrayList<Element>();
        }

        elements.add(element);
    }

    public void clearElements() {
        if (null != elements) {
            elements.clear();
        }
    }

    protected void hideMenuButton() {
        hideMenuButton = true;
    }

    protected void hideBackButton() {
        hideBackButton = true;
    }

    protected void showKeyboard(EditText editTextView) {
        InputMethodManager imm = (InputMethodManager)questionnaire.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT);
     }

    protected void hideKeyboard(EditText editTextView) {
        InputMethodManager imm = (InputMethodManager)questionnaire.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }

    @Override
    public void enter() {
        Log.d(TAG, "enter...");
        if (enterCalled) {
            Log.e(TAG, "Enter already called once!");
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                Log.e(TAG, stackTraceElement.toString());
            }
        }

        createView();
        if (!hideTopPanel) {
            questionnaire.push(this);
        }

        if (questionnaire.isBackPressed()) {
            questionnaire.goBack();
        }

        enterCalled = true;
    }

    protected void createView() {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        activity = questionnaire.getActivity();

        // Figure out how to do the layout...
        // text, any*, list, any2*, [buttons] -> { _text_ } {any*} list {any2} {buttons}
        // text1, text2, any*, [buttons] -> { _text_ } {tex2, any} {buttons}

        boolean hasEditText = false;
        EditTextElement lastEditTextElement = null;

        StringBuffer sb = new StringBuffer();
        for (Element e : elements) {
            char ch;
            if (e instanceof ButtonElement || e instanceof TwoButtonElement) {
                ch = 'B';
            } else if (e instanceof TextViewElement) {
                ch = 'T';
            } else if (e instanceof PatientMessageBubbleElement || e instanceof ClinicMessageBubbleElement) {
                ch = 'M';
            } else if (e instanceof ListViewElement) {
                ch = 'L';
            } else {
                if (e instanceof EditTextElement) {
                    hasEditText = true;
                    lastEditTextElement = (EditTextElement)e;
                }
                ch = 'Z';
            }
            sb.append(ch);
        }

        if (lastEditTextElement != null)
            lastEditTextElement.setLastElement(true);

        String types = sb.toString();

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (types.contains("L")) { // List/Menu
            outerLayout = (LinearLayout) inflater.inflate(R.layout.menunode, null);
            headerLayout = (LinearLayout) outerLayout.findViewById(R.id.header);
            innerLayout = null;
            menuFrame = (FrameLayout) outerLayout.findViewById(R.id.menu);

            int first = 0;
            int last = elements.size() - 1;
            if (types.matches("T(T|L).*")) {
                TextViewElement tve = (TextViewElement) elements.get(first++);
                tve.setHeader(true);
                headerLayout.addView(tve.getView());
            }

            for (int e = first; e <= last; e++) {
                Element element = elements.get(e);
                if (element instanceof ListViewElement) {
                    menuFrame.addView(element.getView());
                } else {
                    outerLayout.addView(element.getView());
                }
            }
        } else { // General form with text, fields, buttons, ...
            outerLayout = (LinearLayout) inflater.inflate(R.layout.ionode, null);
            LinearLayout headerLayout = (LinearLayout) outerLayout.findViewById(R.id.header);
            innerLayout = (LinearLayout) outerLayout.findViewById(R.id.inner_ionode);
            menuFrame = null;

            int first = 0;
            int last = elements.size() - 1;
            if (types.matches("T(T|L).*")) {
                TextViewElement tve = (TextViewElement) elements.get(first++);
                tve.setHeader(true);
                headerLayout.addView(tve.getView());
            }

            if (types.matches("[^B]+B")) {
                last--;
            }
            for (int e = first; e <= last; e++) {
                Element element = elements.get(e);
                innerLayout.addView(element.getView());
            }

            if (types.matches("[^B]+B")) {
                if (hasEditText) //to make buttons scroll with input fields when soft keyboard is visible:
                    innerLayout.addView(elements.get(++last).getView());
                else
                    outerLayout.addView(elements.get(++last).getView());
            }

        }

        linkTopPanel(outerLayout);

        if (types.matches(".*M.*")) {
            ViewTreeObserver vto = innerLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    innerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ScrollView scrollView = (ScrollView) outerLayout.findViewById(R.id.inner_ionode_scroll);
                    int target = innerLayout.getBottom();
                    if (target > 1500) {
                        scrollView.scrollTo(0, target);
                    } else {
                        ObjectAnimator animScrollToBottom = ObjectAnimator.ofInt(scrollView, "scrollY", target);
                        animScrollToBottom.setInterpolator(new AccelerateDecelerateInterpolator());
                        animScrollToBottom.setDuration(2000);
                        animScrollToBottom.start();
                    }
                }
            });
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questionnaire.getRootLayout().removeAllViews();
                questionnaire.getRootLayout().addView(outerLayout);
            }
        });
    }

    protected void linkTopPanel(View topBarParent) {
        topPanel = topBarParent.findViewById(R.id.top_panel);

        if (hideTopPanel) {
            topPanel.setVisibility(View.GONE);
        } else {
            TextView userId = (TextView) topBarParent.findViewById(R.id.user_id);
            userId.setText(questionnaire.getFullName());

            final Button back = (Button) topBarParent.findViewById(R.id.back);
            final Button menu = (Button) topBarParent.findViewById(R.id.main_menu);

            if (hideBackButton) {
                back.setVisibility(View.GONE);
            } else {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enterCalled) {
                            IONode.this.getQuestionnaire().back();
                        }
                    }
                });
            }

            if (hideMenuButton) {
                menu.setVisibility(View.GONE);
            } else {
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enterCalled) {
                            IONode.this.getQuestionnaire().setCurrentNode(MainQuestionnaire.getInstance().getMainMenu());
                        }
                    }
                });
            }

            Button logout = (Button) topBarParent.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut();
                }
            });
        }
    }

    public void logOut() {
        getQuestionnaire().logout();
    }

    @Override
    public void leave() {
        Log.d(TAG, "leave..");
        if (!enterCalled) {
            throw new RuntimeException("Enter was not called!");
        }
        enterCalled = false;
        if (null != elements && !elements.isEmpty()) {
            for (Element element : elements) {
                element.leave();
            }
        }
        removeViews();
    }

    private void removeViews() {
        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (headerLayout != null) {
                    headerLayout.removeAllViews();
                    headerLayout = null;
                }
                if (innerLayout != null) {
                    innerLayout.removeAllViews();
                    innerLayout = null;
                }
                if (menuFrame != null) {
                    menuFrame.removeAllViews();
                    menuFrame = null;
                }
                if (outerLayout != null) {
                    outerLayout.removeAllViews();
                    outerLayout = null;
                }
                for (Element element : elements) {
                    View view = element.getView();
                    ViewParent parent = view.getParent();
                    if (parent != null && parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView(view);
                    }
                }
            }
        });
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        if (null != elements && !elements.isEmpty()) {
            for (Element element : elements) {
                element.linkNodes(map);
                element.setNode(this);
            }
        }
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        if (null != elements && !elements.isEmpty()) {
            for (Element element : elements) {
                element.linkVariables(map);
            }
        }
    }

    public boolean validates() {
        boolean result = true;
        for (Element element : elements) {
            result &= element.validates();
        }

        return result;
    }

    public void setHideBackButton(boolean hideBackButton) {
        this.hideBackButton = hideBackButton;
    }

    public void setHideTopPanel(boolean hideTopPanel) {
        this.hideTopPanel = hideTopPanel;
    }

    public void setHideMenuButton(boolean hideMenuButton) {
        this.hideMenuButton = hideMenuButton;
    }
}
