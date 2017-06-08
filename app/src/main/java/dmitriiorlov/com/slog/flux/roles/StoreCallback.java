package dmitriiorlov.com.slog.flux.roles;

import android.content.Context;

import java.util.List;

import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;

/**
 * Created by Dmitry on 6/7/2017.
 */

public interface StoreCallback {

    void registerToDispatcher();
    void deregisterFromDispatcher();
    void destroySelf();

    /**
     * The sign in method
     * @param context
     * @param email
     * @param password
     * @return
     */
    boolean signIn(Context context, String email, String password);

    /**
     * The sign up method
     * @param context
     * @param name
     * @param email
     * @param password
     * @return
     */
    boolean signUp(Context context, String name, String email, String password);

    /**
     * Checks if the connection is established
     * @param context
     * @return
     */
    boolean checkConnection(Context context);

    void onProfileDataChanged(User user);
    // void currentUserDataChanged(Context context);

    void onProfileDocumentsChanged(List<Document> documentList);
}
