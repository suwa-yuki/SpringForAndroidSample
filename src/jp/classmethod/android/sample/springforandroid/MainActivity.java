package jp.classmethod.android.sample.springforandroid;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.get_xml_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("format", "xml");
                getSupportLoaderManager().initLoader(0, bundle, callbacks);
            }
        });
        
        findViewById(R.id.get_json_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("format", "json");
                getSupportLoaderManager().initLoader(0, bundle, callbacks);
            }
        });
    }
    
    private LoaderCallbacks<String> callbacks = new LoaderCallbacks<String>() {
        
        @Override
        public void onLoaderReset(Loader<String> loader) {
        }
        
        @Override
        public void onLoadFinished(Loader<String> loader, String value) {
            getSupportLoaderManager().destroyLoader(loader.getId());
            TextView v = (TextView) findViewById(R.id.result_text_view);
            v.setText(v.getText() + value);
        }
        
        @Override
        public Loader<String> onCreateLoader(int id, Bundle bundle) {
            CustomLoader loader = new CustomLoader(getApplicationContext(), bundle);
            loader.forceLoad();
            return loader;
        }
    };
    
    private static class CustomLoader extends AsyncTaskLoader<String> {

        private String mFormat;
        
        public CustomLoader(Context context, Bundle bundle) {
            super(context);
            mFormat = bundle.getString("format");
        }

        @Override
        public String loadInBackground() {
            if (TextUtils.equals("xml", mFormat)) {
                return getXml();
            } else {
                return getJson();
            }
        }
        
        private String getXml() {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
            String url = "http://192.168.2.232:8888/book.xml";
            try {
                ResponseEntity<BookXml> responseEntity = template.exchange(url, HttpMethod.GET, null, BookXml.class);
                BookXml res = responseEntity.getBody();
                return res.toString();
            } catch (Exception e) {
                Log.d("Error", e.toString());
                return null;
            }
        }
        
        private String getJson() {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String url = "http://192.168.2.232:8888/book.json";
            try {
                ResponseEntity<BookJson> responseEntity = template.exchange(url, HttpMethod.GET, null, BookJson.class);
                BookJson res = responseEntity.getBody();
                return res.toString();
            } catch (Exception e) {
                Log.d("Error", e.toString());
                return null;
            }
        }
        
    }
}
