package arvolear.expansions_tutorial.expansions;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

import arvolear.expansions_tutorial.R;
import arvolear.expansions_tutorial.utility.AssetsLoader;

import static android.view.View.generateViewId;

/* This class basically holds all the UI when expansions are downloading
 * It may seem the class is overcomplicated a bit for the tutorial,
 * but I wanted to make the downloading screen beautiful
 */
public class ExpansionPage
{
    private AppCompatActivity activity;
    private View.OnClickListener controller;

    private DownloadProgressInfo prevProgressInfo = null;

    private TreeMap<Integer, Bitmap> tree;
    private AssetsLoader loader;

    private boolean stopPhrases;
    private int curPhraseIndex;

    private FrameLayout contentLayout;
    private FrameLayout messagesLayout;

    private FrameLayout phrasesLayout;
    private LinearLayout cellularLayout;

    private TextView nameSign;

    private TextView state;
    private TextView progressFraction;
    private TextView progressPercentage;
    private DownloadBar downloadBar;
    private TextView progressSpeed;
    private TextView progressTime;
    private Button resumePauseButton;
    private TextView phrase;

    private TextView cellularInfo;
    private LinearLayout cellularButtons;
    private Button cellularResumeButton;
    private Button cellularCancelButton;

    /* Constructor accepts MainActivity and ExpansionController to track clicks */
    public ExpansionPage(AppCompatActivity activity, View.OnClickListener controller)
    {
        this.activity = activity;
        this.controller = controller;

        tree = new TreeMap<>();
        loader = new AssetsLoader(activity, tree);

        /* Very important.
         * Configures AssetsLoader to import images from local "assets" folder
         * not from expansion file
         */
        loader.setLoadFromLocalAssets(true);

        contentLayout = activity.findViewById(R.id.contentLayout);
        messagesLayout = activity.findViewById(R.id.messagesLayout);

        init();
    }

    private void init()
    {
        /* Create the progress bar */
        downloadBar = new DownloadBar(activity, "textures/expansions/bar", 0.0);
        phrase = new TextView(activity);

        configureBackground();

        /* Configure all button, text views ... */
        contentLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP0.gravity = Gravity.CENTER;

                FrameLayout.LayoutParams LP1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP1.gravity = Gravity.START | Gravity.CENTER_VERTICAL;

                FrameLayout.LayoutParams LP2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP2.gravity = Gravity.END | Gravity.CENTER_VERTICAL;

                FrameLayout.LayoutParams LP3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP3.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

                LinearLayout.LayoutParams LP4 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

                phrasesLayout = new FrameLayout(activity);
                phrasesLayout.setLayoutParams(LP4);

                cellularLayout = new LinearLayout(activity);
                cellularLayout.setLayoutParams(LP4);
                cellularLayout.setOrientation(LinearLayout.VERTICAL);
                cellularLayout.setGravity(Gravity.CENTER);

                nameSign = new TextView(activity);
                nameSign.setLayoutParams(LP0);
                nameSign.setGravity(Gravity.CENTER);
                nameSign.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                nameSign.setText("Downloading Files");
                nameSign.setBackgroundColor(Color.TRANSPARENT);
                nameSign.setTextColor(activity.getResources().getColor(R.color.white_text));
                nameSign.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 10.0f);
                nameSign.setTransformationMethod(null);

