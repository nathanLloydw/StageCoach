package com.example.nathanwilliams.attendencemonitor;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

class globalFunctions
{
    Context context;

    globalFunctions(Context context)
    {
        this.context = context;
    }

    void centerTitle(Activity activity)
    {
        ArrayList<View> textViews = new ArrayList<>();
        activity.getWindow().getDecorView().findViewsWithText(textViews, activity.getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0)
        {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1)
            {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            }
            else
            {
                for(View v : textViews)
                {
                    if(v.getParent() instanceof Toolbar)
                    {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null)
            {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
}
