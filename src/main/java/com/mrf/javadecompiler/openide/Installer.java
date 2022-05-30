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
package com.mrf.javadecompiler.openide;

import com.machinezoo.noexception.Exceptions;
import com.mrf.javadecompiler.constants.Constants;
import com.mrf.javadecompiler.exception.ExceptionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public boolean closing() {
        this.clearTempFolder(Path.of(Constants.TEMP_DIR_PLUGIN));
        return super.closing();
    }
    /**
     * Will remove recursivelly all decompiled classes, when close Netbeans.
     * 
     * @param path 
     */
    private void clearTempFolder(Path path) {
        Exceptions.wrap(ex -> ExceptionHandler.handleException(ex)).run(() -> {
            if (Files.isDirectory(path) && Files.list(path).count() > 0) {
                Files.list(path).forEach(it -> this.clearTempFolder(it));
            }
            Files.deleteIfExists(path);
        });
    }

}
