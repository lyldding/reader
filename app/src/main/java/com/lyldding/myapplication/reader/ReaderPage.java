package com.lyldding.myapplication.reader;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;

import com.lyldding.myapplication.ClickWordListener;
import com.lyldding.myapplication.SmoothLinkMovementMethod;

import java.util.List;

/**
 * @author lyldding
 */
public class ReaderPage extends AppCompatTextView {

    private SpannableString mSpannableString;
    private ForegroundColorSpan mSelectedForegroundSpan;

    private WordInfo mLastClickWordInfo;
    private ClickWordListener mClickWordListener;

    public ReaderPage(Context context) {
        super(context);
        init(context);
    }

    public ReaderPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReaderPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSelectedForegroundSpan = new ForegroundColorSpan(Color.RED);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setMovementMethod(SmoothLinkMovementMethod.getInstance());
        setHighlightColor(Color.TRANSPARENT);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setWordInfos(List<WordInfo> list, String text) {
        mSpannableString = new SpannableString(text);
        for (int i = 0; i < list.size(); i++) {
            WordInfo info = list.get(i);
            mSpannableString.setSpan(new WordClickableSpan(info), info.getStart(), info.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(mSpannableString);
    }


    private void setSelectedSpan(int start, int end) {
        mSpannableString.setSpan(mSelectedForegroundSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(mSpannableString);
    }

    private void removeSelectedSpan() {
        mSpannableString.removeSpan(mSelectedForegroundSpan);
        setText(mSpannableString);
    }

    public void setClickWordListener(ClickWordListener clickWordListener) {
        mClickWordListener = clickWordListener;
        ((SmoothLinkMovementMethod) getMovementMethod()).setClickWordListener(clickWordListener);
    }

    public class WordClickableSpan extends ClickableSpan {
        private WordInfo mWordInfo;

        private WordClickableSpan(WordInfo info) {
            mWordInfo = info;
        }

        @Override
        public void onClick(View widget) {
            if (mWordInfo.isClicked()) {
                removeSelectedSpan();
            } else {
                if (mClickWordListener != null) {
                    mClickWordListener.onClickWord(mWordInfo.getWord());
                }
                setSelectedSpan(mWordInfo.getStart(), mWordInfo.getEnd());
            }
            mWordInfo.setClicked(!mWordInfo.isClicked());
            if (mLastClickWordInfo != mWordInfo) {
                if (mLastClickWordInfo != null) {
                    mLastClickWordInfo.setClicked(false);
                }
                mLastClickWordInfo = mWordInfo;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
        }
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        removeSelectedSpan();
        super.setCustomSelectionActionModeCallback(actionModeCallback);
    }
}
