package dmitriiorlov.com.slog.domains.documents;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;
import dmitriiorlov.com.slog.flux.roles.StoreCallback;

/**
 * Created by Dmitry on 6/8/2017.
 */

public class DocumentStore implements Store, StoreCallback {

    // [ SINGLETON PATTERN STUFF ]

    private static volatile DocumentStore mInstance;

    public static DocumentStore getInstance() {
        if (mInstance == null) {
            synchronized (DocumentStore.class) {
                if (mInstance == null) {
                    mInstance = new DocumentStore();
                }
            }
        }
        return mInstance;
    }

    // [ SINGLETON PATTERN STUFF ]

    private List<Document> mDocumentList;

    // list of the ControllerViews Observing the store
    private List<ControllerView> mControllerViews;

    private  DocumentStore() {
        this.mControllerViews = new ArrayList<>();
        this.registerToDispatcher();

        this.mDocumentList = new ArrayList<>();
    }

    public List<Document> getDocumentList(){
        return this.mDocumentList;
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

    @Override
    public boolean signIn(Context context, String email, String password) {
        return false;
    }

    @Override
    public boolean signUp(Context context, String name, String email, String password) {
        return false;
    }

    @Override
    public boolean checkConnection(Context context) {
        return false;
    }

    @Override
    public void onProfileDataChanged(User user) {
        // ignore..
    }

    @Override
    public void onProfileDocumentsChanged(List<Document> documentList) {
        // TODO: handle the new documents!
        this.mDocumentList = documentList;
        notifyStateChange();
    }

    @Override
    public boolean signOut(Context context) {
        this.destroySelf();
        return false;
    }

    // TODO: think whether you can move it to an abstract class... cause it is fucking annoying to retype...
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
        return StoreTypes.DOCUMENT_STORE;
    }

    @Override
    public void notifyStateChange() {
        for (ControllerView cv : this.getInstance().mControllerViews) {
            cv.updateStoreState(this);
        }
    }
}
