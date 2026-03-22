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
package io.github.moacirrf.javadecompiler.fernflower;

import io.github.moacirrf.javadecompiler.Decompiler;
import static com.machinezoo.noexception.Exceptions.wrap;
import io.github.moacirrf.javadecompiler.ExceptionHandler;
import io.github.moacirrf.javadecompiler.files.FileSystemHelper;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerClassImpl implements Decompiler<String, FileObject> {
    private static final String VERSION = "pycharm~252.27397.106";
    public static final String HEADER_COMMENT = "// Source code recreated by Apache Netbeans (NB Java Decompiler) \n"
	    + "/*\n"
	    + " * Decompiled with Fernflower " + VERSION + ".\n"
	    + " */\n";

    private final Map<String, Object> options;

    public DecompilerClassImpl() {
	options = new HashMap<>();
	options.put("rbr", "1");
	options.put("rsy", "1");
	options.put("din", "1");
	options.put("dc4", "1");
	options.put("das", "1");
	options.put("hes", "1");
	options.put("hdc", "1");
	options.put("dgs", "1");
	options.put("rer", "1");
	options.put("den", "1");
	options.put("udv", "1");
	options.put("ump", "1");
	options.put("ner", "1");
	options.put("ind", "    ");
	options.put("nls", "1");
	options.put("log", "WARN");
    }

    @Override
    public String decompile(FileObject file) {
	return wrap(ExceptionHandler::handleException).get(() -> decompileInternal(file));
    }

    private String decompileInternal(FileObject file) throws Exception {
	String className = FileSystemHelper.extractName(file);
	FileSystemHelper helper = FileSystemHelper.of(file);

	IFernflowerLogger logger = new PrintStreamLogger(System.err);
	IBytecodeProvider provider = new NetbeansBytecodeProviderImpl(helper, className);
	IResultSaver saver = new StringResultSaver();

	BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
	decompiler.addSource(new java.io.File(className));
	decompiler.decompileContext();

	String result = ((StringResultSaver) saver).getResult();
	return HEADER_COMMENT + (result != null ? result : "");
    }

}
