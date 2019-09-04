package com.lyldding.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.lyldding.myapplication.reader.ReaderPage;
import com.lyldding.myapplication.reader.WordInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ReaderPage mReaderPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReaderPage = ((ReaderPage) findViewById(R.id.text));
        mReaderPage.setText("\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n\n\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n\n\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n\n\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n\n\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life." +
                "\n\n" +
                "It is said that the true nature of being is veiled. The labor of words, the expression of art, the seemingly ceaseless buzz that is human thought all have in common the need to get at what really is so. The hope to draw close to and possess the truth of being can be a feverish one. In some cases it can even be fatal, if pleasure is one's truth and its attainment more important than life itself. In other lives, though, the search for what is truthful gives life.");
        mReaderPage.setWordInfos(getWordInfos(mReaderPage.getText().toString()), mReaderPage.getText().toString());
        mReaderPage.setClickWordListener(new ClickWordListener() {
            @Override
            public void onClickWord(String word) {
                Toast.makeText(MainActivity.this, word, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickEmpty() {
                Toast.makeText(MainActivity.this, "点击空白区域", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 英文文本获取所有单词
     */
    public List<String> splitWord(@NonNull String text) {
        if (TextUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z-']+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group(0));
        }
        return words;
    }

    private List<WordInfo> getWordInfos(String text) {
        List<String> words = splitWord(text);
        List<WordInfo> wordInfos = new ArrayList<>(words.size());
        int startIndex = 0;
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            int start = text.indexOf(word, startIndex);
            int end = start + word.length();
            startIndex = end;
            WordInfo wordInfo = new WordInfo();
            wordInfo.setStart(start);
            wordInfo.setEnd(end);
            wordInfo.setWord(word);
            wordInfos.add(wordInfo);
        }
        return wordInfos;
    }


}
