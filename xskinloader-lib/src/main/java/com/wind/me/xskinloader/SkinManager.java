package com.wind.me.xskinloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.wind.me.xskinloader.entity.SkinAttr;
import com.wind.me.xskinloader.entity.SkinConstant;
import com.wind.me.xskinloader.impl.SkinResourceManagerImpl;
import com.wind.me.xskinloader.parser.SkinAttributeParser;
import com.wind.me.xskinloader.pluginLoader.PluginLoadUtils;
import com.wind.me.xskinloader.skinInterface.ISkinResDeployer;
import com.wind.me.xskinloader.skinInterface.ISkinResourceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

public class SkinManager {

    private static final String TAG = SkinManager.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static SkinManager sInstance;
    private Context mContext;
    private String mPluginSkinPath;
    private ISkinResourceManager mSkinResourceManager;
    private boolean hasInited = false;

    //使用这个map保存所有需要换肤的view和其对应的换肤属性及资源
    //使用WeakHashMap两个作用，1.避免内存泄漏，2.避免重复的view被添加
    //使用HashMap存SkinAttr，为了避免同一个属性值存了两次
    private WeakHashMap<View, HashMap<String, SkinAttr>> mSkinAttrMap = new WeakHashMap<>();
    private WeakHashMap<Drawable, Integer> mDrawableResIdMap = new WeakHashMap<>();

    @MainThread
    public static SkinManager get() {
        if (sInstance == null) {
            sInstance = new SkinManager();
        }
        return sInstance;
    }

    // 在provider中自动初始化，不用手动调用
    @MainThread
    public void init(Context context) {
        if (hasInited) {
            Log.w(TAG, " SkinManager has been inited, don't init again !!");
            return;
        }
        hasInited = true;
        mContext = context.getApplicationContext();
        mSkinResourceManager = new SkinResourceManagerImpl(mContext, null, null);
    }

    public void restoreToDefaultSkin() {
        mSkinResourceManager.setPluginResourcesAndPkgName(null, null);
        notifySkinChanged();
    }

    /**
     * 加载已经用户默认设置的皮肤资源
     */
    public boolean loadSkin(String skinApkPath) {
        if (!loadNewSkin(skinApkPath)) {
            Log.w(TAG, " Try to load skin apk, but file is not exist, file path -->  " + skinApkPath +
                    " So, restore to default skin.");
            restoreToDefaultSkin();
            return false;
        }
        return true;
    }

    public void testGetColorList() {
//        Log.w(TAG,mContext.getResources().getColor(R.color.activityBackgroundColor)+ " 554827690");
//        Log.w(TAG,mContext.getResources().getColor(R.color.statusBarColor)+ " 1157566975");
//        mSkinResourceManager.getSelectorColor(R.color.selector_text_color);
//        StateListDrawable listDrawable = mSkinResourceManager.getSelectorDrawable(R.drawable.selector_car);
//        Log.w(TAG,+listDrawable.getOpacity() + " stateList");
    }

    /**
     * 加载新皮肤
     *
     * @param skinApkPath 新皮肤路径
     * @return true 加载新皮肤成功 false 加载失败
     */
    private boolean loadNewSkin(String skinApkPath) {
        return doNewSkinLoad(skinApkPath);
    }

    public void setTextViewColor(View view, int resId) {
        setSkinViewResource(view, SkinConstant.TEXT_COLOR, resId);
    }

    public void setHintTextColor(View view, int resId) {
        setSkinViewResource(view, SkinConstant.TEXT_COLOR_HINT, resId);
    }

    public void setViewBackground(View view, int resId) {
        if (resId == 0) {
            HashMap hashMap = this.mSkinAttrMap.get(view);
            if (hashMap != null) {
                SkinAttr skinAttr = (SkinAttr) hashMap.get("background");
                if (skinAttr != null) {
                    skinAttr.attrValueRefId = resId;
                    saveSkinView(view, skinAttr);
                }
            }
            view.setBackgroundResource(0);
        } else {
            setSkinViewResource(view, SkinConstant.BACKGROUND, resId);
        }
    }

    public void setViewBackground(View view, Drawable paramDrawable) {
        if (paramDrawable == null) {
            setViewBackground(view, 0);
        } else if (!setSkinViewDrawable(view, SkinConstant.BACKGROUND, paramDrawable)) {
            view.setBackground(paramDrawable);
        }
    }

    public void setImageDrawable(View view, int resId) {
        setSkinViewResource(view, SkinConstant.IMAGE_SRC, resId);
    }

    public void setCompoundButtonDrawable(View paramView, int paramInt) {
        setSkinViewResource(paramView, "button", paramInt);
    }

    boolean setSkinViewDrawable(View paramView, String paramString, Drawable paramDrawable) {
        if (paramView != null && !TextUtils.isEmpty(paramString) && paramDrawable != null) {
            Integer integer = this.mDrawableResIdMap.get(paramDrawable);
            if (integer != null) {
                setSkinViewResource(paramView, paramString, integer.intValue());
                return true;
            }
        }
        return false;
    }

