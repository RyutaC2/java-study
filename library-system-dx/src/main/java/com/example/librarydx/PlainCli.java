package com.example.librarydx;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** A usable command mode for terminals that cannot provide raw keyboard input. */
final class PlainCli {
    private enum Page { HOME, BOOKS, MEMBERS, LOANS, RESERVATIONS, REPORTS, HISTORY, HELP }
    private static final String CYAN = "\u001b[38;2;72;214;202m";
    private static final String PURPLE = "\u001b[38;2;179;150;246m";
    private static final String AMBER = "\u001b[38;2;248;204;118m";
    private static final String RED = "\u001b[38;2;255;132;142m";
    private static final String DIM = "\u001b[38;2;148;166;185m";
    private static final String RESET = "\u001b[0m";
    private static final String RULE = "-".repeat(76);
    private static final int PAGE_SIZE = 4;
    private final LibraryService service;
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
    private final boolean color = System.getenv("NO_COLOR") == null;
    private Page page = Page.HOME;
    private int pageIndex = 0;
    private String notice = "番号で画面を選択できます。help で操作一覧を表示します。";
    private String filter = "";

    PlainCli(LibraryService service) { this.service = service; }

    void loop() throws IOException {
        while (true) {
            render();
            String command;
            try { command = prompt("DX").trim().toLowerCase(Locale.ROOT); }
            catch (EOFException ex) { break; }
            if (command.equals("q") || command.equals("quit") || command.equals("exit") || command.equals("0")) break;
            try { execute(command); }
            catch (EOFException ex) { break; }
            catch (IllegalArgumentException | IOException ex) { notice = "エラー: " + ex.getMessage(); }
        }
        System.out.println("\nLibrary System DX を終了しました。");
    }

    private void execute(String command) throws IOException {
        switch (command) {
            case "h", "help", "?", "8" -> go(Page.HELP);
            case "1", "home" -> go(Page.HOME);
            case "2", "books" -> go(Page.BOOKS);
            case "3", "members" -> go(Page.MEMBERS);
            case "4", "loans" -> go(Page.LOANS);
            case "5", "reservations" -> go(Page.RESERVATIONS);
            case "6", "reports" -> go(Page.REPORTS);
            case "7", "history" -> go(Page.HISTORY);
            case "next", "more" -> pageIndex++;
            case "prev", "back" -> pageIndex = Math.max(0, pageIndex - 1);
            case "search", "/" -> { filter = prompt("検索キーワード").trim(); pageIndex = 0; notice = "検索条件を更新しました"; }
            case "clear" -> { filter = ""; pageIndex = 0; notice = "検索条件を解除しました"; }
            case "add-book" -> addBook();
            case "edit-book" -> editBook();
            case "archive-book" -> archiveBook();
            case "add-member" -> addMember();
            case "edit-member" -> editMember();
            case "deactivate-member" -> deactivateMember();
            case "checkout" -> checkout();
            case "return" -> returnBook();
            case "renew" -> renew();
            case "reserve" -> reserve();
            case "cancel-reservation" -> cancelReservation();
            case "settings" -> settings();
            default -> notice = "エラー: 不明なコマンドです。help で一覧を表示します。";
        }
    }

    private void go(Page target) {
        page = target;
        pageIndex = 0;
        filter = "";
        notice = target == Page.HOME ? "番号で画面を選択できます。" : target.name() + " を表示しています。";
    }

    private void render() {
        if (color) System.out.print("\u001b[2J\u001b[H");
        else System.out.println("\n".repeat(2));
        System.out.println(tint("+" + "-".repeat(74) + "+", CYAN));
        System.out.println("  " + tint("LIBRARY SYSTEM", CYAN) + "  /  DX"
                + "                                      " + tint(LocalDate.now().toString(), DIM));
        System.out.println("  図書館運営コンソール  |  LOCAL DATA  |  JAVA 17");
        System.out.println(tint(RULE, DIM));
        System.out.println("  " + nav("01 概要", Page.HOME) + "    " + nav("02 蔵書", Page.BOOKS)
                + "    " + nav("03 利用者", Page.MEMBERS) + "    " + nav("04 貸出", Page.LOANS));
        System.out.println("  " + nav("05 予約", Page.RESERVATIONS) + "    " + nav("06 分析", Page.REPORTS)
                + "    " + nav("07 履歴", Page.HISTORY) + "    " + nav("08 ヘルプ", Page.HELP));
        System.out.println(tint(RULE, DIM));
        switch (page) {
            case HOME -> home();
            case BOOKS -> books();
            case MEMBERS -> members();
            case LOANS -> loans();
            case RESERVATIONS -> reservations();
            case REPORTS -> reports();
            case HISTORY -> history();
            case HELP -> help();
        }
        System.out.println();
        System.out.println(tint(RULE, DIM));
        System.out.println("  " + tint(notice, notice.startsWith("エラー") ? RED : CYAN));
        System.out.println("  " + tint("操作: ", PURPLE) + actions() + "    " + tint("0", AMBER) + " 終了");
        System.out.println(tint(RULE, DIM));
    }

