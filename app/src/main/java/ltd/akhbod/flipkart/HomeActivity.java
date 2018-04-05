package ltd.akhbod.flipkart;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener,ItemFragment.FragmentListener{

    private static final String TAG ="annonyms" ;
    FirebaseAuth mAuth;
    android.app.FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth=FirebaseAuth.getInstance();

        signInAnnonemously();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onIdSelected(String id, ListDetails model, String childID) {
        Log.d("guddi","onidselected");

        ItemFragment fa= new ItemFragment();
        fa.onIdSelected(id,model,childID);
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.activityhome_linearlayout,fa,"B");
        transaction.commit();
    }

    public void signInAnnonemously(){

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser==null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                manager=getFragmentManager();
                                ListFragment fa= new ListFragment();
                                FragmentTransaction transaction=manager.beginTransaction();
                                transaction.add(R.id.activityhome_linearlayout,fa,"A");
                                transaction.commit();


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(HomeActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else
        {
            manager=getFragmentManager();
            ListFragment fa= new ListFragment();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.add(R.id.activityhome_linearlayout,fa,"A");
            transaction.commit();
        }

    }

    @Override
    public void cartActivityCall() {
        Intent intent=new Intent(this,CartActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
