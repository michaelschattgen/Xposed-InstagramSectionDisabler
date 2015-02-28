package reverb.com.instagramsectiondisabler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class InstagramSectionDisabler implements IXposedHookLoadPackage {

    private static void log(String log) {
        XposedBridge.log("InstagramSectionDisabler: " + log);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.instagram.android"))
            return;

        XposedHelpers.findAndHookMethod("com.instagram.android.fragment.bu", loadPackageParam.classLoader, "c", new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                methodHookParam.setResult(null);
                return null;
            }
        });


    }
}
