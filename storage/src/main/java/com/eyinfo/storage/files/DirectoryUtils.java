package com.eyinfo.storage.files;

import android.content.Context;
import android.text.TextUtils;

import com.eyinfo.android_pure_utils.utils.ObjectJudge;
import com.eyinfo.android_pure_utils.utils.PathsUtils;
import com.eyinfo.storage.beans.ElementEntry;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/4/4
 * Description:目录管理;
 * 存储方式:[images->[forum->[video,temp],user->[info,vip],comments]]
 * 取方式:{@link FileUtils}.root + images->forum->video,此时可以传
 * 注:
 * 1.目录结构确保不重复,若不同的目录下存在名字相同的两个子目录则取前一个优先匹配到的一个;
 * 2.最后一
 * Modifier:
 * ModifyContent:
 */
public class DirectoryUtils {

    private static volatile DirectoryUtils directoryUtils;
    private ElementEntry elementEntry = new ElementEntry();
    private HashMap<String, File> directorieMap = new HashMap<String, File>();
    //隐藏文件目录
    private Set<String> hideDirectoryMap = new HashSet<String>();

    private DirectoryUtils() {
        //init
    }

    public static DirectoryUtils getInstance() {
        if (directoryUtils == null) {
            synchronized (DirectoryUtils.class) {
                if (directoryUtils == null) {
                    directoryUtils = new DirectoryUtils();
                }
            }
        }
        return directoryUtils;
    }

    /**
     * 添加目录
     *
     * @param directory       目录
     * @param isHideDirectory 是否隐藏
     * @return ElementEntry
     */
    public DirectoryUtils addDirectory(String directory, boolean isHideDirectory) {
        if (TextUtils.isEmpty(directory)) {
            return this;
        }
        elementEntry = elementEntry.addElement(directory);
        if (isHideDirectory) {
            hideDirectoryMap.add(directory);
        }
        return this;
    }

    /**
     * 添加目录
     *
     * @param directory 目录
     * @return ElementEntry
     */
    public DirectoryUtils addDirectory(String directory) {
        return addDirectory(directory, false);
    }

    /**
     * 添加子目录
     *
     * @param directory       子目录
     * @param isHideDirectory 是否隐藏目录
     * @return ElementEntry
     */
    public DirectoryUtils addChildDirectory(String directory, boolean isHideDirectory) {
        if (TextUtils.isEmpty(directory)) {
            return this;
        }
        elementEntry = elementEntry.next(directory);
        if (isHideDirectory) {
            hideDirectoryMap.add(directory);
        }
        return this;
    }

    /**
     * 添加子目录
     *
     * @param directory 子目录
     * @return ElementEntry
     */
    public DirectoryUtils addChildDirectory(String directory) {
        return addChildDirectory(directory, false);
    }

    /**
     * 根据层级数返回上级目录并添加目录(directory)
     *
     * @param prevSeriesCount 层级数
     * @param directory       要添加的目录
     * @param isHideDirectory 是否隐藏目录
     * @return ElementEntry
     */
    public DirectoryUtils prevDirectory(int prevSeriesCount, String directory, boolean isHideDirectory) {
        if (TextUtils.isEmpty(directory)) {
            return this;
        }
        elementEntry = elementEntry.prev(prevSeriesCount, directory);
        if (isHideDirectory) {
            hideDirectoryMap.add(directory);
        }
        return this;
    }

    /**
     * 根据层级数返回上级目录并添加目录(directory)
     *
     * @param prevSeriesCount 层级数
     * @param directory       要添加的目录
     * @return ElementEntry
     */
    public DirectoryUtils prevDirectory(int prevSeriesCount, String directory) {
        return prevDirectory(prevSeriesCount, directory, false);
    }

    /**
     * 返回上一级目录并添加目录(directory)
     *
     * @param directory       要添加的目录
     * @param isHideDirectory 是否隐藏目录
     * @return ElementEntry
     */
    public DirectoryUtils prevDirectory(String directory, boolean isHideDirectory) {
        if (TextUtils.isEmpty(directory)) {
            return this;
        }
        elementEntry = elementEntry.prev(directory);
        if (isHideDirectory) {
            hideDirectoryMap.add(directory);
        }
        return this;
    }

    /**
     * 返回上一级目录并添加目录(directory)
     *
     * @param directory 要添加的目录
     * @return ElementEntry
     */
    public DirectoryUtils prevDirectory(String directory) {
        return prevDirectory(directory, false);
    }

    /**
     * 构建目录
     * (根目录为{@link FileUtils}.root)
     */
    public void buildDirectories(Context applicationContext) {
        File rootDir = FileUtils.getRootDir(applicationContext);
        LinkedList<String> allElementPaths = elementEntry.getAllElementPaths();
        for (String elementPath : allElementPaths) {
            File relative = new File(elementPath);
            if (hideDirectoryMap.contains(relative.getName())) {
                File parentFile = relative.getParentFile();
                String path = PathsUtils.combine(parentFile.getPath(), String.format(".%s", relative.getName()));
                File dir = new File(rootDir, path);
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                }
            } else {
                File dir = new File(rootDir, elementPath);
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                }
            }
        }
    }

    /**
     * 获取目录
     * (根目录为{@link FileUtils}.root)
     *
     * @param directoryName 目录名称
     * @return 目录
     */
    public File getDirectory(Context applicationContext, String directoryName) {
        File rootDir = FileUtils.getRootDir(applicationContext);
        if (TextUtils.isEmpty(directoryName)) {
            return rootDir;
        }
        if (directorieMap.containsKey(directoryName)) {
            File dir = directorieMap.get(directoryName);
            if (dir != null) {
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                }
                return dir;
            }
        }
        if (TextUtils.equals(rootDir.getName(), directoryName)) {
            return rootDir;
        }
        File directory = getMatchDirectory(rootDir, directoryName);
        if (directory == null) {
            //表示该目录未创建,根目录创建
            File file = new File(rootDir, directoryName);
            boolean mkdirs = file.mkdirs();
            return file;
        }
        directorieMap.put(directory.getName(), directory);
        return directory;
    }

    private File getMatchDirectory(File dir, String directoryName) {
        File[] files = dir.listFiles();
        if (ObjectJudge.isNullOrEmpty(files)) {
            return null;
        }
        for (File file : files) {
            if (TextUtils.equals(file.getName(), directoryName) ||
                    TextUtils.equals(file.getName(), String.format(".%s", directoryName))) {
                return file;
            } else {
                File directory = getMatchDirectory(file, directoryName);
                if (directory != null) {
                    return directory;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return elementEntry.toString();
    }
}
