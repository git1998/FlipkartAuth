package ltd.akhbod.flipkart;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "abhijit";
    private BroadcastReceiver smsBroadcastReceiver;
    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    public static final String SMS_BUNDLE = "pdus";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    // [START declare_auth]
    private FirebaseAuth mAuth,sAuth;
    // [END declare_auth]
    private FirebaseUser annonymsUser;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mVarificatioCallbacks;

    String phoneNo,password;
    FirebaseAuth annonymsAuth;
    FirebaseUser annonymsUserUsed;
    FirebaseAuth.AuthStateListener AuthStateListener;

    Dialog addUnitDialog;
    DatabaseReference ref;
    ListDetails obj[];
    ArrayList<ListDetails> object = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth=FirebaseAuth.getInstance();
        annonymsUser =mAuth.getCurrentUser();

        if(annonymsUser.isAnonymous()){  Toast.makeText(OtpActivity.this, "is Annonyms"+annonymsUser.getUid().toString(),
                Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(OtpActivity.this, "Not Annonyms",
                    Toast.LENGTH_SHORT).show();}


        AuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(annonymsUser.isAnonymous()){  Log.e("sarthakshubham","isAnnonyms"); }
                else {
                    Log.e("state","NotAnnonyms");}


            }};


        smsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("smsBroadcastReceiver", "onReceive");
                Bundle pudsBundle = intent.getExtras();
                Object[] pdus = (Object[]) pudsBundle.get(SMS_BUNDLE);
                SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
                Log.i(TAG,  messages.getMessageBody());

                String firebaseVerificationCode = messages.getMessageBody().trim().split(" ")[0];//only a number code
                Toast.makeText(getApplicationContext(), firebaseVerificationCode,Toast.LENGTH_SHORT).show();

            }};

        final TextInputLayout mPhone=findViewById(R.id.signup_mobileno);
        Button mContinue=findViewById(R.id.signup_continue);

        if (savedInstanceState != null) {
            Log.d("vow","savedInstaceState");
            onRestoreInstanceState(savedInstanceState);
        }

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout mPassword=findViewById(R.id.signup_password);
                password=mPassword.getEditText().getText().toString();
                phoneNo=mPhone.getEditText().getText().toString();
                startPhoneNumberVarification("+91"+phoneNo);

                addUnitDialog = new Dialog(OtpActivity.this);
                addUnitDialog.setContentView(R.layout.dialgo_otp);
                addUnitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextInputLayout mOtpCode=addUnitDialog.findViewById(R.id.signup_enterotp);

                Button mResend=addUnitDialog.findViewById(R.id.signup_resend);
                mResend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reStartPhoneNumberVerification("+91"+phoneNo);
                    }});

                Button mNext=addUnitDialog.findViewById(R.id.signup_next);
                mNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String otp=mOtpCode.getEditText().getText().toString();
                        AuthCredential emailAuthCredential =  EmailAuthProvider.getCredential(phoneNo+"@gmail.com", password);
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,otp);
                        link(emailAuthCredential,credential);

                    }});

                addUnitDialog.show();
            }});



        mVarificatioCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("abhi", "onVerificationCompleted: ");
                mVerificationInProgress=false;

                AuthCredential emailAuthCredential =  EmailAuthProvider.getCredential(phoneNo+"@gmail.com", password);
                link(emailAuthCredential,phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.d("abhi","Verification failed")   ;
                mVerificationInProgress=false;
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                Log.d("abhi", "onCodeSent: ");
                Toast.makeText(getApplicationContext(),"code has been sent!!!",Toast.LENGTH_SHORT).show();
                mVerificationId=s;
                Log.d("varificationid",s);
                mVerificationInProgress=true;

                mResendToken=forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                Log.d("timeout",s);
            }
        };


    }


    @Override
    protected void onStart() {
        super.onStart();


        Log.d("shubham", ""+mVerificationInProgress);
        if(mVerificationInProgress==true){
            Log.d("shubham", "onStart:");
            startPhoneNumberVarification(phoneNo);
        }

    }


    public void startPhoneNumberVarification(String phoneNo){
        Log.d("shubham", "onStartPhoneNumberAuth:");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mVarificatioCallbacks
        );

        mVerificationInProgress=true;


    }

    public void reStartPhoneNumberVerification(String phoneNo){
        Log.d("vow","onRestartVerificationNumber");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mVarificatioCallbacks,
                mResendToken
        );

        mVerificationInProgress=true;

    }


    private void link(AuthCredential credential, final PhoneAuthCredential phoneAuthCredential) {

    //String id = user.getUid();
    String id2 = annonymsUser.getUid();
    if(annonymsUser.isAnonymous()){  Toast.makeText(OtpActivity.this, "is Annonyms",
            Toast.LENGTH_SHORT).show(); }
            else {
        Toast.makeText(OtpActivity.this, "Not Annonyms",
                Toast.LENGTH_SHORT).show();}

                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

        annonymsUser.linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("stageAuthentication", "linkWithCredential:success");
                            FirebaseUser muser = task.getResult().getUser();
                            Toast.makeText(OtpActivity.this, "link: success.",
                                    Toast.LENGTH_SHORT).show();

                            signInWIthCredential(phoneAuthCredential);


                        } else {
                            Log.d("stageAuthentication", "linkWithCredential:failed");
                            Toast.makeText(OtpActivity.this, "link: failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    private void signInWIthCredential(PhoneAuthCredential credential) {

        String firebaseUser=mAuth.getCurrentUser().getUid();
        Log.d("firebaseusers","user2="+firebaseUser);




            ref = FirebaseDatabase.getInstance().getReference().child("users").child(annonymsUser.getUid().toString());
            //Firebase
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        object.add(snap.getValue(ListDetails.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }});

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"account varified",Toast.LENGTH_SHORT).show();
                            Log.d("stageAuthentication", "signInWithCredential:success");


                            mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(),"account deleted",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                                    Bundle args = new Bundle();
                                    args.putSerializable("ARRAYLIST",(Serializable)object);
                                    intent.putExtra("BUNDLE",args);
                                    startActivity(intent);
                                    finish();
                                }});

                        } else {
                            Log.d("stageAuthentication", "signInWithCredential:failed");
                            unlink();
                        }
                    }
                });


    }

    public void unlink(){

        final FirebaseUser user=mAuth.getCurrentUser();
        List<? extends UserInfo> providerData = user.getProviderData();
        final String providerId[] = new String[2];
        int i=0;

        for (UserInfo userInfo : providerData ) {
            Log.d(TAG, "providerId = " + userInfo.getProviderId());
            providerId[i] = userInfo.getProviderId();
            i++;
        }


            if (providerId[1].equals("password")) {

                Log.d("stageAuthentication", "unlinking starts");
                user.unlink(providerId[1])
                        .addOnCompleteListener(this,
                                new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Log.d("stageAuthentication", "unlink :success");
                                            Toast.makeText(getApplicationContext(),"unlink success",Toast.LENGTH_SHORT).show();


                                        }
                                        else {
                                            Log.d("stageAuthentication", "unlink firebase:failed");
                                        }

                                        addUnitDialog.dismiss();
                                    }
                                });
            }

        if(user.isAnonymous()){  Toast.makeText(OtpActivity.this, "is Annonyms"+user.getUid().toString(),
                Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(OtpActivity.this, "Not Annonyms",
                    Toast.LENGTH_SHORT).show();}

        }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean("progress",mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mVerificationInProgress=savedInstanceState.getBoolean("progress");
    }



}

