package ltd.akhbod.flipkart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import id.zelory.compressor.Compressor;

public class AddItemActivity extends AppCompatActivity {

    EditText color,shirtname;
    int i=1;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("tshirt");
        storageReference = FirebaseStorage.getInstance().getReference();


        Button image1, image2, image3, post, postdetails;

        color = findViewById(R.id.color);
        shirtname = findViewById(R.id.shirtname);

        image1 = findViewById(R.id.button1);
        image2 = findViewById(R.id.button2);
        image3 = findViewById(R.id.button3);


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
                AllDetails obj = new AllDetails("1000", "50", "1 T Shirt", "1", "Round Neck", "Mens", "Stripped", "Western Wear", "Slim Fit", "Cotton", "Slim", "Narrow", "No", "Gentle Machine Wash");
                databaseReference.child("details").setValue(obj);
                databaseReference.child("productname").setValue(shirtname.getText().toString());
                i=1;
            }
        });


    }
    private void addImage() {

        Intent intent = CropImage.activity().setMinCropResultSize(400, 400)
                .setMaxCropResultSize(3500, 3500)
                .getIntent(this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("image", "onActivityResult");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d("image", "onCropAcivity");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri ImageUri = result.getUri();
                post(ImageUri);
            }
        }
    }

    private void post(final Uri imageUri) {

        final String[] imageurl = new String[1];
        final String[] thumbimageurl = new String[1];

        DatabaseReference ref=databaseReference.push();
        final String push=ref.getKey().toString();
        storageReference.child("mainimages").child(color.getText().toString()).child(push+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                imageurl[0] =taskSnapshot.getDownloadUrl().toString();
                Url obj=new Url();
                obj.setUrl(imageurl[0]);
                databaseReference.child("mainimage").child(color.getText().toString()).child(push).setValue(obj);
                Toast.makeText(getApplicationContext(),"Storage=image:success",Toast.LENGTH_SHORT).show();


                File thumb_path=new File((imageUri).getPath());
                Bitmap thumb_bit = new Compressor(getApplicationContext()).setMaxHeight(200)
                        .setMaxWidth(200).setQuality(50)
                        .compressToBitmap(thumb_path);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                                final StorageReference thumb_file_path=storageReference.child("thumbimage").child(color.getText().toString()).child(push+".jpg");
                                UploadTask uploadTask = (UploadTask) thumb_file_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if(task.isSuccessful()){
                                            thumbimageurl[0] =task.getResult().getDownloadUrl().toString();
                                            Url obj=new Url();
                                            Log.d("abhi",thumbimageurl[0]);
                                            obj.setUrl(thumbimageurl[0]);
                                            databaseReference.child("thumbimage").child(color.getText().toString()).child(push).setValue(obj);
                                            if(i==1){
                                                i++;
                                                Url obj1=new Url();
                                                obj1.setUrl(imageurl[0]);
                                                databaseReference.child("multiclorsthumbimages").child(color.getText().toString()).setValue(obj1);
                                            }
                                            if(i==3){i=1;}
                                            Toast.makeText(getApplicationContext(),"Storage=thumbimage:success",Toast.LENGTH_SHORT).show();
                                        }}})

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(getApplicationContext(),"Storage=thumbimage:failed",Toast.LENGTH_SHORT).show();
                                Log.d("image", "Image uploading:failed");
                            }});



            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Storage=image:failed",Toast.LENGTH_SHORT).show();

            }});
    }

    public class Url{
        String url;

        public Url(){

        }

        public Url(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


}
