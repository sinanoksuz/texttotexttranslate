package com.example.sinan.texttotexttranslate;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

import static com.example.sinan.texttotexttranslate.MainActivity.TranslateTextQuickStart.Translate;
import static com.example.sinan.texttotexttranslate.MainActivity.txtTranslatedText;


/*
import io.github.firemaples.language.Language;
import io.github.firemaples.translate.Translate;
*/


public class MainActivity extends AppCompatActivity {
    String key = "4eb27dc8067e4dd0a989ed73fa51b83b";
    public static TextView txtTranslatedText;
    static EditText txtOriginalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtOriginalText = findViewById(R.id.editText);
        txtTranslatedText = findViewById(R.id.textView);

    }

    public void onClick(View view) throws Exception {
        txtTranslatedText.setText(Translate());

    }


    public static class TranslateTextQuickStart {
        static String subscriptionKey = "4eb27dc8067e4dd0a989ed73fa51b83b";

       static   String host = "https://api.cognitive.microsoft.com/sts/v1.0";
        static  String path = "/V2/Http.svc/Translate";

         static String target = "fr-fr";

         static String text = txtOriginalText.getText().toString();


        public static String Translate() throws Exception {
            String encoded_query = URLEncoder.encode(text, "UTF-8");
            String params = "?to=" + target + "&text=" + text;
            URL url = new URL(host + path + params);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
            connection.setDoOutput(true);

            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        }


    }
}