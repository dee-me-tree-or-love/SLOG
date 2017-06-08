package dmitriiorlov.com.slog.flux.roles;

/**
 * Created by Dmitry on 6/7/2017.
 */

public interface ControllerView {
    @Deprecated
    void updateStoreState();

    void updateStoreState(Store store);
    void unsubscribeFromAll();

}
