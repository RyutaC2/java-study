package com.example.librarydx;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.ansi.ANSITerminal;
import org.jline.terminal.Attributes;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Connects Lanterna's renderer to JLine's native Windows/Unix console handling. */
final class JLineTerminal extends ANSITerminal {
    private final org.jline.terminal.Terminal nativeTerminal;
    private final Attributes originalAttributes;

    static JLineTerminal open() throws IOException {
        org.jline.terminal.Terminal terminal = TerminalBuilder.builder()
                .system(true).encoding(StandardCharsets.UTF_8).build();
        var size = terminal.getSize();
        if (terminal.getType().startsWith("dumb") || size.getColumns() < 1 || size.getRows() < 1) {
            terminal.close();
            throw new UnsupportedOperationException("全画面操作に対応した端末がありません");
        }
        try {
            return new JLineTerminal(terminal);
        } catch (RuntimeException ex) {
            terminal.close();
            throw ex;
        }
    }

    private JLineTerminal(org.jline.terminal.Terminal terminal) {
        super(terminal.input(), terminal.output(), StandardCharsets.UTF_8);
        this.nativeTerminal = terminal;
        this.originalAttributes = terminal.enterRawMode();
    }

    @Override
    protected TerminalSize findTerminalSize() {
        var size = nativeTerminal.getSize();
        return new TerminalSize(Math.max(1, size.getColumns()), Math.max(1, size.getRows()));
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            nativeTerminal.setAttributes(originalAttributes);
            nativeTerminal.close();
        }
    }
}
