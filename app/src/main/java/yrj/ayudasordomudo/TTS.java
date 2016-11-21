package yrj.ayudasordomudo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by YeraY on 18/07/2016.
 */
//Text To Speech            Texto a Voz
public class TTS {
    private TextToSpeech textToSpeech;
    private LanguageEnum _language;
    private Locale _locale;
    private Context _context;

    public TTS(Context context, LanguageEnum language) {
        _language = language;
        _context = context;
        set_language(language);
        textToSpeech = new TextToSpeech(_context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(_locale);
            }
        });
    }

    public Locale get_locale(){
        return _locale;
    }
    public LanguageEnum get_language(){
        return _language;
    }
    public void set_language(LanguageEnum language){
        _language = language;
        switch (_language){
            case English:
                _locale = Idiomas.ENGLISH_LOCALE;
                break;
            case Spanish:
                _locale = Idiomas.SPANISH_LOCALE;
                break;
            case French:
                _locale = Idiomas.FRENCH_LOCALE;
                break;
            case German:
                _locale = Idiomas.GERMAN_LOCALE;
                break;
        }
        if(textToSpeech != null){
            textToSpeech.setLanguage(_locale);
        }

    }

    //region public void Speak(String toSpeak){}
    //Fuente: http://stackoverflow.com/questions/27968146/texttospeech-with-api-21/28000527#28000527
    public void Speak(String toSpeak){
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
    //endregion
    public void StopSpeaking(){
        if(textToSpeech != null){
            textToSpeech.stop();
        }
    }

}
