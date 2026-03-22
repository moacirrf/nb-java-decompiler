/*
 * Copyright (C) 2022 Moacir da Roza Flores <moacirrf@gmail.com>
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
package io.github.moacirrf.javadecompiler.fernflower;

import io.github.moacirrf.javadecompiler.files.FileSystemHelper;
import java.io.IOException;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class NetbeansBytecodeProviderImpl implements IBytecodeProvider {

    private final FileSystemHelper helper;
    private String className;

    public NetbeansBytecodeProviderImpl(FileSystemHelper helper, String className) {
	this.helper = helper;
	this.className = className;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
	String path = externalPath;

	if (path == null || path.isBlank()) {
	    path = internalPath;
	}

	FileObject fileObj = helper.findResource(className);
	if (fileObj == null) {
	    return null;
	}

	return fileObj.asBytes();
    }

}
