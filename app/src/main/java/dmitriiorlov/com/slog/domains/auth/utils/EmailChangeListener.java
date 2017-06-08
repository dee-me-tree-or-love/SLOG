package dmitriiorlov.com.slog.domains.auth.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dmitriiorlov.com.slog.domains.auth.AuthManager;

/**
 * Created by Dmitry on 6/8/2017.
 */
    public class EmailChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String emailInput = ((EditText) v).getText().toString();
                AuthManager.getInstance().updateInputEmail(emailInput);
            }
        }
    }
