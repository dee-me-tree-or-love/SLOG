package dmitriiorlov.com.slog.flux;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.flux.roles.Dispatcher;
import dmitriiorlov.com.slog.flux.roles.StoreCallback;

/**
 * Created by Dmitry on 6/7/2017.
 */

public class GlobalDispatcher implements Dispatcher {

    // [ SINGLETON PATTERN STUFF ]

    // TODO: consider using Hash Set to avoid duplicates
    private List<StoreCallback> mStoreCallbacks = new ArrayList<>();

    // solution from SourceMaking.com
    private static class GlobalDispatcherHolder {
        private static final GlobalDispatcher INSTANCE = new GlobalDispatcher();
    }

    public static GlobalDispatcher getInstance() {
        return GlobalDispatcher.GlobalDispatcherHolder.INSTANCE;
    }

    private GlobalDispatcher() {
    }

    public void addStoreCallback(StoreCallback sc) {
        this.mStoreCallbacks.add(sc);
    }

    public void removeStoreCallback(StoreCallback sc) {
        this.mStoreCallbacks.remove(sc);
    }

    // [ SINGLETON PATTERN STUFF ]

    // [ EXPOSED ACTIONS ]
    public boolean performSignIn(Context context, String email, String password) {
        try {
            // TODO: consider multithreading and a count down latch for optimisation!
            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.signIn(context, email, password);
            }
            return true;
        } catch (Exception e) {

            Log.e("Perform SignIn problem", e.getMessage());
            return false;
        }
    }

    public boolean performSignUp(Context context, String name, String email, String password) {
        try {
            // TODO: consider multithreading and a count down latch for optimisation!
            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.signUp(context, name, email, password);
            }
            return true;
        } catch (Exception e) {

            Log.e("Perform SignUp problem", e.getMessage());
            return false;
        }
    }

    public boolean checkNetworkConnection(Context context) {
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.checkConnection(context);
            }
            return true;
        } catch (Exception e) {

            Log.e("Network Check problem", e.getMessage());
            return false;
        }
    }

    public void updateUserData(User user) {
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.onProfileDataChanged(user);
            }
        } catch (Exception e) {

            Log.e("Update profile problem", e.getMessage());
        }
    }

    public void updateUserDocumentList(List<Document> documentList) {
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.onProfileDocumentsChanged(documentList);
            }
        } catch (Exception e) {

            Log.e("Document update problem", e.getMessage());
        }
    }

//    public void updateUserDocumentList(List<Document> documentList, List<String> keys){
//        try {
//
//            for (StoreCallback sc : this.mStoreCallbacks) {
//                sc.onProfileDocumentsChanged(documentList, keys);
//            }
//        } catch (Exception e) {
//
//            Log.e("Document update problem", e.getMessage());
//        }
//    }

    public void performSignOut(Context context) {
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.signOut(context);
            }
        } catch (Exception e) {

            Log.e("Signing out problem", e.getMessage());
        }
    }

    public void requestDocumentByKey(Context context, String key) {
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.queryDocumentByKey(context,key);
            }
        } catch (Exception e) {

            Log.e("Signing out problem", e.getMessage());
        }
    }

    public void retrievedSingleDocument(Document document, String key){
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.onDocumentDataRetrieved(document, key);
            }
        } catch (Exception e) {

            Log.e("Signing out problem", e.getMessage());
        }
    }

    public void attemptDocumentSubmit(Document localDocument){
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.onDocumentSubmitAttempt(localDocument);
            }
        } catch (Exception e) {

            Log.e("Signing out problem", e.getMessage());
        }
    }

    public void attemptDocumentRemove(String key){
        try {

            for (StoreCallback sc : this.mStoreCallbacks) {
                sc.onDocumentRemoveAttempt(key);
            }
        } catch (Exception e) {

            Log.e("Signing out problem", e.getMessage());
        }
    }

}
