package com.example.petro.newtesting;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by petro on 14.05.2017.
 */

class CurrentAnswer {
    int id, test, mark, time;
    String student, date, scores;
    private String answer;
    ArrayList<ArrayList<Boolean>> answerM;
    public void set() {
        Gson gson = new Gson();
        String[] helpArray = gson.fromJson(answer, String[].class);
        answerM = new ArrayList<>();
        for (String str : helpArray) {
            answerM.add(new ArrayList<Boolean>());
            int index = answerM.size() - 1;
            for (boolean bool : gson.fromJson(str, boolean[].class))
                answerM.get(index).add(bool);
        }
    }
}
