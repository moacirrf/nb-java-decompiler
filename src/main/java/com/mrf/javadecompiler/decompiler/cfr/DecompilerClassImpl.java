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
package com.mrf.javadecompiler.decompiler.cfr;

import com.mrf.javadecompiler.decompiler.Decompiler;
import static com.machinezoo.noexception.Exceptions.wrap;
import com.mrf.javadecompiler.exception.ExceptionHandler;
import com.mrf.javadecompiler.filesystems.FileSystemHelper;
import java.util.Map;
import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.apiunreleased.ClassFileSource2;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerClassImpl implements Decompiler<String, FileObject> {

    public static final String HEADER_COMMENT = "// Source code recreated by Apache Netbeans (NB Java Decompiler) \n";

    private final Options options;

    public DecompilerClassImpl() {
        options = new OptionsImpl(Map.of("comments", "false", "innerclasses", "true"));
    }

    @Override
    public String decompile(FileObject file) {
        return wrap(ExceptionHandler::handleException).get(() -> {

            String className = FileSystemHelper.extractName(file);
            FileSystemHelper helper = FileSystemHelper.of(file);

            ClassFileSource2 classFileSource = new NetbeansClassFileSourceImpl(helper);

            StringBuilder out = new StringBuilder(HEADER_COMMENT);
            Main.doClass(new DCCommonState(options, classFileSource), className, false, new PluginDumperFactory(out, options));

            return out.toString();
        });
    }

}
