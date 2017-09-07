package com.androidmads.okiodemo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class MainActivity extends AppCompatActivity {

    File folder, file;
    String imageLink = "https://lh3.googleusercontent.com/-8awtMI1rwkE/V2VAjxHDxCI/AAAAAAAAAG4/LVNkDQHM4A4GXlWVxAWV_h9WcGnrZ1qMACEw/w140-h140-p/ic_launcher.png";
    ImageView imageView;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folder = new File(Environment.getExternalStorageDirectory(), "/Downloads");
        imageView = (ImageView) findViewById(R.id.imageView);
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
    }

    public void downloadImg(View view) {
        pd.show();
        try {
            Request request = new Request.Builder()
                    .url(imageLink)
                    .build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!folder.exists()) {
                        boolean folderCreated = folder.mkdir();
                        Log.v("folderCreated", folderCreated + "");
                    }
                    file = new File(folder.getPath() + "/downloadedImage.png");
                    if (file.exists()) {
                        boolean fileDeleted = file.delete();
                        Log.v("fileDeleted", fileDeleted + "");
                    }
                    boolean fileCreated = file.createNewFile();
                    Log.v("fileCreated", fileCreated + "");
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(response.body().source());
                    sink.close();
                    new DownloadImage(file).execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadImage extends AsyncTask<Void, Object, String> {

        String imagePath = "";

        DownloadImage(File file) {
            imagePath = file.getPath();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return imagePath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("Result", s + "");
            imageView.setImageURI(Uri.parse(s));
            pd.dismiss();
        }
    }
}
