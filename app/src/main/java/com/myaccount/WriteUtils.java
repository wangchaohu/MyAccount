package com.myaccount;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by wangchaohu on 2017/2/17.
 */

public class WriteUtils {
    JsonUtils jsonUtils = null;

    boolean isFirst = false;
    private DoFile doFile;

    public WriteUtils(DoFile doFile) {
        this.doFile = doFile;
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(final String strcontent, final Boolean isSameDay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory() + "/myAccount";
                String fileName = "/account.json";
                //生成文件夹之后，再生成文件，不然会出错
                makeFilePath(filePath, fileName);
                String strFilePath = filePath + fileName;
                // 每次写入时，都换行写

                try {
                    File file = new File(strFilePath);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        isFirst = true;
                    }
                    RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                    long fileLength = file.length();
                    if (!isFirst && fileLength > 0) {
                        if (isSameDay) {
                            fileLength = fileLength - 4;
                        } else {
                            fileLength = fileLength - 2;
                        }

                    }

                    if (fileLength > 0) {
                        raf.seek(fileLength);
                    }
                    raf.write(strcontent.getBytes());
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                doFile.writeSuccess();
            }
        }).start();


    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取文件
     */
    public void readData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                try {
       /* 创建File对象，确定需要读取文件的信息 */
                    File urlFile = new File(Environment.getExternalStorageDirectory() + "/myAccount/account.json");
                    if (urlFile.exists()) {
                        InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
                        BufferedReader br = new BufferedReader(isr);

                        String mimeTypeLine = null;
                        while ((mimeTypeLine = br.readLine()) != null) {
                            str = str + mimeTypeLine;
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                doFile.readSuccess(str);
            }
        }).start();
    }
}
