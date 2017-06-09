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
        try{

            this.mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        }catch (Exception e){

            Log.e("Reference setup",e.getMessage());
        }
    }

    private static class SingletonHolder {
        private static final FireBaseUtil INSTANCE = new FireBaseUtil();
    }

    public static FireBaseUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // is used to get the instances
    public FirebaseAuth getFirebaseAuth(){
        return this.mFirebaseAuth;
    }

    public FirebaseDatabase getFirebaseDatabase(){
        return this.mDatabase;
    }

    public DatabaseReference getDatabaseReference(){
        return this.mDatabaseReference;
    }

    public void requestCurrentUserData(){

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

    public void requestCurrentUserDocuments(){
        FirebaseUser currentUser = FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser();
        DatabaseReference ref = FireBaseUtil.getInstance().getDatabaseReference()
                .child("users").child(currentUser.getUid()).child("documents");
        // getting the data once from the firebase
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Document> documentList = new ArrayList<Document>();
                for(DataSnapshot docSnapshot: dataSnapshot.getChildren()){
                    Document doc = docSnapshot.getValue(Document.class);
                    documentList.add(doc);
                }
                GlobalDispatcher.getInstance().updateUserDocumentList(documentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Doc data request", "Failed to retrieve the documents");
            }
        });
    }

    public boolean checkWhetherIsLoggedIn(Context context){
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
