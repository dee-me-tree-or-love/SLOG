package dmitriiorlov.com.slog.utils.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dmitry on 6/6/2017.
 */

public class NetworkConnectionStatus {
    public Context context;

    public NetworkConnectionStatus(Context appContext){
        this.context = appContext;
    }

    /**
     * Checks whether the device is online
     * @return boolean
     */
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
