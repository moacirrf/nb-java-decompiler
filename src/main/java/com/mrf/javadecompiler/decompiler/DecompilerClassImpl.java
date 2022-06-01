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

import com.mrf.javadecompiler.decompiler.jdcore.PrinterImpl;
import com.mrf.javadecompiler.decompiler.jdcore.LoaderImpl;
import static com.machinezoo.noexception.Exceptions.wrap;
import com.mrf.javadecompiler.exception.ExceptionHandler;
import com.mrf.javadecompiler.filesystems.FileSystemHelper;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerClassImpl implements Decompiler<String, FileObject> {

    public static final String HEADER_COMMENT = "//\n"
            + "// Source code recreated by Apache Netbeans\n"
            + "// (powered by Java Decompiler http://java-decompiler.github.io )\n"
            + "//\n";

    @Override
    public String decompile(FileObject file) {
        return wrap(ExceptionHandler::handleException).get(() -> {
            LoaderImpl loader = new LoaderImpl(FileSystemHelper.of(file));
            PrinterImpl printer = new PrinterImpl();

            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            decompiler.decompile(loader, printer, FileSystemHelper.extractName(file));

            return HEADER_COMMENT + printer.toString();
        });
    }

}
