package com.example.sinan.texttotexttranslate;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    TextView translate;
    EditText origintxt;
    ImageButton imgg;
    Spinner spinner,spinner2;
    String from,to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        translate=findViewById(R.id.textView);
        origintxt=findViewById(R.id.editText);
        spinner=findViewById(R.id.spinner);
        spinner2=findViewById(R.id.spinner2);
        String s=spinner.getSelectedItem().toString();
        String s1=spinner2.getSelectedItem().toString();

            //selected itemlere kısalatmalarını yaptık
            switch (s) {
                case "English":
                     from ="en";
                case "Turkish":
                    from="tr";
                case "Spanish":
                    from="sp";
                case "French":
                    from="fr";
            }
            switch (s1) {
                case "English":
                    to ="en";
                case "Turkish":
                    to="tr";
                case "Spanish":
                    to="sp";
                case "French":
                    to="fr";
            }

        final String[] textOrig = new String[1];
        SharedPreferences p1= PreferenceManager.getDefaultSharedPreferences((getApplicationContext()));
        SharedPreferences.Editor editor=p1.edit();
        editor.putString("from",from);
        editor.putString("to",to);

        origintxt.setOnKeyListener(new View.OnKeyListener()
            //keyboardda entera basınca yada dpad center yapınca çevirmeyi yapması için

        {       @Override
                public boolean onKey(View view, int keyCode, KeyEvent Event) {

                    if(Event.getAction()==KeyEvent.ACTION_DOWN) {
                    switch(keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            textOrig[0] = origintxt.getText().toString();

                            new TransAsynTask().execute(textOrig[0]);
                            return true;
                            default:
                                break;
                    }
                }


                return false ;
        }

        });

  }


    public String getTranslation(String translatedTextStr) {

        try {
            String authenticationUrl = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
            HttpsURLConnection authConn = (HttpsURLConnection) new URL(authenticationUrl).openConnection();
            authConn.setRequestMethod("POST");
            authConn.setDoOutput(true);
            authConn.setRequestProperty("Ocp-Apim-Subscription-Key", "4eb27dc8067e4dd0a989ed73fa51b83b");
            IOUtils.write("", authConn.getOutputStream(), "UTF-8");
            String token = IOUtils.toString(authConn.getInputStream(), "UTF-8");

            String appId = URLEncoder.encode("Bearer " + token, "UTF-8");
            String text = URLEncoder.encode(translatedTextStr, "UTF-8");
            SharedPreferences p1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String from =p1.getString("from","N/A");
            String to =p1.getString("to","N/A");

            String translatorTextApiUrl = String.format("https://api.microsofttranslator.com/v2/http.svc/GetTranslations?appid=%s&text=%s&from=%s&to=%s&maxTranslations=5", appId, text, from, to);
            HttpsURLConnection translateConn = (HttpsURLConnection) new URL(translatorTextApiUrl).openConnection();
            translateConn.setRequestMethod("POST");
            translateConn.setRequestProperty("Accept", "application/xml");
            translateConn.setRequestProperty("Content-Type", "text/xml");
            translateConn.setDoOutput(true);
            String TranslationOptions = "<TranslateOptions xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\">" +
                    "<Category>general</Category>" +
                    "<ContentType>text/plain</ContentType>" +
                    "<IncludeMultipleMTAlternatives>True</IncludeMultipleMTAlternatives>" +
                    "<ReservedFlags></ReservedFlags>" +
                    "<State>contact with each other</State>" +
                    "</TranslateOptions>";
            translateConn.setRequestProperty("TranslationOptions", TranslationOptions);
            IOUtils.write("", translateConn.getOutputStream(), "UTF-8");
            String resp = IOUtils.toString(translateConn.getInputStream(), "UTF-8");

            System.out.println(resp+"\n\n");
            String s=resp;
            Pattern assign_op= Pattern.compile("(<TranslatedText>)"
                    + "|(<\\/TranslatedText>)"
                    + "|[()\\\\[\\\\]{};=#.,'\\\\^:@!$%&_`*-<>]"
                    + "|[a-zA-Z0-9\\s]*"
                    + "");
            Matcher m = assign_op.matcher(s) ;

            String actualTranslation="";
            Boolean endOfTransTxt=false,startOfTransTxt=false,concat=false;
            String foundRegexStr="",tempStr="";

            while (m.find()) {
                foundRegexStr=m.group();

                if(m.group().matches("(<TranslatedText>)"))  {
                    startOfTransTxt=true;
                }
                else if(m.group().matches("(<\\/TranslatedText>)"))    {
                    endOfTransTxt=true;
                    concat=false;
                }
                else{
                    startOfTransTxt=false;
                    endOfTransTxt=false;
                }

                if(startOfTransTxt==true)  {
                    concat=true;
                }
                else if(concat==true) {
                    tempStr=tempStr+""+m.group();
                }
                else   {

                }
            }
            translatedTextStr=tempStr;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return translatedTextStr;



    }
    private class TransAsynTask extends AsyncTask<String, Void, String>{
    @Override
    protected  void onPreExecute(){
        String S="...";
        translate.setText("...");
    }
        @Override
        protected String doInBackground(String... strings) {
            String output = getTranslation(strings[0]);

            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            translate.setText("Output: " + s);
            super.onPostExecute(s);
        }
    }


}
