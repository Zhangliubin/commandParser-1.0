package edu.sysu.pmglab.suranyi.easytools;

import edu.sysu.pmglab.suranyi.check.Assert;
import edu.sysu.pmglab.suranyi.check.ioexception.IOExceptionOptions;
import edu.sysu.pmglab.suranyi.container.VolumeByteStream;
import edu.sysu.pmglab.suranyi.unifyIO.FileStream;
import edu.sysu.pmglab.suranyi.unifyIO.options.FileOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author suranyi
 * @description 文件工具
 */

public class FileUtils {
    /**
     * 判断文件是否存在
     * @param fileName 文件名
     * @return 当前文件是否存在
     */
    public static boolean exists(String fileName) {
        return Files.exists(Paths.get(fileName));
    }

    /**
     * 判断文件是否为文件夹
     * @param fileName 文件名
     * @return 当前文件路径是否为文件夹
     */
    public static boolean isDirectory(String fileName) {
        return Files.isDirectory(Paths.get(fileName));
    }

    /**
     * 递归删除指定文件
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public static boolean delete(String fileName) {
        try {
            if (isDirectory(fileName)) {
                for (String subFileName : listFiles(fileName)) {
                    delete(subFileName);
                }
            }

            return Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 递归删除多个文件
     * @param fileNames 文件名
     * @return 是否全部删除成功
     */
    public static boolean delete(String... fileNames) {
        for (String fileName : fileNames) {
            if (!delete(fileName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 修改文件名
     * @param oldName 旧文件名
     * @param newName 新文件名
     * @return 是否改名成功
     */
    public static boolean rename(String oldName, String newName) {
        // 修改文件名
        File fileOld = new File(oldName);
        File fileNew = new File(newName);

        // 修改本类指定的文件名
        return fileOld.renameTo(fileNew);
    }

    /**
     * 递归获取文件大小
     * @param inputFileName 输入文件名/文件夹名
     * @return 文件大小
     */
    public static long sizeOf(String inputFileName) throws IOException {
        if (isDirectory(inputFileName)) {
            long size = 0;
            for (String subFileName : listFiles(inputFileName)) {
                size += sizeOf(subFileName);
            }
            return size;
        } else {
            return Files.size(Paths.get(inputFileName));
        }
    }

    /**
     * 列出所有的一级子文件绝对路径
     * @param fileName 文件夹名
     * @return 一级子文件构成的数组列表
     */
    public static String[] listFiles(String fileName) throws IOException {
        Assert.that(isDirectory(fileName), fileName + " is not a folder.");

        File[] subFiles = Assert.NotNull(new File(fileName).listFiles());
        String[] out = new String[subFiles.length];
        for (int i = 0; i < subFiles.length; i++) {
            out[i] = subFiles[i].getAbsolutePath();
        }
        return out;
    }

    /**
     * 递归列出所有的子文件绝对路径
     * @param fileNames 多个文件夹名
     * @return 子文件构成的数组列表
     */
    public static String[] listFilesDeeply(String... fileNames) {
        ArrayList<String> allInputFileNames = new ArrayList<>(fileNames.length);
        for (String fileName : fileNames) {
            if (FileUtils.isDirectory(fileName)) {
                allInputFileNames.addAll(FileUtils.listFilesDeeply0(fileName));
            } else {
                allInputFileNames.add(fileName);
            }
        }

        return ArrayUtils.toStringArray(allInputFileNames);
    }

    /**
     * 递归列出所有的子文件绝对路径
     * @param fileNames 多个文件夹名
     * @return 子文件构成的数组列表
     */
    public static String[] listFilesDeeply(Collection<String> fileNames) {
        ArrayList<String> allInputFileNames = new ArrayList<>(fileNames.size());
        for (String fileName : fileNames) {
            if (FileUtils.isDirectory(fileName)) {
                allInputFileNames.addAll(FileUtils.listFilesDeeply0(fileName));
            } else {
                allInputFileNames.add(fileName);
            }
        }

        return ArrayUtils.toStringArray(allInputFileNames);
    }

    /**
     * 递归列出所有的子文件绝对路径
     * @param fileNames 一个或多个文件夹名
     * @return 子文件构成的数组列表
     */
    private static ArrayList<String> listFilesDeeply0(String fileNames) {
        Assert.that(isDirectory(fileNames), fileNames + " is not a folder.");

        File[] subFiles = Objects.requireNonNull(new File(fileNames).listFiles());
        ArrayList<String> files = new ArrayList(subFiles.length);

        for (File subFile : subFiles) {
            String subFileName = subFile.getAbsolutePath();
            if (isDirectory(subFileName)) {
                files.addAll(listFilesDeeply0(subFileName));
            } else {
                files.add(subFileName);
            }
        }
        return files;
    }

    /**
     * 将文件的换行符修复为标准换行符
     * @param fileName 输入文件
     */
    public static void TransformToStandardNewlineCharacter(String fileName) throws IOException {
        TransformToStandardNewlineCharacter(fileName, fileName, true);
    }

    /**
     * 将文件的换行符修复为标准换行符
     * @param inputFileName 输入文件
     * @param outputFileName 输出文件
     */
    public static void TransformToStandardNewlineCharacter(String inputFileName, String outputFileName) throws IOException {
        TransformToStandardNewlineCharacter(inputFileName, outputFileName, inputFileName.equals(outputFileName));
    }

    /**
     * 将文件的换行符修复为标准换行符
     * @param inputFileName 输入文件
     * @param outputFileName 输出文件
     * @param deleteOldFile 是否删除旧文件
     */
    public static void TransformToStandardNewlineCharacter(String inputFileName, String outputFileName, boolean deleteOldFile) throws IOException {
        Assert.that(FileUtils.exists(inputFileName), IOExceptionOptions.FileNotFoundException);

        FileStream fsIn = new FileStream(inputFileName, FileOptions.DEFAULT_READER);
        FileStream fsOut;

        boolean changeToInputFileName = false;
        if (!inputFileName.equals(outputFileName)) {
            fsOut = new FileStream(outputFileName, FileOptions.CHANNEL_WRITER);
        } else {
            Assert.that(deleteOldFile, IOExceptionOptions.FileAlreadyExistsException, outputFileName + " is same as inputFileName, please set `deleteOldFile = true` or other `outputFileName`");

            changeToInputFileName = true;
            fsOut = new FileStream(outputFileName + ".~$temp", FileOptions.CHANNEL_WRITER);
        }

        VolumeByteStream lineCache = new VolumeByteStream(2 << 20);
        if (fsIn.readLine(lineCache) != -1) {
            // 写入第一行
            fsOut.write(lineCache, 0, lineCache.size());
            lineCache.reset(0);
        }

        while (fsIn.readLine(lineCache) != -1) {
            fsOut.write(ByteCode.NEWLINE);
            fsOut.write(lineCache, 0, lineCache.size());
            lineCache.reset(0);
        }

        if (deleteOldFile) {
            fsIn.delete();
        } else {
            fsIn.close();
        }

        if (changeToInputFileName) {
            fsOut.rename(fsIn.getFileName());
        } else {
            fsOut.close();
        }
    }

    /**
     * 获取文件的文件名
     * @param filePath 文件的绝对路径或相对路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        return new File(filePath).getName();
    }

    /**
     * 按照扩展名过滤文件
     * @param src 需要过滤的文件名
     * @param extensions 扩展名
     */
    public static String[] filterByExtension(String[] src, String... extensions) {
        boolean[] status = ArrayUtils.endWiths(src, extensions);
        String[] out = new String[ArrayUtils.valueCounts(status, true)];
        for (int i = 0, index = 0; i < src.length; i++) {
            if (status[i]) {
                out[index++] = src[i];
            }
        }
        return out;
    }

    /**
     * 按照扩展名过滤文件
     * @param src 需要过滤的文件名
     * @param extensions 扩展名
     */
    public static String[] filterByExtension(ArrayList<String> src, String... extensions) {
        return filterByExtension(ArrayUtils.toStringArray(src), extensions);
    }

    /**
     * 修复/修正 文件扩展名
     * @param fileName 需要修改的文件名
     * @param extension 目标扩展名
     * @param matchers 待匹配的项目
     */
    public static String fixExtension(String fileName, String extension, String... matchers) {
        // 修改文件扩展名
        extension = extension.length() == 0 ? "" : (extension.startsWith(".") ? extension : "." + extension);

        // 如果有匹配的 matchers，则去除这些字段
        for (String match : matchers) {
            match = match.startsWith(".") ? match : "." + match;
            if (fileName.endsWith(match)) {
                fileName = fileName.substring(0, fileName.length() - match.length()) + extension;
                return fileName;
            }
        }

        // 如果没有匹配的 matchers，但是 fileName 带有 extension，则不进行修改
        if (fileName.endsWith(extension)) {
            return fileName;
        } else {
            return fileName + extension;
        }
    }

    /**
     * 将源文件移动到目标文件夹
     * @param targetDir 目标文件夹
     * @param sourceFileNames 源文件
     */
    public static void moveTo(String targetDir, String... sourceFileNames) {
        for (String sourceFileName : sourceFileNames) {
            rename(sourceFileName, targetDir + "/" + getFileName(sourceFileName));
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean mkdir(String fileName) {
        return new File(fileName).mkdir();
    }

    /**
     * 创建文件夹
     */
    public static boolean mkdirs(String fileName) {
        return new File(fileName).mkdirs();
    }

    /**
     * 文件大小字符串格式化转换器
     * @param size 文件大小
     * @param decimal 精度
     */
    public static String sizeTransformer(long size, int decimal) {
        String format = "%." + decimal + "f";

        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            return String.format(format + " KB", (float) size / 1024);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format(format + " MB", (float) size / 1024 / 1024);
        } else if (size < 1024L * 1024 * 1024 * 1024) {
            return String.format(format + " GB", (float) size / 1024 / 1024 / 1024);
        } else {
            return String.format(format + " TB", (float) size / 1024 / 1024 / 1024);
        }
    }
}
