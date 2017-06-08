package dmitriiorlov.com.slog.domains.global;

import android.content.Context;

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

    // list of the ControllerViews Observing the store
    private List<ControllerView> mControllerViews;

    private  ProfileStore(){
        this.mControllerViews = new ArrayList<>();
        this.registerToDispatcher();

        this.mProfileName = "";
        this.mProfileEmail = "";
    }

    // just a way to solve it for now, later use countdown latch for this purpose!
    public void queryTheUserProfile(){
        FireBaseUtil.getInstance().requestCurrentUserData();
        FireBaseUtil.getInstance().requestCurrentUserDocuments();
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
        // name should be retrieved
        return false;
    }

    @Override
    public boolean signUp(Context context, String name, String email, String password) {
        // we can assume that this is allowed and will be working safely, but we need to be careful
        this.mProfileName = name;
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
