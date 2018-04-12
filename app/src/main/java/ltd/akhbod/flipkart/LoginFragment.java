package ltd.akhbod.flipkart;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ibm on 26-03-2018.
 */

public class LoginFragment extends Fragment {

    View mView;
    FirebaseAuth mAuth;
    TextInputLayout email, password;
    ProgressDialog progressDialog;

    FragmentActivity c;

    public interface FragmentListener {
        void signUp();

        void finishActivity();
    }

    FragmentListener listener;

    FirebaseUser annonymsUser;
    String annonymsID;
    DatabaseReference ref;

    ArrayList<ListDetails> object = new ArrayList<>();

    DataSnapshot cartItemSnapShot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        progressDialog = new ProgressDialog(getActivity());

        c = (FragmentActivity) getActivity();
        //Firebase

        mAuth = FirebaseAuth.getInstance();
        annonymsUser = FirebaseAuth.getInstance().getCurrentUser();
        ref=FirebaseDatabase.getInstance().getReference();
        annonymsID = annonymsUser.getUid().toString();


        email = mView.findViewById(R.id.login_email_id);
        password = mView.findViewById(R.id.login_pass_id);

        Button loginBtn = mView.findViewById(R.id.login_loginbtn);
        Button signUpBtn = mView.findViewById(R.id.login_signupbtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_ = email.getEditText().getText().toString();
                String password_ = password.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email_) || !TextUtils.isEmpty(password_)) {

                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("just a moment....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    login(email_ + "@gmail.com", password_);

                } else

                    Toast.makeText(c, "Fill All Fields!!", Toast.LENGTH_SHORT).show();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.signUp();
            }
        });


        ref.child(annonymsID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemSnapShot=dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    private void login(final String Email, final String Password) {

        boolean isAnnonyms=annonymsUser.isAnonymous();

        String id=annonymsID;
        annonymsUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(c, "Annonyms Account Deleted:success", Toast.LENGTH_SHORT).show();
                Log.d("accountINFO", "annonyms account deleted:success");

                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        String userID;
                        if(!task.isSuccessful())
                        {
                            mAuth.signInAnonymously();
                        }
                        userID=mAuth.getCurrentUser().getUid();

                        for ( DataSnapshot snap : cartItemSnapShot.getChildren()) {
                            ListDetails obj=snap.getValue(ListDetails.class);
                            String keyID=snap.getKey();

                            ref.child(userID).child(keyID).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("accountINFO", "added to account");
                                        ref.child(annonymsID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("accountINFO","annonymsID DELETED");
                                            }});}
                                }});
                        }
                    }});

            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Annonyms Account Deleted:failed", Toast.LENGTH_SHORT).show();
                Log.d("accountINFO", "annonyms account deleted:failed"+e.getMessage());
            }});

        progressDialog.dismiss();
    }
}