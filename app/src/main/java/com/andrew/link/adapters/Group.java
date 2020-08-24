package com.andrew.link.adapters;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<Integer> children = new ArrayList<Integer>();

    public Group(String string) {
        this.string = string;
    }

}