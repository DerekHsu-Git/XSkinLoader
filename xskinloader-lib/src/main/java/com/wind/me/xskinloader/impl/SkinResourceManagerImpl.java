package com.wind.me.xskinloader.impl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;

import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Windy on 2018/1/10.
 */

public class SkinResourceManagerImpl implements ISkinResourceManager {

    private static final String TAG = "SkinResourceManagerImpl";

    private Resources mDefaultResources;
    private String mSkinPluginPackageName;
    private Resources mSkinPluginResources;

    public SkinResourceManagerImpl(Context context, String pkgName, Resources resources) {
        mDefaultResources = context.getResources();
        mSkinPluginPackageName = pkgName;
        mSkinPluginResources = resources;
    }

    private AnimationDrawable getAnimationDrawable(AnimationDrawable paramAnimationDrawable, int resId) {
        try {
            XmlResourceParser xmlResourceParser = this.mDefaultResources.getXml(resId);
            int numberOfFrames = paramAnimationDrawable.getNumberOfFrames();
            AnimationDrawable animationDrawable = new AnimationDrawable();
            int j;
            for (resId = 0; xmlResourceParser.getEventType() != XmlPullParser.END_DOCUMENT && resId < numberOfFrames; resId = j) {
                if (xmlResourceParser.getEventType() == XmlPullParser.START_TAG) {
                    if ("item".equals(xmlResourceParser.getName()))
                        for (byte b = 0; b < xmlResourceParser.getAttributeCount(); b++) {
                            String name = xmlResourceParser.getAttributeName(b);
                            String value = xmlResourceParser.getAttributeValue(b);
                            if ("drawable".equals(name)) {
                                animationDrawable.addFrame(getDrawable(Integer.parseInt(value.substring(1))), paramAnimationDrawable.getDuration(resId));
                                break;
                            }
                        }
                    j = resId;
                } else {
                    j = resId;
                    if (xmlResourceParser.getEventType() == XmlPullParser.END_TAG) {
                        if ("item".equals(xmlResourceParser.getName()))
                            j = resId + 1;
                    }
                }
                xmlResourceParser.next();
            }
            return animationDrawable;
        } catch (XmlPullParserException | IOException e) {
            Log.i("SkinResourceManagerImpl", "getAnimationDrawable ignored");
            e.printStackTrace();
            return paramAnimationDrawable;
        }
    }

