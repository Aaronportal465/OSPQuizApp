package com.example.aaronsquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


// OS FUNCTIONS
import android.os.CountDownTimer;
import android.os.Vibrator;

public class QuizActivity extends AppCompatActivity {

    public static final String OTHER_SCORE = "otherScore";

    private static final long COUNTDOWN_IN_MILLIS = 30000;
    private static final String TAG = "A Monkey String?";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;

    //Radio Buttons
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    //Default Colors
    private ColorStateList rbDefaultColor;
    private ColorStateList countdownDefaultColor;

    //CountDownTimer
    private CountDownTimer countDownTimer;
    private long millisRemaining;

    //Question vars
    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //connects to the corresponding textView from the xml file
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);

        //connects the RadioButtons from the xml file
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        //default colors
        rbDefaultColor = rb1.getTextColors();
        countdownDefaultColor = textViewCountDown.getTextColors();

        //SQLite database
        QuizSQLDatabase sqlDB = new QuizSQLDatabase(this);
        questionList = sqlDB.getAllQuestions();
        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (!selected) {
                    // if any option is checked sees if correct
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()){
                        checkAnswer();
                    }
                    // otherwise if user tries to go to next question without answering vibrates and sends toast
                    else {
                        vibrateOnCommand();
                        Toast.makeText(QuizActivity.this, "Not so fast there kid, you need to choose an answer", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion(){

        // sets all radio buttons to initial default color
        rb1.setTextColor(rbDefaultColor);
        rb2.setTextColor(rbDefaultColor);
        rb3.setTextColor(rbDefaultColor);
        rb4.setTextColor(rbDefaultColor);
        rbGroup.clearCheck();

        // if more questions
        if (questionCounter < questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            // sets next question with options
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            // iterates current question #, sets appropriate textView, starts countdown for question
            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal );
            selected = false;
            buttonConfirmNext.setText("Confirm");

            millisRemaining = COUNTDOWN_IN_MILLIS;
            startCountDown();
        }

        // otherwise finishQuiz
        else {
            finishQuiz();
        }
    }

    // sets up 30 second countdown for each question
    private void startCountDown(){
        countDownTimer = new CountDownTimer(millisRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            //when time runs out checks answer and sends toast
            public void onFinish() {
                millisRemaining = 0;
                updateCountDownText();
                checkAnswer();
                Toast.makeText(QuizActivity.this, "Sorry buddy, but you ran out of time!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    // updates textView of the countdowntimer
    private void updateCountDownText(){

        int min = 0;
        int seconds = (int) (millisRemaining/ 1000) % 60;
        String clock = String.format(Locale.getDefault(), "%02d:%02d", min, seconds);
        textViewCountDown.setTextColor(countdownDefaultColor);
        textViewCountDown.setText(clock);

        // sets color to red under 10 secs
        if (millisRemaining < 11000){
            textViewCountDown.setTextColor(Color.RED);
        }
    }

    // checks if user answer is correct and iterates score otherwise vibrates
    private void checkAnswer(){
        selected = true;
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNum = rbGroup.indexOfChild(rbSelected) + 1;
        if (answerNum == currentQuestion.getAnswerNum()) {
            score++;
            textViewScore.setText("Score: " + score);
        }
        else{
            vibrateOnCommand();
        }
        showSolution();
    }

    // shows correct/incorrect solutions (green and red)
    private void showSolution() {
        // red by default
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        // green if option is correct
        String darkGreen = "#3bb300";
        switch (currentQuestion.getAnswerNum()){
            case 1:
                rb1.setTextColor(Color.parseColor(darkGreen));
                textViewQuestion.setText("Option 1 was correct");
                break;
            case 2:
                rb2.setTextColor(Color.parseColor(darkGreen));
                textViewQuestion.setText("Option 2 was correct");
                break;
            case 3:
                rb3.setTextColor(Color.parseColor(darkGreen));
                textViewQuestion.setText("Option 3 was correct");
                break;
            case 4:
                rb4.setTextColor(Color.parseColor(darkGreen));
                textViewQuestion.setText("Option 4 was correct");
                break;
        }

        // if more questions textView -> Next
        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");

        }

        // otherwise textView -> Finish
        else {
            buttonConfirmNext.setText("Finish");
        }
    }

    // stores highscore with putExtra and finishes quiz
    private void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(OTHER_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // sets a vibrate for 0.4 seconds (400ms)
    private void vibrateOnCommand(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
    }

    private boolean monkeyTampering(){
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager.isUserAMonkey()){
            Log.d(TAG, "Monkey's have been tampering with the system");
            return true;
        }
        Log.d(TAG, "No monkey's have been tampering with the system");
        return false;
    }
}
