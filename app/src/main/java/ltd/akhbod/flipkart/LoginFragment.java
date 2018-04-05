package ltd.akhbod.flipkart;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

/**
 * Created by ibm on 26-03-2018.
 */

public class LoginFragment extends Fragment {

    View mView;
    FirebaseAuth mAuth;
    TextInputLayout email,password;
    ProgressDialog progressDialog;

    FragmentActivity c;

    public interface FragmentListener{
        void signUp();
        void finishActivity();
    }
    FragmentListener listener;

    FirebaseUser annonymsUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_login,container,false);
        progressDialog=new ProgressDialog(getActivity());

        c= (FragmentActivity) getActivity();
        //Firebase

        mAuth=FirebaseAuth.getInstance();
        annonymsUser=FirebaseAuth.getInstance().getCurrentUser();
        //Firebase

        email= mView.findViewById(R.id.login_email_id);
        password= mView.findViewById(R.id.login_pass_id);

        Button loginBtn= mView.findViewById(R.id.login_loginbtn);
        Button signUpBtn= mView.findViewById(R.id.login_signupbtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_=email.getEditText().getText().toString();
                String password_=password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email_) || !TextUtils.isEmpty(password_)) {

                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("just a moment....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    loginForDelet(email_+"@gmail.com",password_);
                }
                else

                    Toast.makeText(c,"Fill All Fields!!",Toast.LENGTH_SHORT).show();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            listener.signUp();
            }});

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            listener = (FragmentListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException(c.toString() + " must implement OnFragmentInteractionListener");
        }

    }

    private void loginForDelet(final String Email, final String Password) {


       mAuth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
           @Override
           public void onSuccess(AuthResult authResult) {
               FirebaseUser firebaseUser=mAuth.getCurrentUser();
               firebaseUser.delete();
               Toast.makeText(c,"login:Successfully!!",Toast.LENGTH_SHORT).show();
               login(Email,Password);

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d("auth",e.getMessage());
               Toast.makeText(c,"login:failed!!",Toast.LENGTH_SHORT).show();
           }
       });

        progressDialog.dismiss();


    }

    private void login(String email,String password) {

        final AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        Log.d("auth",annonymsUser.getUid().toString());
        annonymsUser.linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(c,"link successfully!!",Toast.LENGTH_SHORT).show();
                listener.finishActivity();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("auth",e.getMessage());
                Toast.makeText(c,"error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }});



    }
}
