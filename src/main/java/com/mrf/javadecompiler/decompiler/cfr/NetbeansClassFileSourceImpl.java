/*
 * Copyright (C) 2022 moacirrf
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

import com.mrf.javadecompiler.filesystems.FileSystemHelper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.isNull;
import org.benf.cfr.reader.apiunreleased.ClassFileSource2;
import org.benf.cfr.reader.apiunreleased.JarContent;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.util.AnalysisType;
import org.openide.filesystems.FileObject;

/**
 *
 * @author moacirrf
 */
public class NetbeansClassFileSourceImpl implements ClassFileSource2 {

    private final FileSystemHelper helper;

    public NetbeansClassFileSourceImpl(FileSystemHelper helper) {
        this.helper = helper;
    }

    @Override
    public JarContent addJarContent(String jarPath, AnalysisType analysisType) {
        //we dont decompile complete jars
        return null;
    }

    @Override
    public void informAnalysisRelativePathDetail(String usePath, String classFilePath) {
        // not used
    }

    @Override
    public Collection<String> addJar(String jarPath) {
         // not used
        return List.of();
    }

    @Override
    public String getPossiblyRenamedPath(String path) {
        return path;
    }

    @Override
    public Pair<byte[], String> getClassFileContent(String path) throws IOException {
        FileObject fileObj = helper.findResource(path);
        if (isNull(fileObj)) {
            return null;
        }

        return Pair.make(fileObj.asBytes(), path);
    }

}
