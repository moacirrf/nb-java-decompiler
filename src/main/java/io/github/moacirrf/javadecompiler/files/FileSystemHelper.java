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
import io.github.moacirrf.javadecompiler.ExceptionHandler;
import java.net.URL;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author moacirrf
 */
public final class FileSystemHelper {

    public static final String CLASSFILE_BINARY_NAME = "classfile-binaryName";
    public static final String CLASSFILE_SOURCE_FILE = "classfile-sourcefil";
    public static final String CLASSFILE_ROOT = "classfile-root";

    /**
     * Helper method to extract correct name of FileObject usefull and three
     * situations:
     *
     * 1- When class file is inside of a jar.
     *
     * 2- When class file is opened on Netbeans editor, showing javap output.
     *
     * 3- When class is on any other folder inside of project folder.
     *
     * @param file File to extract file name
     * @return
     */
    public static String extractName(FileObject file) {
        //When .class is opened on editor of netbeans.
        Object fileName = file.getAttribute(CLASSFILE_BINARY_NAME);
        if (nonNull(fileName)) {
            return String.valueOf(fileName);
        }
        return file.getPath();
    }

    private FileObject file;

    private ClassPath classpath;

    public static FileSystemHelper of(FileObject fileObject) {
        FileSystemHelper input = new FileSystemHelper();
        input.file = fileObject;
        input.classpath = ClassPath.getClassPath(fileObject, ClassPath.EXECUTE);
        return input;
    }

    private FileSystemHelper() {
    }

    public FileObject findResource(String internalName) {
        return Exceptions.wrap(ExceptionHandler::handleException).get(() -> {
            String ext = internalName.endsWith(".class") ? "" : ".class";
            FileObject fileObject = getClassFromJar(internalName + ext);
            if (isNull(fileObject)) {
                fileObject = getClassIfOpenEditor(internalName + ext);
            }

            if (isNull(fileObject) && nonNull(classpath)) {
                fileObject = classpath.findResource(internalName+ext);
            }

            return fileObject;
        });

    }

    private FileObject getClassIfOpenEditor(String internalName) throws FileStateInvalidException {
        // find jar path from attribute
        URL url = (URL) file.getAttribute(CLASSFILE_ROOT);
        if (nonNull(url)) {
            FileObject jarFile = FileUtil.toFileObject(FileUtil.archiveOrDirForURL(url));
            return FileUtil
                    .getArchiveRoot(jarFile)
                    .getFileSystem().findResource(internalName);
        }
        return null;
    }

    private FileObject getClassFromJar(String internalName) throws FileStateInvalidException {
        return file.getFileSystem().findResource(internalName);
    }
}
