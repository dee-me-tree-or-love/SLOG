package dmitriiorlov.com.slog.domains.documents.browse;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.domains.auth.LoginActivity;
import dmitriiorlov.com.slog.domains.documents.DocumentStore;
import dmitriiorlov.com.slog.domains.documents.browse.viewholders.BrowseDocumentViewHolder;
import dmitriiorlov.com.slog.domains.documents.edit.EditActivity;
import dmitriiorlov.com.slog.domains.global.ConnectivityStore;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;

public class BrowseActivity extends AppCompatActivity implements ControllerView {

    @BindView(R.id.browse_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.browse_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.browse_fab_new_note)
    FloatingActionButton mNewNoteFab;

    private boolean mRecyclerViewIsSetUp = false;
    private FirebaseRecyclerAdapter mDocumentBrowseAdapter;

    // Stores:
    // responsible for the document(s) related data
    private DocumentStore mDocumentStore;
    // responsible for the connectivity issues
    private ConnectivityStore mConnectivityStore;
    // responsible for the profile data
    private ProfileStore mProfileStore;

    private PopupMenu mCloudPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // bind all the views
        ButterKnife.bind(this);
        // bind the stores
        // TODO: figure out how to make the goddam orientation change not break anything....
        if(savedInstanceState==null){
            this.subscribeToStores();
        }


        // set the app bar
        setSupportActionBar(mToolbar);
        // TODO: good try, maybe switch to having the connectivity thing on the left, but it is not the major concern
        // for adding stuff you should still use a FAB
//        mToolbar.setNavigationIcon(R.drawable.ic_cloud_done_black_24dp);
        // setup the recycler view with notes

