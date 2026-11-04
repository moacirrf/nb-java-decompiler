/*
 * Copyright (C) 2026 Moacir da Roza Flores <moacirrf@gmail.com>
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
package io.github.moacirrf.javadecompiler.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * Controls the JavaDecompiler panel in Tools > Options > Miscellaneous.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
@OptionsPanelController.SubRegistration(
	id = "JavaDecompiler",
	displayName = "#Options_JavaDecompiler_Tab"
)
public final class JavaDecompilerOptionsPanelController extends OptionsPanelController {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private JavaDecompilerOptionsPanel panel;
    private boolean changed;

    @Override
    public void update() {
	changed = false;
	if (panel != null) {
	    panel.load();
	}
    }

    @Override
    public void applyChanges() {
	if (panel != null) {
	    panel.store();
	}
	changed = false;
	support.firePropertyChange(PROP_CHANGED, false, true);
    }

    @Override
    public void cancel() {
	if (panel != null) {
	    panel.load();
	}
	changed = false;
    }

    @Override
    public boolean isValid() {
	// At least one decompiler is always enforced by the panel checkboxes.
	return true;
    }

    @Override
    public boolean isChanged() {
	return changed;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
	panel = new JavaDecompilerOptionsPanel();
	panel.setModificationCallback(() -> {
	    changed = true;
	    support.firePropertyChange(PROP_CHANGED, false, true);
	});
	return panel;
    }

    @Override
    public HelpCtx getHelpCtx() {
	return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
	support.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
	support.removePropertyChangeListener(l);
    }
}
