package ltd.akhbod.flipkart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by ibm on 23-03-2018.
 */

public class CartListFragment extends Fragment {
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    View mView;
    FragmentActivity c;

    interface FragmentListener{
        public void AuthActivty();
    }
    FragmentListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_cartlist,container,false);
        c= (FragmentActivity) getActivity();

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("cart").child(mAuth.getCurrentUser().getUid());

        recyclerView=mView.findViewById(R.id.fragmentcartlist_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);

        Button continueToCheckout=mView.findViewById(R.id.cartlist_continue);
        TextView cartList=mView.findViewById(R.id.cartlist_price);

        cartList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.signOut();
             Toast.makeText(c,"logged out",Toast.LENGTH_SHORT).show();

                FirebaseAuth firebaseAuth1=FirebaseAuth.getInstance();
            firebaseAuth1.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                 @Override
                 public void onSuccess(AuthResult authResult) {
                     Toast.makeText(c,"signed in Annonemously",Toast.LENGTH_SHORT).show();
                 }});
            }});
        continueToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            listener.AuthActivty();
            }});



        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            listener = (CartListFragment.FragmentListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException(c.toString() + " must implement OnFragmentInteractionListener");
        }

        final FirebaseRecyclerAdapter<ListDetails,CartListFragment.ViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<ListDetails,CartListFragment.ViewHolder>(
                ListDetails.class,
                R.layout.single_cartitem,
                CartListFragment.ViewHolder.class,
                databaseReference
        ) {

            @Override
            protected void populateViewHolder(CartListFragment.ViewHolder viewHolder, final ListDetails model, int position) {

                final String childID=getRef(position).getKey().toString();
                viewHolder.setlayout(c,model.getPrductNAME(),model.getSelectedSize(),model.getImage());
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }});
            }};

        recyclerView.setAdapter(firebaseRecyclerAdapter1);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView productName,size,finalPrice,basePrice,discount;

        public ViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.singlecartitem_image);
            productName=itemView.findViewById(R.id.singlecartitem_productname);
            size=itemView.findViewById(R.id.singlecartitem_size);
            finalPrice=itemView.findViewById(R.id.singlecartitem_finalprice);
            basePrice=itemView.findViewById(R.id.singlecartitem_baseprice);
            discount=itemView.findViewById(R.id.singlecartitem_discount);

        }

        public void setlayout(FragmentActivity c, String productname, String selectedsize, String imageUrl) {

            Picasso.with(c).load(imageUrl).into(image);
            productName.setText(productname);

                String[] ptr=selectedsize.split(" ");
                size.setText("Size: "+ptr[0]);
                finalPrice.setText(""+ptr[1]);
                basePrice.setText(""+ptr[2]);
                discount.setText(ptr[3]+"% off");
        }
    }

}

