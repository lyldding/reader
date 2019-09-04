/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lyldding.myapplication;


import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * @author lyldding
 * @date 2019/9/1
 */
public class SmoothLinkMovementMethod extends ScrollingMovementMethod {
    private static final int CLICK = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private int mTouchSlop = 0;
    private boolean mIsMoved;
    private float mDownX;
    private float mDownY;
    private ClickWordListener mClickWordListener;

    @Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    @Override
    protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode,
                                        int movementMetaState, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getRepeatCount() == 0 && action(CLICK, widget, buffer)) {
                        return true;
                    }
                }
                break;
        }
        return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
    }

    @Override
    protected boolean up(TextView widget, Spannable buffer) {
        if (action(UP, widget, buffer)) {
            return true;
        }

        return super.up(widget, buffer);
    }

    @Override
    protected boolean down(TextView widget, Spannable buffer) {
        if (action(DOWN, widget, buffer)) {
            return true;
        }

        return super.down(widget, buffer);
    }

    @Override
    protected boolean left(TextView widget, Spannable buffer) {
        if (action(UP, widget, buffer)) {
            return true;
        }

        return super.left(widget, buffer);
    }

    @Override
    protected boolean right(TextView widget, Spannable buffer) {
        if (action(DOWN, widget, buffer)) {
            return true;
        }

        return super.right(widget, buffer);
    }

    private boolean action(int what, TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();

        int padding = widget.getTotalPaddingTop() +
                widget.getTotalPaddingBottom();
        int areaTop = widget.getScrollY();
        int areaBot = areaTop + widget.getHeight() - padding;

        int lineTop = layout.getLineForVertical(areaTop);
        int lineBot = layout.getLineForVertical(areaBot);

        int first = layout.getLineStart(lineTop);
        int last = layout.getLineEnd(lineBot);

        ClickableSpan[] candidates = buffer.getSpans(first, last, ClickableSpan.class);

        int a = Selection.getSelectionStart(buffer);
        int b = Selection.getSelectionEnd(buffer);

        int selStart = Math.min(a, b);
        int selEnd = Math.max(a, b);

        if (selStart < 0) {
            if (buffer.getSpanStart(FROM_BELOW) >= 0) {
                selStart = selEnd = buffer.length();
            }
        }

        if (selStart > last)
            selStart = selEnd = Integer.MAX_VALUE;
        if (selEnd < first)
            selStart = selEnd = -1;

        switch (what) {
            case CLICK:
                if (selStart == selEnd) {
                    return false;
                }

                ClickableSpan[] link = buffer.getSpans(selStart, selEnd, ClickableSpan.class);

                if (link.length != 1)
                    return false;

                link[0].onClick(widget);
                break;

            case UP:
                int bestStart, bestEnd;

                bestStart = -1;
                bestEnd = -1;

                for (int i = 0; i < candidates.length; i++) {
                    int end = buffer.getSpanEnd(candidates[i]);

                    if (end < selEnd || selStart == selEnd) {
                        if (end > bestEnd) {
                            bestStart = buffer.getSpanStart(candidates[i]);
                            bestEnd = end;
                        }
                    }
                }

                if (bestStart >= 0) {
                    Selection.setSelection(buffer, bestEnd, bestStart);
                    return true;
                }

                break;

            case DOWN:
                bestStart = Integer.MAX_VALUE;
                bestEnd = Integer.MAX_VALUE;

                for (int i = 0; i < candidates.length; i++) {
                    int start = buffer.getSpanStart(candidates[i]);

                    if (start > selStart || selStart == selEnd) {
                        if (start < bestStart) {
                            bestStart = start;
                            bestEnd = buffer.getSpanEnd(candidates[i]);
                        }
                    }
                }

                if (bestEnd < Integer.MAX_VALUE) {
                    Selection.setSelection(buffer, bestStart, bestEnd);
                    return true;
                }

                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        if (mTouchSlop == 0) {
            mTouchSlop = ViewConfiguration.get(widget.getContext()).getScaledTouchSlop();
        }
        ClickableSpan[] links;
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsMoved = false;
                //设置拖拽状态
                super.onTouchEvent(widget, buffer, event);
                mDownY = event.getY();
                mDownX = event.getX();
                links = findClickableSpans(widget, buffer, event);
                if (links.length != 0) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(links[0]),
                            buffer.getSpanEnd(links[0]));
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mDownX) > mTouchSlop ||
                        Math.abs(event.getY() - mDownY) > mTouchSlop) {
                    mIsMoved = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //清除拖拽状态
                super.onTouchEvent(widget, buffer, event);
                links = findClickableSpans(widget, buffer, event);
                //如果滑动，则不触发点击事件
                if (!mIsMoved && links.length != 0) {
                    links[0].onClick(widget);
                    return true;
                } else {
                    if (!mIsMoved && mClickWordListener != null) {
                        mClickWordListener.onClickEmpty();
                    }
                    Selection.removeSelection(buffer);
                }
                break;
            default:
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    /**
     * 如果点击区域为空白区域，返回空集合
     */
    private ClickableSpan[] findClickableSpans(TextView widget, Spannable buffer, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();
        x += widget.getScrollX();
        y += widget.getScrollY();
        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        int lineMax = (int) layout.getLineMax(line);
        //点击空白区域返回空集合
        if (x > lineMax) {
            return new ClickableSpan[]{};
        }
        return buffer.getSpans(off, off, ClickableSpan.class);
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
        text.removeSpan(FROM_BELOW);
    }

    @Override
    public void onTakeFocus(TextView view, Spannable text, int dir) {
        Selection.removeSelection(text);

        if ((dir & View.FOCUS_BACKWARD) != 0) {
            text.setSpan(FROM_BELOW, 0, 0, Spannable.SPAN_POINT_POINT);
        } else {
            text.removeSpan(FROM_BELOW);
        }
    }

    public static SmoothLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new SmoothLinkMovementMethod();

        return sInstance;
    }

    private static SmoothLinkMovementMethod sInstance;
    private static Object FROM_BELOW = new NoCopySpan.Concrete();


    public void setClickWordListener(ClickWordListener listener) {
        mClickWordListener = listener;
    }

}
