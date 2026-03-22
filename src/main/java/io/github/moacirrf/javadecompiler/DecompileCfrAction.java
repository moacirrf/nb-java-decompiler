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
	id = "io.github.moacirrf.javadecompiler.DecompileCfrAction"
)
@ActionRegistration(
	displayName = "#CTL_DecompileWithCFR"
)
@ActionReferences({
    @ActionReference(path = "Editors/Popup", position = 200),
    @ActionReference(path = "UI/ToolActions", position = 200)
})
@Messages("CTL_DecompileWithCFR=Decompile with CFR")
public final class DecompileCfrAction implements ActionListener {

    private static final String DECOMPILER = "cfr";

    private final DataObject context;
    private final Path decompilerDir;

    public DecompileCfrAction(DataObject context) {
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
	    Decompiler<String, FileObject> decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
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