        // check if the user is actually retrieved
        try {
            Log.i("Current user", FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser().getEmail());
        } catch (Exception e) {
            Log.e("Current user", e.getMessage());
        }
    }

    private void subscribeToStores(){
        mDocumentStore = DocumentStore.getInstance();
        mDocumentStore.subscribeControllerView(this);

        mConnectivityStore = ConnectivityStore.getInstance();
        mConnectivityStore.subscribeControllerView(this);

        mProfileStore = ProfileStore.getInstance();
        mProfileStore.subscribeControllerView(this);
    }

    // opening the edit activity with the blank key --> the new note is being created
    @OnClick(R.id.browse_fab_new_note)
    public void createNewNote(View view) {
//        Toast.makeText(this, "Creating a new note", Toast.LENGTH_SHORT).show();
        GlobalDispatcher.getInstance().requestDocumentByKey(this, "");
    }

    private void setupRecyclerView() {
        Query query = FireBaseUtil.getInstance()
                .getUserDocumentsDatabaseReference()
                .orderByChild("date");

//        DatabaseReference ref = FireBaseUtil.getInstance().getUserDocumentsDatabaseReference();
        // TODO: verify that it works
        mDocumentBrowseAdapter = new FirebaseRecyclerAdapter<Document, BrowseDocumentViewHolder>
                (Document.class, R.layout.layout_document_row, BrowseDocumentViewHolder.class, query) {
            @Override
            protected void populateViewHolder(BrowseDocumentViewHolder viewHolder, Document model, int position) {
                viewHolder.setTextViewDate(model.getDate());
                viewHolder.setTextViewText(model.getText());
            }

            // handling clicks
            @Override
            public BrowseDocumentViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                BrowseDocumentViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                final ViewGroup parentRef = parent;
                viewHolder.setOnClickListener(new BrowseDocumentViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO: make the handling of the redirection to the clicked note
//                        Toast.makeText(parentRef.getContext(), "Item clicked at "
//                                + position + " with "
//                                + mDocumentBrowseAdapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                        GlobalDispatcher.getInstance().requestDocumentByKey(parent.getContext(),
                                // getting the key of the object in the firebase
                                mDocumentBrowseAdapter.getRef(position).getKey().toString());
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(parentRef.getContext(), "Item long clicked at " + position, Toast.LENGTH_SHORT).show();
                    }
                });
                return viewHolder;
            }

        };

        try {
            // this will make it reversed -- used to order the notes in descending
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setStackFromEnd(true);
            llm.setReverseLayout(true);
            mRecyclerView.setLayoutManager(llm);

            mRecyclerView.setAdapter(mDocumentBrowseAdapter);

            mRecyclerViewIsSetUp = true;
        } catch (NullPointerException e) {

            Log.e("RecyclerViewFail", e.getMessage());
            // Maybe binding didn't work
            this.mRecyclerView = (RecyclerView) findViewById(R.id.browse_recycler_view);
            this.setupRecyclerView();
        }
    }


    // [ MENU STUFF ]
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        // check whether the app is online
        GlobalDispatcher.getInstance().checkNetworkConnection(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.miBrowseSearch:
                // automatically triggers the search input
                break;

            case R.id.miBrowseCloud:
//                Toast.makeText(this,"Pressed Cloud",Toast.LENGTH_SHORT).show();
                this.showMenuPopup(findViewById(item.getItemId()));
                break;

            default:
                break;
        }
        return false;
    }

    /**
     * Used to setup the popup menu and its option handlers
     *
     * @param v
     */
    private void preparePopupMenu(View v) {
        mCloudPopupMenu = new PopupMenu(this, v);
        final Context context = this;
        mCloudPopupMenu.getMenuInflater().inflate(R.menu.menu_browse_popup, mCloudPopupMenu.getMenu());
        // registering actions:
        mCloudPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.browse_popup_connection_logout:

                        // do the logout stuff
                        GlobalDispatcher.getInstance().performSignOut(context);
                        break;

                    case R.id.browse_popup_connection_authorize:

                        // switch it to the login page
                        // after a delay?
                        // I HAVE NO CLUE HOW, BUT IT WORKED!
//                        final Handler handler = new Handler();
//                        Log.i("DelayedTask", "posting now");
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //Do something after 100ms
//                                Log.i("DelayedTask", "executing");
                        Log.i("Switch activity", "executing");
                        try {

                            finish();
                            unsubscribeFromAll();
                            startActivity(new Intent(context, LoginActivity.class));

                        } catch (Exception e) {

                            Log.e("DelayedTask EXC", e.getMessage());
                        }
//                            }
//                        }, 100);

                        // The line below DOES WORK, while the above shit does not!
//                        Toast.makeText(context, "WHHHHHHHAAAT? : " + item.getItemId(), Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(context, "You Clicked : " + item.getItemId() + "\nAnd there's nothing you can do about it", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void showMenuPopup(View v) {
        if (mCloudPopupMenu == null) {
            this.preparePopupMenu(v);
        }
        // TODO: research WHY! does it give the errors sometimes...
        try {

            mCloudPopupMenu.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PopupMenuFailure", e.getMessage());
        }
    }

    @Override
    public void updateStoreState() {
        // deprecated
    }


    // [ STORE STATE OBSERVATION HANDLERS ]

    private void handleDocumentStoreNotification() {
        if (!this.mRecyclerViewIsSetUp) {
            this.setupRecyclerView();
        }

        // check if needs redirecting to the edit activity:
        if(DocumentStore.getInstance().getIsInEditMode()){

            // do the switch of the activities
            // no finish in this case, as we do not need to loose the state
            startActivity(new Intent(this, EditActivity.class));
        }
//        mDocumentBrowseAdapter.notifyDataSetChanged();
    }

    private void handleConnectivityStoreNotification() {
        if (ConnectivityStore.getInstance().getIsNetworkConnected()) {

            Menu menu = this.mToolbar.getMenu();
            MenuItem mi = menu.findItem(R.id.miBrowseCloud);
            mi.setIcon(R.drawable.ic_cloud_done_black_24dp);
        } else {

            Menu menu = this.mToolbar.getMenu();
            MenuItem mi = menu.findItem(R.id.miBrowseCloud);
            mi.setIcon(R.drawable.ic_cloud_off_black_24dp);
        }

    }

    private void handleProfileStoreNotification() {
        try {

            boolean signedIn = (ProfileStore.getInstance().getIsSignedIn());
            this.setLogoutButtonEnabled(signedIn);
            // Basically now switched to local mode??
            // The funny thing that is the user now can not read any of the documents anymore...
            // or maybe better to take the user to the login screen?


        } catch (Exception e) {
            Log.e("Logout view prblm", e.getMessage());
        }
        // set Title
        this.mToolbar.setTitle("SLOG of " + ProfileStore.getInstance().getProfileName());
    }

    @Override
    public void updateStoreState(Store store) {
        StoreTypes st = store.getType();
        switch (st) {

//            //!!!!!!!! case DOCUMENT_STORE:
            case DOCUMENT_STORE:

                // do the related document display adjustments
                this.handleDocumentStoreNotification();
                break;

//            //!!!!!!!! case CONNECTIVITY_STORE:
            case CONNECTIVITY_STORE:

                // do the connectivity related display adjustments
                this.handleConnectivityStoreNotification();
                break;

//            //!!!!!!!! case PROFILE_STORE:
            case PROFILE_STORE:
                this.handleProfileStoreNotification();
                break;
        }
    }

    /**
     * Renders the popup menu according to the application state
     *
     * @param enabled
     */
    private void setLogoutButtonEnabled(boolean enabled) {
        try {
            try {

                if (enabled) {
                    this.mToolbar.getMenu()
                            .findItem(R.id.miBrowseCloud)
                            .setIcon(R.drawable.ic_cloud_done_black_24dp);
                } else {
                    this.mToolbar.getMenu()
                            .findItem(R.id.miBrowseCloud)
                            .setIcon(R.drawable.ic_cloud_queue_black_24dp);
                }
            } catch (Exception e) {
                // nothing to do about it...
            }

            if (mCloudPopupMenu == null) {
                this.preparePopupMenu(findViewById(R.id.miBrowseCloud));
            }
            MenuItem miOut = mCloudPopupMenu.getMenu().findItem(R.id.browse_popup_connection_logout);
            miOut.setEnabled(enabled);
            MenuItem miIn = mCloudPopupMenu.getMenu().findItem(R.id.browse_popup_connection_authorize);
            miIn.setEnabled(!enabled);

        } catch (Exception e) {
            Log.e("RENDER BROWSE ERROR", e.getMessage());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void unsubscribeFromAll() {
        mProfileStore.unSubscribeControllerView(this);
        mConnectivityStore.unSubscribeControllerView(this);
        mDocumentStore.unSubscribeControllerView(this);
    }
}