    private LayerDrawable getLayerListDrawable(LayerDrawable paramLayerDrawable, int paramInt) {
        try {
            XmlResourceParser xmlResourceParser = this.mDefaultResources.getXml(paramInt);
            int numberOfLayers = paramLayerDrawable.getNumberOfLayers();
            int j;
            for (paramInt = 0; xmlResourceParser.getEventType() != XmlPullParser.END_DOCUMENT && paramInt < numberOfLayers; paramInt = j) {
                if (xmlResourceParser.getEventType() == XmlPullParser.START_TAG) {
                    String str = xmlResourceParser.getName();
                    for (j = 0; j < xmlResourceParser.getAttributeCount(); j++) {
                        String name = xmlResourceParser.getAttributeName(j);
                        String value = xmlResourceParser.getAttributeValue(j);
                        Drawable drawable = getDrawable(Integer.parseInt(value.substring(1)));
                        if ("drawable".equals(name)) {
                            if ("item".equals(str)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    paramLayerDrawable.setDrawable(paramInt, drawable);
                                } else {
                                    int layoutId = paramLayerDrawable.getId(paramInt);
                                    paramLayerDrawable.setDrawableByLayerId(layoutId, drawable);
                                }
                                break;
                            }
                            if ("clip".equals(str)) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        ((ClipDrawable) paramLayerDrawable.getDrawable(paramInt)).setDrawable(drawable);
                                    } else {
                                        int layoutId = paramLayerDrawable.getId(paramInt);
                                        paramLayerDrawable.setDrawableByLayerId(layoutId, drawable);
                                        ((ClipDrawable) paramLayerDrawable.getDrawable(paramInt)).invalidateSelf();
                                    }
                                } catch (Exception exception) {
                                    String stringBuilder = "update ClipDrawable failed: " +
                                            exception.getMessage();
                                    Log.i("SkinResourceManagerImpl", stringBuilder);
                                }
                            }
                            break;
                        }
                    }
                    j = paramInt;
                } else {
                    j = paramInt;
                    if (xmlResourceParser.getEventType() == XmlPullParser.END_TAG) {
                        if ("item".equals(xmlResourceParser.getName()))
                            j = paramInt + 1;
                    }
                }
                xmlResourceParser.next();
            }
            return paramLayerDrawable;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return paramLayerDrawable;
        }
    }

    private Drawable getOriginDrawable(int paramInt) throws Resources.NotFoundException {
        LayerDrawable layerDrawable = null;
        Drawable drawable1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable1 = this.mDefaultResources.getDrawable(paramInt, null);
        } else {
            drawable1 = this.mDefaultResources.getDrawable(paramInt);
        }
        if (drawable1 instanceof LayerDrawable)
            layerDrawable = getLayerListDrawable((LayerDrawable) drawable1, paramInt);
        return (Drawable) layerDrawable;
    }

    public ColorStateList getSelectorColor(int xmlRes) {
        ArrayList<int[]> arrayList = new ArrayList();
        ArrayList<Integer> arrayList1 = new ArrayList();
        try {
            XmlResourceParser xmlResourceParser = this.mDefaultResources.getXml(xmlRes);
            while (true) {
                int type = xmlResourceParser.getEventType();
                int stateInt = 0;
                if (type != XmlPullParser.END_DOCUMENT) {
                    if (xmlResourceParser.getEventType() == XmlPullParser.START_TAG && "item".equals(xmlResourceParser.getName())) {
                        ArrayList<Integer> arrayList2 = new ArrayList();
                        type = 0;
                        int colorInt = 0;
                        while (type < xmlResourceParser.getAttributeCount()) {
                            String name = xmlResourceParser.getAttributeName(type);
                            String value = xmlResourceParser.getAttributeValue(type);
                            int isEnablePreffix = "true".equals(value) ? 1 : -1;
                            switch (name) {
                                default:
                                    stateInt = -1;
                                    break;
                                case "state_accelerated":
                                    stateInt = isEnablePreffix * android.R.attr.state_accelerated;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_active":
                                    stateInt = isEnablePreffix * android.R.attr.state_active;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_activated":
                                    stateInt = isEnablePreffix * android.R.attr.state_activated;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_checkable":
                                    stateInt = isEnablePreffix * android.R.attr.state_checkable;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_checked":
                                    stateInt = isEnablePreffix * android.R.attr.state_checked;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_drag_can_accept":
                                    stateInt = isEnablePreffix * android.R.attr.state_drag_can_accept;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_drag_hovered":
                                    stateInt = isEnablePreffix * android.R.attr.state_drag_hovered;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_enabled":
                                    stateInt = isEnablePreffix * android.R.attr.state_enabled;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_first":
                                    stateInt = isEnablePreffix * android.R.attr.state_first;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_focused":
                                    stateInt = isEnablePreffix * android.R.attr.state_focused;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_hovered":
                                    stateInt = isEnablePreffix * android.R.attr.state_hovered;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_last":
                                    stateInt = isEnablePreffix * android.R.attr.state_last;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_middle":
                                    stateInt = isEnablePreffix * android.R.attr.state_middle;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_pressed":
                                    stateInt = isEnablePreffix * android.R.attr.state_pressed;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_selected":
                                    stateInt = isEnablePreffix * android.R.attr.state_selected;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_single":
                                    stateInt = isEnablePreffix * android.R.attr.state_single;
                                    arrayList2.add(stateInt);
                                    break;
                                case "state_window_focused":
                                    stateInt = isEnablePreffix * android.R.attr.state_window_focused;
                                    arrayList2.add(stateInt);
                                    break;
                                case "color":
                                    colorInt = getColor(Integer.parseInt(value.substring(1)));
                                    break;
                            }
                            type++;
                        }
                        int[] colors = new int[arrayList2.size()];
                        for (int i = 0; i < colors.length; i++) {
                            colors[i] = arrayList2.get(i);
                        }
                        arrayList.add(colors);
                        arrayList1.add(colorInt);
                    }
                    xmlResourceParser.next();
                    continue;
                }
                type = arrayList.size();
                int[][] arrayOfInt = new int[type][0];
                while (stateInt < type) {
                    arrayOfInt[stateInt] = arrayList.get(stateInt);
                    stateInt++;
                }
                int[] colors = new int[arrayList1.size()];
                for (int i = 0; i < colors.length; i++) {
                    colors[i] = arrayList1.get(i);
                }
                return new ColorStateList(arrayOfInt, colors);
            }
        } catch (Resources.NotFoundException | XmlPullParserException | IOException notFoundException) {
            Log.i("SkinResourceManagerImpl", "getSelectorColor ignored");
            return null;
        }
    }

    public StateListDrawable getSelectorDrawable(int xmlRes) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        try {
            XmlResourceParser xmlResourceParser = this.mDefaultResources.getXml(xmlRes);
            while (xmlResourceParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlResourceParser.getEventType() == XmlPullParser.START_TAG && "item".equals(xmlResourceParser.getName())) {
                    ArrayList<Integer> arrayList = new ArrayList();
                    Drawable drawable = null;
                    int stateInt = 0;
                    for (byte b = 0; b < xmlResourceParser.getAttributeCount(); b++) {
                        String name = xmlResourceParser.getAttributeName(b);
                        String value = xmlResourceParser.getAttributeValue(b);
                        int isEnablePreffix = "true".equals(value) ? 1 : -1;
                        switch (name) {
                            default:
                                stateInt = -1;
                                break;
                            case "state_accelerated":
                                stateInt = isEnablePreffix * android.R.attr.state_accelerated;
                                arrayList.add(stateInt);
                                break;
                            case "state_active":
                                stateInt = isEnablePreffix * android.R.attr.state_active;
                                arrayList.add(stateInt);
                                break;
                            case "state_activated":
                                stateInt = isEnablePreffix * android.R.attr.state_activated;
                                arrayList.add(stateInt);
                                break;
                            case "state_checkable":
                                stateInt = isEnablePreffix * android.R.attr.state_checkable;
                                arrayList.add(stateInt);
                                break;
                            case "state_checked":
                                stateInt = isEnablePreffix * android.R.attr.state_checked;
                                arrayList.add(stateInt);
                                break;
                            case "state_drag_can_accept":
                                stateInt = isEnablePreffix * android.R.attr.state_drag_can_accept;
                                arrayList.add(stateInt);
                                break;
                            case "state_drag_hovered":
                                stateInt = isEnablePreffix * android.R.attr.state_drag_hovered;
                                arrayList.add(stateInt);
                                break;
                            case "state_enabled":
                                stateInt = isEnablePreffix * android.R.attr.state_enabled;
                                arrayList.add(stateInt);
                                break;
                            case "state_first":
                                stateInt = isEnablePreffix * android.R.attr.state_first;
                                arrayList.add(stateInt);
                                break;
                            case "state_focused":
                                stateInt = isEnablePreffix * android.R.attr.state_focused;
                                arrayList.add(stateInt);
                                break;
                            case "state_hovered":
                                stateInt = isEnablePreffix * android.R.attr.state_hovered;
                                arrayList.add(stateInt);
                                break;
                            case "state_last":
                                stateInt = isEnablePreffix * android.R.attr.state_last;
                                arrayList.add(stateInt);
                                break;
                            case "state_middle":
                                stateInt = isEnablePreffix * android.R.attr.state_middle;
                                arrayList.add(stateInt);
                                break;
                            case "state_pressed":
                                stateInt = isEnablePreffix * android.R.attr.state_pressed;
                                arrayList.add(stateInt);
                                break;
                            case "state_selected":
                                stateInt = isEnablePreffix * android.R.attr.state_selected;
                                arrayList.add(stateInt);
                                break;
                            case "state_single":
                                stateInt = isEnablePreffix * android.R.attr.state_single;
                                arrayList.add(stateInt);
                                break;
                            case "state_window_focused":
                                stateInt = isEnablePreffix * android.R.attr.state_window_focused;
                                arrayList.add(stateInt);
                                break;
                            case "drawable":
                                drawable = getDrawable(Integer.parseInt(value.substring(1)));
                                break;
                        }
                    }
                    int[] states = new int[arrayList.size()];
                    for (int i = 0; i < states.length; i++) {
                        states[i] = arrayList.get(i);
                    }
                    stateListDrawable.addState(states, drawable);
                }
                xmlResourceParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateListDrawable;
    }

    private Drawable reDirectResId(Drawable paramDrawable, int paramInt) {
        Drawable drawable;
        if (paramDrawable instanceof StateListDrawable) {
            drawable = (Drawable) getSelectorDrawable(paramInt);
        } else if (paramDrawable instanceof LayerDrawable) {
            drawable = (Drawable) getLayerListDrawable((LayerDrawable) paramDrawable, paramInt);
        } else if (paramDrawable instanceof AnimationDrawable) {
            drawable = (Drawable) getAnimationDrawable((AnimationDrawable) paramDrawable, paramInt);
        } else {
            drawable = null;
        }
        if (drawable != null)
            paramDrawable = drawable;
        return paramDrawable;
    }

    @Override
    public String getPkgName() {
        return mSkinPluginPackageName;
    }

    @Override
    public Resources getPluginResource() {
        return mSkinPluginResources;
    }

    @Override
    public void setPluginResourcesAndPkgName(Resources resources, String pkgName) {
        mSkinPluginResources = resources;
        mSkinPluginPackageName = pkgName;
    }

    @Override
    public int getColor(int resId) throws Resources.NotFoundException {
        int originColor = mDefaultResources.getColor(resId);
        if (mSkinPluginResources == null) {
            return originColor;
        }

        String resName = mDefaultResources.getResourceEntryName(resId);

        int trueResId = mSkinPluginResources.getIdentifier(resName, SkinConfig.RES_TYPE_NAME_COLOR, mSkinPluginPackageName);

        if (trueResId == 0) {
            String stringBuilder = "trueResId: 0, resName: " + resName + " Not found, use origin color";
            Log.d("SkinResourceManagerImpl", stringBuilder);
            trueResId = originColor;
        } else {
            try {
                trueResId = mSkinPluginResources.getColor(trueResId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                String stringBuilder = "trueResId: 0, resName: " + resName + " Not found, use origin color";
                Log.d("SkinResourceManagerImpl", stringBuilder);
                trueResId = originColor;
            }
        }
        return trueResId;
    }

    @Override
    public ColorStateList getColorStateList(int resId) throws Resources.NotFoundException {
        boolean isExtendSkin = true;

        if (mSkinPluginResources == null) {
            isExtendSkin = false;
        }
        String resName = mDefaultResources.getResourceEntryName(resId);
        if (isExtendSkin) {
            int trueResId = mSkinPluginResources.getIdentifier(resName, SkinConfig.RES_TYPE_NAME_COLOR, mSkinPluginPackageName);
            ColorStateList trueColorList;
            if (trueResId == 0) { // 如果皮肤包没有复写该资源，但是需要判断是否是ColorStateList
                try {
                    ColorStateList colorStateList = getSelectorColor(resId);
                    if (colorStateList == null) {
                        Log.d(TAG, resName + " not colorState list, use default resource. ");
                        colorStateList = mDefaultResources.getColorStateList(resId);
                    }
                    return colorStateList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    if (SkinConfig.DEBUG) {
                        Log.d(TAG, "resName = " + resName + " NotFoundException : " + e.getMessage());
                    }
                }
            } else {
                try {
                    trueColorList = mSkinPluginResources.getColorStateList(trueResId);
                    if (SkinConfig.DEBUG) {
                        Log.d(TAG, "getColorStateList the trueColorList is = " + trueColorList);
                    }
                    return trueColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "resName = " + resName + " NotFoundException :" + e.getMessage());
                }
            }
        } else {
            try {
                return mDefaultResources.getColorStateList(resId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "resName = " + resName + " NotFoundException :" + e.getMessage());
            }
        }

        int[][] states = new int[1][1];
        return new ColorStateList(states, new int[]{mDefaultResources.getColor(resId)});
    }

    //TODO compatible with low version
    @Override
    public Drawable getDrawable(int resId) throws Resources.NotFoundException {
        Drawable originDrawable = mDefaultResources.getDrawable(resId);
        if (mSkinPluginResources == null) {
            return originDrawable;
        }
        String resName = mDefaultResources.getResourceEntryName(resId);
        int trueResId = mSkinPluginResources.getIdentifier(resName, SkinConfig.RES_TYPE_NAME_DRAWABLE, mSkinPluginPackageName);
        Drawable trueDrawable;
        try {
            if (trueResId == 0) {
                String stringBuilder1 = "trueResId: 0, redirect resName: " + resName;
                Log.d(TAG, stringBuilder1);
                trueDrawable = reDirectResId(originDrawable, resId);
            } else {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                    trueDrawable = mSkinPluginResources.getDrawable(trueResId);
                } else {
                    trueDrawable = mSkinPluginResources.getDrawable(trueResId, null);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }
        return trueDrawable;
    }

    @Override
    public Drawable getDrawableForMapmip(int resId) throws Resources.NotFoundException {
        Drawable originDrawable = mDefaultResources.getDrawable(resId);
        if (mSkinPluginResources == null) {
            return originDrawable;
        }
        String resName = mDefaultResources.getResourceEntryName(resId);

        int trueResId = mSkinPluginResources.getIdentifier(resName, SkinConfig.RES_TYPE_NAME_MIPMAP, mSkinPluginPackageName);

        Drawable trueDrawable;
        try {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                trueDrawable = mSkinPluginResources.getDrawable(trueResId);
            } else {
                trueDrawable = mSkinPluginResources.getDrawable(trueResId, null);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }
        return trueDrawable;
    }
}
