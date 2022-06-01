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
package com.mrf.javadecompiler.decompiler.jdcore;

import com.mrf.javadecompiler.filesystems.FileSystemHelper;
import java.io.IOException;
import static java.util.Objects.nonNull;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class LoaderImpl implements Loader {

    private final FileSystemHelper input;
    
    public LoaderImpl(FileSystemHelper input) {
        this.input = input;
    }

    @Override
    public boolean canLoad(String internalName) {
        FileObject file = input.findResource(internalName);
        if (nonNull(file)) {
            return file.canRead();
        }        
        return false;
    }

    @Override
    public byte[] load(String internalName) throws LoaderException {
        try {
            FileObject file = input.findResource(internalName);
            if (nonNull(file)) {
                return file.asBytes();
            }
            return null;
        } catch (IOException ex) {
            throw new LoaderException(ex);
        }
    }
}