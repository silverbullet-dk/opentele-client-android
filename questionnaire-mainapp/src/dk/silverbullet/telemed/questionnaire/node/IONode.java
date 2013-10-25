package dk.silverbullet.telemed.questionnaire.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.ClinicMessageBubbleElement;
import dk.silverbullet.telemed.questionnaire.element.Element;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.PatientMessageBubbleElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class IONode extends Node {
    private boolean enterCalled = false;
    private boolean hideTopPanel;
    private boolean hideBackButton;
    private boolean hideMenuButton;

    private static final String TAG = Util.getTag(IONode.class);

    @Expose
    private List<Element> elements = new ArrayList<Element>();

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Context ctx;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Activity activity;

    private View topPanel;
    private LinearLayout outerLayout;
    private LinearLayout innerLayout;
    private LinearLayout headerLayout;
    private FrameLayout menuFrame;
    private boolean shouldShowVersionNumber;

    public IONode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    public void addElement(Element element) {
        if (null == elements) {
            elements = new ArrayList<Element>();
        }

        elements.add(element);
    }

    public void addVersionNumber() {
        shouldShowVersionNumber = true;
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
        ctx = activity.getApplicationContext();

        // Figure out how to do the layout...
        // text, any*, list, any2*, [buttons] -> { _text_ } {any*} list {any2} {buttons}
        // text1, text2, any*, [buttons] -> { _text_ } {tex2, any} {buttons}

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
                ch = 'Z';
            }
            sb.append(ch);
        }

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
                outerLayout.addView(elements.get(++last).getView());
            }

        }

        if (shouldShowVersionNumber) {
            addVersionNumberView();
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

    private void addVersionNumberView() {
        Activity activity = questionnaire.getActivity();

        TextView versionTextView = new TextView(activity);
        String versionText = activity.getString(R.string.client_version);

        Variable<?> environment = getQuestionnaire().getValuePool().get(Util.SERVER_ENVIRONMENT);
        if (environment != null) {
            versionText = versionText + " - " + environment.evaluate();
        }

        versionTextView.setText(versionText);
        outerLayout.addView(versionTextView, outerLayout.getChildCount() - 1);
    }

    protected void linkTopPanel(ViewGroup topBarParent) {
        topPanel = topBarParent.findViewById(R.id.top_panel);

        if (getQuestionnaire().isLoggedIn() && !hideTopPanel) {
            TextView userId = (TextView) topBarParent.findViewById(R.id.user_id);
            userId.setText(questionnaire.getFullName());

            Button back = (Button) topBarParent.findViewById(R.id.back);
            if (hideBackButton) {
                back.setVisibility(View.GONE);
            } else {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IONode.this.getQuestionnaire().back();
                    }
                });
            }

            Button menu = (Button) topBarParent.findViewById(R.id.main_menu);
            if (hideMenuButton) {
                menu.setVisibility(View.GONE);
            } else {
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IONode.this.getQuestionnaire().setCurrentNode(MainQuestionnaire.getInstance().getMainMenu());
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
        } else {
            topPanel.setVisibility(View.GONE);
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
}
