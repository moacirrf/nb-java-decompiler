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

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class DecompilerFactory {

    public enum DecompilerType {
        CFR,
        FERNFLOWER
    }

    private static DecompilerType defaultType = DecompilerType.CFR;

    /**
     * Create a decompiler using the default type (CFR)
     * @return Decompiler instance
     */
    public static Decompiler create() {
        return create(defaultType);
    }

    /**
     * Create a decompiler of the specified type
     * @param type The type of decompiler to create
     * @return Decompiler instance
     */
    public static Decompiler create(DecompilerType type) {
        if (type == DecompilerType.FERNFLOWER) {
            return new io.github.moacirrf.javadecompiler.fernflower.DecompilerClassImpl();
        }
        return new io.github.moacirrf.javadecompiler.cfr.DecompilerClassImpl();
    }

    /**
     * Set the default decompiler type to use
     * @param type The default type
     */
    public static void setDefaultType(DecompilerType type) {
        defaultType = type;
    }

    /**
     * Get the current default decompiler type
     * @return The default type
     */
    public static DecompilerType getDefaultType() {
        return defaultType;
    }

    private DecompilerFactory() {
    }
}
