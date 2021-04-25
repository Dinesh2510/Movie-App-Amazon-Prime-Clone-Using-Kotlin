package com.movie.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.movie.app.Adapter.VideosListAdapter;
import com.movie.app.Model.MediaFileListModel;
import com.movie.app.R;

import java.io.File;
import java.util.ArrayList;

public class GallaryVideoList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<MediaFileListModel> mediaFileListModelArrayList;
    private LinearLayout noMediaLayout;
    private OnFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary_video_list);

        Log.d("InternalSize_Total", "onCreate: " + getAvailableInternalMemorySize());
        Log.d("InternalSize", "onCreate: " + getAvailableInternalStoragePercentage());

        Log.d("ExternalSize_total", "onCreate: " + getAvailableExternalMemorySize());
        Log.d("ExternalSize", "onCreate: " + getAvailableExternalStoragePercentage());


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_videos_list);
        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);
        mediaFileListModelArrayList = new ArrayList<>();
        VideosListAdapter videosListAdapter = new VideosListAdapter(mediaFileListModelArrayList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(videosListAdapter);
        getVideosList();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MediaFileListModel mediaFileListModel = mediaFileListModelArrayList.get(position);
                Uri fileUri = Uri.fromFile(new File(mediaFileListModel.getFilePath()));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(fileUri, "video/mp4");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(GallaryVideoList.this, "Long Press", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void getVideosList() {
        @SuppressWarnings("deprecation") final Cursor mCursor = getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Video.Media.TITLE + ") ASC");
        if (mCursor != null) {
            if (mCursor.getCount() == 0) {
                noMediaLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noMediaLayout.setVisibility(View.GONE);
            }
            if (mCursor.moveToFirst()) {
                do {
                    MediaFileListModel mediaFileListModel = new MediaFileListModel();
                    mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                    mediaFileListModelArrayList.add(mediaFileListModel);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        } else {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private int getAvailableExternalStoragePercentage() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
            long totalSize = totalBlocks * blockSize;
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            Log.d("here is", "" + ((availableSize * 100) / totalSize));
            int size = (int) ((availableSize * 100) / totalSize);
            return 100 - size;
        } else {
            return 0;
        }
    }

    private static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d("getPath", path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize, "free");
    }

    private static String getAvailableExternalMemorySize() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            @SuppressWarnings("deprecation") long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize, "free");
        } else {
            return "0";
        }
    }

    private int getAvailableInternalStoragePercentage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
        long totalSize = totalBlocks * blockSize;
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        long availableSize = availableBlocks * blockSize;
        Log.d("here is", "" + ((availableSize * 100) / totalSize));
        int size = (int) ((availableSize * 100) / totalSize);
        return 100 - size;
    }

    private static String formatSize(long size, String tag) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}