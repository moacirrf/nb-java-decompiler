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
package io.github.moacirrf.javadecompiler.files;

import static io.github.moacirrf.javadecompiler.files.FileSystemHelper.CLASSFILE_BINARY_NAME;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author moacirrf
 */
public class FileSystemHelperTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void test_extractName_when_class_file_is_opened_on_editor() throws IOException {
        Path tempDir = tempFolder.newFolder("temp").toPath();

        FileObject file = FileUtil.toFileObject(Files.createFile(tempDir.resolve("test.java")).toFile());
        file.setAttribute(CLASSFILE_BINARY_NAME, "test");

        String expResult = "test";
        String result = FileSystemHelper.extractName(file);

        assertEquals(expResult, result);
    }

    @Test
    public void test_extractName_when_class_file_is_on_a_simple_folder() throws IOException {
        Path tempDir = tempFolder.newFolder("temp").toPath();
        FileObject file = FileUtil.toFileObject(Files.createFile(tempDir.resolve("test.class")).toFile());

        String expResult = FileUtil.toFileObject(new File(tempDir.toFile() + File.separator + "test.class")).getPath();
        String result = FileSystemHelper.extractName(file);

        assertEquals(expResult, result);
    }

    @Test
    public void test_extractName_when_class_file_is_inside_jar() throws IOException {
        Path tempDir = tempFolder.newFolder("temp").toPath();
        Path jar = tempDir.resolve("test.jar");
        createJarFile(jar);

        FileObject fileObject = FileUtil.getArchiveRoot(FileUtil.toFileObject(jar.toFile()));
        FileObject file = fileObject.getFileSystem().findResource("test.class");

        String expResult = "test.class";
        String result = FileSystemHelper.extractName(file);
        assertEquals(expResult, result);

    }

    @Test
    public void test_findResource_inside_jar() throws IOException {
        Path tempDir = tempFolder.newFolder("temp").toPath();
        Path jar = tempDir.resolve("test.jar");
        createJarFile(jar);

        FileObject fileObject = FileUtil.getArchiveRoot(FileUtil.toFileObject(jar.toFile()));
        FileObject file = fileObject.getFileSystem().findResource("test.class");

        FileSystemHelper instance = FileSystemHelper.of(file);

        FileObject result = instance.findResource("test.class");
        assertNotNull(result);
    }

    private static void createJarFile(Path fileName) throws IOException {
        if (!Files.exists(fileName)) {
            File file = Files.createFile(fileName).toFile();
            try ( JarOutputStream stream = new JarOutputStream(new FileOutputStream(file))) {
                stream.putNextEntry(new ZipEntry("test.class"));
                stream.finish();
            }
        }
    }
}
