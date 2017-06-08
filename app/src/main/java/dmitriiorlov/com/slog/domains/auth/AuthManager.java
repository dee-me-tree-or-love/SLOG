package dmitriiorlov.com.slog.domains.auth;

import android.content.Context;

import dmitriiorlov.com.slog.flux.roles.DomainManager;

/**
 * Created by Dmitry on 6/7/2017.
 */

public class AuthManager implements DomainManager {

    private AuthStore mAuthStore;

    // [ SINGLETON PATTERN STUFF ]

//    private AuthStore mInstance;

    // solution from SourceMaking.com
    private static class AuthStoreManagerHolder {
        private static final AuthManager INSTANCE = new AuthManager();
    }

//    private void createInstance() {
//        this.mInstance = new AbstractStore();
//    }

    public static AuthManager getInstance() {
        return AuthManager.AuthStoreManagerHolder.INSTANCE;
    }

    private AuthManager() {
//        this.mAuthStore = AuthStore.getInstance();
    }

    // [ SINGLETON PATTERN STUFF ]

//    @Deprecated
//    @Override
//    public boolean onViewInitialized(Context context) {
//        return false;
//    }

    public void updateInputEmail(String email) {
        AuthStore.getInstance().setInputEmail(email);
    }

    public void updateInputName(String name) {
        AuthStore.getInstance().setInputName(name);
    }
}
