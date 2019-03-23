package com.cw.ClientJSON;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// ref https://stackoverflow.com/questions/21376147/pass-value-from-android-to-jsp
public class MainActivity extends AppCompatActivity {
    String url = "http://10.1.1.3:8080/LiteNoteWeb/viewNote/viewNote_json.jsp";
    TextView jsonText;
    String idStr;
    EditText editId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

	    jsonText = (TextView) findViewById(R.id.json_text);
	    jsonText.setTextColor(Color.BLACK);
	    jsonText.setText("json text");

        editId = findViewById(R.id.note_id);
    }


    public void requestTitle(View v)
    {
        idStr = ((EditText)findViewById(R.id.note_id)).getText().toString();
        System.out.println("idStr = " + idStr);
        new Thread(runnable).start();
    }


    String strResult;
    Runnable runnable = new Runnable(){
        @Override
        public void run() {

            try { //select by Id
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                if(!idStr.isEmpty()) {
                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("note_id", idStr));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);

                    strResult = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                    System.out.println("strResult = " + strResult);

                    JSONObject jsonObj = new JSONObject(strResult);
                    int id = jsonObj.getInt("note_id");
                    String title = jsonObj.getString("note_title");
                    String uri = jsonObj.getString("note_link_uri");

                    jsonText.setText(id + ". " + title + "\n" + uri);
                }
                else // select all
                {
                    HttpResponse response = client.execute(post);

                    strResult = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                    System.out.println("strResult = " + strResult);

                    JSONArray jsonArray = new JSONArray(strResult);
                    String titleStrings = "";

                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                        int id = jsonObject.getInt("note_id");
                        String title = jsonObject.getString("note_title");
                        String uri = jsonObject.getString("note_link_uri");
                        titleStrings = titleStrings.concat(id + ". " + title + "\n" + uri + "\n\n");
                    }

                    jsonText.setText(titleStrings);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}