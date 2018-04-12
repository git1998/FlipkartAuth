package ltd.akhbod.flipkart;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AuthActivity extends AppCompatActivity implements LoginFragment.FragmentListener{
    android.app.FragmentManager manager;
    String annonymsID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        manager=getFragmentManager();
        LoginFragment fa= new LoginFragment();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.auth_linearlayout,fa,"A");

        if(!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            Intent intent=getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE");
            fa.object = (ArrayList<ListDetails>) args.getSerializable("ARRAYLIST");
        }

        transaction.commit();
    }

    @Override
    public void signUp() {
      Intent intent=new Intent(this,OtpActivity.class);
      startActivity(intent);
    }

    @Override
    public void finishActivity() {
        Intent intent=getIntent();
        finish();}

}
