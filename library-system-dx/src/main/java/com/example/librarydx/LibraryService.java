package com.example.librarydx;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Business rules. UI code never changes the state directly. */
final class LibraryService {
    private final LibraryStore store;
    private final LibraryState state;

    LibraryService(LibraryStore store) throws IOException {
        this.store = store;
        this.state = store.load();
        if (!store.exists()) seedDemo();
    }

    LibraryState state() { return state; }

    List<LibraryState.Book> books() {
        return state.books.values().stream().filter(b -> !b.archived()).toList();
    }

    List<LibraryState.Member> members() {
        return state.members.values().stream().filter(m -> !m.inactive()).toList();
    }

    List<LibraryState.Loan> loans() {
        return state.loans.values().stream().sorted(Comparator
                .comparing((LibraryState.Loan l) -> !l.active())
                .thenComparing(LibraryState.Loan::due)).toList();
    }

    List<LibraryState.Reservation> reservations() {
        return state.reservations.values().stream()
                .sorted(Comparator.comparing(LibraryState.Reservation::created)).toList();
    }

    List<LibraryState.Event> events() {
        List<LibraryState.Event> result = new ArrayList<>(state.events.values());
        result.sort(Comparator.comparing(LibraryState.Event::id).reversed());
        return result;
    }

    String bookTitle(int id) {
        var book = state.books.get(id);
        return book == null ? "不明な本 #" + id : book.title();
    }

    String memberName(int id) {
        var member = state.members.get(id);
        return member == null ? "不明な利用者 #" + id : member.name();
    }

    LibraryState.Loan activeLoanFor(int bookId) {
        return state.loans.values().stream().filter(l -> l.bookId() == bookId && l.active())
                .findFirst().orElse(null);
    }

    long activeLoanCount(int memberId) {
        return state.loans.values().stream().filter(l -> l.memberId() == memberId && l.active()).count();
    }

    long overdueCount() { return state.loans.values().stream().filter(LibraryState.Loan::overdue).count(); }

    String bookStatus(int id) {
        LibraryState.Loan loan = activeLoanFor(id);
        if (loan == null) return "貸出可能";
        return loan.overdue() ? "延滞中" : "貸出中";
    }

    LibraryState.Book addBook(String title, String author, String isbn, String category, int year) throws IOException {
        validateBook(title, author, isbn, year, -1);
        int id = state.nextBookId++;
        var book = new LibraryState.Book(id, title.trim(), author.trim(), isbn.trim(),
                category.isBlank() ? "未分類" : category.trim(), year, false);
        state.books.put(id, book);
        commit("本を登録", "#" + id + " " + book.title());
        return book;
    }

    void editBook(int id, String title, String author, String isbn, String category, int year) throws IOException {
        LibraryState.Book old = activeBook(id);
        validateBook(title, author, isbn, year, id);
        state.books.put(id, old.edited(title.trim(), author.trim(), isbn.trim(),
                category.isBlank() ? "未分類" : category.trim(), year));
        commit("本を編集", "#" + id + " " + title.trim());
    }

    void archiveBook(int id) throws IOException {
        LibraryState.Book book = activeBook(id);
        require(activeLoanFor(id) == null, "貸出中の本は保管できません");
        require(state.reservations.values().stream().noneMatch(r -> r.bookId() == id), "予約中の本は保管できません");
        state.books.put(id, book.archive());
        commit("本を保管", "#" + id + " " + book.title());
    }

    LibraryState.Member addMember(String name, String email, String phone) throws IOException {
        validateMember(name, email);
        int id = state.nextMemberId++;
        var member = new LibraryState.Member(id, name.trim(), email.trim(), phone.trim(), false);
        state.members.put(id, member);
        commit("利用者を登録", "#" + id + " " + member.name());
        return member;
    }

    void editMember(int id, String name, String email, String phone) throws IOException {
        LibraryState.Member old = activeMember(id);
        validateMember(name, email);
        state.members.put(id, old.edited(name.trim(), email.trim(), phone.trim()));
        commit("利用者を編集", "#" + id + " " + name.trim());
    }

    void deactivateMember(int id) throws IOException {
        LibraryState.Member member = activeMember(id);
        require(activeLoanCount(id) == 0, "貸出中の利用者は無効化できません");
        require(state.reservations.values().stream().noneMatch(r -> r.memberId() == id), "予約中の利用者は無効化できません");
        state.members.put(id, member.deactivate());
        commit("利用者を無効化", "#" + id + " " + member.name());
    }

    LibraryState.Loan checkout(int bookId, int memberId) throws IOException {
        LibraryState.Book book = activeBook(bookId);
        LibraryState.Member member = activeMember(memberId);
        require(activeLoanFor(bookId) == null, "この本は貸出中です");
        require(activeLoanCount(memberId) < state.maxLoans, "貸出上限（" + state.maxLoans + "冊）に達しています");
        LibraryState.Reservation first = reservations().stream().filter(r -> r.bookId() == bookId)
                .findFirst().orElse(null);
        require(first == null || first.memberId() == memberId, "予約待ちの先頭は別の利用者です");
        if (first != null) state.reservations.remove(first.id());
        LocalDate today = LocalDate.now();
        int id = state.nextLoanId++;
        var loan = new LibraryState.Loan(id, bookId, memberId, today,
                today.plusDays(state.loanDays), null, 0);
        state.loans.put(id, loan);
        commit("貸出", "#" + bookId + " " + book.title() + " → " + member.name());
        return loan;
    }

    void returnBook(int loanId) throws IOException {
        LibraryState.Loan loan = activeLoan(loanId);
        state.loans.put(loanId, loan.returnOn(LocalDate.now()));
        commit("返却", "#" + loan.bookId() + " " + bookTitle(loan.bookId())
                + " ← " + memberName(loan.memberId()));
    }

