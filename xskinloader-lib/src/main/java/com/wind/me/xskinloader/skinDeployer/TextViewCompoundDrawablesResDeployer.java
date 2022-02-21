package com.wind.me.xskinloader.skinDeployer;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;

public class TextViewCompoundDrawablesResDeployer implements ISkinResDeployer {
    public void deploy(View view, SkinAttr skinAttr, ISkinResourceManager resource) {
        Drawable drawable = null;
        if (!(view instanceof TextView))
            return;
        if (SkinConfig.RES_TYPE_NAME_COLOR.equals(skinAttr.attrValueTypeName)) {
            drawable = new ColorDrawable(resource.getColor(skinAttr.attrValueRefId));
        } else if (SkinConfig.RES_TYPE_NAME_DRAWABLE.equals(skinAttr.attrValueTypeName)) {
            drawable = resource.getDrawable(skinAttr.attrValueRefId);
        }
        TextView textView = (TextView) view;
        Drawable[] drawablesArrays = textView.getCompoundDrawables(); //left \ top \ right \bottom
        Drawable[] drawablesArraysForRl = textView.getCompoundDrawablesRelative();// start \ top \ end \ bottom for relative layout
        Drawable drawableLeft, drawableTop, drawableRight, drawableBottom;
        boolean hasBorder = (drawablesArraysForRl[0] != null || drawablesArraysForRl[2] != null);
        if (hasBorder) {
            drawableLeft = drawablesArraysForRl[0];
        } else {
            drawableLeft = drawablesArrays[0];
        }
        drawableTop = drawablesArrays[1];
        if (hasBorder) {
            drawableRight = drawablesArraysForRl[2];
        } else {
            drawableRight = drawablesArrays[2];
        }
        drawableBottom = drawablesArrays[3];
        Drawable drawableLeft_new, drawableTop_new, drawableRight_new, drawableBottom_new;
        if ("drawableStart".equals(skinAttr.attrName) || "drawableLeft".equals(skinAttr.attrName)) {
            if (drawableLeft != null && drawable != null)
                drawable.setBounds(drawableLeft.getBounds());
            drawableBottom_new = drawableBottom;
            drawableTop_new = drawableTop;
            drawableLeft_new = drawable;
            drawableRight_new = drawableRight;
        } else if ("drawableTop".equals(skinAttr.attrName)) {
            if (drawableTop != null && drawable != null)
                drawable.setBounds(drawableTop.getBounds());
            drawableRight_new = drawableRight;
            drawableLeft_new = drawableLeft;
            drawableTop_new = drawable;
            drawableBottom_new = drawableBottom;
        } else if ("drawableEnd".equals(skinAttr.attrName) || "drawableRight".equals(skinAttr.attrName)) {
            if (drawableRight != null && drawable != null)
                drawable.setBounds(drawableRight.getBounds());
            drawableRight_new = drawable;
            drawableLeft_new = drawableLeft;
            drawableTop_new = drawableTop;
            drawableBottom_new = drawableBottom;
        } else {
            drawableRight_new = drawableRight;
            drawableLeft_new = drawableLeft;
            drawableTop_new = drawableTop;
            drawableBottom_new = drawableBottom;
            if ("drawableBottom".equals(skinAttr.attrName)) {
                if (drawableBottom != null && drawable != null)
                    drawable.setBounds(drawableBottom.getBounds());
                drawableRight_new = drawableRight;
                drawableLeft_new = drawableLeft;
                drawableTop_new = drawableTop;
                drawableBottom_new = drawable;
            }
        }
        //TODO lower version not got be work
        if (hasBorder) {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setCompoundDrawablesRelative(drawableLeft_new, drawableTop_new, drawableRight_new, drawableBottom_new);
            } else {
                textView.setCompoundDrawables(drawableLeft_new, drawableTop_new, drawableRight_new, drawableBottom_new);
            }
        } else {
            textView.setCompoundDrawables(drawableLeft_new, drawableTop_new, drawableRight_new, drawableBottom_new);
        }
    }
}