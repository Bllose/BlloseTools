package com.bllose.io;

import java.io.*;

/**
 * <pre>
 *
 * </pre>
 *
 * @Author Bllose
 * @Date 2019/5/26 4:15
 */
public class FileHandler {

    /**
     * 完整地加载一个文件
     * @Author Bllose
     * @Date 2019/5/26 4:16
     */
    public static String loadFile(String abstractName, String encoding){
        File targetFile = new File(abstractName);
        if(!targetFile.exists() || !targetFile.isFile()){
            System.err.println("AbstractName should be a FILE!");
            return "";
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(targetFile), encoding));

            String content = "";
            String line = null;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
            reader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
