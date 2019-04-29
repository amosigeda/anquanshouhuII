package vip.inteltech.gat.chatutil;

import java.io.*;

import vip.inteltech.gat.utils.AppContext;

public class FileUtils {
    /**
     * 需要知道当前SD卡的目录，Environment.getExternalStorageDierctory()
     */

    private static String SDPATH;

    private FileUtils() {

    }

    public static String getSDPATH() {
        if(SDPATH==null||SDPATH.trim().length()==0){
            // 目录名/sdcard
            SDPATH = AppContext.getContext().getFilesDir().getAbsolutePath() + "/";
        }
        return SDPATH;
    }

    // 在sdcard卡上创建文件
    public static File createSDFile(String fileName) throws IOException {
        File file = new File(getSDPATH() + fileName);
        System.out.println(getSDPATH() + fileName);
        file.createNewFile();
        return file;
    }

    // 在sd卡上创建目录
    public static File createSDDir(String dirName) {
        File dir = new File(getSDPATH() + dirName);
        // mkdir只能创建一级目录 ,mkdirs可以创建多级目录
        dir.mkdir();
        return dir;
    }

    // 判断sd卡上的文件夹是否存在
    public static boolean isFileExist(String fileName) {
        File file = new File(getSDPATH() + fileName);
        return file.exists();
    }

    /**
     * 将一个inputstream里面的数据写入SD卡中 第一个参数为目录名 第二个参数为文件名
     */
    public static File write2SDFromInput(String path, String fileName, InputStream inputstream) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            // System.out.println(createSDDir(path).getParentFile());
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            // 4k为单位，每4K写一次
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            while ((temp = inputstream.read(buffer)) != -1) {
                // 获取指定信,防止写入没用的信息
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static File write2SD(String fileName,byte[] bytes){
        File file=null;
        OutputStream output = null;
        try {
            createSDDir(getSDPATH());
            file = createSDFile(getSDPATH() + fileName);
            output=new FileOutputStream(file);
            output.write(bytes);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeStream(output);
        }
        return file;
    }

    public static void closeStream(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }
}
