package site.isleti.triwake.ui.painting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.UUID;

import site.isleti.triwake.R;

public class SaveActvity extends Activity {

    private ImageButton mBack;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private LinearLayout download;
    private LinearLayout newDrawing;
    private Drawable mDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Intent intent = this.getIntent();
        byte[] drawing = intent.getByteArrayExtra("image");
        mBitmap = BitmapFactory.decodeByteArray(drawing,0,drawing.length);
        //mBitmap = intent.getParcelableExtra("image");
        mDrawable = new BitmapDrawable(mBitmap);
        mImageView = findViewById(R.id.show_drawing);
        mImageView.setImageDrawable(mDrawable);
        backDrawing();
        showDrawing();
        newDrawing();
        download();
    }

    private void showDrawing() {

    }

    private void newDrawing() {
        newDrawing = findViewById(R.id.new_drawing);
        newDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveActvity.this,DrawActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void backDrawing() {
        mBack = findViewById(R.id.back_drawing);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void download() {
        download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
    }

    private void saveImage() {
        mImageView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), mImageView.getDrawingCache(),
                UUID.randomUUID().toString()+".png", "drawing");
        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "画作已保存", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! 保存失败", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }
        mImageView.destroyDrawingCache();


    }
}
