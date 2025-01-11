package com.young.microservices.mlagenteval.service;

import java.io.IOException;
import java.io.InputStream;

public class NfsService {

    private String nasAddress;
    private String workingDir;


    public NfsService(String nasAddress, String workingDir) {
        this.nasAddress = nasAddress;
        this.workingDir = workingDir;
    }

    public InputStream downloadNfsFile(String datasetPath) {

        return new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
    }

    public void copyNfsFileToDir(String filePath, String currDestDir) {

    }

    public void copyNfsFileToDirAndRename(String filePath, String lastDestDir, String s) throws IOException{

    }
}
