package dmitriiorlov.com.slog.flux;

/**
 * Created by Dmitry on 6/8/2017.
 */

public enum StoreTypes {
    /**
     * The authorization store -> manages the sign in/up logic and data
     */
    AUTH_STORE,
    /**
     * The connectivity store makes sure the connection is available and notifies the connection state
     */
    CONNECTIVITY_STORE,
    /**
     * The document store is responsible for managing the document (notes) data
     * Manages such stuff as adding / deleting / editing the documents
     */
    DOCUMENT_STORE,
    /**
     * The profile store is responsible for maintaining the profile information
     */
    PROFILE_STORE
}
