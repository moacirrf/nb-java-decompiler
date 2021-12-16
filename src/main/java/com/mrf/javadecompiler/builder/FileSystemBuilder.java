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
package com.mrf.javadecompiler.builder;

import static java.util.Objects.nonNull;
import static com.mrf.javadecompiler.exception.ExceptionHandler.handleException;
import static com.machinezoo.noexception.Exceptions.wrap;
import static com.mrf.javadecompiler.constants.Constants.CLASSFILE_ROOT;
import static com.mrf.javadecompiler.constants.Constants.EXCLAM;
import static com.mrf.javadecompiler.constants.Constants.FILE;
import static com.mrf.javadecompiler.constants.Constants.JAR;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.JarFileSystem;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class FileSystemBuilder {

    public static FileSystem build(FileObject file) {
        return wrap(e -> handleException(e)).get(() -> {
            FileSystem fileSys = getJarFileSystem(file);
            return nonNull(fileSys)
              ? fileSys
              : file.getFileSystem();
        });

    }

    private static JarFileSystem getJarFileSystem(FileObject file) throws IOException {
        if (file.getFileSystem() instanceof JarFileSystem) {
            return new JarFileSystem(((JarFileSystem) file.getFileSystem()).getJarFile());
        }
        Object root = file.getAttribute(CLASSFILE_ROOT);
        if (nonNull(root)) {
            return new JarFileSystem(getJarFile((URL) root));
        }
        return null;
    }

    private static File getJarFile(URL url) {
        return new File(url.getFile()
          .replace(JAR, "")
          .replace(FILE, "")
          .replace(EXCLAM, ""));
    }

    private FileSystemBuilder() {
    }
}
