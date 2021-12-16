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
package com.mrf.javadecompiler.decompiler;

import static com.machinezoo.noexception.Exceptions.wrap;
import static com.mrf.javadecompiler.constants.Constants.CLASSFILE_BINARY_NAME;
import static com.mrf.javadecompiler.constants.Constants.CLASS_EXT;
import static com.mrf.javadecompiler.exception.ExceptionHandler.handleException;
import static java.io.File.separatorChar;
import static java.util.Objects.nonNull;
import com.mrf.javadecompiler.builder.FileSystemBuilder;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class DecompilerClassImpl implements Decompiler<String> {

    @Override
    public String decompile(FileObject file) {
        return wrap(e -> handleException(e)).get(() -> {
            LoaderImpl loader = new LoaderImpl(FileSystemBuilder.build(file));
            PrinterImpl printer = new PrinterImpl();
            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(loader, printer, getFileName(file));
            return printer.toString();
        });
    }

    private String getFileName(FileObject file) {
        Object fileName = file.getAttribute(CLASSFILE_BINARY_NAME);
        if (nonNull(fileName)) {
            return String.valueOf(fileName) + CLASS_EXT;
        }
        return file.getParent().getPath() + separatorChar + file.getName() + CLASS_EXT;
    }
}
