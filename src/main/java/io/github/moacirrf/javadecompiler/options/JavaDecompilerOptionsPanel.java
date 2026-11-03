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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openide.util.NbBundle;

/**
 * Tool > Options > Miscellaneous > Java Decompiler panel.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class JavaDecompilerOptionsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel descriptionLabel = new JLabel();
    private final JCheckBox cfrCheckBox = new JCheckBox();
    private final JCheckBox fernflowerCheckBox = new JCheckBox();
    private final JLabel hintLabel = new JLabel();

    /**
     * Called whenever the user toggles a checkbox, to mark the panel as changed.
     */
    private Runnable modificationCallback;

    /**
     * True while reloading values from the current options, to avoid reporting
     * programmatic updates as user changes.
     */
    private boolean loading;

    public JavaDecompilerOptionsPanel() {
	descriptionLabel.setText(text("Options_JavaDecompiler_Description"));
	cfrCheckBox.setText(text("Options_JavaDecompiler_CFR"));
	fernflowerCheckBox.setText(text("Options_JavaDecompiler_Fernflower"));
	hintLabel.setText(text("Options_JavaDecompiler_Hint"));

	setLayout(new GridBagLayout());

	add(descriptionLabel, constraints(0, 0, 2, GridBagConstraints.WEST, new Insets(12, 12, 4, 12)));
	add(cfrCheckBox, constraints(0, 1, 2, GridBagConstraints.WEST, new Insets(4, 24, 0, 12)));
	add(fernflowerCheckBox, constraints(0, 2, 2, GridBagConstraints.WEST, new Insets(4, 24, 0, 12)));
	add(hintLabel, constraints(0, 3, 2, GridBagConstraints.WEST, new Insets(10, 12, 12, 12)));

	JLabel filler = new JLabel();
	GridBagConstraints fillerConstraints = constraints(0, 4, 2, GridBagConstraints.NORTHWEST, new Insets(0, 0, 0, 0));
	fillerConstraints.fill = GridBagConstraints.HORIZONTAL;
	fillerConstraints.weighty = 1d;
	add(filler, fillerConstraints);

	// At least one decompiler must remain selected.
	ItemListener guard = event -> {
	    if (event.getStateChange() == ItemEvent.DESELECTED
		    && !cfrCheckBox.isSelected() && !fernflowerCheckBox.isSelected()) {
		((JCheckBox) event.getItemSelectable()).setSelected(true);
	    }
	    if (modificationCallback != null && !loading) {
		modificationCallback.run();
	    }
	};
	cfrCheckBox.addItemListener(guard);
	fernflowerCheckBox.addItemListener(guard);

	load();
    }

    void setModificationCallback(Runnable modificationCallback) {
	this.modificationCallback = modificationCallback;
    }

    void load() {
	loading = true;
	try {
	    cfrCheckBox.setSelected(DecompilerSettings.isCfrEnabled());
	    fernflowerCheckBox.setSelected(DecompilerSettings.isFernflowerEnabled());
	} finally {
	    loading = false;
	}
    }

    void store() {
	DecompilerSettings.setCfrEnabled(cfrCheckBox.isSelected());
	DecompilerSettings.setFernflowerEnabled(fernflowerCheckBox.isSelected());
    }

    private static String text(String key) {
	return NbBundle.getMessage(JavaDecompilerOptionsPanel.class, key);
    }

    private static GridBagConstraints constraints(int gridx, int gridy, int gridwidth, int anchor, Insets insets) {
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = gridx;
	c.gridy = gridy;
	c.gridwidth = gridwidth;
	c.anchor = anchor;
	c.insets = insets;
	c.weightx = 1d;
	return c;
    }
}
