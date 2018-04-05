package ltd.akhbod.flipkart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by ibm on 22-03-2018.
 */

public class ListFragment extends Fragment {


    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FragmentActivity c;
    View mView;

    public interface OnListFragmentInteractionListener {
        public void onIdSelected(String number, ListDetails model, String childID);
    }

    private OnListFragmentInteractionListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_list,container,false);



        databaseReference= FirebaseDatabase.getInstance().getReference().child("list");

        c= (FragmentActivity) getActivity();
        recyclerView=mView.findViewById(R.id.fragmentlist_recyclerview);
        GridLayoutManager mLayoutManager= new GridLayoutManager(c,2);
        recyclerView.setLayoutManager(mLayoutManager);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            listener = (OnListFragmentInteractionListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException(c.toString() + " must implement OnFragmentInteractionListener");
        }


        final FirebaseRecyclerAdapter<ListDetails,ViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<ListDetails,ViewHolder>(
                ListDetails.class,
                R.layout.single_item_layout,
                ListFragment.ViewHolder.class,
                databaseReference
        ) {

            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final ListDetails model, int position) {

                final String childID=getRef(position).getKey().toString();
                viewHolder.setlayout(c,model.getImage(),model.getPrductNAME(),model.getShopNAME());
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    listener.onIdSelected(model.getProductID(),model,childID);
                    }});
            }};

        recyclerView.setAdapter(firebaseRecyclerAdapter1);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView productName,shopName;

        public ViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.singleitem_image);
            productName=itemView.findViewById(R.id.singleitem_productname);
            shopName=itemView.findViewById(R.id.singleitem_shopname);
        }

        public void setlayout(FragmentActivity c, String imageUrl, String prductNAME, String shopNAME) {

            Picasso.with(c).load(imageUrl).into(image);
            productName.setText(prductNAME);
            shopName.setText("("+shopNAME+")");
        }
    }
}
