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
package com.mrf.javadecompiler.openide.action;

import static com.machinezoo.noexception.Exceptions.wrap;
import static java.util.Objects.nonNull;
import com.mrf.javadecompiler.decompiler.Decompiler;
import com.mrf.javadecompiler.decompiler.DecompilerFactory;
import com.mrf.javadecompiler.exception.ExceptionHandler;
import com.mrf.javadecompiler.filesystems.TempDir;
import com.mrf.javadecompiler.validator.FileValidator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "com.mrf.javadecompiler.openapi.DecompileAction"
)
@ActionRegistration(
        iconBase = "com/mrf/javadecompiler/openapi/jd_icon_16.png",
        displayName = "#CTL_DecompileAction"
)
@ActionReferences(value = {
    @ActionReference(path = "Editors/Popup", position = 1425),
    @ActionReference(path = "UI/ToolActions", position = 2950)
})
@Messages("CTL_DecompileAction=Decompile")
public final class DecompileAction implements ActionListener {

    private final DataObject context;
    private final Path decompilerDir;

    public DecompileAction(DataObject context) {
        this.context = context;
        this.decompilerDir = TempDir.getTempDir();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileObject file = context.getPrimaryFile();
        if (FileValidator.validate(file)) {
            Decompiler<String, FileObject> decompiler = DecompilerFactory.create();
            writeToNewClass(file, decompiler.decompile(file));
        }
    }

    private void writeToNewClass(FileObject file, String decompiled) {
        if (nonNull(decompiled) && !decompiled.isEmpty()) {
            wrap(ExceptionHandler::handleException).run(() -> {
                Path newFile = Path.of(decompilerDir.toString(), file.getName().concat(".java"));
                if (Files.exists(newFile)) {
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
