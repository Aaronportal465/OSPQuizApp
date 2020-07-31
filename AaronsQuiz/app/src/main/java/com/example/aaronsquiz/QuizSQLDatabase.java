package com.example.aaronsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.aaronsquiz.QuizBaseCol.*;

import java.util.ArrayList;
import java.util.List;

public class QuizSQLDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StarWarsQuiz.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;

    public QuizSQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        this.database = database;
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NUM + " INTEGER" + ")";

        database.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // can setup later
    }


    private void fillQuestionsTable() {

        // populates the questions data
        Question q1 = new Question("Who is NOT a Master on the Jedi council?", "Anakin Skywalker", "Obi Wan Kenobi", "Mace Windu", "Yoda", 1);
        Question q2 = new Question("Who was Anakin Skywalker's padawan?", "Kit Fisto", "Ashoka Tano", "Barris Ofee", "Jacen Solo",2);
        Question q3 = new Question("What Separatist leader liked to make find additions to his collection?", "Pong Krell", "Count Dooku", "General Grevious", "Darth Bane", 3);
        Question q4 = new Question("Choose the correct Response:\n Hello There!", "General Kenobi!", "You are a bold one!", "You never should have come here!", "You turned her against me!", 1);
        Question q5 = new Question("What ancient combat technique did Master Obi Wan Kenobi use to his advantage throughout the Clone Wars?", "Kendo arts", "The High ground", "Lightsaber Form VIII", "Force healing", 2);
        Question q6 = new Question("What was the only surviving member of Domino squad?", "Fives", "Heavy", "Echo", "Jesse", 3);
        Question q7 = new Question("What Jedi brutally murdered children as a part of his descent to become a Sith?", "Quinlan Vos", "Plo Koon", "Kit Fisto", "Anakin Skywalker", 4);
        Question q8 = new Question("What Sith was the first to reveal himself to the Jedi after a millenia and was subsquently cut in half shortly after?", "Darth Plagieus", "Darth Maul", "Darth Bane", "Darth Cadeus", 2);
        Question q9 = new Question("What 4 armed creature operates a diner on the upper levels of Coruscant?", "Dexter Jettster", "Cad Bane", "Aurua Sing", "Dorme Amidala", 1);
        Question q10 = new Question("What ruler fell in love with an underage boy and subsequently married him once he became a Jedi?", "Aurua Sing", "Duttchess Satine", "Mara Jade", "Padme Amidala", 4);

        // adds them to the list
        addQuestion(q1);
        addQuestion(q2);
        addQuestion(q3);
        addQuestion(q4);
        addQuestion(q5);
        addQuestion(q6);
        addQuestion(q7);
        addQuestion(q8);
        addQuestion(q9);
        addQuestion(q10);
    }

    //adds the questions
    private void addQuestion(Question question) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        contentValues.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        contentValues.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        contentValues.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        contentValues.put(QuestionsTable.COLUMN_OPTION4, question.getOption4());
        contentValues.put(QuestionsTable.COLUMN_ANSWER_NUM, question.getAnswerNum());
        database.insert(QuestionsTable.TABLE_NAME, null, contentValues);
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        database = getReadableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION4)));
                question.setAnswerNum(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NUM)));
                questionList.add(question);
            } while (c.moveToNext());
        }
        c.close();
        return questionList;
    }


}