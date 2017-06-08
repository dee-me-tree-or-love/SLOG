package dmitriiorlov.com.slog.domains.documents.browse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.data.firebase.FireBaseUtil;
import dmitriiorlov.com.slog.domains.documents.DocumentStore;
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


    // Stores:
    // responsible for the document(s) related data
    private DocumentStore mDocumentStore;
    // responsible for the connectivity issues
    private ConnectivityStore mConnectivityStore;
    // responsible for the profile data
    private ProfileStore mProfileStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // bind all the views
        ButterKnife.bind(this);
        // bind the stores
        mDocumentStore = DocumentStore.getInstance();
        mDocumentStore.subscribeControllerView(this);

        mConnectivityStore = ConnectivityStore.getInstance();
        mConnectivityStore.subscribeControllerView(this);

        mProfileStore = ProfileStore.getInstance();
        mProfileStore.subscribeControllerView(this);

        // set the app bar
        //TODO: change the title to the user's name
//        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        // check if the user is actually retrieved
        try {
            Log.i("Current user", FireBaseUtil.getInstance().getFirebaseAuth().getCurrentUser().getEmail());
        } catch (Exception e) {
            Log.e("Current user", e.getMessage());
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

    private void showMenuPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_browse_popup, popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public void updateStoreState() {
        // deprecated
    }

    @Override
    public void updateStoreState(Store store) {
        StoreTypes st = store.getType();
        switch (st) {
            case DOCUMENT_STORE:
                // do the related document display adjustments
                Toast.makeText(this,"Document Store notification: " + DocumentStore.getInstance().getDocumentList().size(),Toast.LENGTH_SHORT).show();
                break;
            case CONNECTIVITY_STORE:

                // do the connectivity related display adjustments
                if(ConnectivityStore.getInstance().getIsNetworkConnected()){
                    Menu menu = this.mToolbar.getMenu();
                    MenuItem mi = menu.findItem(R.id.miBrowseCloud);
                    mi.setIcon(R.drawable.ic_cloud_done_black_24dp);
                }else{

                    Menu menu = this.mToolbar.getMenu();
                    MenuItem mi = menu.findItem(R.id.miBrowseCloud);
                    mi.setIcon(R.drawable.ic_cloud_off_black_24dp);}

//                Toast.makeText(this,"Connectivity Store notifiacation", Toast.LENGTH_SHORT).show();
                break;
            case PROFILE_STORE:

                // set Title
                this.mToolbar.setTitle("SLOG of " + ProfileStore.getInstance().getProfileName());
        }
    }

    @Override
    public void unsubscribeFromAll() {
        mProfileStore.unSubscribeControllerView(this);
        mConnectivityStore.unSubscribeControllerView(this);
        mDocumentStore.unSubscribeControllerView(this);
    }
}
