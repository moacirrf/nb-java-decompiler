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

import static com.machinezoo.noexception.Exceptions.wrap;
import static com.mrf.javadecompiler.constants.Constants.CLASSFILE_ROOT;
import static com.mrf.javadecompiler.constants.Constants.CLASS_EXT;
import static com.mrf.javadecompiler.constants.Constants.EXCLAM;
import static com.mrf.javadecompiler.constants.Constants.FILE;
import static com.mrf.javadecompiler.constants.Constants.JAR;
import static com.mrf.javadecompiler.exception.ExceptionHandler.handleException;
import static java.io.File.separator;
import static java.io.File.separatorChar;
import static java.util.Objects.nonNull;
import com.mrf.javadecompiler.openapi.ui.SourceWindowTopComponent;
import java.net.URL;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class SourceWindowBuilder {

    public static final String COMMENT = "//\n"
      + "// Source code recreated by Apache Netbeans\n"
      + "// (powered by Java Decompiler http://java-decompiler.github.io )\n"
      + "//\n";

    public static SourceWindowTopComponent build(FileObject file, String decompiledSource) {
        SourceWindowTopComponent window = new SourceWindowTopComponent();
        window.setName(getName(file));
        window.setToolTipText(getTooltip(file));
        window.setDecompiledSource(COMMENT + decompiledSource);
        return window;
    }

    private static String getName(FileObject file) {
        String parent = file.getParent().getName();
        String name = file.getName();
        return parent + separator + name + CLASS_EXT;
    }

    private static String getTooltip(FileObject file) {

        return wrap(e -> handleException(e)).get(() -> {
            FileSystem fileSystem = file.getFileSystem();
            Object fileName = file.getAttribute("classfile-binaryName");
            Object root = file.getAttribute(CLASSFILE_ROOT);

            if (nonNull(fileName) && nonNull(root)) {
                return getNameFrom((URL) root)
                  + separator
                  + String.valueOf(fileName)
                  + CLASS_EXT;
            }

            return fileSystem.getDisplayName()
              + separator
              + file.getParent().getPath()
              + separatorChar
              + file.getName() + CLASS_EXT;
        });
    }

    private static String getNameFrom(URL url) {
        return url.toString()
          .replace(JAR, "")
          .replace(FILE, "")
          .replace(EXCLAM, "");
    }

    private SourceWindowBuilder() {
    }
}
