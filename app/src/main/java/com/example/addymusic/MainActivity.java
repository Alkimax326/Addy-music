package com.example.addymusic;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        runtimePermission();
    }

    public void runtimePermission() {

        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displaySong();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    public ArrayList<File> findSong(File file) throws NullPointerException
    {
        ArrayList<File> songList = new ArrayList<>();
        File[] files = file.listFiles();

        if(files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden())
                    songList.addAll(findSong(singleFile));
                else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                        songList.add(singleFile);
                }
            }
        }
        return songList;
    }

     public void displaySong()
     {
         final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
         items = new String[mySongs.size()];

         for(int i = 0; i < mySongs.size(); i++)
         {
             items[i] = mySongs.get(i).getName().replace(".mp3","").replace(".wav"," ");
         }

         customAdapter cusAdap = new customAdapter();
         listView.setAdapter(cusAdap);

         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 String songName = (String) listView.getItemAtPosition(i);

                 startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                         .putExtra("songs", mySongs)
                         .putExtra("songName", songName)
                         .putExtra("pos", i)
                 );

             }
         });
     }

    class customAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.list_items,null);
            TextView txtSong = view.findViewById(R.id.textSong);
            txtSong.setSelected(true);
            txtSong.setText(items[i]);
            return view;
        }
    }
}