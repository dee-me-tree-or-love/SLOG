package dmitriiorlov.com.slog.domains.auth;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
import dmitriiorlov.com.slog.domains.auth.fragments.SignInFragment;
import dmitriiorlov.com.slog.domains.auth.interfaces.OnAuthActionListener;
import dmitriiorlov.com.slog.domains.documents.browse.BrowseActivity;
import dmitriiorlov.com.slog.domains.global.ConnectivityStore;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ActionCreator;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;


public class LoginActivity
        extends
        AppCompatActivity
        implements
        OnAuthActionListener,
        ControllerView,
        ActionCreator {

    private AuthStore mAuthStore;
    private AuthManager mAuthManager;
    private ProfileStore mProfileStore;

    @BindView(R.id.login_toolbar)
    Toolbar mToolbar;

    private ConnectivityStore mConnectivityStore;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuthManager = AuthManager.getInstance();
        // register the view with the store
        mAuthStore = AuthStore.getInstance();
        // subscribe
        mAuthStore.subscribeControllerView(this);


        // initialize the profile store
        mProfileStore = ProfileStore.getInstance();
        mProfileStore.subscribeControllerView(this);

        // subscribe to connectivity store
        mConnectivityStore = ConnectivityStore.getInstance();
        mConnectivityStore.subscribeControllerView(this);

        mProgressDialog = new ProgressDialog(this);

        // perform the startup actions:
        // boolean isLoggedIn = mAuthManager.onViewInitialized(this);
        String inputEmail = mAuthStore.getInputEmail();
        // check connection + the log in session
        GlobalDispatcher.getInstance().checkNetworkConnection(this);


        // check if the fragment container is present
        if (findViewById(R.id.fragment_container) != null) {
            // if there is a saved instance, we do not need to load the new stuff - what if we join together this activity and the feed activity further?
            if (savedInstanceState != null) {
                return; // we do not apply any changes
            }

            // if everything is clear, we add the new fragment
            SignInFragment signInFragment = SignInFragment.newInstance(inputEmail);
            this.setFragment(signInFragment);
        }
    }


    /**
     * Set the new fragment in the fragment container
     *
     * @param fr the fragment to be rendered
     */
    protected void setFragment(Fragment fr) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fr).commit();
    }


    // implement the interface between the fragments and the activity
    @Override
    public void onSwitchAuthFragment(Fragment fragment) {
        this.setFragment(fragment);
    }


    @Override
    public void onSignUp(final String name, String email, String password) {
        GlobalDispatcher.getInstance().performSignUp(this, name, email, password);
    }

    @Override
    public void onSignIn(String email, String password) {
        GlobalDispatcher.getInstance().performSignIn(this, email, password);
    }


    @Override
    public void updateStoreState() {
        if (AuthStore.getInstance().isLoggingIn()) {
            this.mProgressDialog.setMessage("Fasten the seat belt!");
            this.mProgressDialog.show();
        } else {
            try {

                this.mProgressDialog.dismiss();
                if (AuthStore.getInstance().isSignInIsSuccessful()) {

                    finish();
                    // unsubscribed
                    AuthStore.getInstance().unSubscribeControllerView(this);
                    startActivity(new Intent(this, BrowseActivity.class));
                } else {

                    Toast.makeText(this, "Oupsie... A road accident...\nCheck your inputs and Internet connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

                Log.e("Progress bar broke", e.getMessage());
            }
        }
    }



    @Override
    public void updateStoreState(Store store) {
        StoreTypes st = store.getType();
        switch (st) {
            case AUTH_STORE:
                if (AuthStore.getInstance().isLoggingIn()) {
                    this.mProgressDialog.setMessage("Fasten the seat belt!");
                    this.mProgressDialog.show();
                } else {
                    try {
                        if (this.mProgressDialog.isShowing()) {

                            this.mProgressDialog.dismiss();
                        }

                        if (AuthStore.getInstance().isSignInIsSuccessful() && FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser() != null) {

                            finish();
                            unsubscribeFromAll();
                            startActivity(new Intent(this, BrowseActivity.class));
                        } else {

                            String err = AuthStore.getInstance().getSignInError();
                            if(err != ""){

                                Toast.makeText(this, "Oupsie... A road accident...\n"+err, Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {

                        Log.e("Progress bar broke", e.getMessage());
                    }
                }
                break;
            case CONNECTIVITY_STORE:
                // is not displayed for some reason, whatever...
                if (ConnectivityStore.getInstance().getIsNetworkConnected()) {

                    mToolbar.setVisibility(View.GONE);
                    Toast.makeText(this, "Internet connected", Toast.LENGTH_LONG).show();
                } else {

                    // Change it to be using an icon for it!

                    mToolbar.setVisibility(View.VISIBLE);
                    mToolbar.setTitle("No Connection");
                    Toast.makeText(this, "Internet not connected!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void unsubscribeFromAll() {
        this.mAuthStore.unSubscribeControllerView(this);
        this.mConnectivityStore.unSubscribeControllerView(this);
        this.mProfileStore.unSubscribeControllerView(this);
    }
}
