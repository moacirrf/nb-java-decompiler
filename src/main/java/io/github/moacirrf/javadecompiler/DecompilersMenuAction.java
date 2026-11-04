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
package io.github.moacirrf.javadecompiler;

import io.github.moacirrf.javadecompiler.options.DecompilerSettings;
import io.github.moacirrf.javadecompiler.validator.FileValidator;

import java.awt.event.ActionEvent;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * Builds the "Decompiler" submenu with the decompilers enabled in
 * Tools > Options > Miscellaneous > Java Decompiler.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
@ActionID(
	category = "Tools",
	id = "io.github.moacirrf.javadecompiler.DecompilersMenuAction"
)
@ActionRegistration(
	displayName = "#CTL_DecompilersMenu",
	lazy = false,
	iconInMenu = false
)
@ActionReferences({
    @ActionReference(path = "Editors/Popup", position = 10),
    @ActionReference(path = "UI/ToolActions/Decompiler", position = 10)
})
public final class DecompilersMenuAction extends AbstractAction implements ContextAwareAction, Presenter.Popup {

    private static final long serialVersionUID = 1L;

    private static final String ACTIONS_POOL = "Actions/Tools";

    /**
     * Lookup bound to this instance, or null to use the global context computed
     * whenever the submenu is opened.
     */
    private final Lookup boundContext;

    public DecompilersMenuAction() {
	this(null);
    }

    private DecompilersMenuAction(Lookup boundContext) {
	this.boundContext = boundContext;
	putValue(Action.NAME, NbBundle.getMessage(DecompilersMenuAction.class, "CTL_DecompilersMenu"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	// Nothing to do: the submenu items do the decompilation.
    }

    /**
     * The submenu is listed on the Tools popup only when at least one
     * enabled decompiler is available in the context.
     */
    @Override
    public boolean isEnabled() {
	return !buildSubmenuItems(getLookup()).isEmpty();
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
	return new DecompilersMenuAction(actionContext);
    }

    @Override
    public JMenuItem getPopupPresenter() {
	if (boundContext != null) {
	    return new DecompilersMenu(() -> boundContext);
	}
	return new DecompilersMenu(Utilities::actionsGlobalContext);
    }

    private Lookup getLookup() {
	return boundContext != null ? boundContext : Utilities.actionsGlobalContext();
    }

    /**
     * Builds the submenu items of the decompilers enabled in the options,
     * considering only the actions enabled in the given lookup context. The
     * submenu is empty (and hidden) when the context is not a decompilable
     * file: only class files, and java files generated on the netbeans gensrc
     * cache folder, are accepted.
     */
    static List<JMenuItem> buildSubmenuItems(Lookup lookup) {
	List<JMenuItem> items = new ArrayList<>();
	if (isNull(lookup) || !isDecompilableContext(lookup)) {
	    return items;
	}
	buildItem(items, lookup, DecompilerSettings.isCfrEnabled(), DecompilerSettings.CFR_ACTION_ID);
	buildItem(items, lookup, DecompilerSettings.isFernflowerEnabled(), DecompilerSettings.FERNFLOWER_ACTION_ID);
	return items;
    }

    private static boolean isDecompilableContext(Lookup lookup) {
	DataObject dataObject = lookup.lookup(DataObject.class);
	return nonNull(dataObject) && FileValidator.isDecompilable(dataObject.getPrimaryFile());
    }

    private static void buildItem(List<JMenuItem> items, Lookup lookup, boolean enabled, String actionId) {
	if (!enabled) {
	    return;
	}
	String instancePath = ACTIONS_POOL.concat("/").concat(actionId.replace('.', '-')).concat(".instance");
	Action action = FileUtil.getConfigObject(instancePath, Action.class);
	if (action == null) {
	    return;
	}
	Action contextAction = action;
	if (lookup != null && action instanceof ContextAwareAction) {
	    contextAction = ((ContextAwareAction) action).createContextAwareInstance(lookup);
	}
	if (contextAction.isEnabled()) {
	    JMenuItem item = new JMenuItem();
	    Actions.connect(item, contextAction, true);
	    items.add(item);
	}
    }

    /**
     * Rebuilds its children every time its own popup menu becomes visible,
     * so the menu always reflects the current options and context. When there
     * are no items to show the menu hides itself from the parent popup.
     */
    private static final class DecompilersMenu extends JMenu implements DynamicMenuContent, PopupMenuListener {

	private static final long serialVersionUID = 1L;

	private final Supplier<Lookup> lookupSupplier;

	private DecompilersMenu(Supplier<Lookup> lookupSupplier) {
	    super(NbBundle.getMessage(DecompilersMenuAction.class, "CTL_DecompilersMenu"));
	    this.lookupSupplier = lookupSupplier;
	    getPopupMenu().addPopupMenuListener(this);
	}

	@Override
	public JComponent[] getMenuPresenters() {
	    return synchMenuPresenters(new JComponent[0]);
	}

	@Override
	public JComponent[] synchMenuPresenters(JComponent[] items) {
	    removeAll();
	    Lookup lookup = lookupSupplier.get();
	    if (nonNull(lookup)) {
		buildSubmenuItems(lookup).forEach(this::add);
	    }
	    return getMenuComponentCount() == 0 ? new JComponent[0] : new JComponent[]{this};
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	    synchMenuPresenters(new JComponent[0]);
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	    // Nothing to do
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	    // Nothing to do
	}
    }
}
