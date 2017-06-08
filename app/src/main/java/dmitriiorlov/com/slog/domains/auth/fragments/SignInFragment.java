package dmitriiorlov.com.slog.domains.auth.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.domains.auth.interfaces.OnAuthActionListener;
import dmitriiorlov.com.slog.domains.auth.utils.EmailChangeListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAuthActionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

//
public class SignInFragment extends Fragment {

    private OnAuthActionListener listenerContext;

    @BindView(R.id.input_email)
    EditText email;

    @BindView(R.id.input_password)
    EditText password;


    public SignInFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment SignInFragment.
     */
    public static SignInFragment newInstance(String email) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(SignUpFragment.ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }


    @OnClick(R.id.btn_login)
    public void onSignIn() {
        String email_str = email.getText().toString().trim();
        String password_str = password.getText().toString().trim();

        //Checking for inputs
        if (TextUtils.isEmpty(email_str)) {
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password_str)) {
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        this.listenerContext.onSignIn(email_str,password_str);
    }

    // method to switch to the SignUp screen
    @OnClick(R.id.link_signup)
    public void onGoToSignUp() {
        if (listenerContext != null) {
            SignUpFragment signUpFragment = SignUpFragment.newInstance(email.getText().toString());
            listenerContext.onSwitchAuthFragment(signUpFragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        //Butterknife configuration
        ButterKnife.bind(this, v);
        this.email.setOnFocusChangeListener(new EmailChangeListener());

        if (getArguments() != null) {
            email.setText(getArguments().getString(SignUpFragment.ARG_PARAM1));
        }
        return v;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnAuthActionListener) {
            listenerContext = (OnAuthActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement the sign in interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerContext = null;
    }
}
