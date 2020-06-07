package arvolear.expansions_tutorial.expansions;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class ExpansionDownloaderService extends DownloaderService
{
    private static final String BASE64_PUBLIC_KEY = "YOUR KEY GOES HERE"; // TODO change

    private static final byte[] SALT = new byte[]
            {
            }; // TODO fill in with values [-128; 127]

    @Override
    public String getPublicKey()
    {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT()
    {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName()
    {
        return ExpansionAlarmReceiver.class.getName();
    }
}
