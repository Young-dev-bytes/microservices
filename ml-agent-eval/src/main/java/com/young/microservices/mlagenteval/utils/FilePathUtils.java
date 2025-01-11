package com.young.microservices.mlagenteval.utils;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;

import java.io.File;
import java.util.List;

public class FilePathUtils {
    private static final int TEMP_DIR_EXPIRED_MINUTES = 10;

    /**
     * path separator
     */
    private static final String PATH_SEPARATOR = "/";

    private FilePathUtils() {
    }

    /**
     * 组装文件目录
     * @param dirNames directory
     * @return result
     */
    public static String buildFilePath(String... dirNames) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < dirNames.length; index++) {
            String dirName = dirNames[index];
            if (index > 0 && dirName.startsWith(PATH_SEPARATOR)) {
                dirName = dirName.substring(1);
            }
            stringBuilder.append(dirName);
            if (!dirName.endsWith(PATH_SEPARATOR)) {
                stringBuilder.append(PATH_SEPARATOR);
            }
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * 递归获取文件夹下文件路径（包括子目录）
     * @param dir directory
     * @param currentPath current path
     * @param result result
     */
    public static void listFile(File dir, String currentPath, List<String> result) {
        String[] fileNames = dir.list();
        if (ArrayUtils.isEmpty(fileNames)) {
            return;
        }

        for (String fileName : fileNames) {
            File file = new File(dir, fileName);
            if (file.isFile()) {
                result.add(currentPath + file.getName());
            } else {
                listFile(file, currentPath + file.getName() + File.separator, result);
            }
        }
    }

    /**
     * get entry name
     *
     * @param entryName entry name
     * @param isDirectory directory
     * @return formatted entry name
     */
    public static String formatEntryName(String entryName, boolean isDirectory) {
        int index = 0;
        for (; index < entryName.length() && entryName.charAt(index) == '/'; index++) {
            continue;
        }
        String formattedEntryName = entryName.substring(index);
        if (isDirectory) {
            formattedEntryName += "/";
        }
        return formattedEntryName;
    }
}