    public void setImageDrawable(View paramView, Drawable paramDrawable) {
        if (!setSkinViewDrawable(paramView, "src", paramDrawable) && paramView instanceof ImageView)
            ((ImageView) paramView).setImageDrawable(paramDrawable);
    }

    public void setProgressBarProgressDrawable(View paramView, int paramInt) {
        setSkinViewResource(paramView, "progressDrawable", paramInt);
    }

    public void setProgressBarProgressDrawable(View paramView, Drawable paramDrawable) {
        if (!setSkinViewDrawable(paramView, "progressDrawable", paramDrawable) && paramView instanceof SeekBar)
            ((SeekBar) paramView).setProgressDrawable(paramDrawable);
    }

    public void setSeekBarThumbDrawable(View paramView, int paramInt) {
        setSkinViewResource(paramView, "thumb", paramInt);
    }

    public void setSeekBarThumbDrawable(View paramView, Drawable paramDrawable) {
        if (!setSkinViewDrawable(paramView, "thumb", paramDrawable) && paramView instanceof SeekBar)
            ((SeekBar) paramView).setThumb(paramDrawable);
    }

    public void setListViewSelector(View view, int resId) {
        setSkinViewResource(view, SkinConstant.LIST_SELECTOR, resId);
    }

    public void setListViewDivider(View view, int resId) {
        setSkinViewResource(view, SkinConstant.DIVIDER, resId);
    }

    public void setWindowStatusBarColor(Window window, @ColorRes int resId) {
        View decorView = window.getDecorView();
        setSkinViewResource(decorView, SkinConstant.ACTIVITY_STATUS_BAR_COLOR, resId);
    }

    public void setProgressBarIndeterminateDrawable(View view, int resId) {
        setSkinViewResource(view, SkinConstant.PROGRESSBAR_INDETERMINATE_DRAWABLE, resId);
    }

    public void setSwitchThumbDrawable(View paramView, Drawable paramDrawable) {
        if (!setSkinViewDrawable(paramView, "thumb", paramDrawable) && paramView instanceof Switch)
            ((Switch) paramView).setThumbDrawable(paramDrawable);
    }

    public void setSwitchTrackDrawable(View paramView, Drawable paramDrawable) {
        if (!setSkinViewDrawable(paramView, "track", paramDrawable) && paramView instanceof Switch)
            ((Switch) paramView).setTrackDrawable(paramDrawable);
    }

    public void setTextViewCompoundDrawables(View paramView, Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4) {
        if (!(paramView instanceof TextView))
            return;
        ((TextView) paramView).setCompoundDrawables(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
        if (paramDrawable1 == null) {
            removeObservableViewAttr(paramView, "drawableStart");
            removeObservableViewAttr(paramView, "drawableLeft");
        } else {
            setSkinViewDrawable(paramView, "drawableLeft", paramDrawable1);
        }
        if (paramDrawable2 == null) {
            removeObservableViewAttr(paramView, "drawableTop");
        } else {
            setSkinViewDrawable(paramView, "drawableTop", paramDrawable2);
        }
        if (paramDrawable3 == null) {
            removeObservableViewAttr(paramView, "drawableRight");
            removeObservableViewAttr(paramView, "drawableEnd");
        } else {
            setSkinViewDrawable(paramView, "drawableRight", paramDrawable3);
        }
        if (paramDrawable4 == null) {
            removeObservableViewAttr(paramView, "drawableBottom");
        } else {
            setSkinViewDrawable(paramView, "drawableBottom", paramDrawable4);
        }
    }


    /**
     * 设置可以换肤的view的属性
     *
     * @param view     设置的view
     * @param attrName 这个取值只能是 {@link SkinConstant#BACKGROUND} {@link SkinConstant#DIVIDER} {@link SkinConstant#TEXT_COLOR}
     *                 {@link SkinConstant#LIST_SELECTOR} {@link SkinConstant#IMAGE_SRC} 等等
     * @param resId    资源id
     */
    @MainThread
    public void setSkinViewResource(View view, String attrName, int resId) {
        if (TextUtils.isEmpty(attrName)) {
            return;
        }

        SkinAttr attr = SkinAttributeParser.parseSkinAttr(view.getContext(), attrName, resId);
        if (attr != null) {
            doSkinAttrsDeploying(view, attr);
            saveSkinView(view, attr);
        }
    }

    private boolean doNewSkinLoad(String skinApkPath) {
        if (TextUtils.isEmpty(skinApkPath)) {
            return false;
        }
        PackageInfo packageInfo = null;
        Resources pluginResources = null;
        if (isPkgName(skinApkPath)) {
            try {
                packageInfo = this.mContext.getPackageManager().getPackageInfo(skinApkPath,
                        PackageManager.GET_META_DATA | PackageManager.GET_SERVICES | PackageManager.GET_ACTIVITIES);
                pluginResources = this.mContext.createPackageContext(packageInfo.packageName, Context.CONTEXT_IGNORE_SECURITY).getResources();
            } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
                Log.e(TAG, "packageName = " + skinApkPath +
                        " NameNotFoundException : " + nameNotFoundException.getMessage());
                return false;
            }
        } else {
            File file = new File(skinApkPath);
            if (!file.exists()) {
                return false;
            }
            packageInfo = PluginLoadUtils.getInstance(mContext).getPackageInfo(skinApkPath);
            pluginResources = PluginLoadUtils.getInstance(mContext).getPluginResources(skinApkPath);
        }
        if (packageInfo == null || pluginResources == null) {
            return false;
        }
        String skinPackageName = packageInfo.packageName;
        if (TextUtils.isEmpty(skinPackageName)) {
            return false;
        }
        mSkinResourceManager.setPluginResourcesAndPkgName(pluginResources, skinPackageName);
        mPluginSkinPath = skinApkPath;
        notifySkinChanged();
        return true;
    }

