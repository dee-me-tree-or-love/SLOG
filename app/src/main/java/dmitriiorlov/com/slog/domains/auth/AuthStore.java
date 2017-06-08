package dmitriiorlov.com.slog.domains.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.domains.auth.utils.NetworkUtil;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;
import dmitriiorlov.com.slog.flux.roles.StoreCallback;

/**
 * Created by Dmitry on 6/7/2017.
 */

public class AuthStore implements Store, StoreCallback {

    // [ SINGLETON PATTERN STUFF ]

    private static volatile AuthStore mInstance;

    // solution from SourceMaking.com #1 -- proved itself not useful...
//    private static class AbstractStoreHolder {
//        private static final AuthStore INSTANCE = new AuthStore();
//    }

//    private void createInstance() {
//        this.mInstance = new AbstractStore();
//    }

    public static AuthStore getInstance() {
        if (mInstance == null) {
            synchronized (AuthStore.class) {
                if (mInstance == null) {
                    mInstance = new AuthStore();
                }
            }
        }
        return mInstance;
    }

    // [ SINGLETON PATTERN STUFF ]

    // [ STORE IMPLEMENTATION STUFF ]

    // list of the ControllerViews Observing the store
    private List<ControllerView> mControllerViews;
    // the email entered by the user
    private String mEmail;
    private String mInputEmail;
    private String mInputName;
    private boolean mIsLoggingIn;
    private boolean mSignInIsSuccessful;
    private String mSignInError;


    protected AuthStore() {
        this.mControllerViews = new ArrayList<>();
        this.mEmail = "";
        this.mInputName = "";
        this.mInputEmail = "";
        this.mIsLoggingIn = false;
        this.mSignInIsSuccessful = false;
        this.mSignInError = "";
        // subscribe as the Dispatcher callback
        this.registerToDispatcher();
    }


    public boolean isLoggingIn() {
        return this.mIsLoggingIn;
    }

    public boolean isSignInIsSuccessful() {
        return this.mSignInIsSuccessful;
    }

    @Deprecated
    public String getCurrentEmail() {
        return this.mEmail;
    }

    public String getInputEmail() {
        return this.mEmail;
    }

    public String getInputName() {
        return this.mInputName;
    }

    public String getSignInError() {
        return this.mSignInError;
    }

    public void setInputEmail(String changeInputEmail) {
        this.mInputEmail = changeInputEmail;
        // Should be done, but is ignored in this case
//        notifyStateChange();
    }

    public void setInputName(String changeInputName) {
        this.mInputName = changeInputName;
        // Should be done, but is ignored in this case
//        notifyStateChange();
    }

    @Override
    public void subscribeControllerView(ControllerView cv) {
        this.getInstance().mControllerViews.add(cv);
    }

    @Override
    public void unSubscribeControllerView(ControllerView cv) {
        this.getInstance().mControllerViews.remove(cv);
    }

    @Override
    public StoreTypes getType() {
        return StoreTypes.AUTH_STORE;
    }

    @Override
    public void notifyStateChange() {
        for (ControllerView cv : this.getInstance().mControllerViews) {
            cv.updateStoreState(this);
        }
    }

    /**
     * Checks if there is already a user logged in
     *
     * @param context
     * @return true if logged in and false if not
     */
    public boolean isFirebaseLoggedIn(Context context) {
        //if getCurrentUser does not returns null => already logged in
        return (FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser() != null);
    }

    @Override
    public void registerToDispatcher() {
        GlobalDispatcher.getInstance().addStoreCallback(this);
    }

    @Override
    public void deregisterFromDispatcher() {
        GlobalDispatcher.getInstance().removeStoreCallback(this);
    }

    @Override
    synchronized
    public void destroySelf() {
        this.deregisterFromDispatcher();
        this.mInstance = null;
    }

    // TODO: implement the shit
    @Override
    synchronized
    public boolean signIn(Context context, final String email, String password) {

        this.mIsLoggingIn = true;
        this.notifyStateChange();

        // go directly to context to set the progress bar!
        FireBaseUtil.getInstance()
                .getFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                // try signing in, notify when managed to
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // managed to login
                            mIsLoggingIn = false;
                            mSignInIsSuccessful = true;

                            // make the profile view query the profile updates from now on
                            ProfileStore.getInstance().queryTheUserProfile();
                            notifyStateChange();
                        } else {

                            mSignInError = task.getException().getMessage();
                            // failed to login
                            mIsLoggingIn = false;
                            mSignInIsSuccessful = false;
                            notifyStateChange();
                        }

                    }
                });
        mSignInError = "";
        return mSignInIsSuccessful;
    }

    @Override
    synchronized
    public boolean signUp(Context context, String name, final String email, String password) {
        this.mIsLoggingIn = true;
        this.notifyStateChange();
        final String nameToSubmit = name;
        final String emailToSubmit = email;
        // go directly to context to set the progress bar!
        FireBaseUtil.getInstance()
                .getFirebaseAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // managed to login
                            mIsLoggingIn = false;
                            // add new user
                            FirebaseUser user = FireBaseUtil
                                    .getInstance()
                                    .getFirebaseAuth()
                                    .getCurrentUser();

                            User userData = new User(nameToSubmit, emailToSubmit);

                            try {

                                FireBaseUtil.getInstance()
                                        .getDatabaseReference()
                                        .child("users").child(user.getUid()).setValue(userData);
                                mSignInIsSuccessful = true;
                                // make the profile view query the profile updates from now on
                                ProfileStore.getInstance().queryTheUserProfile();

                            } catch (Exception e) {

                                Log.e("Sign Up Error", e.getMessage());
                                mSignInIsSuccessful = false;
                                mSignInError = e.getMessage();
                            }

                            notifyStateChange();
                        } else {

                            // failed to login
                            mIsLoggingIn = false;
                            mSignInIsSuccessful = false;
                            mSignInError = task.getException().getMessage();
                            notifyStateChange();
                        }

                    }
                });
        mSignInError = "";
        return mSignInIsSuccessful;
    }

    // nothing...
    @Override
    public boolean checkConnection(Context context) {

        boolean connectionEstablished = NetworkUtil.checkConnection(context);

        // check for the login with firebase
        boolean isLoggedIn = this.isFirebaseLoggedIn(context);
        this.mSignInIsSuccessful = isLoggedIn;
        this.mIsLoggingIn = false;


        if (connectionEstablished) {
            if(isLoggedIn){
                // should get the profile in any way!
                ProfileStore.getInstance().queryTheUserProfile();
            }

            notifyStateChange();
            return isLoggedIn;

        } else {

            if(!isLoggedIn){

                this.mSignInIsSuccessful = false;
                this.mIsLoggingIn = false;
            }

            notifyStateChange();
            // TODO: check whether a user has ever logged in {local db}
            return connectionEstablished;
        }
    }

    @Override
    public void onProfileDataChanged(User user) {
        // ignore
    }

    @Override
    public void onProfileDocumentsChanged(List<Document> documentList) {

    }


}

//        //if getCurrentUser does not returns null => already logged in
//        if(mAuth.getCurrentUser() != null){
//
//            finish();
//            //open profile activity
//            startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
//        }
