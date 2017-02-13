package yrj.ayudasordomudo;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import yrj.ayudasordomudo.interfaces.IMvp;
import yrj.ayudasordomudo.model.Idiomas;
import yrj.ayudasordomudo.presenter.Main_Presenter;


public class MainActivity extends AppCompatActivity implements IMvp.View{
    EditText etText;
    Button btTextToSpeech;
    Button btSpeechToText;
    TextView tvTextReceived;
    Spinner spOrigenLanguage;
    Main_Presenter presenter;
    protected static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActivity();
    }

    private void initActivity() {
        //Setting presenter
        presenter = new Main_Presenter(this);
        //Setting controls
        etText=(EditText)findViewById(R.id.etText);
        btTextToSpeech=(Button)findViewById(R.id.btTextToSpeech);
        btSpeechToText=(Button)findViewById(R.id.btSpeechToText);
        tvTextReceived=(TextView)findViewById(R.id.tvTextReceived);
        spOrigenLanguage=(Spinner) findViewById(R.id.spOrigenLanguage);

        //Setting adapter
        ArrayAdapter<String> aAdapter = new ArrayAdapter<>(this,R.layout.spinner_item, Idiomas.LANGUAGES);
        spOrigenLanguage.setAdapter(aAdapter);

        //Click events
        btTextToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etText.getText().toString().trim();
                presenter.textToSpeech(text);
            }
        });

        btSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText(presenter.getLanguage());
            }
        });

        spOrigenLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spOrigenLanguage.getSelectedItem().toString();
                Locale languageEnum = null;
                switch (selected) {
                    case Idiomas.ENGLISH_LANGUAGE:
                        languageEnum = Idiomas.ENGLISH_LOCALE;
                        break;
                    case Idiomas.FRENCH_LANGUAGE:
                        languageEnum = Idiomas.FRENCH_LOCALE;
                        break;
                    case Idiomas.GERMAN_LANGUAGE:
                        languageEnum = Idiomas.GERMAN_LOCALE;
                        break;
                    case Idiomas.SPANISH_LANGUAGE:
                        languageEnum = Idiomas.SPANISH_LOCALE;
                        break;
                }
                if (languageEnum != null)
                    presenter.setLanguage(languageEnum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setDefaultLanguage();
    }
    public void setDefaultLanguage() {
        String language = Locale.getDefault().getLanguage();
        Locale locale;
        int position;
        switch (language){
            case "en":
                locale = Idiomas.ENGLISH_LOCALE;
                position = 0;
                break;
            case "fr":
                locale = Idiomas.FRENCH_LOCALE;
                position = 1;
                break;
            case "de":
                locale = Idiomas.GERMAN_LOCALE;
                position = 2;
                break;
            case "es":
                locale = Idiomas.SPANISH_LOCALE;
                position = 3;
                break;
            default:
                locale = Idiomas.ENGLISH_LOCALE;
                position = 0;
                break;
        }
        spOrigenLanguage.setSelection(position);
        presenter.setLanguage(locale);
    }
    @Override
    public void setMessageError(String messageError, int idView) {
        Toast.makeText(this, messageError, Toast.LENGTH_SHORT).show();
    }

    public void speechToText(Locale language) {
        String languagePref = language.toString();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);

        try {
            startActivityForResult(intent, RESULT_SPEECH);
            tvTextReceived.setText("");
        } catch(ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.go_play_store_title)
                    .setMessage(R.string.go_play_store_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String appPackageName = "com.google.android.googlequicksearchbox";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvTextReceived.setText(text.get(0));
                }
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.shutdown();
    }
}