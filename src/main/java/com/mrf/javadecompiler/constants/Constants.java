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
package com.mrf.javadecompiler.constants;

import static java.lang.System.getProperty;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class Constants {

    public static final String HEADER_COMMENT = "//\n"
            + "// Source code recreated by Apache Netbeans\n"
            + "// (powered by Java Decompiler http://java-decompiler.github.io )\n"
            + "//\n";

    public static final String TEMP_DIR_PLUGIN = getProperty("java.io.tmpdir") + "/nb_java_decompiler";
    public static final String CLASSFILE_BINARY_NAME = "classfile-binaryName";
    public static final String CLASSFILE_ROOT = "classfile-root";
    public static final String CLASS_EXT = ".class";
    public static final String JAR = "jar:";
    public static final String FILE = "file:";
    public static final String EXCLAM = "!";

    private Constants() {
    }
}
