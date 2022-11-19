package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

import java.util.Locale;

public class Main2Activity extends AppCompatActivity{
    StringBuilder sp;

    Uri imageuri;

    EditText rsltedt;
    Button button;
    ImageView imgprev;
    Spinner spinner;
    String detectedLanguage="null",selectedLanguage="Select";
    EditText txtLanguage;
    TextView identifyLang, textSentiment;
    EditText txtResult, txtSentimentProcess;
    private Object String;
    private java.lang.Object Object;

    TextToSpeech textToSpeech;

    Button tts1,tts2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String imageUrl = getIntent().getStringExtra("imageUri");
        imageuri = Uri.parse(imageUrl);
        rsltedt = findViewById(R.id.textFromPic);
        imgprev = findViewById(R.id.imgpreview);
        textSentiment=findViewById(R.id.textSentiment);
        button = findViewById(R.id.button);

        tts1=findViewById(R.id.btnTTS1);
        tts2=findViewById(R.id.btnTTS2);

        imgprev.setImageURI(imageuri);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txtLanguage=findViewById(R.id.textFromPic);
        identifyLang=findViewById(R.id.detectedLanguage);
        txtResult=findViewById(R.id.translatedText);
        txtSentimentProcess=findViewById(R.id.translatedTextEnglishSentiment);

