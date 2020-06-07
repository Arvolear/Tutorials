package arvolear.expansions_tutorial.expansions;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Messenger;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import java.io.File;

import arvolear.expansions_tutorial.MainActivity;
import arvolear.expansions_tutorial.R;

public class ExpansionController implements View.OnClickListener, IDownloaderClient
{
    private AppCompatActivity activity;

    public static final boolean EXP_IS_MAIN = true; // TODO change?
    public static final int EXP_VERSION = 1; // TODO change?
    public static final long EXP_SIZE = 0; // TODO change?

    public static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final int PERMISSION_CODE = 1;

    private boolean statePaused = false;
    private boolean cellularShown = false;
    private int state;

    private IStub downloaderClientStub;
    private IDownloaderService remoteService;

    private ExpansionPage expansionPage;

    public ExpansionController(AppCompatActivity activity)
    {
        this.activity = activity;
    }

    private void initUI()
    {
        /* Create downloader */
        downloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ExpansionDownloaderService.class);

        /* Set new view (downloading screen) */
        activity.setContentView(R.layout.download);

        /* initialize new downloading screen */
        expansionPage = new ExpansionPage(activity, this);
    }

    /* Simple "read" permission checker */
    public boolean checkPermission()
    {
        /* Permission is not available */
        if (ContextCompat.checkSelfPermission(activity, PERMISSION) == PackageManager.PERMISSION_DENIED)
        {
            /* Get expansion file */
            File obb = new File(Helpers.getExpansionAPKFileName(activity, EXP_IS_MAIN, EXP_VERSION));

            /* Try to read the expansion file */
            if (!obb.canRead())
            {
                /* Request permission on fail */
                ActivityCompat.requestPermissions(activity, new String[]{PERMISSION}, PERMISSION_CODE);
                return false; // Reading failed
            }
        }

        return true; // Everything is fine, go on
    }

    /* Function that creates parental directory for the expansion file
    *  Parental name is actually the name of the package
    */
    private void checkAndCreateDirs()
    {
        File packageFile = new File(activity.getObbDir() + activity.getPackageName());

        if (!packageFile.exists())
        {
            /* Create dir if it doesn't exist */
            packageFile.mkdirs();
        }
    }

    /* Simply check the expansion file availability */
    private boolean expansionFilesDelivered()
    {
        /* Firstly check existence of expansion parental directory */
        checkAndCreateDirs();

        /* Get expansion name from its type and version */
        String expName = Helpers.getExpansionAPKFileName(activity, EXP_IS_MAIN, EXP_VERSION);

        /* Check whether file with exact name and size exists and delete on mismatch */
        return Helpers.doesFileExist(activity, expName, EXP_SIZE, true);
    }

    /* This function is called from MainActivity onCreate() method.
     * It checks whether we need to start downloading the expansions or
     * the expansions are already here.
     */
    public boolean downloadContent()
    {
        /* Check expansion delivery */
        if (!expansionFilesDelivered())
        {
            Intent notifierIntent = new Intent(activity, MainActivity.class);
            notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            try
            {
                /* Trying to start downloading */
                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(activity, pendingIntent, ExpansionDownloaderService.class);

                /* if response code is not NO_DOWNLOAD_REQUIRED, we must download the expansion */
                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED)
                {
                    initUI();
                    return true; // Yes, we started downloading the expansion
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return false; // No, we didn't start downloading -> expansion is OK
    }

    /* Function that is called when expansion if fully downloaded.
     * It simply calls the same activity, popping current one from the stack.
     *
     * Because expansions are now downloaded, we will end up launching the game
     */
    private void launchTheGame()
    {
        Thread launchThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    /* Sleeping one sec for smooth experience */
                    Thread.sleep(1000);
                }
                catch (Exception ex)
                {
                }

                Intent menuIntent = new Intent(activity, MainActivity.class);

                /* Calling the same activity */
                activity.startActivity(menuIntent);
                activity.overridePendingTransition(R.anim.expansions_alpha_up, R.anim.expansions_alpha_down);

                /* Popping current activity from the activity stack */
                activity.finish();
            }
        });

        launchThread.start();
    }

    /* On each start we have to connect the downloading client */
    public void start()
    {
        if (downloaderClientStub != null)
        {
            downloaderClientStub.connect(activity);
            expansionPage.start();
        }
    }

    /* On each stop we have to disconnect the client */
    public void stop()
    {
        if (downloaderClientStub != null)
        {
            downloaderClientStub.disconnect(activity);
            expansionPage.stop();
        }
    }

    /* Function to track incoming clicks.
     * It handles "resume" "pause" states as well as
     * cellular downloading
     */
    @Override
    public void onClick(View v)
    {
        if (cellularShown)
        {
            /* If user presses "resume over cellular" button.
             * Flag cellularShown will be disabled in the onDownloadStateChanged() function
             */
            if (v.getId() == expansionPage.getCellularResumeButtonId())
            {
                remoteService.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
                remoteService.requestContinueDownload();
            }
            else if (v.getId() == expansionPage.getCellularCancelButtonId())
            {
                cellularShown = false;

                /* Some UI updates */
                expansionPage.waitForWifi();
                expansionPage.triggerCellular(cellularShown);
            }
        }
        /* Called when used presses "resume" or "pause" buttons */
        else if (v.getId() == expansionPage.getResumePauseButtonId())
        {
            statePaused = !statePaused;

            if (statePaused)
            {
                remoteService.requestPauseDownload();
            }
            else
            {
                remoteService.requestContinueDownload();
            }

            /* UI update */
            expansionPage.updateResumePauseButton(statePaused);
        }
    }

    /* Must override */
    @Override
    public void onServiceConnected(Messenger m)
    {
        remoteService = DownloaderServiceMarshaller.CreateProxy(m);
        remoteService.onClientUpdated(downloaderClientStub.getMessenger());
    }

    /* This function is called when downloading process changes its state.
     * We can track the change and react respectively
     */
    @Override
    public void onDownloadStateChanged(int stateId)
    {
        /* 1) initially we don't want to show cellular message
         * 2) we are running, not paused
         * 3) we have time borders
         */
        boolean showCellMessage = false;
        boolean paused = false;
        boolean indeterminate = false;

        switch (stateId)
        {
            case IDownloaderClient.STATE_IDLE:
            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                indeterminate = true;
                break;

            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                paused = true;
                break;

            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                paused = true;
                showCellMessage = true;
                break;

            case IDownloaderClient.STATE_DOWNLOADING:
                break;

            case IDownloaderClient.STATE_COMPLETED:
                expansionPage.setFinished(); // set 100% downloaded
                launchTheGame(); // launch the game
                break;

            default:
                paused = true;
                indeterminate = true;
        }

        state = stateId;
        statePaused = paused;

        /* If cellular message show changes */
        if (cellularShown != showCellMessage)
        {
            cellularShown = showCellMessage;
            /* Either show cellular message or hide */
            expansionPage.triggerCellular(cellularShown);
        }

        /* Simple UI updates */
        expansionPage.triggerIndeterminate(indeterminate);
        expansionPage.updateState(state);
        expansionPage.updateResumePauseButton(statePaused);
    }

    /* Function is called when downloading is in progress.

     * Note that we have to manually set 100% progress because
     * this callback function is not called on 100%
     */
    @Override
    public void onDownloadProgress(DownloadProgressInfo progress)
    {
        expansionPage.updateProgress(progress);
    }
}
