package dmitriiorlov.com.slog.domains.documents.browse.viewholders;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmitriiorlov.com.slog.R;

/**
 * Created by Dmitry on 6/9/2017.
 */

public class BrowseDocumentViewHolder extends RecyclerView.ViewHolder {

    final static private int SUBSTRING_SIZE = 60;

    @BindView(R.id.text_document_browse_date)
    TextView mTextViewDate;
    @BindView(R.id.text_document_browse_text)
    TextView mTextViewText;

//    private String mKey;

    public BrowseDocumentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
//        itemView.setOnClickListener(this);

        //listener set on ENTIRE ROW, you may set on individual components within a row.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

    }

    public String getTextViewDateString() {
        return mTextViewDate.getText().toString();
    }

    public String getTextViewTextString() {
        return mTextViewText.getText().toString();
    }

    public void setTextViewDate(long date) {
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String dateString = sdf.format(new Date(date));
        this.mTextViewDate.setText(dateString);
    }

    public void setTextViewText(String text) {
        int lastIndex = BrowseDocumentViewHolder.SUBSTRING_SIZE - 1;
        String trailer = "...";
        String textFormatted = "";
        // text might be not set...
        try {
            if (text.length() < lastIndex) {
                lastIndex = text.length() - 1;
                trailer = "";
            }
            if (text.length() != 0) {
                textFormatted = text.substring(0, lastIndex);
            }
        } catch (NullPointerException e) {

            Log.e("TXTFAILURE", e.getMessage());
        }

        this.mTextViewText.setText(textFormatted + trailer);
    }


    private BrowseDocumentViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(BrowseDocumentViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
