/*
 * Copyright (C) 2022 moacirrf
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
package com.mrf.javadecompiler.decompiler.cfr;

import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.DumperFactory;
import org.benf.cfr.reader.util.output.ExceptionDumper;
import org.benf.cfr.reader.util.output.FileSummaryDumper;
import org.benf.cfr.reader.util.output.IllegalIdentifierDump;
import org.benf.cfr.reader.util.output.MethodErrorCollector;
import org.benf.cfr.reader.util.output.NopSummaryDumper;
import org.benf.cfr.reader.util.output.ProgressDumper;
import org.benf.cfr.reader.util.output.ProgressDumperNop;
import org.benf.cfr.reader.util.output.StringStreamDumper;
import org.benf.cfr.reader.util.output.SummaryDumper;

public class PluginDumperFactory implements DumperFactory {

    private final IllegalIdentifierDump illegalIdentifierDump = new IllegalIdentifierDump.Nop();

    private final StringBuilder outBuffer;
    private final Options options;

    public PluginDumperFactory(StringBuilder out, Options options) {
        this.outBuffer = out;
        this.options = options;
    }

    @Override
    public Dumper getNewTopLevelDumper(JavaTypeInstance classType, SummaryDumper summaryDumper, TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
        return new StringStreamDumper(new MethodErrorCollector.SummaryDumperMethodErrorCollector(classType, summaryDumper), outBuffer, typeUsageInformation, options, this.illegalIdentifierDump);
    }

    @Override
    public Dumper wrapLineNoDumper(Dumper dumper) {
        return dumper;
    }

    /*
         * A summary dumper will receive errors.  Generally, it's only of value when dumping jars to file.
     */
    @Override
    public SummaryDumper getSummaryDumper() {
        if (!options.optionIsSet(OptionsImpl.OUTPUT_DIR)) {
            return new NopSummaryDumper();
        }

        return new FileSummaryDumper(options.getOption(OptionsImpl.OUTPUT_DIR), options, null);
    }

    @Override
    public ProgressDumper getProgressDumper() {
        return ProgressDumperNop.INSTANCE;
    }

    @Override
    public ExceptionDumper getExceptionDumper() {
        return null;
    }

    @Override
    public DumperFactory getFactoryWithPrefix(String prefix, int version) {
        return this;
    }
}
