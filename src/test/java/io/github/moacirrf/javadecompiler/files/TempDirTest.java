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

import static io.github.moacirrf.javadecompiler.files.TempDir.getTempDir;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.Files.createFile;
import java.nio.file.Path;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author moacirrf
 */
public class TempDirTest {

    @Test
    public void testGetTempDir() {
        Path tempDir = getTempDir();
        assertTrue(Files.exists(tempDir));
    }

    @Test
    public void testRemoveTempDir() throws IOException {
        Path tempDir = getTempDir();
        Path testClass = tempDir.resolve("Test.class");
        if(!Files.exists(testClass)){
            createFile(testClass);
        }
        TempDir.removeTempDir();
        assertFalse(Files.exists(tempDir));
    }

}
