package com.liamfruzyna.android.lister;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liamfruzyna.android.lister.Data.AutoList;
import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Item;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Fragments.WLFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mail929 on 3/17/16.
 */
public class Views
{

    public static View createEditItem(LayoutInflater inflater, final int i, LinearLayout list, final WLFragment f)
    {
        View view = inflater.inflate(R.layout.checkbox_edit_item, list, false);

        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
        final EditText name = (EditText) view.findViewById(R.id.itemName);
        Button remove = (Button) view.findViewById(R.id.remove);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button append = (Button) view.findViewById(R.id.append);

        final Item item = Data.getItems().get(i);
        cb.setChecked(item.done);
        name.setText(item.item);

        //listen for checkbox to be checked
        cb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.done = cb.isChecked();
                IO.saveList();
            }
        });

        if (Data.getCurrentList().auto)
        {
            remove.setVisibility(View.GONE);
        } else
        {
            remove.setVisibility(View.VISIBLE);
            remove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //remove the item
                    f.edit = -1;
                    IO.log("EditItemDialog", "Removing " + item);
                    Data.getItems().remove(item);
                    Data.getCurrentList().items.remove(item);
                    f.removeItemSnackbar(item);
                    IO.saveList();
                    f.updateList();
                }
            });
        }
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //transition back to just checkbox
                if(name.getText().toString().equals(""))
                {
                    IO.log("EditItemDialog", "Removing " + item);
                    Data.getItems().remove(item);
                    Data.getCurrentList().items.remove(item);
                    f.removeItemSnackbar(item);
                    IO.saveList();
                }
                f.edit = -1;
                f.updateList();
            }
        });
        append.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                f.edit = -1;
                IO.log("EditItemDialog", "Updating " + item.item + " to " + name.getText().toString());
                item.item = name.getText().toString();
                IO.saveList();
                f.updateList();
            }
        });
        return view;
    }

    //creates the textview with a lists tags
    public static TextView createTags(Context c)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tags: ");
        for (String list : Data.getListFromName(Data.getCurrentName()).tags)
        {
            sb.append(list + " ");
        }
        TextView tv = new TextView(c);
        tv.setText(sb.toString());
        return tv;
    }

    //creates the textview with a lists tags
    public static String createEditTags()
    {
        StringBuilder sb = new StringBuilder();
        for (String list : Data.getListFromName(Data.getCurrentName()).tags)
        {
            sb.append(list + " ");
        }
        return sb.toString();
    }

    //creates the textview with a lists criteria
    public static TextView createCriteria(Context con)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Criteria:");
        for (String c : ((AutoList) Data.getListFromName(Data.getCurrentName())).getCriteria())
        {
            sb.append("\n" + c);
        }
        TextView tv = new TextView(con);
        tv.setText(sb.toString());
        return tv;
    }


    //creates the item view that is displayed on screen
    public static View createItem(LayoutInflater inflater, final int i, LinearLayout list, final WLFragment f)
    {
        View view = inflater.inflate(R.layout.checkbox_list_item, list, false);

        //init checkbox and set text, checked status, and color
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        int color = Color.parseColor(Data.getItems().get(i).color);

        //color item text based off date (late is red, day of is orange)
        SharedPreferences settings = f.getActivity().getSharedPreferences(IO.PREFS, 0);
        boolean highlight = settings.getBoolean(IO.HIGHLIGHT_DATE_PREF, true);
        if (highlight)
        {
            Date date = Data.getItems().get(i).date;
            Date today = Calendar.getInstance().getTime();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate() && !Data.getItems().get(i).done)
            {
                color = Color.parseColor("#FFA500");
            } else if (date.compareTo(today) < 0 && !Data.getItems().get(i).done)
            {
                color = Color.RED;
            }
        }

        SpannableStringBuilder s = Util.colorTags(Data.getItems().get(i).item, color);
        cb.setText(s);
        cb.setTextColor(color);
        cb.setChecked(Data.getItems().get(i).done);

        //if item is done cross it out
        if (Data.getItems().get(i).done)
        {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            cb.setPaintFlags(0);
        }

        //listen for checkbox to be checked
        cb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Data.getItems().get(i).done = cb.isChecked();
                //if it is checked cross it out
                if (cb.isChecked())
                {
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else
                {
                    cb.setPaintFlags(0);
                }
                IO.saveList();

            }
        });

        //listen for item to be long pressed
        cb.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //transition to edit checkbox
                f.edit = i;
                f.updateList();
                return true;
            }
        });
        return view;
    }
}