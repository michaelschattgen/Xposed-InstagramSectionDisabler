package reverb.com.instagramsectiondisabler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class InstagramSectionDisabler implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public XSharedPreferences prefs;

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

        if(isEnabled("photos")) {
            XposedHelpers.findAndHookMethod("com.instagram.android.fragment.bu", loadPackageParam.classLoader, "c", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    methodHookParam.setResult(null);
                    return null;
                }
            });
        }
    }

    public boolean isEnabled(String preference) {
        boolean enabled = prefs.getBoolean(preference, false);
        log(preference + " is " + enabled);
        return enabled;
    }

}
