package yrj.ayudasordomudo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText etText;
    Button btnToSpeech;
    Button btnToText;
    TextView tvTextRecived;
    Spinner spOrigenLanguage;
    Spinner spFinalLanguage;
    TTS textToSpeech;
    LanguageEnum defaultLanguage;
    protected static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Guardando controles
        etText=(EditText)findViewById(R.id.etText);
        btnToSpeech=(Button)findViewById(R.id.btnToSpeech);
        btnToText=(Button)findViewById(R.id.btnToText);
        tvTextRecived=(TextView)findViewById(R.id.tvTextRecived);
        spOrigenLanguage=(Spinner) findViewById(R.id.spOrigenLanguage);
        spFinalLanguage=(Spinner) findViewById(R.id.spOrigenLanguage);

        ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, Idiomas.LANGUAGES);
        spOrigenLanguage.setAdapter(aAdapter);
        spFinalLanguage.setAdapter(aAdapter);

        Locale defaultLocale = Locale.getDefault();
        SetGetDefaultLanguage();
        textToSpeech = new TTS(getApplicationContext(), defaultLanguage);

        //Eventos
        spOrigenLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = spOrigenLanguage.getSelectedItem().toString();
                LanguageEnum languageEnum = null;
                switch (selected) {
                    case Idiomas.ENGLISH_LANGUAGE:
                        languageEnum = LanguageEnum.English;
                        break;
                    case Idiomas.FRENCH_LANGUAGE:
                        languageEnum = LanguageEnum.French;
                        break;
                    case Idiomas.GERMAN_LANGUAGE:
                        languageEnum = LanguageEnum.German;
                        break;
                    case Idiomas.SPANISH_LANGUAGE:
                        languageEnum = LanguageEnum.Spanish;
                        break;
                }
                textToSpeech.set_language(languageEnum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = etText.getText().toString().trim();
                if (toSpeak != null || toSpeak != ""){
                    textToSpeech.Speak(toSpeak);
                }
            }
        });

        btnToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String language = "";
                LanguageEnum getLanguage = textToSpeech.get_language();
                switch (getLanguage){
                    case English:
                        language = Idiomas.ENGLISH_ACRONYM;
                        break;
                    case Spanish:
                        language = Idiomas.SPANISH_ACRONYM;
                        break;
                    case French:
                        language = Idiomas.FRENCH_ACRONYM;
                        break;
                    case German:
                        language = Idiomas.GERMAN_ACRONYM;
                        break;
                }
                SpeechToText(language);
            }
        });
    }
    /**
     *  Obtiene el idioma que utiliza el sistema Android y marca el Spinner con ese idioma. Si es otro marca por defecto English
     */
    private void SetGetDefaultLanguage() {

        String languageString;
        switch (Locale.getDefault().getLanguage()){
            case Idiomas.ENGLISH_ACRONYM:
                defaultLanguage = LanguageEnum.English;
                languageString = Idiomas.ENGLISH_LANGUAGE;
                break;
            case Idiomas.FRENCH_ACRONYM:
                defaultLanguage = LanguageEnum.French;
                languageString = Idiomas.FRENCH_LANGUAGE;
                break;
            case Idiomas.GERMAN_ACRONYM:
                defaultLanguage = LanguageEnum.German;
                languageString = Idiomas.GERMAN_LANGUAGE;
                break;
            case Idiomas.SPANISH_ACRONYM:
                defaultLanguage = LanguageEnum.Spanish;
                languageString = Idiomas.SPANISH_LANGUAGE;
                break;
            default:
                defaultLanguage = LanguageEnum.English;
                languageString = Idiomas.ENGLISH_LANGUAGE;
                break;
        }
        spOrigenLanguage.setSelection(((ArrayAdapter<String>)spOrigenLanguage.getAdapter()).getPosition(languageString));
    }

    public void SpeechToText(String language)
    {
        String languagePref = language;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);

        try {
            startActivityForResult(intent, RESULT_SPEECH);
            tvTextRecived.setText("");
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvTextRecived.setText(text.get(0));
                }
                break;
            }
        }
    }

    /* Cuando tenga que añadir opciones, quitamos este comentario
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }
    */

}