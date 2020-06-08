package arvolear.expansions_tutorial.expansions;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.expansions_tutorial.R;
import arvolear.expansions_tutorial.utility.AssetsLoader;

public class DownloadBar extends FrameLayout
{
    private AppCompatActivity activity;

    private Bitmap barBoundsBitmap;
    private Bitmap barProgressBitmap;

    private FrameLayout progressBarLayout;

    private String path;

    private TreeMap<Integer, Bitmap> tree;
    private AssetsLoader assetsLoader;

    private double rawProgress;
    private int currentProgress;

    private ImageView barBounds;
    private ImageView barProgress;

    private ClipDrawable barCurrent;

    /* Simple progress bar class.
     * The class uses ClipDrawable to achieve beautiful result
     * path - path to dir with progress bar images
     * rawProgress - progress in range [0.0; 1.0]
     */
    public DownloadBar(AppCompatActivity activity, String path, double rawProgress)
    {
        super(activity);

        this.activity = activity;
        this.path = path;

        progressBarLayout = activity.findViewById(R.id.progressBarLayout);
        progressBarLayout.addView(this);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);
        assetsLoader.setLoadFromLocalAssets(true);

        /* ClipDrawable only accepts values from range [0; 10000] */
        this.rawProgress = Math.min(rawProgress, 1.0f);
        this.currentProgress = (int) (rawProgress * 10000.0);

        init();
    }

    private void init()
    {
        /* Load two images from local "assets" folder */
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", true);
        assetsLoader.loadBitmapFromAssets(1, path + "/1.png", true);
        barBoundsBitmap = tree.get(0);
        barProgressBitmap = tree.get(1);

        /* Here we configure ImageView elements */
        progressBarLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.CENTER;

                barBounds = new ImageView(activity);
                barBounds.setLayoutParams(LP0);
                barBounds.setImageBitmap(barBoundsBitmap);
                barBounds.setPadding(progressBarLayout.getWidth() / 8, 0, progressBarLayout.getWidth() / 8, 0);
                barBounds.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Scale to fit
                barBounds.setAdjustViewBounds(true);

                /* Configure the progress bar */
                barCurrent = new ClipDrawable(new BitmapDrawable(activity.getResources(), barProgressBitmap), Gravity.START, ClipDrawable.HORIZONTAL);
                barCurrent.setLevel(currentProgress);

                barProgress = new ImageView(activity);
                barProgress.setLayoutParams(LP0);
                barProgress.setImageDrawable(barCurrent);
                barProgress.setPadding(progressBarLayout.getWidth() / 8, 0, progressBarLayout.getWidth() / 8, 0);
                barProgress.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Scale to fit
                barProgress.setAdjustViewBounds(true);

                addView(barBounds);
                addView(barProgress);

                /* Set current progress (clip image in the specified place) */
                barCurrent.setLevel(currentProgress);
            }
        });
    }

    /* Simply updates current progress */
    public void setProgress(double rawProgress)
    {
        this.rawProgress = Math.min(rawProgress, 1.0f);
        currentProgress = (int)(this.rawProgress * 10000.0);

        barCurrent.setLevel(currentProgress);
    }
}
