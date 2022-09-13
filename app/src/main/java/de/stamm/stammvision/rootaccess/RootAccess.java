package de.stamm.stammvision.rootaccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;
import de.stamm.stammvision.data.MainModel;

public class RootAccess {

	public static void rebootSU() {
		if (MainModel.ROOTED) {
		    Runtime runtime = Runtime.getRuntime();
		    Process proc = null;
		    OutputStreamWriter osw = null;
	
		    String command = "/system/bin/reboot";
	
		    try { // Run Script
	
		        proc = runtime.exec("su");
		        osw = new OutputStreamWriter(proc.getOutputStream());
		                            osw.write(command);
		                osw.flush();
		        osw.close();
	
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    } finally {
		        if (osw != null) {
		            try {
		                osw.close();
		            } catch (IOException e) {
		                e.printStackTrace();                    
		            }
		        }
		    }
		    try {
		        if (proc != null)
		            proc.waitFor();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		    if (proc.exitValue() != 0) {
		    }
		}
	}
	
	/*
	public static void reboot() {
		if (MainModel.ROOTED) {
			Process process;
			try {
				process = Runtime.getRuntime().exec("su");
		        DataOutputStream os = new DataOutputStream(process.getOutputStream());
		        os.writeBytes("reboot \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/
	  
	public void installNewApk(String targetFile) {
		if (MainModel.ROOTED) {
	        try {
				Process install = Runtime.getRuntime().exec(new String[] {"su", "-c", "pm install -r "+targetFile});
				install.waitFor();
	            if (install.exitValue() == 0) {
	            } else {
	            }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su" };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
