package com.zyk.launcher3.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by zyk on 2016/7/16.
 */
public class Version implements Serializable{

    public int version;//版本好
    public String url;//该版本对应的contents列表的相对地址

    /**
     * @param filePath 文件的全路径
     */
    public void savaFile(String filePath,String fileName){
        try {
            File file = new File(filePath,fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
