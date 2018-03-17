package ltd.akhbod.flipkart;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SelectVariantActivity extends AppCompatActivity {

    RecyclerView multicolorRecycler,sizeRecycler;
    DatabaseReference DatabaseRef;

    int noOfColors = 0;
    String selectedColor = "Blue";

    String userSelectedColor;
    String userSelectedSize;

    TextView mBasePrice,mDiscount,mFinalPrice;
    int BasePrice,Discount,FinalPrice;
    Boolean isAvailable;

    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_variant);

        DatabaseRef= FirebaseDatabase.getInstance().getReference().child("tshirt");


        mBasePrice=findViewById(R.id.ASV_basePrice);
        mDiscount=findViewById(R.id.ASV_discount);
        mFinalPrice=findViewById(R.id.ASV_finalprice);

        multicolorRecycler =findViewById(R.id.selectvariant_multicolorrecycler);                        //RecyclerView 1
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        multicolorRecycler.setLayoutManager(layoutManager1);


        sizeRecycler =findViewById(R.id.selectvariant_sizerecycler);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);//RecyclerView 2
        sizeRecycler.setLayoutManager(layoutManager2);


        Button mApplyBtn=findViewById(R.id.ASV_applybtn);
        mApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAvailable==true) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("size", userSelectedSize);
                    intent.putExtra("color", userSelectedColor);
                    startActivity(intent);
                    finish();
                }
            }});


        attachRecyclerAdapterMultiColor();
        attachRecyclerAdapterSizes();


    }

    private void attachRecyclerAdapterMultiColor() {

        final View layout[] = new View[3];

        FirebaseRecyclerAdapter<Url, SelectVariantActivity.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Url, SelectVariantActivity.ViewHolder>(
                Url.class,
                R.layout.selectvariant_thumbimage,
                SelectVariantActivity.ViewHolder.class,
                DatabaseRef.child("multiclorsthumbimages")
        ) {

            @Override
            protected void populateViewHolder(final SelectVariantActivity.ViewHolder viewHolder, final Url model, final int position) {
                Log.d("question", "populateViewHolder");


                viewHolder.color = getRef(position).getKey().toString();

                layout[position] = viewHolder.ThumbImageBack;

                if(position==0){
                    layout[0].setVisibility(View.VISIBLE);
                    userSelectedColor=viewHolder.color;
                }



                Picasso.with(getApplicationContext()).load(model.getUrl()).into(viewHolder.ThumbImage);
                viewHolder.ColorName.setText(viewHolder.color);
                viewHolder.ColorName.setVisibility(View.VISIBLE);

                viewHolder.ThumbImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userSelectedColor=viewHolder.color;

                        for (int k = 0; k <= 2; k++) {

                            if (k == position) {
                                layout[k].setVisibility(View.VISIBLE);
                            } else
                                layout[k].setVisibility(View.GONE);
                        }
                        checkAvaibility();

                    }
                });
            }
        };


        multicolorRecycler.setAdapter(firebaseRecyclerAdapter);


    }



    private void attachRecyclerAdapterSizes() {

        final View layout[] = new View[3];


        FirebaseRecyclerAdapter<SizeAvailability, SelectVariantActivity.ViewHolder2> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SizeAvailability, SelectVariantActivity.ViewHolder2>(
                SizeAvailability.class,
                R.layout.selectvariant_sizelayout,
                SelectVariantActivity.ViewHolder2.class,
                DatabaseRef.child("size")
        ) {

            @Override
            protected void populateViewHolder(final SelectVariantActivity.ViewHolder2 viewHolder, final SizeAvailability model, final int position) {
                Log.d("question", "populateViewHolder");


                if(model.isAvailable.equals("yes")){

                   layout[i]=viewHolder.SizeBack;
                   viewHolder.SizeNo=getRef(position).getKey().toString();
                   viewHolder.SizeText.setText(viewHolder.SizeNo);

                 if(i==0){viewHolder.SizeBack.setVisibility(View.VISIBLE); userSelectedSize=viewHolder.SizeNo; }
                 i++;

                    viewHolder.SizeText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            userSelectedSize=viewHolder.SizeNo;


                            for (int k = 0; k <= 2; k++) {

                                if (k == position) {
                                    layout[k].setVisibility(View.VISIBLE);
                                } else
                                    layout[k].setVisibility(View.GONE);
                            }checkAvaibility();
                        }});





                }


            }};


        sizeRecycler.setAdapter(firebaseRecyclerAdapter);


    }

    private void checkAvaibility() {

        Log.d("guddi","onCheckAvaibility");
        DatabaseRef.child("checkprice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userSelectedColor)){
                    Log.d("guddi","color-availble");
                    DatabaseRef.child("checkprice").child(userSelectedColor).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot s=dataSnapshot;
                            if(dataSnapshot.hasChild(userSelectedSize)){
                                Log.d("guddi","size-Available");
                                Discount= Integer.parseInt(s.child(userSelectedSize).child("discount").getValue().toString());
                                BasePrice = Integer.parseInt(s.child(userSelectedSize).child("price").getValue().toString());

                                isAvailable =true;
                               Log.d("abhi",Discount+"  "+BasePrice);

                                int Off= (int) (((float)Discount/100) * (float)BasePrice);
                                FinalPrice=BasePrice-Off;

                                mFinalPrice.setText(""+FinalPrice);
                                mBasePrice.setText(""+BasePrice);
                                mBasePrice.setPaintFlags(mBasePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                mDiscount.setText(Discount+"% off");
                            }
                            else{
                            Toast.makeText(getApplicationContext(),"Size Unvailable!!!",Toast.LENGTH_SHORT).show(); isAvailable =false;}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}});
                }
                else{
                    Toast.makeText(getApplicationContext(),"Color Unavailable!!!",Toast.LENGTH_SHORT).show(); isAvailable =false;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});


    }

    private void updateUi() {


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        Boolean isSelect=true;
        ImageView ThumbImage;
        ImageView ThumbImageBack;
        TextView ColorName;
        String color;

        public ViewHolder(View itemView) {
            super(itemView);

            ThumbImage=itemView.findViewById(R.id.SV_thumbimage);
            ThumbImageBack=itemView.findViewById(R.id.SV_back);
            ColorName=itemView.findViewById(R.id.SV_colorname);
        }
    }


    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        String SizeNo;
        ImageView SizeBack;
        TextView SizeText;

        public ViewHolder2(View itemView) {
            super(itemView);

            SizeBack=itemView.findViewById(R.id.SVSize_back);
            SizeText=itemView.findViewById(R.id.SVSize_sizeNo);
        }
    }


}
