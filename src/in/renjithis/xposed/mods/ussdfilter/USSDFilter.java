package in.renjithis.xposed.mods.ussdfilter;

import java.lang.reflect.Method;
import android.widget.Toast;

// Imports for XposedBridge
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

//Imports for PhoneUtils class
import android.content.Context;

public class USSDFilter implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
//        XposedBridge.log("Loaded app: " + lpparam.packageName);
        
        if(!lpparam.packageName.equals("com.android.phone"))
        	return;
        
        XposedBridge.log("Found phone app");
                
    	findAndHookMethod("com.android.phone.PhoneUtils", 
    			lpparam.classLoader, 
    			"displayMMIComplete", 
    			"com.android.internal.telephony.Phone",
    			"android.content.Context",
    			"com.android.internal.telephony.MmiCode",
    			"android.os.Message",
    			"android.app.AlertDialog",
    			new XC_MethodHook() {
		    		@Override
		    		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		    			// this will be called before the clock was updated by the original method
		    			XposedBridge.log("beforeHookedMethod displayMMIComplete");
		    			
		    			Context context=(Context) param.args[1];
		    			Object mmiCode=param.args[2];
		    			Method getMessageMethod=mmiCode.getClass().getDeclaredMethod("getMessage");
		    			
		    			// get this from user. need to change hardcoding
		    			String filterString="free GPRS";
		    			
		    			String text = (String) getMessageMethod.invoke(mmiCode);
		    			XposedBridge.log("text="+text);

		    			if(text.contains(filterString))
		    			{
		    				// need to add more functionality, like logging, etc
		    				
		    				XposedBridge.log("Text contains filterString");
			    			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			    			// This prevents the actual hooked method from being called
			    			param.setResult(mmiCode);
		    			}
		    		}
			});
    }
    
}
