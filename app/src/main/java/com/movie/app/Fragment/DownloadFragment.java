package com.movie.app.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.movie.app.Helper.ArcProgress;
import com.movie.app.R;

import java.io.File;

import static java.util.jar.Pack200.Packer.ERROR;

public class DownloadFragment extends Fragment {

    private TextView lblFreeStorage,lblFreeStorage_1;
    private ArcProgress progressStorage,progress_storage_1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_download, container, false);
        progressStorage = (ArcProgress) view.findViewById(R.id.progress_storage);
        progress_storage_1 = (ArcProgress) view.findViewById(R.id.progress_storage_);
        lblFreeStorage = (TextView) view.findViewById(R.id.id_free_space);
        lblFreeStorage_1 = (TextView) view.findViewById(R.id.id_free_space_1);

        setRamStorageDetails(0);
        setRamStorageDetails(1);

        return view;
    }



    private void setRamStorageDetails(int navItemIndex) {
        if (navItemIndex == 0) {
            //Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
            lblFreeStorage.setText(getAvailableInternalMemorySize());
            progressStorage.setProgress(getAvailableInternalStoragePercentage());
        } else if (navItemIndex == 1) {
           // Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();

            lblFreeStorage_1.setText(getAvailableExternalMemorySize());
            progress_storage_1.setProgress(getAvailableExternalStoragePercentage());

        }
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