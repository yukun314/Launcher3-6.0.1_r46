package com.zyk.launcher3.json;

import java.io.Serializable;

/**
 * Created by zyk on 2016/7/23.
 */
public class ApkVersion implements Serializable{

    public String versionCode;//版本号

    public String versionName;//版本名

    public String versionSize;//版本apk的大小

    public String versionContent;//更新的内容

    public String downloadUrl;//下载地址,相对目录

    @Override
    public String toString() {
        return "ApkVersion{" +
                "versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionSize='" + versionSize + '\'' +
                ", versionContent='" + versionContent + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
