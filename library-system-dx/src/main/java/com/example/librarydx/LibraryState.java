package com.example.librarydx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** The complete in-memory state. IDs stay stable even when an item is archived. */
final class LibraryState {
    final Map<Integer, Book> books = new LinkedHashMap<>();
    final Map<Integer, Member> members = new LinkedHashMap<>();
    final Map<Integer, Loan> loans = new LinkedHashMap<>();
    final Map<Integer, Reservation> reservations = new LinkedHashMap<>();
    final Map<Integer, Event> events = new LinkedHashMap<>();
    int nextBookId = 1;
    int nextMemberId = 1;
    int nextLoanId = 1;
    int nextReservationId = 1;
    int nextEventId = 1;
    int loanDays = 14;
    int maxLoans = 3;

    record Book(int id, String title, String author, String isbn, String category,
                int year, boolean archived) {
        Book edited(String title, String author, String isbn, String category, int year) {
            return new Book(id, title, author, isbn, category, year, archived);
        }

        Book archive() {
            return new Book(id, title, author, isbn, category, year, true);
        }
    }

    record Member(int id, String name, String email, String phone, boolean inactive) {
        Member edited(String name, String email, String phone) {
            return new Member(id, name, email, phone, inactive);
        }

        Member deactivate() {
            return new Member(id, name, email, phone, true);
        }
    }

    record Loan(int id, int bookId, int memberId, LocalDate start, LocalDate due,
                LocalDate returned, int renewals) {
        boolean active() { return returned == null; }
        boolean overdue() { return active() && due.isBefore(LocalDate.now()); }
        Loan returnOn(LocalDate date) { return new Loan(id, bookId, memberId, start, due, date, renewals); }
        Loan renewTo(LocalDate date) { return new Loan(id, bookId, memberId, start, date, null, renewals + 1); }
    }

    record Reservation(int id, int bookId, int memberId, LocalDateTime created) { }
    record Event(int id, LocalDateTime time, String action, String detail) { }
}
