/*
 * Copyright (C) 2021 moacirrf
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
package com.mrf.javadecompiler.openapi.action;

import static java.util.Objects.nonNull;
import com.mrf.javadecompiler.builder.SourceWindowBuilder;
import com.mrf.javadecompiler.decompiler.Decompiler;
import com.mrf.javadecompiler.factory.DecompilerFactory;
import com.mrf.javadecompiler.validator.FileValidator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

@ActionID(
  category = "Tools",
  id = "com.mrf.javadecompiler.openapi.DecompileAction"
)
@ActionRegistration(
  iconBase = "com/mrf/javadecompiler/openapi/jd_icon_16.png",
  displayName = "#CTL_DecompileAction"
)
//@ActionReference(path = "Editors/text/x-java/Popup", position = 400)
//@ActionReferences(value = {
//    @ActionReference(path = "Editors/Popup", position = 2950),
//    @ActionReference(path = "UI/ToolActions", position = 2950)
//})
@ActionReferences(value = {
    @ActionReference(path = "Editors/Popup", position = 4000),
    @ActionReference(path = "UI/ToolActions", position = 2950)
})
@Messages("CTL_DecompileAction=Decompile")
public final class DecompileAction implements ActionListener {

    private final DataObject context;

    public DecompileAction(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileObject file = context.getPrimaryFile();
        if (FileValidator.validate(file)) {
            Decompiler<String> decompiler = DecompilerFactory.create();
            String decompiled = decompiler.decompile(file);

            if (nonNull(decompiled) && !decompiled.isEmpty()) {
                SourceWindowBuilder.build(file, decompiled).open();
            }
        }
    }
}
