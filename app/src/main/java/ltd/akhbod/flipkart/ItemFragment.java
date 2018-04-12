package ltd.akhbod.flipkart;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ibm on 23-03-2018.
 */

public class ItemFragment extends Fragment {

    View mView;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String SELECTED_ID;

    ItemDetails data;
    ListDetails obj;
    FragmentActivity c;

    ArrayList<String> size=new ArrayList<>();
    ArrayList<String> discount=new ArrayList<>();
    ArrayList<String> baseprice=new ArrayList<>();

    //layout elements-------
    ImageView mainThumbImageBack1,mainThumbImageBack2,mainThumbImageBack3;                          //mainimage and thumbimages
    ImageView mainImage,mainThumbImage1, mainThumbImage2, mainThumbImage3;

    View[] SizeView=new View[5];
    ImageView sizeLayoutBack[]=new ImageView[5];//,sizeLayoutBack2 ,sizeLayoutBack3,sizeLayoutBack4,sizeLayoutBack5;
    TextView sizeText[]=new TextView[5];//,sizeText2,sizeText3,sizeText4,sizeText5;

    TextView productName;

    TextView finalPrice,basePrice,mdiscount;

    View mainlayout;
    int selectedSize;
    int selectedColor;
    String cartPushId;

    int k;

    interface FragmentListener{
        public void cartActivityCall();
    }
    FragmentListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_listitem,container,false);

        auth=FirebaseAuth.getInstance();

        c= (FragmentActivity) getActivity();

        View appbar=mView.findViewById(R.id.listitem_appbar);
        ImageView cartImage=appbar.findViewById(R.id.mainappbar_back);
        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.cartActivityCall();
            }});

        View mainThumbImageView1,mainThumbImageView2,mainThumbImageView3;                //include layout of thumbimage
        mainlayout=mView.findViewById(R.id.main_layout);

        mainThumbImageView1=mainlayout.findViewById(R.id.mainlayout_mainthumbimage1);
        mainThumbImageView2=mainlayout.findViewById(R.id.mainlayout_mainthumbimage2);
        mainThumbImageView3=mainlayout.findViewById(R.id.mainlayout_mainthumbimage3);


        //-------------------initializing layout elements-------------------------------------------
        mainImage=mainlayout.findViewById(R.id.mainlayout_mainimage);

        mainThumbImageBack1=mainThumbImageView1.findViewById(R.id.thumbimage1_back);
        mainThumbImageBack1.setVisibility(View.VISIBLE);                                            //mainimage and thumbimages
        mainThumbImageBack2=mainThumbImageView2.findViewById(R.id.thumbimage1_back);
        mainThumbImageBack3=mainThumbImageView3.findViewById(R.id.thumbimage1_back);


        mainThumbImage1=mainThumbImageView1.findViewById(R.id.thumbimage1_image);
        mainThumbImage2=mainThumbImageView2.findViewById(R.id.thumbimage1_image);
        mainThumbImage3=mainThumbImageView3.findViewById(R.id.thumbimage1_image);

        productName=mainlayout.findViewById(R.id.mainlayout_productName);

        finalPrice=mainlayout.findViewById(R.id.mainlayout_price);                                  //pricees
        basePrice=mainlayout.findViewById(R.id.mainlayout_off);
        mdiscount=mainlayout.findViewById(R.id.mainlayout_discount);

        Button addToKart=mView.findViewById(R.id.listitem_addtkart);
        addToKart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.setSelectedSize(size.get(selectedSize)+" "+finalPrice.getText()+" "+baseprice.get(selectedSize)+" "+discount.get(selectedSize));
                String push=databaseReference.push().getKey();
                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child(push).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(c,"Successfully Added to cart!!",Toast.LENGTH_SHORT).show();

                    }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(c,"Failed Adding to cart!!",Toast.LENGTH_SHORT).show();
                    }});
            }});
        //-------------------initializing layout elements-------------------------------------------


        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("listItem").child(SELECTED_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data=dataSnapshot.getValue(ItemDetails.class);
                loadPrices();
                loadLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});


        mainThumbImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(c).load(data.getMainimageColorA1()).into(mainImage);


                mainThumbImageBack1.setVisibility(View.VISIBLE);
                mainThumbImageBack2.setVisibility(View.INVISIBLE);
                mainThumbImageBack3.setVisibility(View.INVISIBLE);
            }});
        mainThumbImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(c).load(data.getMainimageColorA2()).into(mainImage);

                mainThumbImageBack1.setVisibility(View.INVISIBLE);
                mainThumbImageBack2.setVisibility(View.VISIBLE);
                mainThumbImageBack3.setVisibility(View.INVISIBLE);
            }});
        mainThumbImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(c).load(data.getMainimageColorA3()).into(mainImage);


                mainThumbImageBack1.setVisibility(View.INVISIBLE);
                mainThumbImageBack2.setVisibility(View.INVISIBLE);
                mainThumbImageBack3.setVisibility(View.VISIBLE);
            }});


        return mView;
    }

    private void loadPrices() {
        String[] line=data.getCheckpriceColorA().split(",");
       for(String str:line)
       {
           String[] ptr=str.split(" ");
           size.add(ptr[0]);
           discount.add(ptr[1]);
           baseprice.add(ptr[2]);
       }
    }

    private void loadLayout() {
        Picasso.with(c).load(data.getMainimageColorA1()).into(mainImage);
        Picasso.with(c).load(data.getThumbimageColorA1()).into(mainThumbImage1);                    //mainimage and thumbimages
        Picasso.with(c).load(data.getThumbimageColorA2()).into(mainThumbImage2);
        Picasso.with(c).load(data.getThumbimageColorA3()).into(mainThumbImage3);

        productName.setText(data.getProductNAME());
        calculatePriceAndDisplay(Integer.parseInt(size.get(0)),Integer.parseInt(discount.get(0)),Integer.parseInt(baseprice.get(0)));

        loadSizes();


    }

    private void loadSizes() {

        for(int k=0;k<5;k++){
            if(k==0){ SizeView[0]=mainlayout.findViewById(R.id.mainlayout_sizeView1); }
            else if(k==1){SizeView[1]=mainlayout.findViewById(R.id.mainlayout_sizeView2);}
            else if(k==2){SizeView[2]=mainlayout.findViewById(R.id.mainlayout_sizeView3);}
            else if(k==3){SizeView[3]=mainlayout.findViewById(R.id.mainlayout_sizeView4);}
            else
                {SizeView[4]=mainlayout.findViewById(R.id.mainlayout_sizeView5);}
        }

        for(int i=0;i<size.size();i++){
            SizeView[i].setVisibility(View.VISIBLE);
            sizeLayoutBack[i]=SizeView[i].findViewById(R.id.SVSize_back);

            if(i==0){sizeLayoutBack[i].setVisibility(View.VISIBLE);}
            else
            sizeLayoutBack[i].setVisibility(View.INVISIBLE);

            sizeText[i]=SizeView[i].findViewById(R.id.SVSize_sizeNo);
            sizeText[i].setText(size.get(i));

            final int finalI = i;
            SizeView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i1 = 0; i1<size.size(); i1++)
                    {
                        if(i1== finalI){ sizeLayoutBack[i1].setVisibility(View.VISIBLE);
                            selectedSize=finalI;
                            calculatePriceAndDisplay(Integer.parseInt(size.get(i1)),Integer.parseInt(discount.get(i1)),Integer.parseInt(baseprice.get(i1)));}
                        else
                            sizeLayoutBack[i1].setVisibility(View.INVISIBLE);
                    }
                }});
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            listener = (ItemFragment.FragmentListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException(c.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private void calculatePriceAndDisplay(int cSize, int cDiscount, int cBasePrice) {
        int Off = (int) (((float) cDiscount / 100) * (float) cBasePrice);
        int FinalPrice = cBasePrice - Off;

        finalPrice.setText(""+FinalPrice);
        basePrice.setText(""+cBasePrice);
        basePrice.setPaintFlags(basePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mdiscount.setText(""+cDiscount);
    }


    public void onIdSelected(String id, ListDetails model, String childID) {
    SELECTED_ID=id;
    obj=model;
    cartPushId=childID;
    }
}
