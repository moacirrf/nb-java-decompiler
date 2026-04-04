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

import java.util.jar.Manifest;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class StringResultSaver implements IResultSaver {

    private final StringBuilder resultBuffer = new StringBuilder();

    @Override
    public void saveFolder(String path) {
        // Not used for single class decompilation
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
        // Not used for single class decompilation
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
        // Not used for single class decompilation
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {
        // Not used for single class decompilation
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
        // Not used for single class decompilation
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        resultBuffer.append(content);
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        resultBuffer.append(content);
    }

    @Override
    public void closeArchive(String path, String archiveName) {
        // Not used for single class decompilation
    }

    public String getResult() {
        return resultBuffer.toString();
    }
}
