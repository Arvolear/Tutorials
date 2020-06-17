package arvolear.expansions_tutorial.expansions;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class ExpansionDownloaderService extends DownloaderService
{
    private static final String BASE64_PUBLIC_KEY = "YOUR KEY GOES HERE"; // TODO must change with our own key

    private static final byte[] SALT = new byte[]
            {
            }; // TODO fill this in with random values [-128; 127]

    /* The public key goes from your Android Market publisher account.
     * You may find one on Google Console -> your app -> development tools
     */
    @Override
    public String getPublicKey()
    {
        return BASE64_PUBLIC_KEY;
    }

    /* The salt has to be unique across other applications,
     * please generate it carefully
     */
    @Override
    public byte[] getSALT()
    {
        return SALT;
    }

    /* Setting up the alarm receiver */
    @Override
    public String getAlarmReceiverClassName()
    {
        return ExpansionAlarmReceiver.class.getName();
    }
}
