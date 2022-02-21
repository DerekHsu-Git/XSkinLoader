package com.wind.me.xskinloader.skinDeployer;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;
import com.wind.me.xskinloader.util.ReflectUtils;

import java.lang.reflect.Field;

public class ViewScrollBarDrawableResDeployer implements ISkinResDeployer {

    private static final String TAG = "ViewScrollBarDeployer";

    public void deploy(View view, SkinAttr skinAttr, ISkinResourceManager resource) {
        Drawable drawable = null;
        if (view == null)
            return;
        if (SkinConfig.RES_TYPE_NAME_COLOR.equals(skinAttr.attrValueTypeName)) {
            drawable = new ColorDrawable(resource.getColor(skinAttr.attrValueRefId));
        } else if (SkinConfig.RES_TYPE_NAME_DRAWABLE.equals(skinAttr.attrValueTypeName)) {
            drawable = resource.getDrawable(skinAttr.attrValueRefId);
        }
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Object object = ReflectUtils.callMethod(view, "getScrollCache");
                if (object != null) {
                    try {
                        object = ReflectUtils.getField(object, "scrollBar");
                        if (object != null)
                            if ("scrollbarThumbHorizontal".equals(skinAttr.attrName)) {
                                view.setHorizontalScrollbarThumbDrawable(drawable);
                            } else if ("scrollbarThumbVertical".equals(skinAttr.attrName)) {
                                view.setVerticalScrollbarThumbDrawable(drawable);
                            } else if ("scrollbarTrackHorizontal".equals(skinAttr.attrName)) {
                                view.setHorizontalScrollbarTrackDrawable(drawable);
                            } else if ("scrollbarTrackVertical".equals(skinAttr.attrName)) {
                                view.setVerticalScrollbarTrackDrawable(drawable);
                            }
                    } catch (Exception exception) {
                        Log.e(TAG, "convert object to ScrollBarDrawable failed.");
                    }
                }
            } else {
                try {
                    Object mScrollCache = ReflectUtils.getField(view, "mScrollCache");
//                    Field mScrollCacheField = View.class.getDeclaredField();
//                    mScrollCacheField.setAccessible(true);
//                    Object mScrollCache = mScrollCacheField.get(view);
                    Field scrollBarDrawable = mScrollCache.getClass().getDeclaredField("scrollBar");
                    ReflectUtils.callMethod(scrollBarDrawable.getName(), "setVerticalThumbDrawable", drawable);
//                    scrollBarDrawable.setAccessible(true);
//                    Object scrollBar = scrollBarDrawable.get(mScrollCache);
//                    Method method = scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
//                    method.setAccessible(true);
//                    method.invoke(scrollBar, drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}