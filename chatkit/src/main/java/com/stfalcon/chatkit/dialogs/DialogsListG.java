/*******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.stfalcon.chatkit.dialogs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.stfalcon.chatkit.commons.models.IDialogG;

/**
 * Component for displaying list of dialogs
 */
public class DialogsListG extends RecyclerView {

    private DialogListStyle dialogStyle;

    public DialogsListG(Context context) {
        super(context);
    }

    public DialogsListG(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
    }

    public DialogsListG(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        SimpleItemAnimator animator = new DefaultItemAnimator();

        setLayoutManager(layout);
        setItemAnimator(animator);

    }

    /**
     * Don't use this method for setting your adapter, otherwise exception will by thrown.
     * Call {@link #setAdapter(DialogsListAdapterG)} instead.
     */
    @Override
    public void setAdapter(Adapter adapter) {
        throw new IllegalArgumentException("You can't set adapter to DialogsList. Use #setAdapter(DialogsListAdapterG) instead.");
    }

    /**
     * Sets adapter for DialogsList
     *
     * @param adapter  Adapter. Must extend DialogsListAdapterG
     * @param <DIALOG> Dialog model class
     */
    public <DIALOG extends IDialogG>
    void setAdapter(DialogsListAdapterG<DIALOG> adapter) {
        setAdapter(adapter, false);
    }

    /**
     * Sets adapter for DialogsList
     *
     * @param adapter       Adapter. Must extend DialogsListAdapterG
     * @param reverseLayout weather to use reverse layout for layout manager.
     * @param <DIALOG>      Dialog model class
     */
    public <DIALOG extends IDialogG>
    void setAdapter(DialogsListAdapterG<DIALOG> adapter, boolean reverseLayout) {
        SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, reverseLayout);

        setItemAnimator(itemAnimator);
        setLayoutManager(layoutManager);

        adapter.setStyle(dialogStyle);

        super.setAdapter(adapter);
    }

    @SuppressWarnings("ResourceType")
    private void parseStyle(Context context, AttributeSet attrs) {
        dialogStyle = DialogListStyle.parse(context, attrs);
    }

}