    private String nav(String label, Page target) { return tint(label, page == target ? CYAN : DIM); }
    private String tint(String value, String code) { return color ? code + value + RESET : value; }

    private String actions() {
        return switch (page) {
            case BOOKS -> "add-book / edit-book / archive-book / search / next";
            case MEMBERS -> "add-member / edit-member / search / next";
            case LOANS -> "checkout / return / renew / search / next";
            case RESERVATIONS -> "reserve / cancel-reservation / search / next";
            default -> "番号で画面切替 / help / settings";
        };
    }

    private void help() {
        section("GUIDE", "操作ガイド");
        System.out.println("  1-8                  画面を切り替える");
        System.out.println("  search / clear       一覧を絞り込む / 解除する");
        System.out.println("  next / prev          一覧の次・前ページ");
        System.out.println("  add-book             本を登録する");
        System.out.println("  edit-book            本を編集する");
        System.out.println("  archive-book         本を保管する");
        System.out.println("  add-member           利用者を登録する");
        System.out.println("  edit-member          利用者を編集する");
        System.out.println("  deactivate-member    利用者を無効化する");
        System.out.println("  checkout / return    貸し出す / 返却する");
        System.out.println("  renew / reserve      期限を延長する / 予約する");
        System.out.println("  cancel-reservation   予約を取り消す");
        System.out.println("  settings / quit      設定 / 終了");
    }

    private void home() {
        section("OVERVIEW", "運営ダッシュボード");
        long active = service.loans().stream().filter(LibraryState.Loan::active).count();
        int total = service.books().size();
        metric("BOOKS", total, "AVAILABLE", total - (int) active);
        metric("ON LOAN", active, "OVERDUE", service.overdueCount());
        System.out.println();
        System.out.println("  " + tint("要対応", PURPLE) + "  /  延滞中の貸出");
        var overdue = service.loans().stream().filter(LibraryState.Loan::overdue).limit(4).toList();
        if (overdue.isEmpty()) System.out.println("  " + tint("良好  対応が必要な貸出はありません", CYAN));
        else overdue.forEach(l -> System.out.println("  " + tint("!", RED) + "  "
                + shortText(service.bookTitle(l.bookId()), 24) + "  /  "
                + service.memberName(l.memberId()) + "  /  " + l.due()));
        System.out.println();
        System.out.println("  予約待ち " + service.reservations().size() + "件   |   貸出 "
                + service.state().loanDays + "日   |   上限 " + service.state().maxLoans + "冊");
    }

    private void books() {
        section("COLLECTION", "蔵書一覧");
        var list = service.books().stream().filter(b -> matches(b.title(), b.author(), b.category(), b.isbn(),
                service.bookStatus(b.id()))).toList();
        count(list.size());
        list.stream().skip((long) pageIndex * PAGE_SIZE).limit(PAGE_SIZE).forEach(b -> {
            String state = service.bookStatus(b.id());
            System.out.println("  " + tint(String.format("#%03d", b.id()), CYAN) + "  "
                    + tint("[" + state + "]", state.equals("延滞中") ? RED : state.equals("貸出可能") ? CYAN : AMBER)
                    + "  " + shortText(b.title(), 28));
            System.out.println("        " + tint(shortText(b.author(), 20) + "  /  " + b.category(), DIM));
        });
        more(list.size());
    }

    private void members() {
        section("MEMBERS", "利用者一覧");
        var list = service.members().stream().filter(m -> matches(m.name(), m.email(), m.phone())).toList();
        count(list.size());
        list.stream().skip((long) pageIndex * PAGE_SIZE).limit(PAGE_SIZE).forEach(m -> {
            System.out.println("  " + tint(String.format("#%03d", m.id()), CYAN) + "  " + m.name()
                    + "  " + tint("貸出 " + service.activeLoanCount(m.id()) + "/" + service.state().maxLoans + "冊", AMBER));
            System.out.println("        " + tint(m.email().isBlank() ? "連絡先未登録" : m.email(), DIM));
        });
        more(list.size());
    }