    LibraryState.Loan renew(int loanId) throws IOException {
        LibraryState.Loan loan = activeLoan(loanId);
        require(!loan.overdue(), "延滞中の貸出は延長できません");
        require(loan.renewals() < 2, "延長は2回までです");
        require(state.reservations.values().stream().noneMatch(r -> r.bookId() == loan.bookId()),
                "予約待ちがあるため延長できません");
        LibraryState.Loan updated = loan.renewTo(loan.due().plusDays(state.loanDays));
        state.loans.put(loanId, updated);
        commit("期限を延長", "貸出 #" + loanId + " → " + updated.due());
        return updated;
    }

    LibraryState.Reservation reserve(int bookId, int memberId) throws IOException {
        LibraryState.Book book = activeBook(bookId);
        LibraryState.Member member = activeMember(memberId);
        LibraryState.Loan current = activeLoanFor(bookId);
        require(current != null, "貸出可能な本です。直接貸し出してください");
        require(current.memberId() != memberId, "自分が借りている本は予約できません");
        require(state.reservations.values().stream().noneMatch(r -> r.bookId() == bookId && r.memberId() == memberId),
                "この利用者は既に予約しています");
        require(state.reservations.values().stream().filter(r -> r.memberId() == memberId).count() < 5,
                "予約は1人5件までです");
        int id = state.nextReservationId++;
        var reservation = new LibraryState.Reservation(id, bookId, memberId, LocalDateTime.now());
        state.reservations.put(id, reservation);
        commit("予約", "#" + bookId + " " + book.title() + " → " + member.name());
        return reservation;
    }

    void cancelReservation(int id) throws IOException {
        LibraryState.Reservation reservation = state.reservations.remove(id);
        require(reservation != null, "予約が見つかりません");
        commit("予約取消", "#" + reservation.bookId() + " " + bookTitle(reservation.bookId()));
    }

    void updateSettings(int loanDays, int maxLoans) throws IOException {
        require(loanDays >= 1 && loanDays <= 90, "貸出日数は1〜90日にしてください");
        require(maxLoans >= 1 && maxLoans <= 20, "貸出上限は1〜20冊にしてください");
        state.loanDays = loanDays;
        state.maxLoans = maxLoans;
        commit("設定変更", "貸出 " + loanDays + "日 / 上限 " + maxLoans + "冊");
    }

    private LibraryState.Book activeBook(int id) {
        LibraryState.Book b = state.books.get(id);
        require(b != null && !b.archived(), "本が見つかりません");
        return b;
    }

    private LibraryState.Member activeMember(int id) {
        LibraryState.Member m = state.members.get(id);
        require(m != null && !m.inactive(), "利用者が見つかりません");
        return m;
    }

    private LibraryState.Loan activeLoan(int id) {
        LibraryState.Loan l = state.loans.get(id);
        require(l != null && l.active(), "貸出中の記録が見つかりません");
        return l;
    }

    private void validateBook(String title, String author, String isbn, int year, int excludeId) {
        require(title != null && !title.isBlank(), "タイトルを入力してください");
        require(author != null && !author.isBlank(), "著者名を入力してください");
        require(year >= 0 && year <= 2100, "出版年は0〜2100で入力してください");
        if (isbn != null && !isbn.isBlank()) {
            require(state.books.values().stream().noneMatch(b -> b.id() != excludeId && b.isbn().equals(isbn.trim())),
                    "ISBNが重複しています");
        }
    }

    private void validateMember(String name, String email) {
        require(name != null && !name.isBlank(), "利用者名を入力してください");
        require(email == null || email.isBlank() || (email.contains("@") && !email.contains(" ")),
                "メールアドレスの形式を確認してください");
    }

    private void commit(String action, String detail) throws IOException {
        int id = state.nextEventId++;
        state.events.put(id, new LibraryState.Event(id, LocalDateTime.now(), action, detail));
        if (state.events.size() > 250) state.events.remove(state.events.keySet().iterator().next());
        store.save(state);
    }

    private void seedDemo() throws IOException {
        String[][] titles = {
                {"Java入門", "山田太郎", "技術"}, {"Java実践パターン", "佐藤花子", "技術"},
                {"アルゴリズム図鑑", "鈴木次郎", "技術"}, {"宇宙を読む", "高橋美咲", "科学"},
                {"街の記憶", "中村葵", "小説"}, {"データベース設計", "伊藤陽介", "技術"},
                {"日本の植物", "小林優", "自然"}, {"星の航路", "加藤玲奈", "小説"}
        };
        for (String[] row : titles) {
            int id = state.nextBookId++;
            state.books.put(id, new LibraryState.Book(id, row[0], row[1], "", row[2], 2024, false));
        }
        String[] names = {"田中一郎", "佐藤明美", "山本健", "木村由香"};
        for (String name : names) {
            int id = state.nextMemberId++;
            state.members.put(id, new LibraryState.Member(id, name, "", "", false));
        }
        LocalDate today = LocalDate.now();
        state.loans.put(1, new LibraryState.Loan(state.nextLoanId++, 1, 1,
                today.minusDays(21), today.minusDays(7), null, 0));
        state.loans.put(2, new LibraryState.Loan(state.nextLoanId++, 2, 2,
                today.minusDays(4), today.plusDays(10), null, 0));
        state.reservations.put(1, new LibraryState.Reservation(state.nextReservationId++, 1, 3,
                LocalDateTime.now().minusDays(2)));
        commit("初期データ", "本8冊・利用者4名・貸出2件・予約1件を作成");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
