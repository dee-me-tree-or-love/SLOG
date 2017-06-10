package dmitriiorlov.com.slog.domains.documents.edit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.data.models.Document;
import dmitriiorlov.com.slog.domains.documents.DocumentStore;
import dmitriiorlov.com.slog.domains.global.ConnectivityStore;
import dmitriiorlov.com.slog.domains.global.ProfileStore;
import dmitriiorlov.com.slog.flux.GlobalDispatcher;
import dmitriiorlov.com.slog.flux.StoreTypes;
import dmitriiorlov.com.slog.flux.roles.ControllerView;
import dmitriiorlov.com.slog.flux.roles.Store;

public class EditActivity extends AppCompatActivity implements ControllerView {

    @Deprecated
    private void setUpTextFromStore() {
        String text = mDocumentStore.getDocumentInEditMode().getText();
        mTextViewText.setText(text);
    }

    @Deprecated
    private void setUpDateFromStore() {
        // set Up the Date
        Date editDate = new Date();
        long editDateLong = mDocumentStore.getDocumentInEditMode().getDate();
        if (editDateLong != 0) {
            editDate = new Date(editDateLong);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String editDateString = sdf.format(editDate);
        mToolbar.setTitle(editDateString);
    }

    // responsible for the document(s) related data
    private DocumentStore mDocumentStore;
    // responsible for the connectivity issues
    private ConnectivityStore mConnectivityStore;
    // responsible for the profile data
    private ProfileStore mProfileStore;

    @BindView(R.id.edit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_note_text)
    TextView mTextViewText;

    private Document mLocalDocument;

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


        mLocalDocument = new Document();

        // first creates a local copy of the document from the store, to check whether it is different or not
        this.setUpLocalDocument();
        // getting data from store
//        this.setUpDateFromStore();
//        this.setUpTextFromStore();
    }

    // setup the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_document, menu);
        // check whether the app is online
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.miDelete:
                //Toast.makeText(this, "Deleting is not yet possible :)", Toast.LENGTH_SHORT).show();
                GlobalDispatcher.getInstance().attemptDocumentRemove(DocumentStore.getInstance().getSelectedDocumentKey());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnTextChanged(R.id.edit_note_text)
    public void updateDocumentText() {
        this.mLocalDocument.setText(this.mTextViewText.getText().toString());
    }

    // [ SETTING UP VIEWS ]
    private void setUpTextView(String text) {
        mTextViewText.setText(text);
    }

    private void setUpNoteDate(long date) {
        Date editDate = new Date();
        long editDateLong = date;
        if (editDateLong != 0) {
            editDate = new Date(editDateLong);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String editDateString = sdf.format(editDate);
        mToolbar.setTitle(editDateString);
        setSupportActionBar(mToolbar);
    }

    private void setUpLocalDocument() {
        this.mLocalDocument = new Document();
        try{

        this.mLocalDocument.setDate(this.mDocumentStore.getDocumentInEditMode().getDate());
        this.mLocalDocument.setText(this.mDocumentStore.getDocumentInEditMode().getText());
        }catch (NullPointerException e){

            Log.e("EmptyDocumentStore",e.getMessage());

            this.mLocalDocument.setDate((new Date()).getTime());
            this.mLocalDocument.setText("");
        }
        // set up the views from the local stuff
        this.setUpNoteDate(this.mLocalDocument.getDate());
        this.setUpTextView(this.mLocalDocument.getText());
    }


    // [ HANDLING ACTIONS ]
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GlobalDispatcher.getInstance().attemptDocumentSubmit(mLocalDocument);
        this.unsubscribeFromAll();
    }

    @Override
    public void updateStoreState() {

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
            default:
                break;
        }
    }

    private void handleDocumentStoreNotification() {
        if(DocumentStore.getInstance().getIsSuccesfullyDeletedDocument()){

            finish();
        }else{

//            Toast.makeText(this,"Not possible to delete now",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void unsubscribeFromAll() {
        mProfileStore.unSubscribeControllerView(this);
        mConnectivityStore.unSubscribeControllerView(this);
        mDocumentStore.unSubscribeControllerView(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        this.unsubscribeFromAll();
    }

}
