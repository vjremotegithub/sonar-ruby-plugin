package com.fortitudetec.sonar.plugins.ruby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;

@BatchSide
public class PathResolver {
    private static final Logger LOG = LoggerFactory.getLogger(PathResolver.class);

    public String getPath(SensorContext context, String settingKey, String defaultValue) {
        // Prefer the specified path
        String toReturn = context.settings().getString(settingKey);

        // Fall back to a file system search if null or doesn't exist
        if (toReturn == null || toReturn.isEmpty()) {
            LOG.debug("Path {} not specified, falling back to {}", settingKey, defaultValue);
            toReturn = defaultValue;
        }
        else {
            LOG.debug("Found {} path to be '{}'", settingKey, toReturn);
        }

        return getAbsolutePath(context, toReturn);
    }

    private String getAbsolutePath(SensorContext context, String toReturn) {
        if (toReturn != null) {
            File candidateFile = new java.io.File(toReturn);
            if (!candidateFile.isAbsolute()) {
                candidateFile = new java.io.File(context.fileSystem().baseDir().getAbsolutePath(), toReturn);
            }

            if (!doesFileExist(candidateFile)) {
                return null;
            }

            return candidateFile.getAbsolutePath();
        }

        return null;
    }

    private boolean doesFileExist(File f) {
        return f.exists();
    }
}