    private boolean isPkgName(String paramString) {
        boolean bool;
        if (paramString != null) {
            bool = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)+([.][a-zA-Z_][a-zA-Z0-9_]*)+$").matcher(paramString).matches();
        } else {
            bool = false;
        }
        return bool;
    }


    //将View保存到被监听的view列表中,使得在换肤时能够及时被更新
    void saveSkinView(View view, HashMap<String, SkinAttr> viewAttrs) {
        if (view == null || viewAttrs == null || viewAttrs.size() == 0) {
            return;
        }
        HashMap<String, SkinAttr> originalSkinAttr = mSkinAttrMap.get(view);
        if (originalSkinAttr != null && originalSkinAttr.size() > 0) {
            originalSkinAttr.putAll(viewAttrs);
            mSkinAttrMap.put(view, originalSkinAttr);
        } else {
            mSkinAttrMap.put(view, viewAttrs);
        }
    }

    private void saveSkinView(View view, SkinAttr viewAttr) {
        if (view == null || viewAttr == null) {
            return;
        }
        HashMap<String, SkinAttr> viewAttrs = new HashMap<>();
        viewAttrs.put(viewAttr.attrName, viewAttr);
        saveSkinView(view, viewAttrs);
    }


    public void removeObservableView(View view) {
        mSkinAttrMap.remove(view);
        mDrawableResIdMap.remove(view);
    }

    public void removeObservableViewAttr(View paramView, String paramString) {
        HashMap hashMap = this.mSkinAttrMap.get(paramView);
        if (hashMap != null)
            hashMap.remove(paramString);
    }

    public void clear() {
        mSkinAttrMap.clear();
    }

    //更换皮肤时，通知view更换资源
    private void notifySkinChanged() {
        View view;
        HashMap<String, SkinAttr> viewAttrs;
        Iterator iter = mSkinAttrMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            view = (View) entry.getKey();
            viewAttrs = (HashMap<String, SkinAttr>) entry.getValue();
            if (view != null) {
                deployViewSkinAttrs(view, viewAttrs);
            }
        }
    }

    void deployViewSkinAttrs(@Nullable View view, @Nullable HashMap<String, SkinAttr> viewAttrs) {
        if (view == null || viewAttrs == null || viewAttrs.size() == 0) {
            return;
        }
        Iterator iter = viewAttrs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            SkinAttr attr = (SkinAttr) entry.getValue();
            doSkinAttrsDeploying(view, attr);
        }
    }

    public int getColor(int paramInt) {
        try {
            return this.mSkinResourceManager.getColor(paramInt);
        } catch (Resources.NotFoundException notFoundException) {
            return -1;
        }
    }

    public Drawable getDrawable(int paramInt) {
        try {
            Drawable drawable = this.mSkinResourceManager.getDrawable(paramInt);
            this.mDrawableResIdMap.put(drawable, Integer.valueOf(paramInt));
            return drawable;
        } catch (Resources.NotFoundException notFoundException) {
            return null;
        }
    }

    //将新皮肤的属性部署到view上
    private void doSkinAttrsDeploying(@Nullable View view, @Nullable SkinAttr skinAttr) {
        ISkinResDeployer deployer = SkinResDeployerFactory.of(skinAttr);
        if (deployer != null) {
            deployer.deploy(view, skinAttr, mSkinResourceManager);
        }
    }

    public String getCurrentSkinPackageName() {
        return mSkinResourceManager.getPkgName();
    }

    public Resources getPluginResources() {
        return mSkinResourceManager.getPluginResource();
    }

    public boolean isUsingDefaultSkin() {
        return getPluginResources() == null;
    }

    public String getCurrentSkinPath() {
        return mPluginSkinPath;
    }

    public int getSkinViewMapSize() {
        return mSkinAttrMap.size();
    }

}