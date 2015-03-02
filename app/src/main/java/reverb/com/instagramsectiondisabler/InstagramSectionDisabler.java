package reverb.com.instagramsectiondisabler;

import android.view.View;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class InstagramSectionDisabler implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public XSharedPreferences prefs;
    private String photosString = "";
    private String peopleString = "";
    private Boolean showDisabledTitle;

    private static void log(String log) {
        XposedBridge.log("InstagramSectionDisabler: " + log);
    }


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences(InstagramSectionDisabler.class.getPackage()
                .getName());
        prefs.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.instagram.android"))
            return;

        prefs.reload();

        if(isEnabled("photos") && isEnabled("people"))
        {
            showDisabledTitle = true;
        }

        if(isEnabled("photos")) {
            photosString = "Photos";
            XposedHelpers.findAndHookMethod("com.instagram.android.fragment.bu", loadPackageParam.classLoader, "c", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    methodHookParam.setResult(null);
                    return null;
                }
            });
        }

        if(isEnabled("people")) {
            peopleString = "People";
            XposedHelpers.findAndHookMethod("com.instagram.android.fragment.ge", loadPackageParam.classLoader, "b", List.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return null;
                }
            });
        }

        XposedHelpers.findAndHookMethod("com.instagram.ui.widget.scrollabletabbar.ScrollableTabBar", loadPackageParam.classLoader, "setSwitcherButtons", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //Field ok = XposedHelpers.findField(param.getClass(), "localArrayList1");
                List<String> paramList = (List<String>) param.args[0];
                log(paramList.get(paramList.size() - 1).toString());
                int placeOfPeopleString = -1;
                int placeOfPhotosString = -1;
                for (int i = 0; i < paramList.size(); i++) {
                    if (paramList.get(i).equals(peopleString)) {
                        placeOfPeopleString = i;
                    }

                    if(paramList.get(i).equals(photosString)) {
                        placeOfPhotosString = i;
                    }
                }

                if (placeOfPeopleString != -1 && placeOfPhotosString != -1) {
                    paramList.remove(placeOfPhotosString);
                    paramList.remove(placeOfPeopleString-1);
                }else if(placeOfPeopleString != -1) {
                    paramList.remove(placeOfPeopleString-1);
                }else if(placeOfPhotosString != -1)
                {
                    paramList.set(placeOfPhotosString, "Disabled");
                }

                if(showDisabledTitle && paramList.size() == 0)
                {
                    paramList.add("This is disabled.");
                }
            }
        });


    }

    public boolean isEnabled(String preference) {
        boolean enabled = prefs.getBoolean(preference, false);
        log(preference + " is " + enabled);
        return enabled;
    }

}
