package com.byted.camp.todolist.beans;

import android.graphics.Color;

import com.byted.camp.todolist.R;

public enum Priority {
    low(0,R.color.low), middle(1,R.color.middle),high(2,R.color.high);
    public final int intValue;
    public final int intColor;

    Priority(int intValue,int intColor) {
        this.intValue = intValue;
        this.intColor = intColor;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        return low; // default
    }
}
