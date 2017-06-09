package dmitriiorlov.com.slog.utils.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import dmitriiorlov.com.slog.flux.GlobalDispatcher;

/**
 * Created by Dmitry on 6/9/2017.
 * skimmed from https://stackoverflow.com/questions/1783117/network-listener-android
 */

// TODO: ask if someone has dealt with anything similar
// should theoretically figure out the connectivity cut outs

/**
 *  Check https://developers.google.com/android/reference/com/google/android/gms/gcm/GcmNetworkManager
 */
@Deprecated
public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GlobalDispatcher.getInstance().checkNetworkConnection(context);
    }
}
