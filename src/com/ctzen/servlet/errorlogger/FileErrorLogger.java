package com.ctzen.servlet.errorlogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ErrorLogger} that logs to individual files in the designated folder.
 *
 * @author cchang
 */
public class FileErrorLogger implements ErrorLogger {

    private static final Logger LOG = LoggerFactory.getLogger(FileErrorLogger.class);

    /**
     * @param folder    directory to log to
     */
    public FileErrorLogger(final File folder) {
        this.folder = folder;
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Unable to make directory: " + folder);
        }
        LOG.info("folder={}", folder.getAbsolutePath());
    }

    private final File folder;

    public File getFolder() {
        return folder;
    }

    @Override
    public void log(final String errorId, final String error) throws IOException {
        final File f = new File(folder, errorId);
        Files.write(f.toPath(), error.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

}
