package org.ionchain.wallet.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by binny on 2018/6/5.
 */

public class BitmapUtils {

    /**
     * @说明： 保存图片到本地
     */
    public static String rootDir = Environment.getExternalStorageDirectory() .getAbsolutePath()+ File.separator;

    public static void savePicture(Bitmap bitmap, String fileName,String imageName){
        String pathName = rootDir  + fileName + File.separator;

        File myDir = new File(pathName);//目录
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File image = new File(myDir,imageName);//文件

        Log.i("TAG", "savePicture: ");
        FileOutputStream out;
        if (image.exists()) {
            image.delete();
        }
        try
        {
            image.createNewFile();
            out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();


        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            Log.i("qqqq", "savePicture: ");
            e.printStackTrace();
        }

    }
    /**
     * 建立HTTP请求，并获取Bitmap对象。
     *
     * @param urlString
     *            图片的URL地址
     * @return 解析后的Bitmap对象
     */
    public static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
