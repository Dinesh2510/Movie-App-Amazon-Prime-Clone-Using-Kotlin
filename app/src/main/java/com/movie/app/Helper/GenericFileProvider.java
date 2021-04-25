package com.movie.app.Helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class GenericFileProvider extends FileProvider {
    Context context;

    public GenericFileProvider() {
    }


    public GenericFileProvider(Context context) {
        this.context = context;
    }

    public void takeScreenshot(View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        View v1 = view;

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
//            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);

            Bitmap bitmap = screenShot(v1);
//            Bitmap bitmap = fulscreenshot(v1);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap fulscreenshot(NestedScrollView iv){
        Bitmap bitmap = Bitmap.createBitmap(
                iv.getChildAt(0).getWidth(),
                iv.getChildAt(0).getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        iv.getChildAt(0).draw(c);

        return bitmap;
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT))
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);
        else
            uri = Uri.fromFile(imageFile);


        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
