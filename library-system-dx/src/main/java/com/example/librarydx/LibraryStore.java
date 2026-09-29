package com.example.librarydx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

/** A single UTF-8 properties file, replaced atomically whenever possible. */
final class LibraryStore {
    private final Path file;

    LibraryStore(Path file) { this.file = file; }

    boolean exists() { return Files.exists(file); }

    LibraryState load() throws IOException {
        LibraryState state = new LibraryState();
        if (!exists()) return state;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) { p.load(in); }
        try {
            if (!"1".equals(p.getProperty("format"))) throw new IllegalArgumentException("保存形式が未対応です");
            state.nextBookId = number(p, "next.book");
            state.nextMemberId = number(p, "next.member");
            state.nextLoanId = number(p, "next.loan");
            state.nextReservationId = number(p, "next.reservation");
            state.nextEventId = number(p, "next.event");
            state.loanDays = number(p, "setting.loanDays");
            state.maxLoans = number(p, "setting.maxLoans");
            for (int i = 0; i < number(p, "books.count"); i++) {
                String k = "book." + i + ".";
                var b = new LibraryState.Book(number(p, k + "id"), value(p, k + "title"),
                        value(p, k + "author"), value(p, k + "isbn"), value(p, k + "category"),
                        number(p, k + "year"), Boolean.parseBoolean(value(p, k + "archived")));
                state.books.put(b.id(), b);
            }
            for (int i = 0; i < number(p, "members.count"); i++) {
                String k = "member." + i + ".";
                var m = new LibraryState.Member(number(p, k + "id"), value(p, k + "name"),
                        value(p, k + "email"), value(p, k + "phone"),
                        Boolean.parseBoolean(value(p, k + "inactive")));
                state.members.put(m.id(), m);
            }
            for (int i = 0; i < number(p, "loans.count"); i++) {
                String k = "loan." + i + ".";
                String returned = value(p, k + "returned");
                var l = new LibraryState.Loan(number(p, k + "id"), number(p, k + "book"),
                        number(p, k + "member"), LocalDate.parse(value(p, k + "start")),
                        LocalDate.parse(value(p, k + "due")),
                        returned.isEmpty() ? null : LocalDate.parse(returned), number(p, k + "renewals"));
                state.loans.put(l.id(), l);
            }
            for (int i = 0; i < number(p, "reservations.count"); i++) {
                String k = "reservation." + i + ".";
                var r = new LibraryState.Reservation(number(p, k + "id"), number(p, k + "book"),
                        number(p, k + "member"), LocalDateTime.parse(value(p, k + "created")));
                state.reservations.put(r.id(), r);
            }
            for (int i = 0; i < number(p, "events.count"); i++) {
                String k = "event." + i + ".";
                var e = new LibraryState.Event(number(p, k + "id"),
                        LocalDateTime.parse(value(p, k + "time")), value(p, k + "action"),
                        value(p, k + "detail"));
                state.events.put(e.id(), e);
            }
            if (state.loanDays < 1 || state.maxLoans < 1) throw new IllegalArgumentException("設定値が不正です");
        } catch (IllegalArgumentException ex) {
            throw new IOException("保存データを読めません: " + ex.getMessage(), ex);
        }
        return state;
    }

    void save(LibraryState s) throws IOException {
        Properties p = new Properties();
        p.setProperty("format", "1");
        put(p, "next.book", s.nextBookId);
        put(p, "next.member", s.nextMemberId);
        put(p, "next.loan", s.nextLoanId);
        put(p, "next.reservation", s.nextReservationId);
        put(p, "next.event", s.nextEventId);
        put(p, "setting.loanDays", s.loanDays);
        put(p, "setting.maxLoans", s.maxLoans);
        put(p, "books.count", s.books.size());
        int i = 0;
        for (var b : s.books.values()) {
            String k = "book." + i++ + ".";
            put(p, k + "id", b.id());
            p.setProperty(k + "title", b.title());
            p.setProperty(k + "author", b.author());
            p.setProperty(k + "isbn", b.isbn());
            p.setProperty(k + "category", b.category());
            put(p, k + "year", b.year());
            put(p, k + "archived", b.archived());
        }
        put(p, "members.count", s.members.size());
        i = 0;
        for (var m : s.members.values()) {
            String k = "member." + i++ + ".";
            put(p, k + "id", m.id());
            p.setProperty(k + "name", m.name());
            p.setProperty(k + "email", m.email());
            p.setProperty(k + "phone", m.phone());
            put(p, k + "inactive", m.inactive());
        }
        put(p, "loans.count", s.loans.size());
        i = 0;
        for (var l : s.loans.values()) {
            String k = "loan." + i++ + ".";
            put(p, k + "id", l.id());
            put(p, k + "book", l.bookId());
            put(p, k + "member", l.memberId());
            p.setProperty(k + "start", l.start().toString());
            p.setProperty(k + "due", l.due().toString());
            p.setProperty(k + "returned", l.returned() == null ? "" : l.returned().toString());
            put(p, k + "renewals", l.renewals());
        }
        put(p, "reservations.count", s.reservations.size());
        i = 0;
        for (var r : s.reservations.values()) {
            String k = "reservation." + i++ + ".";
            put(p, k + "id", r.id());
            put(p, k + "book", r.bookId());
            put(p, k + "member", r.memberId());
            p.setProperty(k + "created", r.created().toString());
        }
        put(p, "events.count", s.events.size());
        i = 0;
        for (var e : s.events.values()) {
            String k = "event." + i++ + ".";
            put(p, k + "id", e.id());
            p.setProperty(k + "time", e.time().toString());
            p.setProperty(k + "action", e.action());
            p.setProperty(k + "detail", e.detail());
        }
        Files.createDirectories(file.toAbsolutePath().getParent());
        Path temp = Files.createTempFile(file.toAbsolutePath().getParent(), "library-", ".tmp");
        try {
            try (OutputStream out = Files.newOutputStream(temp)) { p.store(out, "Library System DX"); }
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static int number(Properties p, String key) { return Integer.parseInt(value(p, key)); }
    private static String value(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) throw new IllegalArgumentException("項目がありません: " + key);
        return v;
    }
    private static void put(Properties p, String key, Object value) { p.setProperty(key, value.toString()); }
}
