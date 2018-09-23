package com.example.sow2.translate;

import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sow2.translate.configuration.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech TTS;
    private Button buttonSpeakSourceLanguage;
    private Button buttonSpeakTargetLanguage;
    private TextView textSourceContent;
    private TextView textTargetContent;
    private static final int SPEAK_SOURCE_LANG_REQUEST_CODE = 69;
    private static final int SPEAK_TARGET_LANG_REQUEST_CODE = 96;
    private String target_lange = "en";
    Intent intent;
    String targetLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

    }

    private void Init() {
        buttonSpeakSourceLanguage = findViewById(R.id.button_speak_source_language);
        buttonSpeakTargetLanguage = findViewById(R.id.button_speak_target_language);
        textSourceContent = findViewById(R.id.text_source_content);
        textTargetContent = findViewById(R.id.text_target_content);

        intent = getIntent();
        targetLanguage = intent.getStringExtra("targetLanguage");
        if(targetLanguage == null) {
            targetLanguage = "en";
        }
        String flag = targetLanguage;
        if(flag.contains("-")) {
            flag = targetLanguage.replace("-", "");
            flag = flag.toLowerCase();
        }
        int resId = getResources().getIdentifier(flag, "drawable", getPackageName());
        buttonSpeakTargetLanguage.setBackgroundResource(resId);
        Toast.makeText(this, "ngon ngu: " + targetLanguage, Toast.LENGTH_SHORT).show();

        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int language = TTS.setLanguage(new Locale(targetLanguage));

                    if(language == TextToSpeech.LANG_MISSING_DATA
                            || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "NGON NGU KHONG DUOC HO TRO",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {

                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Khong the khoi tao!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSpeakSourceLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSpeechInput(SPEAK_SOURCE_LANG_REQUEST_CODE, "vi");

            }
        });

        buttonSpeakTargetLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput2(SPEAK_TARGET_LANG_REQUEST_CODE, targetLanguage);
            }
        });
    }

    private void say(String content) {
//        TTS.setPitch(0.1f);
//        TTS.setSpeechRate(50);
        TTS.speak(content, TextToSpeech.QUEUE_FLUSH, null);

        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, content);

        String tempFilename = "translateFromSourceToTarget.wav";
        String tempDesFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/" + tempFilename;

        TTS.synthesizeToFile(content, myHashRender, tempDesFile);
    }

    public void getSpeechInput(int requestCode, String locale) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, requestCode);
        }
        else {
            Toast.makeText(this, "Thiet bi khong ho tro", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSpeechInput2(int requestCode, String locale) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, requestCode);
        }
        else {
            Toast.makeText(this, "Thiet bi khong ho tro", Toast.LENGTH_SHORT).show();
        }
    }

    private void translateFromSourceToTarget(String sentence, String targetLanguage) {

        String url = Constants.Server.SOURCE_TALK;
        String query = "dịch câu " + sentence + getTargetLanguageQuery(targetLanguage, "sang");
        JsonObject json = new JsonObject();
        json.addProperty("query", query);

        Ion.with(getApplicationContext())
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonElement eleMessage = result.get("message");
                        String message = eleMessage.getAsString();
                        textTargetContent.setText(message);
                        say(message);
                    }
                });
    }

    private void translateFromTargetToSource(final String sentence, String sourceLanguage) {

        String url = Constants.Server.SOURCE_TALK;
        String query = "translate sentence " + sentence + getTargetLanguageQuery(sourceLanguage, "from") + " to tiếng Việt";
        JsonObject json = new JsonObject();
        json.addProperty("query", query);

        Ion.with(getApplicationContext())
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonElement eleMessage = result.get("message");
                        String message = eleMessage.getAsString();
                        textSourceContent.setText(message);
                        TTS.setLanguage(new Locale("vi"));
                        say(message);
                    }
                });
    }

    private String getTargetLanguageQuery(String targetLanguage, String to) {
        String targetLanguageQuery = "";
        switch (targetLanguage) {
            case "fr":
                targetLanguageQuery = " " + to + " tiếng Pháp";
                break;
            case "th":
                targetLanguageQuery = " " + to + " tiếng Thái";
                break;
            case "ja":
                targetLanguageQuery = " " + to + " tiếng Nhật";
                break;
            case "es":
                targetLanguageQuery = " " + to + " tiếng Tây Ban Nha";
                break;
            case "it":
                targetLanguageQuery = " " + to + " tiếng Ý";
                break;
            case "ko":
                targetLanguageQuery = " " + to + " tiếng Hàn";
                break;
            case "zh-CN":
                targetLanguageQuery = " " + to + " tiếng Trung";
                break;
            case "hi":
                targetLanguageQuery = " " + to + " tiếng Ấn Độ";
                break;
            case "de":
                targetLanguageQuery = " " + to + " tiếng Đức";
                break;
            case "pt":
                targetLanguageQuery = " " + to + " tiếng Bồ Đào Nha";
                break;
            case "ru":
                targetLanguageQuery = " " + to + " tiếng Nga";
                break;
            case "lo":
                targetLanguageQuery = " " + to + " tiếng Lào";
                break;
            case "km":
                targetLanguageQuery = " " + to + " tiếng Khơ Me";
                break;
            case "el":
                targetLanguageQuery = " " + to + " tiếng Hy Lạp";
                break;
            case "ar":
                targetLanguageQuery = " " + to + " tiếng Ả Rập";
                break;
            case "nl":
                targetLanguageQuery = " " + to + " tiếng Hà Lan";
                break;
            case "id":
                targetLanguageQuery = " " + to + " tiếng in đô";
                break;
            case "ms":
                targetLanguageQuery = " " + to + " tiếng mã lai";
                break;
            case "la":
                targetLanguageQuery = " " + to + " tiếng Latin";
                break;
            case "my":
                targetLanguageQuery = " " + to + " tiếng Myanmar";
                break;
            case "pl":
                targetLanguageQuery = " " + to + " tiếng Ba Lan";
                break;
            case "sv":
                targetLanguageQuery = " " + to + " tiếng Thụy Điển";
                break;
            case "tr":
                targetLanguageQuery = " " + to + " tiếng Thổ Nhĩ Kỳ";
                break;
            default:
                targetLanguageQuery = " " + to + " tiếng Anh";
                break;
        }
        return  targetLanguageQuery;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEAK_SOURCE_LANG_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textSourceContent.setText(result.get(0));
                    translateFromSourceToTarget(result.get(0), targetLanguage);
                }
                break;
            case SPEAK_TARGET_LANG_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textTargetContent.setText(result.get(0));
                    translateFromTargetToSource(result.get(0), targetLanguage);
                }
                break;
        }
    }
}
