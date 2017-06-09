package dmitriiorlov.com.slog.domains.documents;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
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
    @Deprecated
    private List<String> mDocumentKeys;

    //    private String mSelectedDocumentKey;
    private Document mDocumentInEditMode;
    private boolean mIsInEditMode;
    // to be requested from the edit document view, since this determines push or update method on the firebase instance
    private boolean mIsNewDocument;
    private String mSelectedDocumentKey;

    // list of the ControllerViews Observing the store
    private List<ControllerView> mControllerViews;

    private DocumentStore() {
        this.mControllerViews = new ArrayList<>();
        this.registerToDispatcher();

        this.mDocumentKeys = new ArrayList<>();
        this.mDocumentList = new ArrayList<>();
        this.mSelectedDocumentKey = "";
        this.mDocumentInEditMode = null;
        this.mIsInEditMode = false;
        mIsNewDocument = false;
    }

    public String getSelectedDocumentKey() {
        return this.mSelectedDocumentKey;
    }

    public boolean getIsNewDocument(){
        return this.mIsNewDocument;
    }

    public boolean getIsInEditMode() {
        return this.mIsInEditMode;
    }

    public Document getDocumentInEditMode() {
        return this.mDocumentInEditMode;
    }

    public List<Document> getDocumentList() {
        return this.mDocumentList;
    }

    @Deprecated
    public List<String> getDocumentKeys() {
        return this.mDocumentKeys;
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

//    @Override
//    public void onProfileDocumentsChanged(List<Document> documentList, List<String> keys) {
//        // TODO: handle the new documents!
//        this.mDocumentList = documentList;
//        this.mDocumentKeys = keys;
//        notifyStateChange();
//    }

    @Override
    public boolean signOut(Context context) {
        this.destroySelf();
        return false;
    }

    @Override
    public void queryDocumentByKey(Context context, String key) {
        // request the resource once from the firebase with a given key
        boolean queryResult = FireBaseUtil.getInstance().getCurrentUserDocumentByKey(key);
        // didn't retrieve anything --> need to create a new document
        if(!queryResult){
            this.mIsInEditMode = true;
            this.mDocumentInEditMode = new Document();
            this.mIsNewDocument = true;
            this.mSelectedDocumentKey = "";
            notifyStateChange();
        }
        // if the query has resulted in true, we can assume that from
        // now the firebase will notify as soon as it gets the correct info...
        // if everything goes well, the method onDocumentDataRetrieved will be called
    }

    @Override
    public void onDocumentDataRetrieved(Document document, String key) {
        this.mDocumentInEditMode = document;
        this.mIsInEditMode = true;
        // since we have successfully retrieved the document
        this.mIsNewDocument = false;
        this.mSelectedDocumentKey = key;
        notifyStateChange();
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
