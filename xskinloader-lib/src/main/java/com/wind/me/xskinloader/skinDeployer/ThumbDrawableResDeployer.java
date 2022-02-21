package com.wind.me.xskinloader.skinDeployer;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;

public class ThumbDrawableResDeployer implements ISkinResDeployer {
    public void deploy(View view, SkinAttr skinAttr, ISkinResourceManager resource) {
        Drawable drawable = null;
        boolean bool1 = view instanceof Switch;
        boolean bool2 = view instanceof SeekBar;
        if (!bool1 && !bool2)
            return;
        if (SkinConfig.RES_TYPE_NAME_COLOR.equals(skinAttr.attrValueTypeName)) {
            drawable = new ColorDrawable(resource.getColor(skinAttr.attrValueRefId));
        } else if (SkinConfig.RES_TYPE_NAME_DRAWABLE.equals(skinAttr.attrValueTypeName)) {
            drawable = resource.getDrawable(skinAttr.attrValueRefId);
        }
        if (drawable != null) {
            if (bool1) {
                Switch switch_;
                switch_ = (Switch) view;
                switch_.setThumbDrawable(drawable);
                switch_.refreshDrawableState();
            } else {
                SeekBar seekBar;
                seekBar = (SeekBar) view;
                seekBar.setThumb(drawable);
            }
        }
    }
}