package arvolear.expansions_tutorial.expansions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

public class ExpansionAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ExpansionDownloaderService.class);
        }
        catch (Exception ex)
        {
        }
    }
}