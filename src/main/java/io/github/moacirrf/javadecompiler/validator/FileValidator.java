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
package io.github.moacirrf.javadecompiler.validator;

import static io.github.moacirrf.javadecompiler.files.FileSystemHelper.CLASSFILE_BINARY_NAME;
import static io.github.moacirrf.javadecompiler.files.FileSystemHelper.CLASSFILE_ROOT;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import java.util.List;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;

public final class FileValidator {

    private static final String INVALID_FILE = "Invalid file.";
    private static final String ACCEPTED_FILES = "Only %s files are accepted.";
    private static final List<String> ACCEPTED_EXTENSION = asList("class");

    public static boolean validate(FileObject file) {
        if (nonNull(file)) {
            return isValidExtensions(file);
        } else {
            StatusDisplayer.getDefault().setStatusText(INVALID_FILE);
            return false;
        }
    }

    private static boolean isValidExtensions(FileObject file) {

        if (!ACCEPTED_EXTENSION.contains(file.getExt().toLowerCase()) && !isClassOriginFromJar(file)) {
            String extension = ACCEPTED_EXTENSION.stream().collect(joining(", "));
            StatusDisplayer.getDefault().setStatusText(String.format(ACCEPTED_FILES, extension));
            return false;
        }
        return true;
    }

    /**
     * When a class file is opened on netbeans editor, the default action is
     * show a JAVAP output, but the address of a original class file and jar is
     * stored on attribute.
     *
     * @param file
     * @return
     */
    private static boolean isClassOriginFromJar(FileObject file) {
        return nonNull(file.getAttribute(CLASSFILE_ROOT))
                && nonNull(file.getAttribute(CLASSFILE_BINARY_NAME));
    }

    private FileValidator() {
    }
}
