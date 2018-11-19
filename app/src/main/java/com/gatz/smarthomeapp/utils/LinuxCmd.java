package com.gatz.smarthomeapp.utils;

import android.util.Log;

import java.io.DataOutputStream;

/**
 * Created by zhouh on 2017/3/22.
 */
public class LinuxCmd {

    public static boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.e("*** runRootCommand ***", "Unexpected error - " + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                    process.destroy();
                }
            } catch (Exception e) {
                // nothing
                e.printStackTrace();
            }
        }
        return true;
    }
}

