package dmitriiorlov.com.slog.domains.auth.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmitriiorlov.com.slog.R;
import dmitriiorlov.com.slog.domains.auth.AuthStore;
import dmitriiorlov.com.slog.domains.auth.interfaces.OnAuthActionListener;
import dmitriiorlov.com.slog.domains.auth.utils.EmailChangeListener;
import dmitriiorlov.com.slog.domains.auth.utils.NameChangeListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAuthActionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    private OnAuthActionListener listenerContext;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "email";

    //Inputs in order to create account
    @BindView(R.id.input_name)
    TextView name;
    @BindView(R.id.input_email)
    TextView email;
    @BindView(R.id.input_password)
    TextView password;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // method to switch to the SignIn screen
    @OnClick(R.id.link_login)
    public void onGoToSignIn() {
        if (listenerContext != null) {
            SignInFragment signInFragment = SignInFragment.newInstance(this.email.getText().toString());
            listenerContext.onSwitchAuthFragment(signInFragment);
//            listenerContext.onSwitchAuthFragment(SignInFragment.class, this.email.getText().toString());
        }
    }

    //create a new account
    @OnClick(R.id.btn_signup)
    public void onSignUp() {
        final String name_str = name.getText().toString();
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

        listenerContext.onSignUp(name_str, email_str, password_str);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance(String email) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        //Butterknife configuration
        ButterKnife.bind(this, v);

        email.setOnFocusChangeListener(new EmailChangeListener());
        name.setOnFocusChangeListener(new NameChangeListener());

        if (getArguments() != null) {
            email.setText(getArguments().getString(ARG_PARAM1));
        }else{
            email.setText(AuthStore.getInstance().getInputEmail());
        }
        name.setText(AuthStore.getInstance().getInputName());
        return v;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnAuthActionListener) {
            listenerContext = (OnAuthActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAuthorizationScreenSwitchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerContext = null;
    }
}
