package dmitriiorlov.com.slog.data.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.data.models.User;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;

/**
 * Created by Dmitry on 6/7/2017.
 */

public class FireBaseUtil {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private FireBaseUtil() {
        try {

            this.mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        } catch (Exception e) {

            Log.e("Reference setup", e.getMessage());
        }
    }

    private static class SingletonHolder {
        private static final FireBaseUtil INSTANCE = new FireBaseUtil();
    }

    public static FireBaseUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // is used to get the instances
    public FirebaseAuth getFirebaseAuth() {
        return this.mFirebaseAuth;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return this.mDatabase;
    }

    public DatabaseReference getDatabaseReference() {
        return this.mDatabaseReference;
    }

    public DatabaseReference getUserDocumentsDatabaseReference() {
        FirebaseUser currentUser = FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser();
        DatabaseReference ref = FireBaseUtil.getInstance().getDatabaseReference()
                .child("users").child(currentUser.getUid()).child("documents");
        return ref;
    }

    public void requestCurrentUserData() {

        FirebaseUser currentUser = FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser();
        DatabaseReference ref = FireBaseUtil.getInstance().getDatabaseReference().child("users").child(currentUser.getUid());
        // getting the data once from the firebase
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                GlobalDispatcher.getInstance().updateUserData(currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data request", "Failed to retrieve the data");
            }
        });
    }

    public void requestCurrentUserDocuments() {
        DatabaseReference ref = this.getUserDocumentsDatabaseReference();
        // getting the data once from the firebase
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Document> documentList = new ArrayList<Document>();
//                List<String> keys = new ArrayList<String>();
                for (DataSnapshot docSnapshot : dataSnapshot.getChildren()) {
                    Document doc = docSnapshot.getValue(Document.class);
                    documentList.add(doc);
//                    keys.add(docSnapshot.getKey());
                }
                // sort the stuff ... maybe?
                // ArrayList.sort is not supported :C
                // TODO: make sure this stuff works well enough...
                Collections.sort(documentList, new Comparator<Document>() {
                    @Override
                    public int compare(Document o1, Document o2) {
                        return (int) (o1.getDate() - o2.getDate());
                    }
                });

                GlobalDispatcher.getInstance().updateUserDocumentList(documentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Doc data request", "Failed to retrieve the documents");
            }
        });
    }

    /**
     * Requests the data about 1 particular document entry or prepares to create a new one, if the key is unset
     *
     * @param key
     * @return result of looking up the data in the Firebase
     */
    public boolean getCurrentUserDocumentByKey(String key) {
        //TODO: implement
        if (key == "") {
            return false;
            // meaning, we are not even going to look for such key, handle it yourself
        }

        // reference to the child of the user's documents => the only one with the specified ID.
        DatabaseReference ref = FireBaseUtil.getInstance()
                .getUserDocumentsDatabaseReference().child(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if the datasnapshot was retrieved succesfully
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DocumentByKeyFail", "Failed to retrieve the document data by key");
            }
        });

        return true;
    }

    public boolean checkWhetherIsLoggedIn(Context context) {
        return (FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser() != null);
    }

//    public static boolean loginWithEmailAndPassword(String email, String password){
//        try{
//
//            getInstance().mFirebaseAuth.signInWithEmailAndPassword(email,password);
//            return true;
//        }catch (Exception e){
//            Log.e("Login exception:",e.getMessage());
//            return false;
//        }
//    }


//    synchronized
//    public static DatabaseReference retrieveUserDataRef(FirebaseUser user){
//        try{
//
//            // DatabaseReference ref = mDatabase.getReference().child("users").child(mUser.getUid()
//            DatabaseReference ref = getInstance().getDatabaseReference().child("users").child(user.getUid());
//            return ref;
//        }catch (Exception e){
//
//            Log.e("Login exception:",e.getMessage());
//            return null;
//        }
//    }
}
