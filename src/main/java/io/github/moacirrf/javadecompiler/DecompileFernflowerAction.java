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
package io.github.moacirrf.javadecompiler;

import static com.machinezoo.noexception.Exceptions.wrap;
import static java.util.Objects.nonNull;
import io.github.moacirrf.javadecompiler.files.TempDir;
import io.github.moacirrf.javadecompiler.validator.FileValidator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.netbeans.api.java.source.UiUtils;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
	category = "Tools",
	id = "io.github.moacirrf.javadecompiler.DecompileFernflowerAction"
)
@ActionRegistration(
	displayName = "#CTL_DecompileWithFernflower"
)
@ActionReferences({
    @ActionReference(path = "Editors/Popup", position = 100),
    @ActionReference(path = "UI/ToolActions", position = 100)
})
@Messages({
    "CTL_DecompileWithFernflower=Decompile with Fernflower"
})
public final class DecompileFernflowerAction implements ActionListener {

    private static final String DECOMPILER = "fernflower";

    private final DataObject context;
    private final Path decompilerDir;

    public DecompileFernflowerAction(DataObject context) {
	this.context = context;
	this.decompilerDir = Path.of(TempDir.getTempDir().toString(), DECOMPILER);
	wrap(ExceptionHandler::handleException).run(() -> {
	    if (!Files.exists(decompilerDir)) {
		Files.createDirectory(decompilerDir);
	    }
	});
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
	FileObject file = context.getPrimaryFile();
	if (FileValidator.validate(file)) {
	    Decompiler<String, FileObject> decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
	    writeToNewClass(file, decompiler.decompile(file));
	}
    }

    private void writeToNewClass(FileObject file, String decompiled) {
	if (nonNull(decompiled) && !decompiled.isEmpty()) {
	    wrap(ExceptionHandler::handleException).run(() -> {
		Path newFile = Path.of(decompilerDir.toString(), file.getName().concat(".java"));
		if (Files.exists(newFile)) {
		    newFile.toFile().setWritable(true);
		    Files.delete(newFile);
		}
		Files.write(newFile, decompiled.getBytes());
		newFile.toFile().setReadOnly();

		FileObject newFileObject = FileUtil.createData(newFile.toFile());
		newFileObject.setAttribute("disable-java-errors", true);
		UiUtils.open(newFileObject, 1);
	    });
	}
    }
}
