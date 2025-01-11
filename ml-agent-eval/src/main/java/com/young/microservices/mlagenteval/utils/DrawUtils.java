package com.young.microservices.mlagenteval.utils;

import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DrawUtils {
    public static void drawOnImage(String imagePath, String destName, String destPath, double[] coords) {
        log.info("imagePath:{}, coords:{}, destPath:{}", imagePath, coords, destPath);
        BufferedImage image;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            log.error("read image error, {}", e.getMessage());
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "read image error");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Graphics2D draw = image.createGraphics();
        draw.setColor(Color.RED);
        log.info("already set color.");
        if (coords.length == 2) {
            double x = coords[0];
            double y = coords[1];
            if (isNormalized(x) && isNormalized(y)) {
                x = x * width;
                y = y * height;
            }
            log.info("length is 2.");
            int radius = 30;
            draw.fillOval((int) x - radius, (int) y - radius, radius * 2, radius * 2);
        } else if (coords.length == 4) {
            double x1 = coords[0];
            double y1 = coords[1];
            double x2 = coords[2];
            double y2 = coords[3];
            if (isNormalized(x1) && isNormalized(y1) && isNormalized(x2) && isNormalized(y2)) {
                x1 = x1 * width;
                y1 = y1 * height;
                x2 = x2 * width;
                y2 = y2 * height;
            }
            draw.setStroke(new BasicStroke(5));
            draw.drawRect((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
        } else {
            log.error("Invalid number of coordinates. Must be 2 for a point or 4 for a bounding box.");
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "Invalid number of coordinates. Must be 2 for a point or 4 for a bounding box.");
        }
        draw.dispose();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            try (InputStream inputStream = new ByteArrayInputStream(baos.toByteArray())) {
                /*NfsService nfsService = new NfsService(Constants.NFS_SERVER, Constants.WORKING_DIR);
                nfsService.uploadFileToNfs(inputStream, destName, destPath);*/
            }
        } catch (IOException e) {
            log.error("upload image to NFS error, {}", e.getMessage());
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "upload image to NFS error.");
        }
    }

    private static boolean isNormalized(double coord) {
        return 0 <= coord && coord <= 1;
    }

    public static double[] extractCoordinates(String rsp) {
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(rsp);
        double[] coordinates = new double[2];
        int index = 0;

        while (matcher.find() && index < 2) {
            coordinates[index++] = Double.parseDouble(matcher.group());
        }
        return coordinates;
    }

    public static void main(String[] args) {
        // String rsp = " tap(0.5987745098039216, 0.9719101123595506)";
        String rsp = "Action: tap(0.8501855287569573, 0.3812199036918138)";
        String imagePath = "D:\\work\\agent-simulation\\b9045f09a02942918dbe0283e25a2d17\\cache\\screenshot.png";
        // String destPath = "D:\\work\\agent-simulation\\b9045f09a02942918dbe0283e25a2d17\\cache\\screenshot_dest.png";
        String destPath = "/finetune/evaluate/sft-test/1f690dbc6bb04726939f8c66aca58eb4/ef0018031add4fd595651a8afb283309/beef092ddd1b4cb5bfdaf25f24d3bb51/cache";
        String destName = "screenshot.png";
        double[] screenSize = {1224,2688};
        if (rsp.contains("tap") || rsp.contains("long_press") || rsp.contains("text") || rsp.contains("scroll")) {
            double[] coordinates = extractCoordinates(rsp);
            double x = coordinates[0];
            double y = coordinates[1];
            x = x * screenSize[0];
            y = y * screenSize[1];
            try {
                drawOnImage(imagePath, destName, destPath, new double[]{x, y});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
