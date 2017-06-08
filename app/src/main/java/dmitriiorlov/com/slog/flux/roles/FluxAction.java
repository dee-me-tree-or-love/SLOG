package dmitriiorlov.com.slog.flux.roles;

import android.content.Context;

/**
 * Created by Dmitry on 6/7/2017.
 */

public abstract class FluxAction {

    private Dispatcher mReceiver;
    private Context mContext;

    public FluxAction(Dispatcher dispatcher){
        this.mReceiver = dispatcher;
    }

    public FluxAction(Dispatcher dispatcher, Context context){
        this.mReceiver = dispatcher;
        this.mContext = context;
    }

    abstract void Execute();
}
