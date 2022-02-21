package com.wind.me.xskinloader;

import static com.wind.me.xskinloader.entity.SkinConstant.ACTIVITY_STATUS_BAR_COLOR;
import static com.wind.me.xskinloader.entity.SkinConstant.BACKGROUND;
import static com.wind.me.xskinloader.entity.SkinConstant.BUTTON;
import static com.wind.me.xskinloader.entity.SkinConstant.DIVIDER;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_BOTTOM;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_END;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_LEFT;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_RIGHT;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_START;
import static com.wind.me.xskinloader.entity.SkinConstant.DRAWABLE_TOP;
import static com.wind.me.xskinloader.entity.SkinConstant.IMAGE_SRC;
import static com.wind.me.xskinloader.entity.SkinConstant.LIST_SELECTOR;
import static com.wind.me.xskinloader.entity.SkinConstant.PROGRESSBAR_INDETERMINATE_DRAWABLE;
import static com.wind.me.xskinloader.entity.SkinConstant.PROGRESS_DRAWABLE;
import static com.wind.me.xskinloader.entity.SkinConstant.SCROLLBAR_THUMB_HORIZONTAL;
import static com.wind.me.xskinloader.entity.SkinConstant.SCROLLBAR_THUMB_VERTICAL;
import static com.wind.me.xskinloader.entity.SkinConstant.SCROLLBAR_TRACK_HORIZONTAL;
import static com.wind.me.xskinloader.entity.SkinConstant.SCROLLBAR_TRACK_VERTICAL;
import static com.wind.me.xskinloader.entity.SkinConstant.SRC_COMPAT;
import static com.wind.me.xskinloader.entity.SkinConstant.TEXT_COLOR;
import static com.wind.me.xskinloader.entity.SkinConstant.TEXT_COLOR_HINT;
import static com.wind.me.xskinloader.entity.SkinConstant.TEXT_CURSOR_DRAWABLE;
import static com.wind.me.xskinloader.entity.SkinConstant.THUMB;
import static com.wind.me.xskinloader.entity.SkinConstant.TRACK;

import android.text.TextUtils;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.skinDeployer.ActivityStatusBarColorResDeployer;
import com.wind.me.xskinloader.skinDeployer.BackgroundResDeployer;
import com.wind.me.xskinloader.skinDeployer.CompoundButtonDrawableDeployer;
import com.wind.me.xskinloader.skinDeployer.EditTextCursorDrawableResDeployer;
import com.wind.me.xskinloader.skinDeployer.ImageDrawableResDeployer;
import com.wind.me.xskinloader.skinDeployer.ListViewDividerResDeployer;
import com.wind.me.xskinloader.skinDeployer.ListViewSelectorResDeployer;
import com.wind.me.xskinloader.skinDeployer.ProgressBarIndeterminateDrawableDeployer;
import com.wind.me.xskinloader.skinDeployer.ProgressDrawableResDeployer;
import com.wind.me.xskinloader.skinDeployer.SwitchTrackDrawableResDeployer;
import com.wind.me.xskinloader.skinDeployer.TextColorHintResDeployer;
import com.wind.me.xskinloader.skinDeployer.TextColorResDeployer;
import com.wind.me.xskinloader.skinDeployer.TextViewCompoundDrawablesResDeployer;
import com.wind.me.xskinloader.skinDeployer.ThumbDrawableResDeployer;
import com.wind.me.xskinloader.skinDeployer.ViewScrollBarDrawableResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Windy on 2018/1/10.
 */

public class SkinResDeployerFactory {


    //存放支持的换肤属性和对应的处理器
    private static Map<String, ISkinResDeployer> sSupportedSkinDeployerMap = new HashMap<>();
    private static TextViewCompoundDrawablesResDeployer sTvCompoundDrawableDeployer = new TextViewCompoundDrawablesResDeployer();
    private static ViewScrollBarDrawableResDeployer sViewScrollBarDrawableDeployer = new ViewScrollBarDrawableResDeployer();

    //静态注册支持的属性和处理器
    static {
        registerDeployer(BACKGROUND, new BackgroundResDeployer());
        registerDeployer(IMAGE_SRC, new ImageDrawableResDeployer());
        registerDeployer(TEXT_COLOR, new TextColorResDeployer());
        registerDeployer(TEXT_COLOR_HINT, new TextColorHintResDeployer());
        registerDeployer(LIST_SELECTOR, new ListViewSelectorResDeployer());
        registerDeployer(DIVIDER, new ListViewDividerResDeployer());
        registerDeployer(ACTIVITY_STATUS_BAR_COLOR, new ActivityStatusBarColorResDeployer());
        registerDeployer(PROGRESSBAR_INDETERMINATE_DRAWABLE, new ProgressBarIndeterminateDrawableDeployer());
        registerDeployer(SRC_COMPAT, new ImageDrawableResDeployer());
        registerDeployer(THUMB, new ThumbDrawableResDeployer());
        registerDeployer(TRACK, new SwitchTrackDrawableResDeployer());
        registerDeployer(PROGRESS_DRAWABLE, new ProgressDrawableResDeployer());
        registerDeployer(BUTTON, new CompoundButtonDrawableDeployer());
        registerDeployer(TEXT_CURSOR_DRAWABLE, new EditTextCursorDrawableResDeployer());
        registerDeployer(DRAWABLE_START, sTvCompoundDrawableDeployer);
        registerDeployer(DRAWABLE_END, sTvCompoundDrawableDeployer);
        registerDeployer(DRAWABLE_TOP, sTvCompoundDrawableDeployer);
        registerDeployer(DRAWABLE_BOTTOM, sTvCompoundDrawableDeployer);
        registerDeployer(DRAWABLE_LEFT, sTvCompoundDrawableDeployer);
        registerDeployer(DRAWABLE_RIGHT, sTvCompoundDrawableDeployer);
        registerDeployer(SCROLLBAR_THUMB_HORIZONTAL, sViewScrollBarDrawableDeployer);
        registerDeployer(SCROLLBAR_THUMB_VERTICAL, sViewScrollBarDrawableDeployer);
        registerDeployer(SCROLLBAR_TRACK_HORIZONTAL, sViewScrollBarDrawableDeployer);
        registerDeployer(SCROLLBAR_TRACK_VERTICAL, sViewScrollBarDrawableDeployer);
    }

    public static void registerDeployer(String attrName, ISkinResDeployer skinResDeployer) {
        if (TextUtils.isEmpty(attrName) || null == skinResDeployer) {
            return;
        }
        if (sSupportedSkinDeployerMap.containsKey(attrName)) {
            throw new IllegalArgumentException("The attrName has been registed, please rename it");
        }
        sSupportedSkinDeployerMap.put(attrName, skinResDeployer);
    }

    public static ISkinResDeployer of(SkinAttr attr) {
        if (attr == null) {
            return null;
        }
        return of(attr.attrName);
    }

    public static ISkinResDeployer of(String attrName) {
        if (TextUtils.isEmpty(attrName)) {
            return null;
        }
        return sSupportedSkinDeployerMap.get(attrName);
    }

    public static boolean isSupportedAttr(String attrName) {
        return of(attrName) != null;
    }

    public static boolean isSupportedAttr(SkinAttr attr) {
        return of(attr) != null;
    }

}