                state = new TextView(activity);
                state.setLayoutParams(LP3);
                state.setGravity(Gravity.CENTER);
                state.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                state.setBackgroundColor(Color.TRANSPARENT);
                state.setTextColor(activity.getResources().getColor(R.color.white_text));
                state.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 22.0f);
                state.setTransformationMethod(null);

                progressFraction = new TextView(activity);
                progressFraction.setLayoutParams(LP1);
                progressFraction.setPadding(contentLayout.getWidth() / 8, 0, 0, 0);
                progressFraction.setGravity(Gravity.CENTER);
                progressFraction.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                progressFraction.setText("0MB / 0MB");
                progressFraction.setBackgroundColor(Color.TRANSPARENT);
                progressFraction.setTextColor(activity.getResources().getColor(R.color.white_text));
                progressFraction.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                progressFraction.setTransformationMethod(null);

                progressPercentage = new TextView(activity);
                progressPercentage.setLayoutParams(LP2);
                progressPercentage.setPadding(0, 0, contentLayout.getWidth() / 8, 0);
                progressPercentage.setGravity(Gravity.CENTER);
                progressPercentage.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                progressPercentage.setText("0%");
                progressPercentage.setBackgroundColor(Color.TRANSPARENT);
                progressPercentage.setTextColor(activity.getResources().getColor(R.color.white_text));
                progressPercentage.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                progressPercentage.setTransformationMethod(null);

                progressSpeed = new TextView(activity);
                progressSpeed.setLayoutParams(LP1);
                progressSpeed.setPadding(contentLayout.getWidth() / 8, 0, 0, 0);
                progressSpeed.setGravity(Gravity.CENTER);
                progressSpeed.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                progressSpeed.setBackgroundColor(Color.TRANSPARENT);
                progressSpeed.setTextColor(activity.getResources().getColor(R.color.white_text));
                progressSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                progressSpeed.setTransformationMethod(null);

                progressTime = new TextView(activity);
                progressTime.setLayoutParams(LP2);
                progressTime.setPadding(0, 0, contentLayout.getWidth() / 8, 0);
                progressTime.setGravity(Gravity.CENTER);
                progressTime.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                progressTime.setBackgroundColor(Color.TRANSPARENT);
                progressTime.setTextColor(activity.getResources().getColor(R.color.white_text));
                progressTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                progressTime.setTransformationMethod(null);

                resumePauseButton = new Button(activity);
                resumePauseButton.setLayoutParams(LP0);
                resumePauseButton.setText("PAUSE");
                resumePauseButton.setBackgroundColor(Color.TRANSPARENT);
                resumePauseButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                resumePauseButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                resumePauseButton.setOnClickListener(controller);
                resumePauseButton.setId(generateViewId());
                resumePauseButton.setSoundEffectsEnabled(false);

                cellularInfo = new TextView(activity);
                cellularInfo.setLayoutParams(LP0);
                cellularInfo.setPadding(contentLayout.getWidth() / 8, 0, contentLayout.getWidth() / 8, 30);
                cellularInfo.setGravity(Gravity.CENTER);
                cellularInfo.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                cellularInfo.setText("Would you like to resume downloading over cellular connection? " +
                        "If you choose not to resume, the download will automatically start when wi-fi is available.");
                cellularInfo.setBackgroundColor(Color.TRANSPARENT);
                cellularInfo.setTextColor(activity.getResources().getColor(R.color.white_text));
                cellularInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 27.5f);
                cellularInfo.setTransformationMethod(null);

                cellularButtons = new LinearLayout(activity);
                cellularButtons.setLayoutParams(LP0);
                cellularButtons.setGravity(Gravity.CENTER);

                int padding = (int) (contentLayout.getHeight() / 10.8f);

                cellularResumeButton = new Button(activity);
                cellularResumeButton.setLayoutParams(LP0);
                cellularResumeButton.setPadding(0, 0, padding, 0);
                cellularResumeButton.setText("RESUME");
                cellularResumeButton.setBackgroundColor(Color.TRANSPARENT);
                cellularResumeButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                cellularResumeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 25.0f);
                cellularResumeButton.setOnClickListener(controller);
                cellularResumeButton.setId(generateViewId());
                cellularResumeButton.setSoundEffectsEnabled(false);

                cellularCancelButton = new Button(activity);
                cellularCancelButton.setLayoutParams(LP0);
                cellularCancelButton.setPadding(padding, 0, 0, 0);
                cellularCancelButton.setText("WAIT FOR WI-FI");
                cellularCancelButton.setBackgroundColor(Color.TRANSPARENT);
                cellularCancelButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                cellularCancelButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 25.0f);
                cellularCancelButton.setOnClickListener(controller);
                cellularCancelButton.setId(generateViewId());
                cellularCancelButton.setSoundEffectsEnabled(false);

                phrase.setLayoutParams(LP3);
                phrase.setGravity(Gravity.CENTER);
                phrase.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                phrase.setBackgroundColor(Color.TRANSPARENT);
                phrase.setTextColor(activity.getResources().getColor(R.color.white_text));
                phrase.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentLayout.getHeight() / 25.0f);
                phrase.setTransformationMethod(null);

                ((FrameLayout) (activity.findViewById(R.id.nameSignLayout))).addView(nameSign);

                ((FrameLayout) (activity.findViewById(R.id.progressBarStateLayout))).addView(state);
                ((FrameLayout) (activity.findViewById(R.id.progressBarInfoLayout))).addView(progressFraction);
                ((FrameLayout) (activity.findViewById(R.id.progressBarInfoLayout))).addView(progressPercentage);
                ((FrameLayout) (activity.findViewById(R.id.progressBarSpeedLayout))).addView(progressSpeed);
                ((FrameLayout) (activity.findViewById(R.id.progressBarSpeedLayout))).addView(progressTime);

                phrasesLayout.addView(phrase);
                phrasesLayout.addView(resumePauseButton);

                cellularButtons.addView(cellularResumeButton);
                cellularButtons.addView(cellularCancelButton);

                cellularLayout.addView(cellularInfo);
                cellularLayout.addView(cellularButtons);

                messagesLayout.addView(phrasesLayout);

                configureFonts();
                triggerIndeterminate(true);
            }
        });
    }

    /* Function that configures background image.
     * It basically loads an image and clips it
     * according to the screen dimension ratio so that
     * an image is not stretched
     */
    private void configureBackground()
    {
        final ImageView backgroundImage = activity.findViewById(R.id.backgroundImage);

        /* Load an image */
        loader.loadBitmapFromAssets(0, "textures/expansions/background/0.jpg", true);
        final Bitmap backBitmap = tree.get(0);

        backgroundImage.post(new Runnable()
        {
            @Override
            public void run()
            {
                float aspectRatio = (float) backgroundImage.getWidth() / (float) backgroundImage.getHeight();

                int desiredWidth = (int) (backBitmap.getHeight() * aspectRatio);
                desiredWidth = Math.min(desiredWidth, backBitmap.getWidth());

                int widthOffset = (backBitmap.getWidth() - desiredWidth) / 2;

                final Bitmap backgroundBitmap = Bitmap.createBitmap(backBitmap, widthOffset, 0, desiredWidth, backBitmap.getHeight());
                backgroundImage.setImageBitmap(backgroundBitmap);
            }
        });
    }

    /* Function that configures the font for all the buttons and text views */
    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        nameSign.setTypeface(face);
        nameSign.setShadowLayer(3.0f, 7.0f, 7.0f, activity.getResources().getColor(R.color.dark_shadow));

        state.setTypeface(face);

        progressFraction.setTypeface(face);
        progressPercentage.setTypeface(face);
        progressSpeed.setTypeface(face);
        progressTime.setTypeface(face);
        resumePauseButton.setTypeface(face, Typeface.BOLD);
        phrase.setTypeface(face);

        cellularInfo.setTypeface(face);
        cellularResumeButton.setTypeface(face, Typeface.BOLD);
        cellularCancelButton.setTypeface(face, Typeface.BOLD);
    }

    /* This function is responsible for showing entertaining messages
     * to the user while downloading is in progress
     */
    private void configurePhrases()
    {
        /* List of messages */
        final ArrayList<String> phrases = new ArrayList<>(Arrays.asList(
                "Please hodl...",
                "Adding hamsters to the wheel...",
                "Counting Ether...",
                "Shovelling coal into the server...",
                "Locating the required gigapixels to render...",
                "Does anyone actually reads this?",
                "Make sure nobody is watching your back...",
                "...gnidrawrof drawkcaB",
                "Feeding the daemons...",
                "Loading the progressbar...",
                "Programmer is sleeping, please wait...",
                "Why don't you go outside?",
                "Counting to 1337...",
                "Checking anti-camp radius..."
        ));

        /* We random shuffle them just in case */
        Collections.shuffle(phrases);

        /* Messages will be shown for 10 seconds in the thread */
        Thread phrasesShower = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (ExpansionPage.this)
                {
                    while (!stopPhrases)
                    {
                        /* Show new phrase */
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                phrase.setText(phrases.get(curPhraseIndex));
                            }
                        });

                        /* This block of code just waits for 10 seconds */
                        try
                        {
                            for (int i = 0; i < 100; i++)
                            {
                                Thread.sleep(100);

                                if (stopPhrases)
                                {
                                    break;
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                        }

                        /* Next phrase index */
                        curPhraseIndex = (curPhraseIndex + 1) % phrases.size();
                    }
                }
            }
        });

        phrasesShower.start();
    }

    public int getResumePauseButtonId()
    {
        return resumePauseButton.getId();
    }

    public int getCellularResumeButtonId()
    {
        return cellularResumeButton.getId();
    }

    public int getCellularCancelButtonId()
    {
        return cellularCancelButton.getId();
    }

    /* If user chooses to wait for the wi-fi and not download over cellular */
    public void waitForWifi()
    {
        state.setText("Waiting for wi-fi");
        progressSpeed.setText("-INF");
        progressTime.setText("INF");
    }

    /* Show or hide cellular message */
    public void triggerCellular(boolean show)
    {
        if (show)
        {
            messagesLayout.addView(cellularLayout);

            cellularLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.expansions_cellular_show));
            phrasesLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.expansions_messages_hide));
        }
        else
        {
            cellularLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.expansions_cellular_hide));
            phrasesLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.expansions_messages_show));

            messagesLayout.removeView(cellularLayout);
        }
    }

    /* If downloading time is unknown */
    public void triggerIndeterminate(boolean trigger)
    {
        if (trigger)
        {
            state.setText("Indeterminate");
            progressSpeed.setText("-INF");
            progressTime.setText("INF");
        }
    }

    /* update current downloading state (just the text) */
    public void updateState(int stateId)
    {
        state.setText(Helpers.getDownloaderStringResourceIDFromState(stateId));
    }

    public void updateResumePauseButton(boolean statePaused)
    {
        if (statePaused)
        {
            resumePauseButton.setText("RESUME");
        }
        else
        {
            resumePauseButton.setText("PAUSE");
        }
    }

    /* This function simply updates the downloading progress.
     * It updates percentage, fraction, speed, remaining time and the progress bar
     */
    public void updateProgress(DownloadProgressInfo progress)
    {
        prevProgressInfo = progress;

        progressPercentage.setText((progress.mOverallProgress * 100 / progress.mOverallTotal) + "%");
        progressFraction.setText(Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal));

        downloadBar.setProgress(progress.mOverallProgress / (float) progress.mOverallTotal);

        progressSpeed.setText(activity.getString(R.string.kilobytes_per_second, Helpers.getSpeedString(progress.mCurrentSpeed)));
        progressTime.setText(activity.getString(R.string.time_remaining, Helpers.getTimeRemaining(progress.mTimeRemaining)));
    }

    /* This function updates UI to show the downloading is finished */
    public void setFinished()
    {
        if (prevProgressInfo != null)
        {
            progressPercentage.setText("100%");
            progressFraction.setText(Helpers.getDownloadProgressString(prevProgressInfo.mOverallTotal, prevProgressInfo.mOverallTotal));

            downloadBar.setProgress(1.0f);

            progressSpeed.setText(activity.getString(R.string.kilobytes_per_second, "0.0"));
            progressTime.setText(activity.getString(R.string.time_remaining, "00:00"));
        }
    }

    /* Wait for the phrases thread to finish and start a new one */
    synchronized public void start()
    {
        stopPhrases = false;
        curPhraseIndex = 0;

        configurePhrases();
    }

    /* Stop showing phrases */
    public void stop()
    {
        stopPhrases = true;
    }
}
