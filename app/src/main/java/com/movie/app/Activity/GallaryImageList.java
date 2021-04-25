package com.movie.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.movie.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GallaryImageList extends AppCompatActivity implements OnitemClcikListerner {
    ArrayList<File>myimageFile;
    CustomAdapter customAdapter;
    List<String> mList;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary_image_list);
        recyclerView=findViewById(R.id.recyclerViewId);
        CheckUserPermsions();
        mList=new ArrayList<>();
    }

    void CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        display();  // init the contact list

    }




    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    display();  // init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText(this, "your message", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private ArrayList<File> findImage(File file) {

        ArrayList<File> imageList=new ArrayList<>();

        File[] imageFile=  file.listFiles();

        for (File singleimage : imageFile){


            if (singleimage.isDirectory() && !singleimage.isHidden()){

                imageList.addAll(findImage(singleimage));

            }else {

                if (singleimage.getName().endsWith(".jpg") ||
                        singleimage.getName().endsWith(".png") ||
                        singleimage.getName().endsWith(".webp")
                ){
                    imageList.add(singleimage);
                }

            }


        }




        return  imageList;
    }



    private void display() {

        myimageFile = findImage(Environment.getExternalStorageDirectory());


        for (int j=0;j<myimageFile.size();j++){

            mList.add(String.valueOf(myimageFile.get(j)));
            customAdapter=new CustomAdapter(mList,this);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        }


    }



    @Override
    public void onClick(int position) {

        Toast.makeText(this, "Postion: "+position, Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(GallaryImageList.this,FullImageActivity.class);
//        intent.putExtra("image",String.valueOf(myimageFile.get(position)));
//        intent.putExtra("pos",position);
//        intent.putExtra("imageList",myimageFile);
//        startActivity(intent);

    }

}