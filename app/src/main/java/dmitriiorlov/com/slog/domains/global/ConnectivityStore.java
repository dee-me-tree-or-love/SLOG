package dmitriiorlov.com.slog.domains.global;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.domains.auth.utils.NetworkUtil;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;
import dmitriiorlov.com.slog.flux.roles.StoreCallback;

/**
 * Created by Dmitry on 6/8/2017.
 */

public class ConnectivityStore implements StoreCallback, Store {

    // [ SINGLETON PATTERN STUFF ]

    private static volatile ConnectivityStore mInstance;

    public static ConnectivityStore getInstance() {
        if (mInstance == null) {
            synchronized (ConnectivityStore.class) {
                if (mInstance == null) {
                    mInstance = new ConnectivityStore();
                }
            }
        }
        return mInstance;
    }

    // [ SINGLETON PATTERN STUFF ]

    // list of the ControllerViews Observing the store
    private HashSet<ControllerView> mControllerViews;
    // private properties
    private boolean mIsConnected;


    private  ConnectivityStore(){
        this.mControllerViews = new HashSet<>();
        this.mIsConnected = false;

        GlobalDispatcher.getInstance().addStoreCallback(this);
    }

    public boolean getIsNetworkConnected(){
        return this.mIsConnected;
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
        return StoreTypes.CONNECTIVITY_STORE;
    }

    @Override
    public void notifyStateChange() {
        for (ControllerView cv : this.getInstance().mControllerViews) {
            cv.updateStoreState(this);
        }
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
        GlobalDispatcher.getInstance().removeStoreCallback(this);
        this.mInstance = null;
    }

    // skips
    @Override
    public boolean signIn(Context context, String email, String password) {
        return false;
    }

    // skips
    @Override
    public boolean signUp(Context context, String name, String email, String password) {
        return false;
    }

    @Override
    public boolean checkConnection(Context context) {
        boolean connectionEstablished = NetworkUtil.checkConnection(context);
        this.mIsConnected = connectionEstablished;
        notifyStateChange();
        return connectionEstablished;
    }

    @Override
    public void onProfileDataChanged(User user) {
        // ignore
    }

    @Override
    public void onProfileDocumentsChanged(List<Document> documentList) {
        // ignore
    }


    @Override
    public boolean signOut(Context context) {
        // TODO: think whether we can ignore it or not
        return false;
    }

    @Override
    public void queryDocumentByKey(Context context, String key) {

    }

    @Override
    public void onDocumentDataRetrieved(Document document, String key) {

    }

    @Override
    public void onDocumentSubmitAttempt(Document localDocument) {

    }

    @Override
    public void onDocumentRemoveAttempt(String key) {

    }

}
