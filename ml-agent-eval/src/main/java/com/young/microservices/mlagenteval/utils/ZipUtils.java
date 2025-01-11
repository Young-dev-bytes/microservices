package com.young.microservices.mlagenteval.utils;

import com.young.microservices.mlagenteval.constant.Constants;
import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
public class ZipUtils {

    /**
     * zipMultiFiles
     *
     * @param dirPath      dirPath
     * @param zipFilePath  zipFilePath
     * @param subFilesList subFilesList
     */
    public static void zipMultiFiles(String dirPath, String zipFilePath, List<String> subFilesList) {
        String currentUser = System.getProperty("user.name");
        log.info("currentUser: {}", currentUser);
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("dirPath not exist or is not a directory, dirPath: {}", dirPath);
            throw new BusinessException(BizErrorCode.PARAM_INVALID);
        }

        // zip each file
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                log.warn("The directory is empty, dirPath: {}", dirPath);
                return;
            }
            if (!CollectionUtils.isEmpty(subFilesList)) {
                subFilesList.add(Constants.AGENT_RESULT);
            }
            for (File item : files) {
                if (CollectionUtils.isEmpty(subFilesList) || subFilesList.contains(item.getName())) {
                    addFileToZip(item, zipOut, "");
                }
            }
        } catch (IOException e) {
            log.error("Error while creating zip file: {}", e.getMessage(), e);
            throw new BusinessException(BizErrorCode.PARAM_INVALID);
        }
    }

    /**
     * addFileToZip
     *
     * @param file      file
     * @param zipOut    zipOut
     * @param parentDir parentDir
     * @throws IOException
     */
    private static void addFileToZip(File file, ZipOutputStream zipOut, String parentDir) throws IOException {
        String zipEntryName = parentDir + file.getName();
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToZip(child, zipOut, zipEntryName + "/");
                }
            }
        } else {
            try (InputStream is = new FileInputStream(file)) {
                zipOut.putNextEntry(new ZipEntry(zipEntryName));
                IOUtils.copy(is, zipOut);
                zipOut.closeEntry();
            }
        }
    }

    public static void main(String[] args) {
        String dirPath = "D:\\work\\agent-simulation\\step-imgs";
        String zipFilePath = "D:\\work\\agent-simulation\\output.zip";
        try {
            zipMultiFiles(dirPath, zipFilePath, null);
            System.out.println("压缩包创建成功: " + zipFilePath);
        } catch (BusinessException e) {
            System.err.println("压缩包创建失败: " + e.getMessage());
        }
    }
}
