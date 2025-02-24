package com.eyinfo.storage.files;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.eyinfo.android_pure_utils.utils.ObjectJudge;
import com.eyinfo.android_pure_utils.utils.PathsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils extends BasicFilename {

    /**
     * 创建主目录
     * 1.项目配置RxAndroid.getInstance().setCacheRootDir(...)属性时请设置StorageUtils.getExternalRootDir(),
     * 避免权限问题无法获取;
     * 2.在获取的同时根据一定的规则会检测sdcard存储是否有相关的权限,便于第三方或项目存储sdcard相关数据;
     *
     * @return 根目录
     */
    public static File getRootDir(Context applicationContext) {
        File cacheRootDir = applicationContext.getCacheDir();
        //如果缓存目录为空则取拼接内部目录{@see Android/data/package name/cache/}
        if (cacheRootDir == null) {
            String externalRootDir = getExternalRootDir(applicationContext);
            cacheRootDir = new File(externalRootDir);
        }
        if (!cacheRootDir.exists()) {
            boolean mkdirs = cacheRootDir.mkdirs();
        }
        return cacheRootDir;
    }

    /**
     * 获取扩展根目录
     *
     * @return 根目录路径
     */
    public static String getExternalRootDir(Context applicationContext) {
        String packageName = applicationContext.getPackageName();
        String path = Environment.getExternalStorageDirectory().getPath();
        if (TextUtils.isEmpty(packageName)) {
            //如果包名为空则保存至sdkcard根目录
            return path;
        }
        return PathsUtils.combine(path, String.format("Android/data/%s/cache/", packageName));
    }

    /**
     * 获取内部根目录
     *
     * @param context 应用程序上下文
     * @return 根目录路径
     */
    public static String getInternalRootDir(Context context) {
        if (context == null) {
            return "";
        }
        File cacheDir = context.getCacheDir();
        return cacheDir == null ? "" : cacheDir.getAbsolutePath();
    }

    /**
     * 获取子目录 如果不存在，则自动创建
     *
     * @param applicationContext 应用程序上下文
     * @param destDirName        子目录名称
     * @return File
     */
    public static File getDir(Context applicationContext, String destDirName) {
        File dir = createDirectory(getRootDir(applicationContext), destDirName);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建目录
     * <p>
     * param dir  主目录
     * param dests 需要创建的子目录
     * return
     */
    public static File createDirectory(File dir, String... dests) {
        if (ObjectJudge.isNullOrEmpty(dests)) {
            return dir;
        }
        String relativePath = PathsUtils.combine(dests);
        if (!relativePath.startsWith(File.separator)) {
            relativePath = File.separator + relativePath;
        }
        if (!relativePath.endsWith(File.separator)) {
            relativePath = relativePath + File.separator;
        }
        File result = new File(dir, relativePath);
        if (!result.exists()) {
            boolean mkdirs = result.mkdirs();
        }
        return result;
    }

    /**
     * 获取文件
     *
     * @param dir      目录
     * @param fileName 文件名称
     * @param delete   如果存在是否删除原文件重新创建
     * @return File
     */
    public static File getFile(File dir, String fileName, boolean delete) {
        File file = new File(dir, fileName);
        try {
            if (file.exists()) {
                if (delete) {
                    if (file.delete()) {
                        boolean newFile = file.createNewFile();
                    }
                }
            } else {
                boolean newFile = file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取文件
     *
     * @param dir      目录
     * @param fileName 文件名称
     * @return File
     */
    public static File getFile(File dir, String fileName) {
        return getFile(dir, fileName, true);
    }

    /**
     * 根据path创建文件
     *
     * @param path   文件路径
     * @param delete true-先删除再创建;false-若存在直接返回;
     * @return
     */
    public static File getFile(String path, boolean delete) {
        File file = new File(path);
        try {
            if (file.exists()) {
                if (delete) {
                    if (file.delete()) {
                        boolean newFile = file.createNewFile();
                    }
                }
            } else {
                boolean newFile = file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 根据path创建文件
     *
     * @param path 文件路径
     * @return
     */
    public static File getFile(String path) {
        return getFile(path, true);
    }

    /**
     * 删除目录或者文件
     * <p>
     * param filepath 文件路径
     * return
     */
    public static boolean deleteQuietly(String filePath) {
        File file = new File(filePath);
        return deleteQuietly(file);
    }

    /**
     * 删除目录或者文件
     * <p>
     * param dir      主目录
     * param filepath 文件名称
     * return
     */
    public static boolean deleteQuietly(String dir, String fileName) {
        File file = new File(dir, fileName);
        return deleteQuietly(file);
    }

    /**
     * 删除目录或者文件。 目录则递归删除， 文件直接删除
     * <p>
     * param file
     * return
     */
    public static boolean deleteQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception ignored) {
        }

        try {
            return file.delete();
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 删除目录中的文件及目录 支持递归删除 删除目录本身
     * <p>
     * param directory
     */
    public static void deleteDirectory(File directory) {
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {// 不是符号链接的，递归删除
            cleanDirectory(directory);
        }
        boolean delete = directory.delete();
    }

    /**
     * 递归删除目录中的目录或者文件 不删除目录本身
     * <p>
     * param directory
     */
    public static void cleanDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            forceDelete(file);
        }
    }

    /**
     * 递归删除目录中的目录或者文件 删除目录本身
     * <p>
     * param file 目录或文件
     */
    public static void forceDelete(File file) {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (filePresent) {
                boolean delete = file.delete();
            }
        }
    }

    /**
     * 是否符号链接判断
     * <p>
     * param file
     * return
     * throws IOException
     */
    private static boolean isSymlink(File file) {
        try {
            if (file == null) {
                return false;
            }
            if (BasicFilename.isSystemWindows()) {
                return false;
            }
            File fileInCanonicalDir = null;
            if (file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }
            return !fileInCanonicalDir.getCanonicalFile().equals(
                    fileInCanonicalDir.getAbsoluteFile());
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * copy文件
     * <p>
     * param fromFile
     * param toFile
     */
    public static void copyFiles(String fromFile, String toFile) throws IOException {
        // 要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        // 如同判断SD卡是否存在或者文件是否存在
        // 如果不存在则 return出去
        if (!root.exists()) {
            return;
        }
        // 如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();
        // 目标目录
        File targetDir = new File(toFile);
        // 创建目录
        if (!targetDir.exists()) {
            boolean mkdirs = targetDir.mkdirs();
        }
        // 遍历要复制该目录下的全部文件
        for (File currentFile : currentFiles) {
            if (currentFile.isDirectory()) {
                // 如果当前项为子目录 进行递归
                File childDir = new File(toFile, currentFile.getName());
                copyFiles(currentFile.getPath() + "/",
                        childDir.getAbsolutePath());
            } else {
                // 如果当前项为文件则进行文件拷贝
                File mfile = new File(toFile, currentFile.getName());
                copyFile(currentFile.getPath(), mfile.getAbsolutePath());
            }
        }
    }

    public static void copyFiles(File fromFile, File toFile) throws IOException {
        String fromPath = fromFile.getAbsolutePath();
        String toPath = toFile.getAbsolutePath();
        copyFiles(fromPath, toPath);
    }

    public static void copyFile(String fromFile, String toFile) throws IOException {
        FileInputStream fosFrom = new FileInputStream(fromFile);
        FileOutputStream fosTo = new FileOutputStream(toFile);
        byte bt[] = new byte[4096];
        int c;
        while ((c = fosFrom.read(bt)) > 0) {
            fosTo.write(bt, 0, c);
        }
        fosFrom.close();
        fosTo.close();
    }

    public static void copyFile(File fromFile, File toFile) throws IOException {
        String frompath = fromFile.getAbsolutePath();
        String topath = toFile.getAbsolutePath();
        copyFile(frompath, topath);
    }

    public static void save(String content, File tofile) throws IOException {
        if (TextUtils.isEmpty(content) || tofile == null) {
            return;
        }
        content = content.trim();
        if (tofile.exists()) {
            boolean delete = tofile.delete();
        }
        boolean newFile = tofile.createNewFile();
        byte[] bs = content.getBytes();
        FileOutputStream fosTo = new FileOutputStream(tofile);
        fosTo.write(bs, 0, content.length());
        fosTo.close();
    }

    public static void appendContent(String content, File tofile) throws IOException {
        if (TextUtils.isEmpty(content) || tofile == null) {
            return;
        }
        if (tofile.getParentFile() == null) {
            return;
        }
        content = content.trim();
        if (!tofile.exists()) {
            boolean newFile = tofile.createNewFile();
        }
        byte[] bs = content.getBytes();
        OutputStream fosTo = new FileOutputStream(tofile, true);
        fosTo.write(bs, 0, content.length());
        fosTo.close();
    }

    /**
     * 根据名称获取assets文件inputStream
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return inputStream
     */
    public static InputStream getAssetsInputStream(Context context, String fileName) throws IOException {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return null;
        }
        return context.getAssets().open(fileName);
    }

    /**
     * 获取文件(一般指文本文件)内容
     * <p>
     * param targetfile 目标文件
     * return 内容
     */
    public static String readContent(File targetfile) throws Exception {
        if (targetfile == null || !targetfile.exists()) {
            return "";
        }
        String result = "";
        FileInputStream fis = new FileInputStream(targetfile);
        int available = fis.available();
        byte[] buffer = new byte[available];
        int read = fis.read(buffer);
        result = new String(buffer, StandardCharsets.UTF_8);
        fis.close();
        return result;
    }

    /**
     * 读取流内容
     *
     * @param is 输出流
     * @return 输出内容
     */
    public static String readContent(InputStream is) throws IOException {
        if (is == null || is.available() <= 0) {
            return "";
        }
        String result = "";
        int available = is.available();
        byte[] buffer = new byte[available];
        int read = is.read(buffer);
        result = new String(buffer, StandardCharsets.UTF_8);
        is.close();
        return result;
    }

    /**
     * assets文件是否存在
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return true-存在;false-不存在;
     */
    public static boolean isExsitsAssetsFile(Context context, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return false;
        }
        AssetManager am = context.getAssets();
        try {
            boolean flag = false;
            String[] names = am.list("");
            for (String name : names) {
                if (name.equals(fileName.trim())) {
                    flag = true;
                    break;
                }
            }
            return flag;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 从assets取文本文件内容
     * <p>
     * param context
     * param fileName 文件名称
     * return
     */
    public static String readAssetsFileContent(Context context, String fileName) {
        try {
            if (context == null || TextUtils.isEmpty(fileName)) {
                return "";
            }
            if (!isExsitsAssetsFile(context, fileName)) {
                //如果不存在则返回
                return "";
            }
            String result = "";
            InputStream is = context.getAssets().open(fileName);
            int available = is.available();
            byte[] buffer = new byte[available];
            int read = is.read(buffer);
            result = new String(buffer, StandardCharsets.UTF_8);
            is.close();
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 保存Bitmap
     * <p>
     * param dir      目录
     * param filename 文件名
     * param bt       图片
     * return
     */
    public static File saveBitmap(File dir, String filename, Bitmap bt) throws IOException {
        File mfile = new File(dir, filename);
        if (mfile.exists()) {
            boolean delete = mfile.delete();
        }
        boolean newFile = mfile.createNewFile();
        FileOutputStream out = new FileOutputStream(mfile);
        bt.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.flush();
        out.close();
        return mfile;
    }

    /**
     * 获取文件或目录大小(单位为字节)
     * <p>
     * param fileOrDirPath 文件或目录
     * return
     */
    public static long getFileOrDirSize(File fileOrDirPath) {
        return getFolderSize(fileOrDirPath);
    }

    /**
     * 获取文件或目录大小(单位为字节)
     * <p>
     * param fileOrDirPath 文件或目录
     * return
     */
    public static long getFileOrDirSize(String fileOrDirPath) {
        File file = new File(fileOrDirPath);
        return getFolderSize(file);
    }

    private static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (File value : fileList) {
            if (value.isDirectory()) {
                size = size + getFolderSize(value);
            } else {
                size = size + value.length();
            }
        }
        return size;
    }

    /**
     * 如果不正在则创建文件
     *
     * @param file file
     */
    public static boolean createFileIfNoExists(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
