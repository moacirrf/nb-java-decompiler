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
package com.mrf.javadecompiler.decompiler;

import org.jd.core.v1.api.printer.Printer;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class PrinterImpl implements Printer {

    private static final String TAB = "  ";
    private static final String NEWLINE = "\n";
    private int indentationCount = 0;
    private final StringBuilder sb = new StringBuilder();

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        // Not used
    }

    @Override
    public void end() {
        // Not used
    }

    @Override
    public void printText(String text) {
        sb.append(text);
    }

    @Override
    public void printNumericConstant(String constant) {
        sb.append(constant);
    }

    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
        sb.append(constant);
    }

    @Override
    public void printKeyword(String keyword) {
        sb.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        sb.append(name);
    }

    @Override
    public void printReference(
      int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        sb.append(name);
    }

    @Override
    public void indent() {
        this.indentationCount++;
    }

    @Override
    public void unindent() {
        this.indentationCount--;
    }

    @Override
    public void startLine(int lineNumber) {
        for (int i = 0; i < indentationCount; i++) {
            sb.append(TAB);
        }
    }

    @Override
    public void endLine() {
        sb.append(NEWLINE);
    }

    @Override
    public void extraLine(int count) {
        while (count-- > 0) {
            sb.append(NEWLINE);
        }
    }

    @Override
    public void startMarker(int type) {
        // Not used
    }

    @Override
    public void endMarker(int type) {
        // Not used
    }
}
