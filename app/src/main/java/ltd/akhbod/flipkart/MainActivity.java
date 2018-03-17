package ltd.akhbod.flipkart;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference DatabaseRef;
    RecyclerView multicolorRecycler, singleColor;

    FirebaseRecyclerAdapter<Url, ViewHolder2> firebaseRecyclerAdapter2;


    //-----layout dependent variables------
    ImageView mainImage;
    LinearLayout mSelectColor;
    Boolean isVisible;
    TextView mSelectColorNo, mSelectedColor,mSelectedColor2;
    LinearLayout mR2Layout;

    TextView mNameOfProduct;
    String ProductName;

    TextView mFinalPrice,mBasePrice,mDiscount;


    //-----layout dependent variables------


    int noOfColors = 0;
    String selectedColor = "Blue";
    String selectedSize;
    //-------maths-----------
    int BasePrice,Discount,Off,FinalPrice;

    AllDetails Details;

    List<String> sizeList=new ArrayList<>();
    boolean isVariantSelected=false;

    LinearLayout mSelectVariant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent=getIntent();
        if(intent!=null) {

            selectedSize = intent.getStringExtra("size");
            if(selectedSize!=null){isVariantSelected=true;}
            selectedColor = intent.getStringExtra("color");
            Log.d("intent","SIZE="+selectedSize);
            Log.d("intent","color="+selectedColor);
        }

        //----------------------firebase variables initialisation---------------------------------------------------------------------------------------
        DatabaseRef=FirebaseDatabase.getInstance().getReference().child("tshirt");



        DatabaseRef.child("size").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    if(snapshot.child("isAvailable").getValue().toString().equals("yes")){ sizeList.add(snapshot.getKey()); }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseRef.child("productname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProductName= (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});



        DatabaseRef.child("multiclorsthumbimages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    noOfColors++;
                    if (noOfColors == 1) {
                        if (selectedColor == null) {selectedColor = snap.getKey();} }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});



        //----------------------firebase variables initialisation------------------------------------------------------------------------------


        View layout = findViewById(R.id.main_layout);

        mainImage = layout.findViewById(R.id.mainlayout_mainimage);                                  //main image and singlecolorthumb layout
        mSelectColorNo = layout.findViewById(R.id.mainlayout_selectcolorno);
        mSelectedColor = layout.findViewById(R.id.mainlayout_selectedcolor);
        mSelectColor = layout.findViewById(R.id.mainlayout_selectcolor);
        mR2Layout = layout.findViewById(R.id.mainlayout_R2LinearLayout);
        isVisible = true;

        mSelectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible == true) {
                    mR2Layout.setVisibility(View.GONE);
                    isVisible = false;
                } else {
                    mR2Layout.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
            }
        });



        mNameOfProduct=layout.findViewById(R.id.mainlayout_productName);                             //price layout
        mFinalPrice=layout.findViewById(R.id.mainlayout_price);
        mBasePrice=layout.findViewById(R.id.mainlayout_off);
        mDiscount=layout.findViewById(R.id.mainlayout_discount);




        multicolorRecycler = layout.findViewById(R.id.mainlayout_multicolor);                        //RecyclerView 2
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        multicolorRecycler.setLayoutManager(layoutManager1);

        singleColor = layout.findViewById(R.id.mainlayout_singlecolor);                              //RecyclerView 1
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        singleColor.setLayoutManager(layoutManager2);


        mSelectVariant=layout.findViewById(R.id.mainlayout_selectVariant);                           //select variant
        mSelectVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVariantSelected=false;
             Intent intent=new Intent(getApplicationContext(),SelectVariantActivity.class);
             startActivity(intent);
            }});



        final TextView mSizeList,mNoOfSizes,mNoOfColors;
        mSelectedColor2=layout.findViewById(R.id.mainlayout_selectcolor2);
        mNoOfColors=layout.findViewById(R.id.mainlayout_selectcolorno2);
        mSizeList=layout.findViewById(R.id.mainlayout_sizes);
        mNoOfSizes=layout.findViewById(R.id.mainlayout_noOfSizes);


        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                Log.d("abhi", noOfColors + " " + selectedColor);

                mSelectColorNo.setText("" + noOfColors + " Colors");
                mNameOfProduct.setText(ProductName+" "+selectedColor);

                mSelectedColor2.setText(selectedColor);
                mNoOfColors.setText("Color("+noOfColors+")");

                String line=new String();
                if(isVariantSelected==false) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < sizeList.size(); i++) {
                        stringBuffer.append("," + sizeList.get(i) + "");
                    }

                    stringBuffer.deleteCharAt(0);
                    line = stringBuffer.toString();
                }
                else
                    line=selectedSize;
                mSizeList.setText(line);
                mNoOfSizes.setText("Size("+(sizeList.size())+")");

                checkAvaibility();

                attachRecyclerAdapter1();
                attachRecyclerViewAdapter2();
            }
        }.start();


    }

    public void additem(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d("question", "onStart");

    }

    public void attachRecyclerAdapter1() {

        final View layout[] = new View[3];

        FirebaseRecyclerAdapter<Url, ViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<Url, ViewHolder>(
                Url.class,
                R.layout.thumbimage1,
                ViewHolder.class,
                DatabaseRef.child("multiclorsthumbimages")
        ) {

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final Url model, final int position) {
                Log.d("question", "populateViewHolder");



                layout[position] = viewHolder.ThumbImageBack;

                if(position==0){
                   layout[0].setVisibility(View.VISIBLE);
                    mSelectedColor.setText(selectedColor);
                }


                viewHolder.color = getRef(position).getKey().toString();

                Picasso.with(getApplicationContext()).load(model.getUrl()).into(viewHolder.ThumbImage);
                viewHolder.ColorName.setText(viewHolder.color);
                viewHolder.ColorName.setVisibility(View.VISIBLE);

                viewHolder.ThumbImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int k = 0; k <= 2; k++) {

                            if (k == position) {
                                layout[k].setVisibility(View.VISIBLE);
                            } else
                                layout[k].setVisibility(View.GONE);
                        }

                        selectedColor = viewHolder.color;

                        checkAvaibility();

                        mSelectedColor2.setText(selectedColor);
                        mSelectedColor.setText(selectedColor);
                        mNameOfProduct.setText(ProductName+" "+selectedColor);
                        firebaseRecyclerAdapter2.cleanup();
                        attachRecyclerViewAdapter2();
                    }
                });
            }
        };


        multicolorRecycler.setAdapter(firebaseRecyclerAdapter1);

    }


    public void attachRecyclerViewAdapter2(){

        final View layout1[]=new View[3];

        firebaseRecyclerAdapter2=new FirebaseRecyclerAdapter<Url,ViewHolder2>(
                Url.class,
                R.layout.thumbimage1,
                ViewHolder2.class,
                DatabaseRef.child("thumbimage").child(selectedColor)
        ) {

            @Override
            protected void populateViewHolder(final  ViewHolder2 viewHolder, final  Url model, final int position) {
                Log.d("question","populateViewHolder");

                final String pushid=getRef(position).getKey().toString();

                if(position==0){
                    viewHolder.ThumbImageBack.setVisibility(View.VISIBLE);
                    DatabaseRef.child("mainimage").child(selectedColor).child(pushid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Url url= dataSnapshot.getValue(Url.class);
                            Picasso.with(getApplicationContext()).load(url.getUrl()).into(mainImage);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}});
                }



                    layout1[position]=viewHolder.ThumbImageBack;

                    viewHolder.ThumbImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for(int k=0;k<=2;k++){

                            if(k==position){  layout1[k].setVisibility(View.VISIBLE);

                            DatabaseRef.child("mainimage").child(selectedColor).child(pushid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Url url= dataSnapshot.getValue(Url.class);
                                    Picasso.with(getApplicationContext()).load(url.getUrl()).into(mainImage);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}});

                            }
                            else
                                layout1[k].setVisibility(View.GONE);
                        }


                    }});


                Picasso.with(getApplicationContext()).load(model.getUrl()).into(viewHolder.ThumbImage);
            }
        };
        singleColor.setAdapter(firebaseRecyclerAdapter2);

    }



    private void checkAvaibility() {

        Log.d("guddi","onCheckAvaibility");
        DatabaseRef.child("checkprice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(selectedColor)){
                    Log.d("guddi","color-availble");
                    DatabaseRef.child("checkprice").child(selectedColor).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (selectedSize == null){

                                for (DataSnapshot s : dataSnapshot.getChildren()) {


                                    Log.d("guddi", "size-Available");
                                    Discount = Integer.parseInt(s.child("discount").getValue().toString());
                                    BasePrice = Integer.parseInt(s.child("price").getValue().toString());

                                    Log.d("abhi", Discount + "  " + BasePrice);

                                    int Off = (int) (((float) Discount / 100) * (float) BasePrice);
                                    FinalPrice = BasePrice - Off;
                                    break;}
                            }
                            else
                            {
                                for (DataSnapshot s : dataSnapshot.getChildren()) {

                                    if(selectedSize.equals(s.getKey())) {
                                        Log.d("guddi", "size-Available");
                                        Discount = Integer.parseInt(s.child("discount").getValue().toString());
                                        BasePrice = Integer.parseInt(s.child("price").getValue().toString());

                                        Log.d("abhi", Discount + "  " + BasePrice);

                                        int Off = (int) (((float) Discount / 100) * (float) BasePrice);
                                        FinalPrice = BasePrice - Off;
                                    }}
                            }

                            mFinalPrice.setText("" + FinalPrice);
                            mBasePrice.setText("" + BasePrice);
                            mBasePrice.setPaintFlags(mBasePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            mDiscount.setText(Discount + "% off");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}});
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});


    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        Boolean isSelect=true;
        ImageView ThumbImage;
        ImageView ThumbImageBack;
        TextView ColorName;
        String color;
        public ViewHolder(View itemView) {
            super(itemView);

            ThumbImage=itemView.findViewById(R.id.thumbimage1_image);
            ThumbImageBack=itemView.findViewById(R.id.thumbimage1_back);
            ColorName=itemView.findViewById(R.id.thumbimage1_colorname);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageView ThumbImage;
        ImageView ThumbImageBack;
        public ViewHolder2(View itemView) {
            super(itemView);

            ThumbImage=itemView.findViewById(R.id.thumbimage1_image);
            ThumbImageBack=itemView.findViewById(R.id.thumbimage1_back);
        }
    }

}
