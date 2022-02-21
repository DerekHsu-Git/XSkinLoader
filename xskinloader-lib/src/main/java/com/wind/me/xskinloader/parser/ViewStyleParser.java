package com.wind.me.xskinloader.parser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConfig;
import com.wind.me.xskinloader.entity.SkinConstant;
import com.wind.me.xskinloader.skinInterface.ISkinStyleParser;
import com.wind.me.xskinloader.util.ReflectUtils;

import java.util.Map;

public class ViewStyleParser implements ISkinStyleParser {
    private static int sViewBackgroundStyleIndex;

    private static int[] sViewStyleList;

    @Override
    public void parseXmlStyle(View paramView, AttributeSet paramAttributeSet, Map<String, SkinAttr> paramMap, String[] paramArrayOfString) {
        Context context = paramView.getContext();
        int[] arrayOfInt = getTextViewStyleableList();
        int i = getTextViewBackgroundStyleableIndex();
        byte b = 0;
        TypedArray typedArray = context.obtainStyledAttributes(paramAttributeSet, arrayOfInt, 0, 0);
        int j = typedArray.getIndexCount();
        while (b < j) {
            int k = typedArray.getIndex(b);
            if (k == i && SkinConfig.isCurrentAttrSpecified(SkinConstant.BACKGROUND, paramArrayOfString)) {
                SkinAttr skinAttr = SkinAttributeParser.parseSkinAttr(context, SkinConstant.BACKGROUND, typedArray.getResourceId(k, -1));
                if (skinAttr != null)
                    paramMap.put(skinAttr.attrName, skinAttr);
            }
            b++;
        }
        typedArray.recycle();
    }

    private static int getTextViewBackgroundStyleableIndex() {
        if (sViewBackgroundStyleIndex == 0) {
            Object object = ReflectUtils.getField("com.android.internal.R$styleable", "View_background");
            if (object != null)
                sViewBackgroundStyleIndex = (int) object;
        }
        return sViewBackgroundStyleIndex;
    }

    private static int[] getTextViewStyleableList() {
        if (sViewStyleList == null || sViewStyleList.length == 0)
            sViewStyleList = (int[]) ReflectUtils.getField("com.android.internal.R$styleable", "View");
        return sViewStyleList;
    }
}