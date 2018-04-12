package ltd.akhbod.flipkart;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CartActivity extends AppCompatActivity implements CartListFragment.FragmentListener {

    FirebaseUser mUser;
    FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mUser=FirebaseAuth.getInstance().getCurrentUser();

        manager=getFragmentManager();
        CartListFragment fa= new CartListFragment();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.activitycart_container,fa,"A");
        transaction.commit();
    }

    @Override
    public void AuthActivty() {
        Log.d("auth","out");
        if(mUser.isAnonymous()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra("annonymsID",mUser.getUid());
            startActivity(intent);
        }
        else
            Toast.makeText(CartActivity.this,"Proceed to checkout",Toast.LENGTH_SHORT).show();
    }

 }
