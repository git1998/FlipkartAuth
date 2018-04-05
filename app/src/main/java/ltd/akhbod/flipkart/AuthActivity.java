package ltd.akhbod.flipkart;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
