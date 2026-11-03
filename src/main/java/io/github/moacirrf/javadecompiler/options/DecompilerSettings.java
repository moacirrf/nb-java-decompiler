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

import java.util.prefs.Preferences;

import org.openide.util.NbPreferences;

/**
 * Stores which decompilers are shown in the decompiler menu.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerSettings {

    static final String KEY_CFR_ENABLED = "cfr.enabled";
    static final String KEY_FERNFLOWER_ENABLED = "fernflower.enabled";

    /**
     * FQCN of the injectable actions, used to find them in the Actions/Tools pool.
     */
    public static final String CFR_ACTION_ID = "io.github.moacirrf.javadecompiler.DecompileCfrAction";
    public static final String FERNFLOWER_ACTION_ID = "io.github.moacirrf.javadecompiler.DecompileFernflowerAction";

    private static final Preferences PREFERENCES = NbPreferences.forModule(DecompilerSettings.class);

    public static boolean isCfrEnabled() {
	return PREFERENCES.getBoolean(KEY_CFR_ENABLED, true);
    }

    public static boolean isFernflowerEnabled() {
	return PREFERENCES.getBoolean(KEY_FERNFLOWER_ENABLED, true);
    }

    public static void setCfrEnabled(boolean enabled) {
	PREFERENCES.putBoolean(KEY_CFR_ENABLED, enabled);
    }

    public static void setFernflowerEnabled(boolean enabled) {
	PREFERENCES.putBoolean(KEY_FERNFLOWER_ENABLED, enabled);
    }

    private DecompilerSettings() {
    }
}
