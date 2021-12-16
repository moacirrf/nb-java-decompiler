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

import java.io.IOException;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class LoaderImpl implements Loader {

    private final FileSystem fileSystem;

    public LoaderImpl(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public boolean canLoad(String internalName) {
        FileObject fileObject = fileSystem.findResource(internalName);
        if (fileObject == null) {
            return false;
        }
        return fileObject.canRead();
    }

    @Override
    public byte[] load(String internalName) throws LoaderException {
        try {
            return fileSystem.findResource(internalName).asBytes();
        } catch (IOException ex) {
            throw new LoaderException(ex);
        }
    }
}