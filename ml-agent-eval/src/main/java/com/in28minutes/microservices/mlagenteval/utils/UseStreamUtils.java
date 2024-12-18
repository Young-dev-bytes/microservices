package com.in28minutes.microservices.mlagenteval.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.in28minutes.microservices.mlagenteval.dto.InstanceTaskInfo;
import com.in28minutes.microservices.mlagenteval.enums.BizErrorCode;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UseStreamUtils extends StreamUtils {
    public static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toString(String.valueOf(StandardCharsets.UTF_8));
    }

    public static void downloadImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } else {
            log.info("No file to download. Server replied HTTP code: [{}]", responseCode);
        }
        httpConn.disconnect();
    }

    public static List<InstanceTaskInfo> readJsonlFile(InputStream inputStream) {
        List<InstanceTaskInfo> tasks = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(InstanceTaskInfo.class);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                InstanceTaskInfo task = reader.readValue(line);
                tasks.add(task);
            }
        } catch (IOException e) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
        }
        return tasks;
    }
}
