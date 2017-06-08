package dmitriiorlov.com.slog.flux.roles;

import dmitriiorlov.com.slog.flux.StoreTypes;

/**
 * Created by Dmitry on 6/7/2017.
 */

public interface Store {

    void subscribeControllerView(ControllerView cv);
    void unSubscribeControllerView(ControllerView cv);
    StoreTypes getType();
    void notifyStateChange();
}
