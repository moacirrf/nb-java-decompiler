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
import static com.mrf.javadecompiler.filesystems.TempDir.getTempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.apiunreleased.ClassFileSource2;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.AnalysisType;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.DumperFactory;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerClassImpl implements Decompiler<String, FileObject> {

    public static final String HEADER_COMMENT = "// Source code recreated by Apache Netbeans\n";

    private final Options options;
    private final ClassFileSource2 classFileSource;
    private final DCCommonState dcCommonState;

    public DecompilerClassImpl() {
        options = new OptionsImpl(Map.of("comments", "false"));
        classFileSource = new ClassFileSourceImpl(options);
        classFileSource.informAnalysisRelativePathDetail(null, null);
        dcCommonState = new DCCommonState(options, classFileSource);
    }

    @Override
    public String decompile(FileObject file) {
        return wrap(ExceptionHandler::handleException).get(() -> {

            FileSystemHelper helper = FileSystemHelper.of(file);
            FileObject fileObject = helper.findResource(FileSystemHelper.extractName(file));

            //copy class file to temp before decompile
            Path classFile = getTempDir().resolve(fileObject.getName() + fileObject.getExt());
            Files.write(classFile, fileObject.asBytes());

            String decompiledClass = HEADER_COMMENT + decompile(classFile.toString());
            
            //remove class file after decompile
            Files.delete(classFile);

            return decompiledClass;
        });
    }

    private String decompile(String classPath) {

        StringBuilder out = new StringBuilder();

        DumperFactory dumperFactory = new PluginDumperFactory(out, options);

        AnalysisType type = options.getOption(OptionsImpl.ANALYSE_AS);

        if (type == null || type == AnalysisType.DETECT) {
            type = dcCommonState.detectClsJar(classPath);
        }

        if (type == AnalysisType.CLASS) {
            Main.doClass(dcCommonState, classPath, false, dumperFactory);
        }
        return out.toString();
    }

}
