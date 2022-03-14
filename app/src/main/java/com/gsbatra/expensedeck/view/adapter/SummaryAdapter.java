package com.gsbatra.expensedeck.view.adapter;

import static com.gsbatra.expensedeck.view.fragments.Summary.EmapMTD;
import static com.gsbatra.expensedeck.view.fragments.Summary.EmapYTD;
import static com.gsbatra.expensedeck.view.fragments.Summary.ImapMTD;
import static com.gsbatra.expensedeck.view.fragments.Summary.ImapYTD;
import static com.gsbatra.expensedeck.view.fragments.Summary.monthtotalexpenses;
import static com.gsbatra.expensedeck.view.fragments.Summary.monthtotalincome;
import static com.gsbatra.expensedeck.view.fragments.Summary.yeartotalexpenses;
import static com.gsbatra.expensedeck.view.fragments.Summary.yeartotalincome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.view.fragments.Summary;

import java.util.HashMap;
import java.util.List;

public class SummaryAdapter extends BaseExpandableListAdapter {

    Summary sum = new Summary();

    Context context;
    List<String> listGroup;
    HashMap<String,List<String>> listItem;



    public SummaryAdapter(Context context, List<String> listGroup, HashMap<String,List<String>>
                          listItem) {
        this.context = context;
        this.listGroup = listGroup;
        this.listItem = listItem;
    }


    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        String group = (String) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_group,null);
        }

        TextView textView = view.findViewById(R.id.list_parent);
        textView.setText(group);

        TextView textView2 = view.findViewById(R.id.list_parent_MTD);

        TextView textView3 = view.findViewById(R.id.list_parent_YTD);

        if (group.equals("Expenses")) {
            textView2.setText(String.valueOf(monthtotalexpenses));
            textView3.setText(String.valueOf(yeartotalexpenses));
        }
        else {
            textView2.setText(String.valueOf(monthtotalincome));
            textView3.setText(String.valueOf(yeartotalincome));
        }
        return view;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        String child = (String) getChild(groupPosition,childPosition);
        String group = (String) getGroup(groupPosition);


        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item,null);
        }

        TextView textView = view.findViewById(R.id.list_child); //!!
        textView.setText(child);

        if (EmapMTD.get(child) == null) {

        }
        else {
            Double mtdval = 0.0;
            if (group.equals("Expenses")) {
                mtdval = EmapMTD.get(child);
            }
            else {
                mtdval = ImapMTD.get(child);
            }
            TextView textView2 = view.findViewById(R.id.list_child_MTD);
            textView2.setText(String.valueOf(mtdval));
        }

        if (EmapYTD.get(child) == null) {

        }
        else {
            Double ytdval = 0.0;
            if (group.equals("Expenses")) {
                ytdval = EmapYTD.get(child);
            }
            else {
                ytdval = ImapYTD.get(child);
            }
            TextView textView3 = view.findViewById(R.id.list_child_YTD);
            textView3.setText(String.valueOf(ytdval));
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
