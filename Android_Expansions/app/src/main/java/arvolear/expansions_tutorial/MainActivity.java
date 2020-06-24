package arvolear.expansions_tutorial;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.expansions_tutorial.expansions.ExpansionController;
import arvolear.expansions_tutorial.utility.AssetsLoader;

public class MainActivity extends AppCompatActivity
{
    private ExpansionController expansionController;

    /* Simple function to handle fullscreen window (see styles.xml). Hides nav bar */
    private void hideNavigationBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /* Dummy function which is called when expansions are downloaded and "read"
     * permission is granted. It demonstrates how one could actually use expansions.
     *
     * Function calls AssetLoader to get desired image and then shows the image to the user
     */
    private void launchTheApp()
    {
        TreeMap < Integer, Bitmap > tree = new TreeMap<>();
        AssetsLoader loader = new AssetsLoader(this, tree);

        /* Loads an image from the expansion file */
        loader.loadBitmapFromAssets(0, "assets/textures/0.png", true);

        ImageView background = findViewById(R.id.fromExpansionsImage);
        background.setImageBitmap(tree.get(0));
    }

    /* Request permissions callback function. It is called from expansionsController
     * when the user accepts or rejects "read external storage" permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* PERMISSION_CODE is an int that stands for "read external storage" permission.
         * Yet it is an arbitrary variable
         */
        if (requestCode == ExpansionController.PERMISSION_CODE)
        {
            /* We will launch the app if permission is granted */
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                launchTheApp();
                return;
            }

            Toast.makeText(this, "Unable to launch the game without permission", Toast.LENGTH_LONG).show();
            finish(); // Close the app if permission is rejected
        }
    }

    /* App's entry point. Here we must check expansions and download the ones if necessary */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /* Important note. Here we set activity_main layout */
        setContentView(R.layout.activity_main);

        /* Creating new expansions controller. It is the class which
         * fully embraces expansions
         */
        expansionController = new ExpansionController(this);

        /* Checking for expansions accessibility */
        if (!expansionController.downloadContent())
        {
            /* Checking for external storage read permission */
            if (expansionController.checkPermission())
            {
                /* Expansions are in place and permission is already granted,
                 * nothing to download so launch the app
                 */
                launchTheApp();
            }
        }
    }

    @Override
    protected void onStart()
    {
        /* Reconnecting downloading client */
        expansionController.start();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        /* Just to make sure the nav bar is hidden */
        hideNavigationBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        /* Hide nav bar if the window gained focus */
        if (hasFocus)
        {
            hideNavigationBar();
        }
    }

    @Override
    protected void onStop()
    {
        /* Disconnecting downloading client */
        expansionController.stop();
        super.onStop();
    }
}