    private void loans() {
        section("CIRCULATION", "貸出一覧");
        var list = service.loans().stream().filter(l -> matches(service.bookTitle(l.bookId()),
                service.memberName(l.memberId()), l.due().toString())).toList();
        count(list.size());
        list.stream().skip((long) pageIndex * PAGE_SIZE).limit(PAGE_SIZE).forEach(l -> {
            String status = l.active() ? l.overdue() ? "延滞" : "貸出中" : "返却済";
            System.out.println("  " + tint(String.format("#%03d", l.id()), CYAN) + "  "
                    + tint("[" + status + "]", l.overdue() ? RED : l.active() ? AMBER : DIM)
                    + "  " + shortText(service.bookTitle(l.bookId()), 28));
            System.out.println("        " + tint(service.memberName(l.memberId()) + "  /  期限 " + l.due(), DIM));
        });
        more(list.size());
    }

    private void reservations() {
        section("QUEUE", "予約待ち");
        var list = service.reservations().stream().filter(r -> matches(service.bookTitle(r.bookId()),
                service.memberName(r.memberId()))).toList();
        count(list.size());
        list.stream().skip((long) pageIndex * PAGE_SIZE).limit(PAGE_SIZE).forEach(r -> {
            System.out.println("  " + tint(String.format("#%03d", r.id()), CYAN) + "  "
                    + shortText(service.bookTitle(r.bookId()), 28));
            System.out.println("        " + tint(service.memberName(r.memberId()) + "  /  受付 " + r.created().toLocalDate(), DIM));
        });
        more(list.size());
    }

