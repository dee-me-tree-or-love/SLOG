package dmitriiorlov.com.slog.domains.auth.interfaces;

import android.app.Fragment;

/**
 * Created by Dmitry on 6/6/2017.
 */

public interface OnAuthActionListener {
    // to support switching activities
    void onSwitchAuthFragment(Fragment fragment);
    // to handle sign up
    void onSignUp(String name, String email, String password);
    // to handle sign in
    void onSignIn(String email, String password);
}