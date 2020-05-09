package com.mburakcakir.sttvstts;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView dil, metin;
    Button konus, dilDegistir, dinle, dur;
    SeekBar konusmaTizligi, konusmaHizi;
    String dilSec = "Türkçe";
    Locale kullanilanDil;
    TextToSpeech yazidanSese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dil = findViewById(R.id.dil);
        metin = findViewById(R.id.metin);
        konus = findViewById(R.id.konus);
        dilDegistir = findViewById(R.id.dilDegistir);
        dinle = findViewById(R.id.dinle);
        dur = findViewById(R.id.dur);
        konusmaTizligi = findViewById(R.id.konusmaTizligi);
        konusmaHizi = findViewById(R.id.konusmaHizi);

        kullanilanDil = new Locale("tr", "TR");
        dil.setText(dilSec);

        konus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konus();
            }
        });

        dilDegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dilSec.equals("Türkçe")) {
                    dilSec = "İngilizce";
                    kullanilanDil = Locale.ENGLISH;
                } else {
                    dilSec = "Türkçe";
                    kullanilanDil = new Locale("tr", "TR");
                }
                dil.setText(dilSec);
            }
        });

        yazidanSese = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    dinle.setEnabled(true);
                } else
                    Toast.makeText(MainActivity.this, "Başarısız.", Toast.LENGTH_SHORT).show();
            }
        });

        dinle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dinle();
            }
        });

        dur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yazidanSese.stop();
            }
        });

    }

    void dinle() {
        yazidanSese.setLanguage(kullanilanDil);
        String soylenenMetin = metin.getText().toString();
        float tizlik = konusmaTizligi.getProgress() / 50;
        if (tizlik < 0.1) tizlik = 0.1f;

        float hiz = konusmaHizi.getProgress() / 50;
        if (hiz < 0.1) hiz = 0.1f;

        yazidanSese.setPitch(tizlik);
        yazidanSese.setSpeechRate(hiz);

        yazidanSese.speak(soylenenMetin, TextToSpeech.QUEUE_FLUSH, null);


    }

    void konus() {
        Intent sestenYaziya = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sestenYaziya.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        sestenYaziya.putExtra(RecognizerIntent.EXTRA_LANGUAGE, kullanilanDil.toString());
        sestenYaziya.putExtra(RecognizerIntent.EXTRA_PROMPT, "KONUŞ");
        startActivityForResult(sestenYaziya, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> sonuc = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                metin.setText(sonuc.get(0));
            }
        }
    }
}
