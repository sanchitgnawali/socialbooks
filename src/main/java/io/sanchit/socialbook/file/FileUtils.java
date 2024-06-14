package io.sanchit.socialbook.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String bookCoverUrl) {

        if (StringUtils.isBlank(bookCoverUrl)) {
            return null;
        }

        try {
            Path path = new File(bookCoverUrl).toPath();
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.warn("No file found in path {}", bookCoverUrl);
        }

        return null;
    }
}
