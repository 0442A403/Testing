package com.example.petro.newtesting;

import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.example.petro.newtesting.MainActivity.COCO;

class CurrentTest {
    String name, autor, date;
    int showwrong, five, four, three, anon, timer, time, size;
    ArrayList<Integer> tasksSizes;
    private String questions, radio, scores, photos, options, correct;
    String[] questionsM, photosM;
    Integer[] scoresM;
    Boolean[] radioM;
    ArrayList<ArrayList<String>> optionsM;
    ArrayList<ArrayList<Boolean>> correctM;
    void set() {
        try {
            Gson gson = new Gson();

            questionsM = gson.fromJson(new String(questions.getBytes("UTF-8"),"UTF-8"), String[].class);
            Log.d("converting", "QuestionM example - " + questionsM[0]);

            scoresM = gson.fromJson(scores, Integer[].class);
            photosM = gson.fromJson(photos, String[].class);
            Log.d(COCO, photos);
            radioM = gson.fromJson(radio, Boolean[].class);

            String[] optionsHelp = gson.fromJson(options, String[].class);
            String[] correctHelp = gson.fromJson(correct, String[].class);

            optionsM = new ArrayList<>();
            correctM = new ArrayList<>();
            tasksSizes = new ArrayList<>();

            for (String str : optionsHelp) {
                optionsM.add(new ArrayList<String>());
                for (String str1 : gson.fromJson(str, String[].class))
                    optionsM.get(optionsM.size() - 1).add(str1);
                tasksSizes.add(optionsM.get(optionsM.size() - 1).size());
            }

            for (String str : correctHelp) {
                correctM.add(new ArrayList<Boolean>());
                for (boolean str1 : gson.fromJson(str, boolean[].class))
                    correctM.get(correctM.size() - 1).add(str1);
            }

            size = questionsM.length;

            Log.d("converting", "converting is done");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}