    private void reports() {
        section("ANALYTICS", "利用状況と分類");
        long active = service.loans().stream().filter(LibraryState.Loan::active).count();
        int total = service.books().size();
        System.out.println("  稼働率  " + bar(active, total, 30) + "  "
                + (total == 0 ? 0 : active * 100 / total) + "%");
        System.out.println("  蔵書 " + total + "冊  /  利用者 " + service.members().size()
                + "人  /  累計貸出 " + service.state().loans.size() + "件");
        System.out.println();
        System.out.println("  " + tint("CATEGORY MIX", PURPLE));
        Map<String, Integer> counts = new HashMap<>();
        service.books().forEach(b -> counts.merge(b.category(), 1, Integer::sum));
        counts.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.println("  " + shortText(e.getKey(), 12) + "  "
                        + tint(bar(e.getValue(), Math.max(1, total), 22), CYAN) + "  " + e.getValue() + "冊"));
    }

    private void history() {
        section("ACTIVITY", "最近の操作履歴");
        service.events().stream().limit(12).forEach(e -> System.out.println("  "
                + tint(e.time().toLocalDate().toString(), DIM) + "  "
                + tint(shortText(e.action(), 12), CYAN) + "  " + shortText(e.detail(), 35)));
    }

    private void section(String english, String japanese) {
        System.out.println();
        System.out.println("  " + tint(english, PURPLE) + "  /  " + japanese);
        System.out.println("  " + tint("-".repeat(72), DIM));
    }

    private void metric(String leftLabel, long leftValue, String rightLabel, long rightValue) {
        System.out.println("  " + tint(String.format("%-15s", leftLabel), DIM)
                + tint(String.format("%02d", leftValue), CYAN) + "       "
                + tint(String.format("%-15s", rightLabel), DIM)
                + tint(String.format("%02d", rightValue), rightLabel.equals("OVERDUE") ? RED : CYAN));
    }

    private void count(int value) {
        if (value == 0) pageIndex = 0;
        else pageIndex = Math.min(pageIndex, (value - 1) / PAGE_SIZE);
        int first = value == 0 ? 0 : pageIndex * PAGE_SIZE + 1;
        int last = Math.min(value, (pageIndex + 1) * PAGE_SIZE);
        System.out.println("  " + tint("表示 " + first + "-" + last + " / " + value + "件", AMBER)
                + (filter.isBlank() ? "" : "   /   検索: " + filter));
        System.out.println();
    }

    private void more(int total) {
        if (total == 0) System.out.println("  該当する項目はありません。");
        else if (total > PAGE_SIZE) System.out.println("  " + tint("next / prev でページを移動", DIM));
    }

    private boolean matches(String... values) {
        if (filter.isBlank()) return true;
        for (String value : values) if (value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) return true;
        return false;
    }

    private static String shortText(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit - 1) + "…";
    }

    private static String bar(long value, long total, int width) {
        int filled = total == 0 ? 0 : (int) Math.min(width, Math.round((double) value / total * width));
        return "#".repeat(filled) + ".".repeat(width - filled);
    }

    private void addBook() throws IOException {
        var book = service.addBook(prompt("タイトル"), prompt("著者名"), prompt("ISBN（任意）"),
                prompt("分類（任意）"), number(prompt("出版年（不明なら0）"), "出版年"));
        go(Page.BOOKS);
        notice = "本 #" + book.id() + " を登録しました。";
    }

    private void editBook() throws IOException {
        int id = number(prompt("本ID"), "本ID");
        var b = service.state().books.get(id);
        if (b == null || b.archived()) throw new IllegalArgumentException("本が見つかりません");
        service.editBook(id, defaultValue("タイトル", b.title()), defaultValue("著者名", b.author()),
                defaultValue("ISBN", b.isbn()), defaultValue("分類", b.category()),
                number(defaultValue("出版年", Integer.toString(b.year())), "出版年"));
        go(Page.BOOKS);
        notice = "本を更新しました。";
    }

    private void archiveBook() throws IOException {
        int id = number(prompt("本ID"), "本ID");
        if (yes()) { service.archiveBook(id); go(Page.BOOKS); notice = "本を保管しました。"; }
    }

    private void addMember() throws IOException {
        var member = service.addMember(prompt("氏名"), prompt("メール（任意）"), prompt("電話番号（任意）"));
        go(Page.MEMBERS);
        notice = "利用者 #" + member.id() + " を登録しました。";
    }

    private void editMember() throws IOException {
        int id = number(prompt("利用者ID"), "利用者ID");
        var m = service.state().members.get(id);
        if (m == null || m.inactive()) throw new IllegalArgumentException("利用者が見つかりません");
        service.editMember(id, defaultValue("氏名", m.name()), defaultValue("メール", m.email()),
                defaultValue("電話番号", m.phone()));
        go(Page.MEMBERS);
        notice = "利用者を更新しました。";
    }

    private void deactivateMember() throws IOException {
        int id = number(prompt("利用者ID"), "利用者ID");
        if (yes()) { service.deactivateMember(id); go(Page.MEMBERS); notice = "利用者を無効化しました。"; }
    }

    private void checkout() throws IOException {
        int bookId = number(prompt("本ID"), "本ID");
        int memberId = number(prompt("利用者ID"), "利用者ID");
        var loan = service.checkout(bookId, memberId);
        go(Page.LOANS);
        notice = "貸出 #" + loan.id() + " 完了 / 返却期限 " + loan.due();
    }

    private void returnBook() throws IOException {
        int id = number(prompt("貸出ID"), "貸出ID");
        service.returnBook(id);
        go(Page.LOANS);
        notice = "返却しました。";
    }

    private void renew() throws IOException {
        int id = number(prompt("貸出ID"), "貸出ID");
        var loan = service.renew(id);
        go(Page.LOANS);
        notice = "新しい返却期限: " + loan.due();
    }

    private void reserve() throws IOException {
        int bookId = number(prompt("本ID"), "本ID");
        int memberId = number(prompt("利用者ID"), "利用者ID");
        var reservation = service.reserve(bookId, memberId);
        go(Page.RESERVATIONS);
        notice = "予約 #" + reservation.id() + " を登録しました。";
    }

    private void cancelReservation() throws IOException {
        int id = number(prompt("予約ID"), "予約ID");
        if (yes()) { service.cancelReservation(id); go(Page.RESERVATIONS); notice = "予約を取り消しました。"; }
    }

    private void settings() throws IOException {
        var s = service.state();
        System.out.printf("現在: 貸出 %d日 / 上限 %d冊%n", s.loanDays, s.maxLoans);
        int days = number(defaultValue("貸出日数", Integer.toString(s.loanDays)), "貸出日数");
        int max = number(defaultValue("上限冊数", Integer.toString(s.maxLoans)), "上限冊数");
        service.updateSettings(days, max);
        notice = "設定を保存しました。";
    }

    private boolean yes() throws IOException { return prompt("実行しますか？ y/N").equalsIgnoreCase("y"); }

    private String defaultValue(String label, String previous) throws IOException {
        String value = prompt(label + " [" + previous + "]（空欄で維持、-で消去）");
        return value.isEmpty() ? previous : value.equals("-") ? "" : value;
    }

    private String prompt(String label) throws IOException {
        System.out.print(label + "> ");
        System.out.flush();
        String value = input.readLine();
        if (value == null) throw new EOFException();
        return value;
    }

    private static int number(String input, String label) {
        try { return Integer.parseInt(input.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(label + "は数字で入力してください"); }
    }
}
