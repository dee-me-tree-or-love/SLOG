package dmitriiorlov.com.slog.domains.auth.utils;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import dmitriiorlov.com.slog.domains.auth.AuthManager;

public class NameChangeListener implements OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String nameInput = ((EditText) v).getText().toString();
                AuthManager.getInstance().updateInputName(nameInput);
            }
        }
    }

