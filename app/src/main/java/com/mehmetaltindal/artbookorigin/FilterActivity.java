package com.mehmetaltindal.artbookorigin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FilterActivity extends AppCompatActivity {
    Bitmap selectedImage;
    ImageView imageViewFilter;
    Button filterButton1,filterButton2,filterButton3,saveButton;
    SQLiteDatabase database2;
    EditText artNameText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        imageViewFilter = findViewById(R.id.imageViewFilter);
        filterButton1 = findViewById(R.id.filterButton1);
        filterButton2 = findViewById(R.id.filterButton2);
        filterButton3 = findViewById(R.id.filterButton3);
        saveButton = findViewById(R.id.saveButton);
        artNameText2 = findViewById(R.id.artNameText2);


        database2 = this.openOrCreateDatabase("Arts2", MODE_PRIVATE, null);

        Intent intent2 = getIntent();
        String info = intent2.getStringExtra("info");

        if (info.matches("new")) {
            artNameText2.setText("");

            filterButton1.setVisibility(View.VISIBLE);
            filterButton2.setVisibility(View.VISIBLE);
            filterButton3.setVisibility(View.VISIBLE);

            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selectimage);
            imageViewFilter.setImageBitmap(selectImage);


        } else {
            int artId = intent2.getIntExtra("artId", 1);
            filterButton1.setVisibility(View.INVISIBLE);
            filterButton2.setVisibility(View.INVISIBLE);
            filterButton3.setVisibility(View.INVISIBLE);

            try {

                Cursor cursor = database2.rawQuery("SELECT * FROM arts2 WHERE id = ?", new String[]{String.valueOf(artId)});

                int artNameIx = cursor.getColumnIndex("artname");

                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {

                    artNameText2.setText(cursor.getString(artNameIx));


                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewFilter.setImageBitmap(bitmap);


                }

                cursor.close();

            } catch (Exception e) {

            }


        }
    }

    public void makeBlackWhite(View view){
        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        Bitmap blackWhite = grayScale(smallImage);

        imageViewFilter.setImageBitmap(blackWhite);

        saveButton.setVisibility(View.VISIBLE);

    }
    public void makeSepia(View view){
        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        Bitmap sepia =sepia(smallImage);

        imageViewFilter.setImageBitmap(sepia);
        saveButton.setVisibility(View.VISIBLE);

    }
    public void makeRed(View view){
        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        Bitmap red =red(smallImage);
        imageViewFilter.setImageBitmap(red);
        saveButton.setVisibility(View.VISIBLE);

    }


    public void save(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save");
        alert.setMessage("Are you sure?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("nabermoruk");
                realSave();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveButton.setVisibility(View.INVISIBLE);
                Toast.makeText(FilterActivity.this,"NOT SAVED",Toast.LENGTH_LONG).show();

            }
        });
        alert.show();

    }

    public void realSave(){
        System.out.println("merhabalar efendim");
        String artName = artNameText2.getText().toString();
        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {
            database2 = this.openOrCreateDatabase("Arts2",MODE_PRIVATE,null);
            database2.execSQL("CREATE TABLE IF NOT EXISTS arts2 (id INTEGER PRIMARY KEY,artname VARCHAR, image BLOB)");

            String sqlString2 = "INSERT INTO arts2 (artname, image) VALUES (?, ?)";
            SQLiteStatement sqLiteStatement2 = database2.compileStatement(sqlString2);
            sqLiteStatement2.bindString(1,artName);
            sqLiteStatement2.bindBlob(2,byteArray);

        }catch (Exception e){

        }
        Intent intent = new Intent(FilterActivity.this,FilterListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri imageData = data.getData();


            try {

                if (Build.VERSION.SDK_INT >= 28) {

                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageViewFilter.setImageBitmap(selectedImage);

                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageViewFilter.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public static Bitmap grayScale (Bitmap image){

        Bitmap grayScale = Bitmap.createBitmap(image.getWidth(),image.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(grayScale);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(image,0,0,paint);

        return grayScale;

    }

    public static Bitmap sepia(Bitmap image){
        int width, height, r,g, b, c, gry;
        height = image.getHeight();
        width = image.getWidth();
        int depth = 20;

        Bitmap Sephia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(Sephia);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(image, 0, 0, paint);
        for(int x=0; x < width; x++) {
            for(int y=0; y < height; y++) {
                c = image.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);

                gry = (r + g + b) / 3;
                r = g = b = gry;

                r = r + (depth * 2);
                g = g + depth;

                if(r > 255) {
                    r = 255;
                }
                if(g > 255) {
                    g = 255;
                }
                Sephia.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
        return Sephia;


    }

    public static Bitmap red(Bitmap image){
        int width, height, r,g, b, c;
        height = image.getHeight();
        width = image.getWidth();


        Bitmap Red = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(Red);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]{ 1, 1, 1, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0 });
        cm.setSaturation(0);
        cm.setScale(.3f,.3f,.3f,1);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(image, 0, 0, paint);
        for(int x=0; x < width; x++) {
            for(int y=0; y < height; y++) {
                c = image.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);



                r = r + g + b;
                g =0;
                b =0;
                if(r > 255) {
                    r = 255;
                }
                if(g > 255) {
                    g = 255;
                }

                Red.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
        return Red;


    }

}
