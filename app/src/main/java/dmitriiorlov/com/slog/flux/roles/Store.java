package dmitriiorlov.com.slog.flux.roles;

import dmitriiorlov.com.slog.flux.StoreTypes;

/**
 * Created by Dmitry on 6/7/2017.
 */

public interface Store {

    // TODO: maybe switch to the HashSet implementation of the holder of the Controller Views

    void subscribeControllerView(ControllerView cv);
    void unSubscribeControllerView(ControllerView cv);
    StoreTypes getType();
    void notifyStateChange();
}
