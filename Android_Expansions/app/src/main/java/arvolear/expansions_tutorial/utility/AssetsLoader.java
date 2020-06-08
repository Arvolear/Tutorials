package arvolear.expansions_tutorial.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.InputStream;
import java.util.TreeMap;

import arvolear.expansions_tutorial.expansions.ExpansionController;

/* Simple util class to load image assets.
 * Loaded images are stored in the TreeMap.
 *
 * The class is capable of loading images either directly from
 * the assets folder or from the provided expansions file
 */
public class AssetsLoader
{
    private AppCompatActivity activity;

    private ZipResourceFile expansionFile;
    private boolean loadFromLocalAssets = false;

    private TreeMap < Integer, Bitmap > tree;

    /* Constructor accepts external TreeMap to store loaded bitmaps in */
    public AssetsLoader(AppCompatActivity activity, TreeMap < Integer, Bitmap > tree)
    {
        this.activity = activity;
        this.tree = tree;

        try
        {
            /* Here we use zip_file library.
             * Trying to get the expansion file with the specified version
             */
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(activity, ExpansionController.EXP_VERSION, ExpansionController.EXP_VERSION);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /* Function to asynchronously load an image from assets
     * index - output bitmap index in the TreeMap
     * path - path to the asset (assets folder is a root dir)
     * join - whether we need to wait for the loading to finish
     */
    public void loadBitmapFromAssets(final int index, final String path, boolean join)
    {
        Thread loader = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                InputStream stream = null;

                try
                {
                    if (loadFromLocalAssets)
                    {
                        /* read directly from assets folder */
                        stream = activity.getAssets().open(path);
                    }
                    else
                    {
                        /* read from expansions file */
                        stream = expansionFile.getInputStream(path);
                    }

                    /* create bitmap from the stream of bytes */
                    Bitmap newBitmap = BitmapFactory.decodeStream(stream);

                    /* We don't want a recourse race :) */
                    synchronized (AssetsLoader.this)
                    {
                        tree.put(index, newBitmap);
                    }
                }
                catch (Exception ex)
                {
                }
                finally
                {
                    try
                    {
                        if (stream != null)
                        {
                            /* Closing the stream */
                            stream.close();
                        }
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        });

        loader.start();

        if (join)
        {
            try
            {
                loader.join();
            }
            catch (Exception ex)
            {
            }
        }
    }

    /* Function to trigger different type of assets loading (assets folder or expansions) */
    public void setLoadFromLocalAssets(boolean loadFromLocalAssets)
    {
        this.loadFromLocalAssets = loadFromLocalAssets;
    }
}
