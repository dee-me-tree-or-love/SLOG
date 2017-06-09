package dmitriiorlov.com.slog.domains.global;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;
import dmitriiorlov.com.slog.flux.roles.StoreCallback;

/**
 * Created by Dmitry on 6/8/2017.
 */

public class ProfileStore implements StoreCallback, Store {

    // [ SINGLETON PATTERN STUFF ]

    private static volatile ProfileStore mInstance;

    public static ProfileStore getInstance() {
        if (mInstance == null) {
            synchronized (ProfileStore.class) {
                if (mInstance == null) {
                    mInstance = new ProfileStore();
                }
            }
        }
        return mInstance;
    }

    // [ SINGLETON PATTERN STUFF ]

    private String mProfileName;
    private String mProfileEmail;
    private boolean mSignedIn;

    // list of the ControllerViews Observing the store
    private List<ControllerView> mControllerViews;

    private  ProfileStore(){
        this.mControllerViews = new ArrayList<>();
        this.registerToDispatcher();

        this.mProfileName = "the ?";
        this.mProfileEmail = "";
        // not sure about this one
        this.mSignedIn = false;
    }

    // just a way to solve it for now, later use countdown latch for this purpose!
    public void queryTheUserProfile(){
        this.mSignedIn = true;
        FireBaseUtil.getInstance().requestCurrentUserData();
        FireBaseUtil.getInstance().requestCurrentUserDocuments();
    }

    public  boolean getIsSignedIn(){
        return this.mSignedIn;
    }

    public String getProfileName(){
        return this.mProfileName;
    }

    public String getProfileEmail(){
        return this.mProfileEmail;
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
    public void destroySelf() {
        this.mInstance = null;
        this.deregisterFromDispatcher();
    }

    @Override
    public boolean signIn(Context context, String email, String password) {
        this.mProfileEmail = email;
        this.mSignedIn = true;
        // name should be retrieved via the query function from the AuthStore to avoid conflicts
        return false;
    }

    @Override
    public boolean signUp(Context context, String name, String email, String password) {
        // we can assume that this is allowed and will be working safely, but we need to be careful
        this.mProfileName = name;
        this.mSignedIn = true;
        this.mProfileEmail = name;
        notifyStateChange();
        return false;
    }

    @Override
    public boolean checkConnection(Context context) {
        return false;
    }

    @Override
    public void onProfileDataChanged(User user) {
        this.mProfileEmail = user.getEmail();
        this.mProfileName = user.getName();
        this.notifyStateChange();
    }

    @Override
    public void onProfileDocumentsChanged(List<Document> documentList) {
        // ignoring...
    }


    @Override
    public boolean signOut(Context context) {
        // TODO: handle the sign out request
        boolean isLoggedIn = FireBaseUtil.getInstance().checkWhetherIsLoggedIn(context);
        if(isLoggedIn){

            FireBaseUtil.getInstance().getFirebaseAuth().signOut();
            this.mSignedIn = false;
            this.unsetProfileName();
            this.mProfileEmail = "";
        }else{

            Log.e("LOGOUT PROBLEM","User appears not to be loggged in!");
        }

        notifyStateChange();
        return isLoggedIn;
    }

    @Override
    public void queryDocumentByKey(Context context, String key) {

    }

    private void unsetProfileName(){
        this.mProfileName = "the ?";
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
        return StoreTypes.PROFILE_STORE;
    }

    @Override
    public void notifyStateChange() {
        for (ControllerView cv : this.getInstance().mControllerViews) {
            cv.updateStoreState(this);
        }
    }
}
