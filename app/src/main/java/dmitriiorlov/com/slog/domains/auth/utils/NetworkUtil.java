package dmitriiorlov.com.slog.domains.auth.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import dmitriiorlov.com.slog.utils.connectivity.NetworkConnectionStatus;

/**
 * Created by Dmitry on 6/8/2017.
 */

public class NetworkUtil {
    public static boolean checkConnection(Context context) {
        NetworkConnectionStatus ncs = new NetworkConnectionStatus(context);
        boolean connected = ncs.isOnline();
        if (connected) {

            Log.i("Internet:","Connected to the Internet");
//            Toast.makeText(context, "Connected to the Internet", Toast.LENGTH_LONG).show();
        } else {

            Log.w("Internet:","Not connected to the Internet");
//            Toast.makeText(context, "No Internet connection", Toast.LENGTH_LONG).show();
        }

        return connected;
    }
}
