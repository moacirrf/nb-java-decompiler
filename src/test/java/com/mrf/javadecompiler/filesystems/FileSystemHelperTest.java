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
package com.mrf.javadecompiler.filesystems;

import static com.mrf.javadecompiler.filesystems.FileSystemHelper.CLASSFILE_BINARY_NAME;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;

/**
 *
 * @author moacirrf
 */
public class FileSystemHelperTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_extractName_when_class_file_is_opened_on_editor() throws IOException {
        FileObject file = FileUtil.toFileObject(Files.createFile(tempDir.resolve("teste.java")).toFile());
        file.setAttribute(CLASSFILE_BINARY_NAME, "teste");

        String expResult = "teste";
        String result = FileSystemHelper.extractName(file);

        assertEquals(expResult, result);
    }

    @Test
    public void test_extractName_when_class_file_is_on_a_simple_folder() throws IOException {
        FileObject file = FileUtil.toFileObject(Files.createFile(tempDir.resolve("teste.java")).toFile());

        String expResult = tempDir.toString() + File.separator + "teste";
        String result = FileSystemHelper.extractName(file);

        assertEquals(expResult, result);
    }

    @Test
    public void test_extractName_when_class_file_is_inside_jar() throws IOException {
        Path jar = tempDir.resolve("teste.jar");
        createJarFile(jar);
        JarFileSystem jarFileSystem = new JarFileSystem(jar.toFile());
        FileObject file = jarFileSystem.findResource("teste.class");

        String expResult = "teste";
        String result = FileSystemHelper.extractName(file);

        assertEquals(expResult, result);
    }

    @Test
    public void test_findResource_inside_jar() throws IOException {
        Path jar = tempDir.resolve("teste.jar");
        createJarFile(jar);        
        FileSystemHelper instance = FileSystemHelper.of(new JarFileSystem(jar.toFile()).findResource("teste.class"));
        
        FileObject result = instance.findResource("teste.class");
        assertNotNull(result);
    }

    private void createJarFile(Path fileName) throws IOException {
        try ( JarOutputStream stream = new JarOutputStream(new FileOutputStream(Files.createFile(fileName).toFile()))) {
            stream.putNextEntry(new ZipEntry("teste.class"));
            stream.flush();
        }
    }
}