        BitmapDrawable drawable = (BitmapDrawable) imgprev.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();


        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        } else {

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            sp = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                TextBlock myItem = items.valueAt(i);
                sp.append(myItem.getValue());
                sp.append("\n");


            }

            rsltedt.setText(sp.toString());
        }

        spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lang , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button btnTranslate=findViewById(R.id.btnTranslate);
        identifyLanguage(txtLanguage.getText().toString());
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyLanguage(txtLanguage.getText().toString());
                if (detectedLanguage.equals("null") || selectedLanguage.equals("Select") || detectedLanguage.equals(selectedLanguage)){
                    Toast.makeText(Main2Activity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                }else {
                    translateToEnglish(txtLanguage.getText().toString(), detectedLanguage,"English");
                    translateText(txtLanguage.getText().toString(), detectedLanguage, selectedLanguage);
                }
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext()
                , new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS){
                    if (detectedLanguage.equals("German")) {
                        int lang = textToSpeech.setLanguage(Locale.GERMAN);
                    }
                    else if (detectedLanguage.equals("French")){
                        int lang = textToSpeech.setLanguage(Locale.FRENCH);
                    }
                    else if (detectedLanguage.equals("Chinese")) {
                        Toast.makeText(Main2Activity.this, "TTS Language not supported!", Toast.LENGTH_SHORT).show();
                    }
                    else if (detectedLanguage.equals("Japanese")) {
                        Toast.makeText(Main2Activity.this, "TTS Language not supported!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            }
        });


        tts1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtLanguage.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "No text to process!", Toast.LENGTH_SHORT).show();
                }
                else {
                    identifyLanguage(txtLanguage.getText().toString());

                    int speech = textToSpeech.speak(txtLanguage.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        tts2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtResult.getText().toString().isEmpty() || selectedLanguage.equals("Select")){
                    Toast.makeText(Main2Activity.this, "No text to process or selected language error!", Toast.LENGTH_SHORT).show();
                }else {
                    identifyLanguage(txtLanguage.getText().toString());

                    int speech = textToSpeech.speak(txtResult.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    public void sentimentAnalysis(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        IamAuthenticator authenticator = new IamAuthenticator("rADRI1unAAINa7QeXpT_rC1WAvDJCQDbXB1OafguV3Z2");
        final ToneAnalyzer service = new ToneAnalyzer("2017-09-21", authenticator);
        String translateToEnglish=txtSentimentProcess.getText().toString();
        String t2=txtSentimentProcess.getText().toString();

// Call the service and get the tone
        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(translateToEnglish)
                .build();

        ToneAnalysis tone = service.tone(toneOptions).execute().getResult();
        String scores="Neutral";
        try{
            scores = tone.getDocumentTone()
                    .getTones()
                    .get(0)
                    .getToneName();
        }catch(Exception e){};

        textSentiment.setText(scores);
    }


    public void identifyLanguage(String textCode){
        if (!textCode.equals("")) {
            FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage
                    .getInstance()
                    .getLanguageIdentification(
                            new FirebaseLanguageIdentificationOptions.Builder()
                                    .setConfidenceThreshold(0.34f)
                                    .build());
            languageIdentifier.identifyLanguage(textCode)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    if (languageCode != "und") {
                                        if (languageCode.equals("en")) {
                                            identifyLang.setText("English");
                                            detectedLanguage="English";
                                        }
                                        else if (languageCode.equals("de")) {
                                            identifyLang.setText("German");
                                            detectedLanguage="German";
                                        }
                                        else if (languageCode.equals("fil")) {
                                            identifyLang.setText("Filipino");
                                            detectedLanguage="Tagalog";
                                        }
                                        else if (languageCode.equals("id")) {
                                            identifyLang.setText("Indonesian");
                                            detectedLanguage="Indonesian";
                                        }
                                        else if (languageCode.equals("es")) {
                                            identifyLang.setText("Spanish");
                                            detectedLanguage="Spanish";
                                        }
                                        else if (languageCode.equals("fr")) {
                                            identifyLang.setText("French");
                                            detectedLanguage="French";
                                        }
                                        else if (languageCode.equals("zh")) {
                                            identifyLang.setText("Chinese");
                                            detectedLanguage="Chinese";
                                        }
                                        else if (languageCode.equals("ja")) {
                                            identifyLang.setText("Japanese");
                                            detectedLanguage="Japanese";
                                        }

                                    } else {
                                        identifyLang.setText("Unidentified");
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Main2Activity.this, "Internal error", Toast.LENGTH_SHORT).show();
                                }
                            });
        }
    }

    public void translateText(String text, String sourceLang, String targetLang){
        FirebaseTranslator Translator = null;
        if (sourceLang.equals("English") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }

        //---------------------------------------------------------------------------------------------------//

        else if (sourceLang.equals("Tagalog") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Tagalog") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Tagalog") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Tagalog") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Tagalog") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//

        else if (sourceLang.equals("Spanish") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//

        else if (sourceLang.equals("German") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//

        else if (sourceLang.equals("French") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        else if (sourceLang.equals("Indonesian") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        else if (sourceLang.equals("Chinese") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        else if (sourceLang.equals("Japanese") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("Tagalog")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.TL)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("Spanish")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.ES)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("German")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.DE)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("French")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.FR)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("Indonesian")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.ID)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        else if (sourceLang.equals("Tagalog") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("Chinese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        else if (sourceLang.equals("English") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Tagalog") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Spanish") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("German") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("French") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("Japanese")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.JA)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        //---------------------------------------------------------------------------------------------------//
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        Translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });


        Translator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                txtResult.setText(translatedText);
                                Toast.makeText(Main2Activity.this,"Text Translated",Toast.LENGTH_SHORT).show();
                                sentimentAnalysis();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main2Activity.this,"Model is still downloading please try again!",Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    public String translateToEnglish(String text, String sourceLang, String targetLang){
        FirebaseTranslator Translator = null;
        if  (sourceLang.equals("Tagalog") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.TL)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }

        else if (sourceLang.equals("Spanish") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ES)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }

        else if (sourceLang.equals("German") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.DE)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }

        else if (sourceLang.equals("French") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.FR)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Indonesian") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ID)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Japanese") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.JA)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("Chinese") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }
        else if (sourceLang.equals("English") && targetLang.equals("English")){
            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
            Translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        }

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        Translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });


        Translator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                txtSentimentProcess.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
        return txtSentimentProcess.getText().toString();
    }

}
