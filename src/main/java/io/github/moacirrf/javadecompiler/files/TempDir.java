/*
 * Copyright (C) 2021 Moacir da Roza Flores <moacirrf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.moacirrf.javadecompiler.files;

import com.machinezoo.noexception.Exceptions;
import static com.machinezoo.noexception.Exceptions.wrap;
import io.github.moacirrf.javadecompiler.ExceptionHandler;
import static java.lang.System.getProperty;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TempDir {

    public static final String TEMP_DIR_PLUGIN = getProperty("java.io.tmpdir") + "/nb_java_decompiler";

    /**
     * Return directory where decompiled classes will be created.
     *
     * @return
     */
    public static Path getTempDir() {
        Path path = Paths.get(TEMP_DIR_PLUGIN);
        if (!Files.exists(path)) {
            wrap(ExceptionHandler::handleException)
                    .run(() -> {
                        if (!Files.exists(path)) {
                            Files.createDirectory(path);
                        }
                    });
        }
        return path;
    }

    public static void removeTempDir() {
        clearTempFolder(getTempDir());
    }

    private static void clearTempFolder(Path path) {
        Exceptions.wrap(ExceptionHandler::handleException).run(() -> {
            if (Files.isDirectory(path) && Files.list(path).count() > 0) {
                Files.list(path).forEach(it -> clearTempFolder(it));
            }
            path.toFile().setWritable(true);
            Files.deleteIfExists(path);
        });
    }

}
