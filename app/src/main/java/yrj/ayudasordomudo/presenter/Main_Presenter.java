package yrj.ayudasordomudo.presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

import yrj.ayudasordomudo.interfaces.IMvp;

/**
 * Created by yeray697 on 21/11/16.
 */

public class Main_Presenter implements IMvp.Presenter {

    IMvp.View view;
    Context context;
    TextToSpeech textToSpeech;
    Locale language;

    public Main_Presenter(IMvp.View view){
        this.view = view;
        this.context = (Context) view;

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS)
                    textToSpeech.setLanguage(language);
            }
        });
    }

    @Override
    public void textToSpeech(String text) {
        if (!TextUtils.isEmpty(text)){
            speak(text);
        } else {
            //TODO Show error
        }
    }

    //Fuente: http://stackoverflow.com/questions/27968146/texttospeech-with-api-21/28000527#28000527
    public void speak(String toSpeak){
        if (textToSpeech != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(toSpeak);
            } else {
                ttsUnder20(toSpeak);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void stopSpeaking(){
        if(textToSpeech != null){
            textToSpeech.stop();
        }
    }


    public Locale getLanguage() {
        return this.language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
        switch (textToSpeech.isLanguageAvailable(this.language)) {
            case TextToSpeech.LANG_AVAILABLE:
                break;
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                Toast.makeText(context, "LANG_COUNTRY_VAR_AVAILABLE", Toast.LENGTH_SHORT).show();
                break;
            case TextToSpeech.LANG_MISSING_DATA:
                Toast.makeText(context, "LANG_MISSING_DATA", Toast.LENGTH_SHORT).show();
                break;
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                Toast.makeText(context, "LANG_COUNTRY_AVAILABLE", Toast.LENGTH_SHORT).show();
                break;
            case TextToSpeech.LANG_NOT_SUPPORTED:
                Toast.makeText(context, "LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }

        textToSpeech.stop();
        textToSpeech.setLanguage(this.language);
            //textToSpeech.setLanguage(this.language);
    }
}
