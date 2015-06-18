package com.example.go.imagedownload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity { // 첫화면
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private boolean downloadMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final EditText editText =  (EditText) findViewById(R.id.editText);
        final Button button =  (Button) findViewById(R.id.button);
        final Button button2 =  (Button) findViewById(R.id.button3);
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        final Context context = this;
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
        gridView.setAdapter(gridAdapter);

        final GridView finalGridView = gridView;
        final GridViewAdapter finalGridAdapter = gridAdapter;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //다운로드 버튼 클릭시
                try {
                    downloadMode = true;
                    imageItems.clear();
                    String html = new DownloadPage().execute(editText.getText().toString()).get();
                    for (String str : ParserImgSrc(html))
                        imageItems.add(new ImageItem(new DownloadImagesTask().execute(str).get(),str));

                    gridAdapter.setData(imageItems);
                    finalGridView.setAdapter(gridAdapter);

                } catch (Exception e) {

                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 즐겨 찾기 버튼 클릭시
                try {
                    downloadMode = false;
                    imageItems.clear();
                    imageItems.addAll(new DatabaseHelper(context).selectAll());
                    gridAdapter.setData(imageItems);
                    finalGridView.setAdapter(gridAdapter);
                } catch (Exception e) {
                }
            }
        });


        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) { // 그리드 화면 클릭시
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Intent intent;
                //Create intent
                if(downloadMode)
                    intent = new Intent(MainActivity.this, DetailsActivity.class);
                else
                    intent = new Intent(MainActivity.this, DetailsActivity2.class);

                intent.putExtra("src", item.getTitle());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });
    }


    protected class DownloadPage extends AsyncTask<String, Void, String> { // 주소를 바탕으로 비동기 쓰레드를 활용하여 다운로드함.

        @Override
        protected String doInBackground(String... urls) {

            String responseStr = null;

            try {
                for (String url : urls) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    HttpResponse httpResponse = httpClient.execute(get);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    responseStr = EntityUtils.toString(httpEntity);
                }
            } catch (Exception e) {

            }
            return responseStr;
        }
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> { // 주소를 바탕으로 비동기 쓰레드를 활용하여 이미지 다운로드함.

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Bitmap", "Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }


    }

    public static List<String> ParserImgSrc(String str) { // html 파싱하여 이미지 주소만 가져오는 메서드

        Pattern nonValidPattern = Pattern // 이미지 주소 뽑아내는 정규식

                .compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");


        List<String> result = new ArrayList<String>();

        Matcher matcher = nonValidPattern.matcher(str);

        while (matcher.find()) {

            result.add(matcher.group(1));

        }

        return result;

    }
}