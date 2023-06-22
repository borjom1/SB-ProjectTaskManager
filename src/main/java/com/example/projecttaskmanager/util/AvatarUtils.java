package com.example.projecttaskmanager.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

@UtilityClass
public class AvatarUtils {

    public static final String AVATARS_DIR_PATH;
    public static final String DEFAULT_AVATAR_PATH;

    static {
        try {
            URI uri = requireNonNull(AvatarUtils.class.getClassLoader().getResource("avatars")).toURI();
            AVATARS_DIR_PATH = Paths.get(requireNonNull(uri))
                    .toFile()
                    .getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        DEFAULT_AVATAR_PATH = AVATARS_DIR_PATH + File.separator + "default";
    }

    public void saveFile(String filename, String content) {
        try (var writer = Files.newBufferedWriter(Path.of(AVATARS_DIR_PATH, filename))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFile(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(path));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}