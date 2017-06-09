package dmitriiorlov.com.slog.domains.documents.edit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.domains.documents.DocumentStore;
import dmitriiorlov.com.slog.domains.global.ConnectivityStore;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;

public class EditActivity extends AppCompatActivity implements ControllerView {

    // responsible for the document(s) related data
    private DocumentStore mDocumentStore;
    // responsible for the connectivity issues
    private ConnectivityStore mConnectivityStore;
    // responsible for the profile data
    private ProfileStore mProfileStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // bind all the views
        ButterKnife.bind(this);
        // bind the stores
        mDocumentStore = DocumentStore.getInstance();
        mDocumentStore.subscribeControllerView(this);

        mConnectivityStore = ConnectivityStore.getInstance();
        mConnectivityStore.subscribeControllerView(this);

        mProfileStore = ProfileStore.getInstance();
        mProfileStore.subscribeControllerView(this);


    }

    @Override
    public void updateStoreState() {

    }

    @Override
    public void updateStoreState(Store store) {
        // do the handling
    }

    @Override
    public void unsubscribeFromAll() {
        mProfileStore.unSubscribeControllerView(this);
        mConnectivityStore.unSubscribeControllerView(this);
        mDocumentStore.unSubscribeControllerView(this);
    }
}
