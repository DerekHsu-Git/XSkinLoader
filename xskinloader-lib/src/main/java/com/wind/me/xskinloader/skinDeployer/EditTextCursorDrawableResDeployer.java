package com.wind.me.xskinloader.skinDeployer;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;

import java.lang.reflect.Field;

public class EditTextCursorDrawableResDeployer implements ISkinResDeployer {
    public void deploy(View view, SkinAttr skinAttr, ISkinResourceManager resource) {
        Drawable drawable = null;
        if (!(view instanceof EditText))
            return;
        if (SkinConfig.RES_TYPE_NAME_COLOR.equals(skinAttr.attrValueTypeName)) {
            drawable = new ColorDrawable(resource.getColor(skinAttr.attrValueRefId));
        } else if (SkinConfig.RES_TYPE_NAME_DRAWABLE.equals(skinAttr.attrValueTypeName)) {
            drawable = resource.getDrawable(skinAttr.attrValueRefId);
        }
        try {
            if (drawable != null) {
                Field editorFiled;
                // Get the editor
                editorFiled = TextView.class.getDeclaredField("mEditor");
                editorFiled.setAccessible(true);
                Object editor = editorFiled.get(view);
                Field cursorDrawableField;
                if (editor != null) {
                    cursorDrawableField = editor.getClass().getDeclaredField("mDrawableForCursor");
                    cursorDrawableField.setAccessible(true);

                    Drawable drawable1 = (Drawable) cursorDrawableField.get(view);
                    if (drawable1 != null) {
                        drawable.setBounds(drawable1.getBounds());
                    } else {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    }
                    cursorDrawableField.set(editor, drawable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
