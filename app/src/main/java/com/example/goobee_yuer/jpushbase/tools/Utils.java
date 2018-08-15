package com.example.goobee_yuer.jpushbase.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.goobee_yuer.jpushbase.jiguangpush.ExampleApplication;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.TimeZone;

public class Utils {
    private static int SCREEN_HEIGHT = 0;
    private static int SCREEN_WIDTH = 0;

    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_PUSH = "push";


    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_TYPE = "type";

    /**
     * 獲取屏幕的實際寬和高 根據不同版本不同處理
     * @param context
     */
    public static void getScreenWH(Activity context) {
        try {
            Display display = context.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                SCREEN_WIDTH = size.x;
                SCREEN_HEIGHT = size.y;
            } else if (android.os.Build.VERSION.SDK_INT < 17
                    && android.os.Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                SCREEN_WIDTH = (Integer) mGetRawW.invoke(display);
                SCREEN_HEIGHT = (Integer) mGetRawH.invoke(display);
            } else {
                SCREEN_WIDTH = metrics.widthPixels;
                SCREEN_HEIGHT = metrics.heightPixels;
            }
            Logger.d("Test", "" + SCREEN_HEIGHT + " " + SCREEN_WIDTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 得到json文件中的内容
     * @param context
     * @param fileName
     * @return
     */
    public static String getJsonString(Context context, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName),"utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串转换为 对象
     * @param json
     * @param type
     * @return
     */
    public  static <T> T JsonToObject(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }


    //得到本地json文本内容
//        String fileName = "test.json";
//        String json = Utils.getJsonString(this, fileName);
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            int time =  jsonObject.optInt("time");
//            Log.d("Json", "onCreate: ---" + time);
//            JSONArray jsonArray = new JSONArray(jsonObject.optJSONArray("exchangeRate"));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONArray jsArr = jsonArray.getJSONArray(i);
//                Log.d("Json", "onCreate: --: " + jsArr.getString(0));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    public static String getPackageVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName  = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName  = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName ;
    }

    public static void deleteOldApk(){
        String apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/myApk.apk";
        java.io.File apkFile = new java.io.File(apkPath);
        if (apkFile.exists()){//存在则删掉
            apkFile.delete();
            Logger.d("Test", "Delete Apk");
        }
    }

    /**
     * 版本号比较
     *
     * @param newVersion
     * @param oldVersion
     * @return
     */
    public static int compareVersion(String newVersion, String oldVersion) {
        if (newVersion.equals(oldVersion)) {
            return 0;
        }
        String[] version1Array = newVersion.split("\\.");
        String[] version2Array = oldVersion.split("\\.");
        Log.d("HomePageActivity", "version1Array=="+version1Array.length);
        Log.d("HomePageActivity", "version2Array=="+version2Array.length);
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        Log.d("HomePageActivity", "verTag2=2222="+version1Array[index]);
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    /**
     * 由二进制字符串和索引值获取weeks
     * @param index 星期几 0代表星期天，1-6代表周一到周六
     * @param weekStr  showTimeTableList.get(i).getWeek()
     * @return
     */
    public static int getWeeks(int index,String weekStr){
//        byte[] b = hexString2Bytes(weekStr.substring(2)); //解析 0x22
        StringBuffer sb = null;
        try {
            String weekData = Integer.toHexString(Integer.parseInt(weekStr));
            if (weekData.length() < 2){
                weekData = "0" + weekData;
            }
            Logger.d("Test16",weekData);
            byte[] b = hexString2Bytes(weekData); //解析 22
            String bin = Integer.toBinaryString(b[0]);
            sb = new StringBuffer(bin);
            while (sb.length()<8){
                sb.insert(0,"0");
            }
            Logger.d("Test16",sb.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return Integer.parseInt(String.valueOf(sb.charAt(index+1)));
    }
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * 获取当天星期几
     * @return
     */
    public static int getTodayWeek(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay;
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            return 0;
        }else if("2".equals(mWay)){
            return 1;
        }else if("3".equals(mWay)){
            return 2;
        }else if("4".equals(mWay)){
            return 3;
        }else if("5".equals(mWay)){
            return 4;
        }else if("6".equals(mWay)){
            return 5;
        }else if("7".equals(mWay)){
            return 6;
        }
        return 0;
    }



    /**
     * 判断当前软键盘是否打开
     *
     * @param activity
     * @return
     */
    public static boolean isSoftInputShow(Activity activity) {
        // 虚拟键盘隐藏 判断view是否为空
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            // 隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
//       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);

            return inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null;
        }
        return false;
    }

    /**
     * 传入文件的路径，转为provider共享
     * @param pathname
     * @return
     */
    public static String getProviderFilePath(String pathname){
        //使用provider
        java.io.File sharedFile = new java.io.File(pathname);
        if (sharedFile.exists()){
            Uri sharedFileUri = FileProvider.getUriForFile(ExampleApplication.getContext(), "com.skylander.sky20180410_dbsad.fileprovider", sharedFile);
            return sharedFileUri.toString();
        }
        return null;
    }



    public static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if(rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    public static Process createSuProcess(String cmd) throws IOException {

        DataOutputStream os = null;
        Process process = createSuProcess();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }


}
