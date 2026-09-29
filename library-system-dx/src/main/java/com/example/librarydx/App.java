package com.example.librarydx;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Full-screen, keyboard-driven terminal interface. */
public final class App {
    private static final TextColor BG = new TextColor.RGB(11, 18, 29);
    private static final TextColor PANEL = new TextColor.RGB(22, 34, 51);
    private static final TextColor SURFACE = new TextColor.RGB(17, 28, 43);
    private static final TextColor SURFACE_ALT = new TextColor.RGB(20, 32, 49);
    private static final TextColor SELECT = new TextColor.RGB(32, 68, 83);
    private static final TextColor WHITE = new TextColor.RGB(231, 239, 247);
    private static final TextColor CYAN = new TextColor.RGB(72, 214, 202);
    private static final TextColor MAGENTA = new TextColor.RGB(179, 150, 246);
    private static final TextColor YELLOW = new TextColor.RGB(248, 204, 118);
    private static final TextColor GREEN = new TextColor.RGB(126, 218, 164);
    private static final TextColor RED = new TextColor.RGB(255, 132, 142);
    private static final TextColor GRAY = new TextColor.RGB(148, 166, 185);
    private static final int SIDE = 22;
    private static final DateTimeFormatter SHORT_TIME = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    private enum View {
        HOME("概要"), BOOKS("本"), MEMBERS("利用者"), LOANS("貸出"),
        RESERVATIONS("予約"), REPORTS("分析"), EVENTS("履歴"), HELP("ヘルプ");
        final String label;
        View(String label) { this.label = label; }
    }

    private record Row(Object value, String... cells) { }
    @FunctionalInterface private interface Action { void run() throws IOException; }

    private final Screen screen;
    private final LibraryService service;
    private View view = View.HOME;
    private int selected = 0;
    private int scroll = 0;
    private String query = "";
    private String notice = "ようこそ。← → で画面を切り替えられます。";
    private boolean running = true;

    private App(Screen screen, LibraryService service) {
        this.screen = screen;
        this.service = service;
    }

    public static void main(String[] args) {
        if (args.length > 1 || args.length == 1 && !"--plain".equals(args[0])) {
            System.out.println("使い方: java -jar target/library-system-dx-1.0.0.jar [--plain]");
            return;
        }
        Screen screen = null;
        JLineTerminal terminal = null;
        try {
            LibraryService service = new LibraryService(new LibraryStore(Path.of("data", "library.properties")));
            if (args.length == 1) {
                new PlainCli(service).loop();
                return;
            }
            try {
                terminal = JLineTerminal.open();
            } catch (UnsupportedOperationException ex) {
                System.out.println(ex.getMessage() + "。行入力モードを起動します。");
                new PlainCli(service).loop();
                return;
            }
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            new App(screen, service).loop();
        } catch (IOException | RuntimeException ex) {
            System.err.println("Library System DX を起動できません: " + ex.getMessage());
        } finally {
            if (screen != null) {
                try { screen.stopScreen(); } catch (IOException ignored) { }
            }
            if (terminal != null) {
                try { terminal.close(); } catch (IOException ignored) { }
            }
        }
    }

    private void loop() throws IOException {
        while (running) {
            render();
            KeyStroke key = screen.readInput();
            if (key != null) handle(key);
        }
    }

    private void handle(KeyStroke key) throws IOException {
        KeyType type = key.getKeyType();
        TerminalSize size = screen.getTerminalSize();
        if (size.getColumns() < 78 || size.getRows() < 24) {
            if (type == KeyType.Character && Character.toLowerCase(key.getCharacter()) == 'q') running = false;
            return;
        }
        if (type == KeyType.ArrowLeft) { switchView(-1); return; }
        if (type == KeyType.ArrowRight) { switchView(1); return; }
        if (type == KeyType.ArrowUp) { move(-1); return; }
        if (type == KeyType.ArrowDown) { move(1); return; }
        if (type == KeyType.PageUp) { move(-visibleRows()); return; }
        if (type == KeyType.PageDown) { move(visibleRows()); return; }
        if (type == KeyType.Enter) { showDetails(); return; }
        if (type == KeyType.Escape) {
            if (!query.isEmpty()) { query = ""; selected = scroll = 0; }
            else setView(View.HOME);
            return;
        }
        if (type != KeyType.Character) return;
        char c = Character.toLowerCase(key.getCharacter());
        if (c >= '1' && c <= '8') { setView(View.values()[c - '1']); return; }
        switch (c) {
            case 'q' -> running = false;
            case '?' -> setView(View.HELP);
            case '/' -> search();
            case 's' -> settings();
            case 'n' -> add();
            case 'e' -> editOrRenew();
            case 'd' -> archiveOrCancel();
            case 'r' -> returnSelected();
            case 'l' -> checkout();
            default -> { }
        }
    }

    private void switchView(int direction) {
        int next = Math.floorMod(view.ordinal() + direction, View.values().length);
        setView(View.values()[next]);
    }

    private void setView(View next) {
        view = next;
        selected = scroll = 0;
        query = "";
        notice = next.label + "を表示しています";
    }

    private void move(int delta) {
        List<Row> rows = rows();
        if (rows.isEmpty()) return;
        selected = Math.max(0, Math.min(rows.size() - 1, selected + delta));
        if (selected < scroll) scroll = selected;
        if (selected >= scroll + visibleRows()) scroll = selected - visibleRows() + 1;
    }

    private int visibleRows() { return Math.max(1, screen.getTerminalSize().getRows() - 17); }

    private List<Row> rows() {
        List<Row> result = new ArrayList<>();
        switch (view) {
            case BOOKS -> {
                for (var b : service.books()) result.add(new Row(b, "#" + b.id(), b.title(),
                        b.author(), b.category(), service.bookStatus(b.id())));
            }
            case MEMBERS -> {
                for (var m : service.members()) result.add(new Row(m, "#" + m.id(), m.name(),
                        m.email().isBlank() ? "—" : m.email(),
                        service.activeLoanCount(m.id()) + "/" + service.state().maxLoans + "冊"));
            }
            case LOANS -> {
                for (var l : service.loans()) result.add(new Row(l, "#" + l.id(),
                        service.bookTitle(l.bookId()), service.memberName(l.memberId()),
                        l.due().toString(), l.active() ? l.overdue() ? "延滞" : "貸出中" : "返却済"));
            }
            case RESERVATIONS -> {
                for (var r : service.reservations()) result.add(new Row(r, "#" + r.id(),
                        service.bookTitle(r.bookId()), service.memberName(r.memberId()),
                        r.created().format(SHORT_TIME)));
            }
            case EVENTS -> {
                for (var e : service.events()) result.add(new Row(e, "#" + e.id(),
                        e.time().format(SHORT_TIME), e.action(), e.detail()));
            }
            default -> { }
        }
        if (query.isBlank()) return result;
        String lower = query.toLowerCase();
        return result.stream().filter(row -> String.join(" ", row.cells()).toLowerCase().contains(lower)).toList();
    }

    private Object selectedValue() {
        List<Row> items = rows();
        if (items.isEmpty()) { notice = "対象がありません"; return null; }
        selected = Math.min(selected, items.size() - 1);
        return items.get(selected).value();
    }

    private void search() throws IOException {
        if (!isList()) { notice = "検索は一覧画面で使用できます"; return; }
        String[] result = form("絞り込み", new String[]{"キーワード（空欄ですべて）"}, new String[]{query});
        if (result != null) { query = result[0].trim(); selected = scroll = 0; notice = "検索: " + (query.isBlank() ? "すべて" : query); }
    }

    private boolean isList() {
        return view == View.BOOKS || view == View.MEMBERS || view == View.LOANS
                || view == View.RESERVATIONS || view == View.EVENTS;
    }

    private void add() throws IOException {
        switch (view) {
            case BOOKS -> {
                String[] v = form("本を登録", new String[]{"タイトル *", "著者名 *", "ISBN", "分類", "出版年（0=不明）"},
                        new String[]{"", "", "", "", "0"});
                if (v != null) act(() -> {
                    var b = service.addBook(v[0], v[1], v[2], v[3], number(v[4], "出版年"));
                    notice = "本 #" + b.id() + " を登録しました";
                });
            }
            case MEMBERS -> {
                String[] v = form("利用者を登録", new String[]{"氏名 *", "メール", "電話番号"}, new String[]{"", "", ""});
                if (v != null) act(() -> {
                    var m = service.addMember(v[0], v[1], v[2]);
                    notice = "利用者 #" + m.id() + " を登録しました";
                });
            }
            case LOANS -> checkout();
            case RESERVATIONS -> reserve();
            default -> notice = "この画面では追加操作はありません";
        }
    }

    private void editOrRenew() throws IOException {
        Object item = selectedValue();
        if (item == null) return;
        if (item instanceof LibraryState.Book b) {
            String[] v = form("本 #" + b.id() + " を編集",
                    new String[]{"タイトル *", "著者名 *", "ISBN", "分類", "出版年"},
                    new String[]{b.title(), b.author(), b.isbn(), b.category(), Integer.toString(b.year())});
            if (v != null) act(() -> { service.editBook(b.id(), v[0], v[1], v[2], v[3], number(v[4], "出版年")); notice = "本を更新しました"; });
        } else if (item instanceof LibraryState.Member m) {
            String[] v = form("利用者 #" + m.id() + " を編集",
                    new String[]{"氏名 *", "メール", "電話番号"}, new String[]{m.name(), m.email(), m.phone()});
            if (v != null) act(() -> { service.editMember(m.id(), v[0], v[1], v[2]); notice = "利用者を更新しました"; });
        } else if (item instanceof LibraryState.Loan l) {
            if (confirm("貸出 #" + l.id() + " の期限を延長しますか？"))
                act(() -> { var updated = service.renew(l.id()); notice = "新しい返却期限: " + updated.due(); });
        }
    }

    private void archiveOrCancel() throws IOException {
        Object item = selectedValue();
        if (item == null) return;
        if (item instanceof LibraryState.Book b && confirm("本 #" + b.id() + " を保管しますか？"))
            act(() -> { service.archiveBook(b.id()); notice = "本を保管しました"; });
        else if (item instanceof LibraryState.Member m && confirm("利用者 #" + m.id() + " を無効化しますか？"))
            act(() -> { service.deactivateMember(m.id()); notice = "利用者を無効化しました"; });
        else if (item instanceof LibraryState.Reservation r && confirm("予約 #" + r.id() + " を取り消しますか？"))
            act(() -> { service.cancelReservation(r.id()); notice = "予約を取り消しました"; });
    }

    private void checkout() throws IOException {
        if (view != View.BOOKS && view != View.LOANS && view != View.HOME) {
            notice = "貸出は本・貸出・概要画面から操作できます"; return;
        }
        String bookDefault = "";
        if (view == View.BOOKS && selectedValue() instanceof LibraryState.Book b) bookDefault = Integer.toString(b.id());
        String[] v = form("本を貸し出す", new String[]{"本ID *", "利用者ID *"}, new String[]{bookDefault, ""});
        if (v != null) act(() -> {
            var loan = service.checkout(number(v[0], "本ID"), number(v[1], "利用者ID"));
            notice = "貸出 #" + loan.id() + " 完了 / 返却期限 " + loan.due();
        });
    }

    private void returnSelected() throws IOException {
        if (view != View.LOANS) { notice = "返却は貸出画面で操作できます"; return; }
        Object item = selectedValue();
        if (item instanceof LibraryState.Loan l && confirm("貸出 #" + l.id() + " を返却しますか？"))
            act(() -> { service.returnBook(l.id()); notice = "返却しました"; });
    }

    private void reserve() throws IOException {
        String[] v = form("本を予約", new String[]{"本ID *", "利用者ID *"}, new String[]{"", ""});
        if (v != null) act(() -> {
            var r = service.reserve(number(v[0], "本ID"), number(v[1], "利用者ID"));
            notice = "予約 #" + r.id() + " を登録しました";
        });
    }

    private void settings() throws IOException {
        var s = service.state();
        String[] v = form("貸出設定", new String[]{"貸出日数（1〜90）", "1人あたりの上限（1〜20）"},
                new String[]{Integer.toString(s.loanDays), Integer.toString(s.maxLoans)});
        if (v != null) act(() -> {
            service.updateSettings(number(v[0], "貸出日数"), number(v[1], "上限"));
            notice = "設定を保存しました";
        });
    }

    private void showDetails() throws IOException {
        Object item = selectedValue();
        if (item instanceof LibraryState.Book b) {
            var loan = service.activeLoanFor(b.id());
            info("本 #" + b.id(), List.of("タイトル: " + b.title(), "著者: " + b.author(),
                    "ISBN: " + (b.isbn().isBlank() ? "未登録" : b.isbn()),
                    "分類: " + b.category() + "  /  出版年: " + (b.year() == 0 ? "不明" : b.year()),
                    "状態: " + service.bookStatus(b.id()),
                    loan == null ? "貸出情報: なし" : "貸出先: " + service.memberName(loan.memberId()) + " / 期限: " + loan.due()));
        } else if (item instanceof LibraryState.Member m) {
            List<String> lines = new ArrayList<>(List.of("氏名: " + m.name(),
                    "メール: " + (m.email().isBlank() ? "未登録" : m.email()),
                    "電話: " + (m.phone().isBlank() ? "未登録" : m.phone()),
                    "貸出中: " + service.activeLoanCount(m.id()) + "/" + service.state().maxLoans + "冊"));
            service.loans().stream().filter(l -> l.active() && l.memberId() == m.id())
                    .forEach(l -> lines.add("  • " + service.bookTitle(l.bookId()) + " / " + l.due()));
            info("利用者 #" + m.id(), lines);
        } else if (item instanceof LibraryState.Loan l) {
            info("貸出 #" + l.id(), List.of("本: " + service.bookTitle(l.bookId()),
                    "利用者: " + service.memberName(l.memberId()), "貸出日: " + l.start(),
                    "返却期限: " + l.due(), "返却日: " + (l.returned() == null ? "未返却" : l.returned()),
                    "延長: " + l.renewals() + "/2回"));
        } else if (item instanceof LibraryState.Reservation r) {
            long ahead = service.reservations().stream().filter(x -> x.bookId() == r.bookId() && x.id() < r.id()).count();
            info("予約 #" + r.id(), List.of("本: " + service.bookTitle(r.bookId()),
                    "利用者: " + service.memberName(r.memberId()), "受付: " + r.created(),
                    "待ち順: " + (ahead + 1) + "番目"));
        } else if (item instanceof LibraryState.Event e) {
            info("操作履歴 #" + e.id(), List.of("日時: " + e.time(), "操作: " + e.action(), "内容: " + e.detail()));
        }
    }

    private void act(Action action) {
        try { action.run(); }
        catch (IllegalArgumentException | IOException ex) { notice = "エラー: " + ex.getMessage(); }
    }

    private static int number(String input, String label) {
        try { return Integer.parseInt(input.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(label + "は数字で入力してください"); }
    }

    private void render() throws IOException {
        screen.clear();
        TerminalSize size = screen.getTerminalSize();
        int w = size.getColumns(), h = size.getRows();
        TextGraphics g = screen.newTextGraphics();
        fill(g, 0, 0, w, h, BG);
        if (w < 78 || h < 24) {
            put(g, 2, 2, "LIBRARY SYSTEM / DX", CYAN, BG, w - 4);
            put(g, 2, 4, "画面を78列×24行以上に広げてください", WHITE, BG, w - 4);
            put(g, 2, 6, "現在 " + w + "×" + h + "    Q で終了", GRAY, BG, w - 4);
            screen.refresh();
            return;
        }
        drawChrome(g, w, h);
        int cw = w - SIDE - 4;
        boolean wide = w >= 112;
        switch (view) {
            case HOME -> drawHome(g, w, h);
            case BOOKS -> drawTable(g, w, h, "蔵書", "本の登録・貸出状況を管理",
                    wide ? new String[]{"ID", "タイトル", "著者", "分類", "状態"} : new String[]{"ID", "タイトル", "状態"},
                    wide ? new int[]{6, cw - 57, 18, 12, 13} : new int[]{6, cw - 26, 14},
                    wide ? new int[]{0, 1, 2, 3, 4} : new int[]{0, 1, 4});
            case MEMBERS -> drawTable(g, w, h, "利用者", "登録情報と現在の貸出冊数",
                    wide ? new String[]{"ID", "氏名", "メール", "貸出"} : new String[]{"ID", "氏名", "貸出"},
                    wide ? new int[]{6, cw - 47, 27, 9} : new int[]{6, cw - 20, 9},
                    wide ? new int[]{0, 1, 2, 3} : new int[]{0, 1, 3});
            case LOANS -> drawTable(g, w, h, "貸出", "返却・延長・延滞を管理",
                    wide ? new String[]{"ID", "本", "利用者", "期限", "状態"} : new String[]{"ID", "本", "状態"},
                    wide ? new int[]{6, cw - 55, 18, 13, 12} : new int[]{6, cw - 24, 12},
                    wide ? new int[]{0, 1, 2, 3, 4} : new int[]{0, 1, 4});
            case RESERVATIONS -> drawTable(g, w, h, "予約", "予約待ちは受付順に表示",
                    wide ? new String[]{"ID", "本", "利用者", "受付"} : new String[]{"ID", "本", "利用者"},
                    wide ? new int[]{6, cw - 43, 18, 14} : new int[]{6, cw - 29, 18},
                    wide ? new int[]{0, 1, 2, 3} : new int[]{0, 1, 2});
            case REPORTS -> drawReports(g, w, h);
            case EVENTS -> drawTable(g, w, h, "操作履歴", "直近の変更を確認",
                    wide ? new String[]{"ID", "日時", "操作", "内容"} : new String[]{"日時", "操作", "内容"},
                    wide ? new int[]{6, 14, 16, cw - 41} : new int[]{14, 16, cw - 35},
                    wide ? new int[]{0, 1, 2, 3} : new int[]{1, 2, 3});
            case HELP -> drawHelp(g, w, h);
        }
        screen.refresh();
    }

    private void drawChrome(TextGraphics g, int w, int h) {
        fill(g, 0, 0, w, 4, PANEL);
        fill(g, 0, 0, w, 1, CYAN);
        put(g, 2, 1, "LIBRARY", WHITE, PANEL, SIDE - 3);
        put(g, 2, 2, "SYSTEM  /  DX", CYAN, PANEL, SIDE - 3);
        put(g, SIDE + 2, 1, "WORKSPACE  /  " + view.label, WHITE, PANEL, w - SIDE - 24);
        put(g, SIDE + 2, 2, "図書館運営コンソール", GRAY, PANEL, w - SIDE - 4);
        put(g, w - 17, 1, java.time.LocalDate.now().toString(), CYAN, PANEL, 15);
        fill(g, 0, 4, SIDE, h - 7, PANEL);
        put(g, 2, 5, "NAVIGATION", GRAY, PANEL, SIDE - 4);
        for (int i = 0; i < View.values().length; i++) {
            int y = 6 + i * 2;
            if (y >= h - 4) break;
            View current = View.values()[i];
            TextColor bg = current == view ? SELECT : PANEL;
            fill(g, 1, y, SIDE - 2, 1, bg);
            put(g, 2, y, current == view ? "▌" : " ", CYAN, bg, 2);
            put(g, 4, y, String.format("%02d", i + 1), current == view ? CYAN : GRAY, bg, 3);
            put(g, 8, y, current.label, current == view ? WHITE : GRAY, bg, SIDE - 10);
        }
        fill(g, 0, h - 3, w, 3, PANEL);
        put(g, 2, h - 3, notice.startsWith("エラー") ? "!" : "●", notice.startsWith("エラー") ? RED : CYAN, PANEL, 2);
        put(g, 5, h - 3, notice, notice.startsWith("エラー") ? RED : WHITE, PANEL, w - 7);
        put(g, 2, h - 2, "←→ 移動   ↑↓ 選択   Enter 詳細   / 検索   N 登録   E 編集", GRAY, PANEL, w - 4);
        put(g, 2, h - 1, "D 保管/取消   L 貸出   R 返却   S 設定   ? ヘルプ   Q 終了", CYAN, PANEL, w - 4);
    }

    private void drawTable(TextGraphics g, int w, int h, String title, String subtitle,
                           String[] headers, int[] widths, int[] columns) {
        int x = SIDE + 2, cw = w - SIDE - 4;
        List<Row> items = rows();
        if (selected >= items.size()) selected = Math.max(0, items.size() - 1);
        int capacity = visibleRows();
        if (selected < scroll) scroll = selected;
        if (selected >= scroll + capacity) scroll = selected - capacity + 1;
        fill(g, x - 1, 5, cw + 2, h - 9, SURFACE);
        put(g, x + 1, 6, title, WHITE, SURFACE, cw - 16);
        put(g, x + 1, 7, subtitle, GRAY, SURFACE, cw - 18);
        put(g, x + cw - 15, 6, items.size() + " ITEMS", CYAN, SURFACE, 13);
        put(g, x + cw - 27, 7, "SEARCH  " + (query.isEmpty() ? "ALL" : query), MAGENTA, SURFACE, 25);
        tableLine(g, x, 9, headers, widths, CYAN, PANEL, cw);
        for (int i = 0; i < capacity && i + scroll < items.size(); i++) {
            int index = i + scroll;
            Row row = items.get(index);
            TextColor background = index == selected ? SELECT : (index % 2 == 0 ? SURFACE : SURFACE_ALT);
            TextColor foreground = index == selected ? WHITE : GRAY;
            String[] cells = new String[columns.length];
            for (int c = 0; c < columns.length; c++) cells[c] = row.cells()[columns[c]];
            tableLine(g, x, 10 + i, cells, widths, foreground, background, cw);
        }
        if (items.isEmpty()) put(g, x + 2, 12, query.isEmpty() ? "データがありません。N で登録できます。" : "検索結果がありません。", YELLOW, SURFACE, cw - 4);
        put(g, x + 1, h - 6, "選択  " + (items.isEmpty() ? "—" : (selected + 1) + " / " + items.size())
                + "    " + (items.isEmpty() ? "" : summary(items.get(selected).value())), CYAN, SURFACE, cw - 2);
    }

    private String summary(Object item) {
        if (item instanceof LibraryState.Book b) return b.title() + "  •  " + service.bookStatus(b.id());
        if (item instanceof LibraryState.Member m) return m.name() + "  •  貸出 " + service.activeLoanCount(m.id()) + "冊";
        if (item instanceof LibraryState.Loan l) return service.bookTitle(l.bookId()) + "  •  期限 " + l.due();
        if (item instanceof LibraryState.Reservation r) return service.bookTitle(r.bookId()) + "  •  " + service.memberName(r.memberId());
        return "Enter で詳細を表示";
    }

    private void tableLine(TextGraphics g, int x, int y, String[] cells, int[] widths,
                           TextColor fg, TextColor bg, int total) {
        fill(g, x, y, total, 1, bg);
        int position = x + 2;
        for (int i = 0; i < cells.length && i < widths.length; i++) {
            if (position >= x + total - 2) break;
            int width = Math.min(widths[i] - 1, x + total - position - 2);
            TextColor color = fg;
            if (i == cells.length - 1 && !bg.equals(PANEL)) {
                if (cells[i].contains("延滞")) color = RED;
                else if (cells[i].contains("貸出可能")) color = GREEN;
                else if (cells[i].contains("貸出中")) color = YELLOW;
            }
            put(g, position, y, cells[i], color, bg, width);
            position += widths[i];
        }
    }

    private void drawHome(TextGraphics g, int w, int h) {
        int x = SIDE + 2, cw = w - SIDE - 4;
        var s = service.state();
        int books = service.books().size();
        long active = service.loans().stream().filter(LibraryState.Loan::active).count();
        long overdue = service.overdueCount();
        int available = books - (int) active;
        fill(g, x - 1, 5, cw + 2, h - 9, SURFACE);
        put(g, x + 1, 6, "運営ダッシュボード", WHITE, SURFACE, cw - 3);
        put(g, x + 1, 7, "今日の状況をひと目で確認", GRAY, SURFACE, cw - 3);
        boolean wide = w >= 112;
        int columns = wide ? 4 : 2;
        int cardWidth = (cw - (columns - 1) * 2) / columns;
        card(g, x, 9, cardWidth, "蔵書", Integer.toString(books), CYAN);
        card(g, x + cardWidth + 2, 9, cardWidth, "貸出可能", Integer.toString(available), GREEN);
        if (wide) {
            card(g, x + (cardWidth + 2) * 2, 9, cardWidth, "貸出中", Long.toString(active), YELLOW);
            card(g, x + (cardWidth + 2) * 3, 9, cardWidth, "延滞", Long.toString(overdue), RED);
        } else {
            card(g, x, 14, cardWidth, "貸出中", Long.toString(active), YELLOW);
            card(g, x + cardWidth + 2, 14, cardWidth, "延滞", Long.toString(overdue), RED);
        }
        int sectionY = wide ? 15 : 19;
        put(g, x + 1, sectionY, "要対応  /  延滞 " + overdue + "件", overdue > 0 ? RED : GREEN, SURFACE, cw - 3);
        int y = sectionY + 2;
        for (var loan : service.loans()) {
            if (!loan.overdue() || y >= h - 6) continue;
            put(g, x + 2, y++, "! " + service.bookTitle(loan.bookId()) + "  /  "
                    + service.memberName(loan.memberId()) + "  /  " + loan.due(), RED, SURFACE, cw - 4);
        }
        if (y == sectionY + 2 && y < h - 5) put(g, x + 2, y, "対応が必要な貸出はありません", GREEN, SURFACE, cw - 4);
        if (wide && h >= 29) {
            put(g, x + 1, 21, "最近のアクティビティ", MAGENTA, SURFACE, cw - 3);
            int eventY = 23;
            for (var event : service.events().stream().limit(3).toList()) {
                if (eventY >= h - 6) break;
                put(g, x + 2, eventY++, event.time().format(SHORT_TIME) + "  "
                        + event.action() + "  /  " + event.detail(), GRAY, SURFACE, cw - 4);
            }
        }
        put(g, x + 1, h - 6, "貸出 " + s.loanDays + "日  •  上限 " + s.maxLoans + "冊  •  予約 " + s.reservations.size() + "件", GRAY, SURFACE, cw - 3);
    }

    private void card(TextGraphics g, int x, int y, int width, String label, String value, TextColor accent) {
        fill(g, x, y, width, 4, SURFACE_ALT);
        put(g, x, y + 1, "▌", accent, SURFACE_ALT, 1);
        put(g, x + 2, y + 1, label, GRAY, SURFACE_ALT, width - 4);
        put(g, x + 2, y + 2, value, accent, SURFACE_ALT, width - 4);
    }

    private void drawReports(TextGraphics g, int w, int h) {
        int x = SIDE + 2, cw = w - SIDE - 4;
        fill(g, x - 1, 5, cw + 2, h - 9, SURFACE);
        put(g, x + 1, 6, "ライブラリ分析", WHITE, SURFACE, cw - 3);
        put(g, x + 1, 7, "蔵書の稼働率と分類の構成", GRAY, SURFACE, cw - 3);
        var s = service.state();
        int total = service.books().size();
        long active = service.loans().stream().filter(LibraryState.Loan::active).count();
        long returned = service.loans().stream().filter(l -> !l.active()).count();
        put(g, x + 1, 9, "稼働率", MAGENTA, SURFACE, cw - 3);
        put(g, x + 1, 10, bar(active, total, Math.min(24, cw - 14)) + "  "
                + (total == 0 ? 0 : active * 100 / total) + "%", CYAN, SURFACE, cw - 3);
        put(g, x + 1, 12, "累計貸出 " + s.loans.size() + "件   返却済 " + returned + "件", WHITE, SURFACE, cw - 3);
        put(g, x + 1, 13, "現在貸出 " + active + "件   延滞 " + service.overdueCount()
                + "件   予約 " + s.reservations.size() + "件", YELLOW, SURFACE, cw - 3);
        put(g, x + 1, 15, "分類別の蔵書", MAGENTA, SURFACE, cw - 3);
        Map<String, Integer> categories = new HashMap<>();
        for (var b : service.books()) categories.merge(b.category(), 1, Integer::sum);
        int y = 17;
        for (var entry : categories.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).toList()) {
            if (y >= h - 5) break;
            put(g, x + 2, y++, pad(entry.getKey(), 12) + bar(entry.getValue(), Math.max(1, total), Math.min(16, cw - 27))
                    + "  " + entry.getValue() + "冊", CYAN, SURFACE, cw - 4);
        }
        if (categories.isEmpty()) put(g, x + 2, y, "登録された本はありません", GRAY, SURFACE, cw - 4);
    }

    private static String bar(long value, long total, int width) {
        int count = total == 0 ? 0 : (int) Math.min(width, Math.round((double) value / total * width));
        return "█".repeat(count) + "░".repeat(width - count);
    }

    private void drawHelp(TextGraphics g, int w, int h) {
        int x = SIDE + 2, cw = w - SIDE - 4;
        fill(g, x - 1, 5, cw + 2, h - 9, SURFACE);
        put(g, x + 1, 6, "キーボードガイド", WHITE, SURFACE, cw - 3);
        put(g, x + 1, 7, "よく使う操作をまとめました", GRAY, SURFACE, cw - 3);
        String[][] lines = {
                {"← → / 1-8", "画面を切り替える"},
                {"↑ ↓ / PgUp PgDn", "一覧を移動する"},
                {"Enter / /", "詳細を見る / 検索する"},
                {"N / E", "登録する / 編集・延長する"},
                {"D / L / R", "保管・取消 / 貸出 / 返却"},
                {"S / ? / Q", "設定 / ヘルプ / 終了"},
                {"フォーム", "Tabで移動、F2で保存"},
                {"Esc", "検索解除・入力の取消"}
        };
        int y = 9;
        for (String[] line : lines) {
            if (y >= h - 5) break;
            put(g, x + 2, y, line[0], CYAN, SURFACE, Math.min(22, cw - 4));
            put(g, x + 24, y, line[1], WHITE, SURFACE, cw - 26);
            y++;
        }
        if (y + 2 < h - 4) {
            put(g, x + 2, y + 1, "変更は操作ごとに自動保存されます。", GRAY, SURFACE, cw - 4);
            put(g, x + 2, y + 2, "予約待ちがある本は先頭の利用者を優先します。", GRAY, SURFACE, cw - 4);
        }
    }

    private String[] form(String title, String[] labels, String[] defaults) throws IOException {
        StringBuilder[] values = new StringBuilder[labels.length];
        for (int i = 0; i < labels.length; i++) values[i] = new StringBuilder(defaults[i]);
        int field = 0;
        int cursor = values[0].length();
        while (true) {
            render();
            TerminalSize size = screen.getTerminalSize();
            int w = Math.min(70, size.getColumns() - 6);
            int height = Math.min(size.getRows() - 4, labels.length * 2 + 6);
            int x = (size.getColumns() - w) / 2, y = (size.getRows() - height) / 2;
            TextGraphics g = screen.newTextGraphics();
            fill(g, x, y, w, height, PANEL);
            fill(g, x, y, w, 1, CYAN);
            put(g, x + 3, y + 1, title, WHITE, PANEL, w - 18);
            put(g, x + w - 15, y + 1, "FIELD " + (field + 1) + "/" + labels.length, CYAN, PANEL, 13);
            for (int i = 0; i < labels.length; i++) {
                int row = y + 3 + i * 2;
                if (row >= y + height - 2) break;
                put(g, x + 3, row, (i == field ? "▸ " : "  ") + labels[i], i == field ? CYAN : GRAY, PANEL, w - 6);
                String shown = values[i].toString();
                if (i == field) {
                    int start = cursor;
                    while (start > 0 && cellWidth(shown.substring(start, cursor)) < (w - 12) / 2) start--;
                    shown = shown.substring(start, cursor) + "│" + shown.substring(cursor);
                }
                TextColor fieldBg = i == field ? SELECT : SURFACE_ALT;
                fill(g, x + 3, row + 1, w - 6, 1, fieldBg);
                put(g, x + 4, row + 1, shown, WHITE, fieldBg, w - 8);
            }
            put(g, x + 3, y + height - 2, "TAB 移動   ENTER 次/保存   F2 保存   ESC 取消", GRAY, PANEL, w - 6);
            screen.refresh();
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            KeyType type = key.getKeyType();
            if (type == KeyType.Escape) return null;
            if (type == KeyType.F2 || (type == KeyType.Enter && field == labels.length - 1)) {
                String[] result = new String[values.length];
                for (int i = 0; i < values.length; i++) result[i] = values[i].toString();
                return result;
            }
            if (type == KeyType.Tab || type == KeyType.ArrowDown || type == KeyType.Enter) {
                field = (field + 1) % labels.length; cursor = values[field].length(); continue;
            }
            if (type == KeyType.ArrowUp) {
                field = Math.floorMod(field - 1, labels.length); cursor = values[field].length(); continue;
            }
            StringBuilder current = values[field];
            if (type == KeyType.Backspace && cursor > 0) { current.deleteCharAt(--cursor); continue; }
            if (type == KeyType.Delete && cursor < current.length()) { current.deleteCharAt(cursor); continue; }
            if (type == KeyType.ArrowLeft) { cursor = Math.max(0, cursor - 1); continue; }
            if (type == KeyType.ArrowRight) { cursor = Math.min(current.length(), cursor + 1); continue; }
            if (type == KeyType.Home) { cursor = 0; continue; }
            if (type == KeyType.End) { cursor = current.length(); continue; }
            if (type == KeyType.Character && key.getCharacter() >= 32 && current.length() < 120) {
                current.insert(cursor++, key.getCharacter());
            }
        }
    }

    private boolean confirm(String question) throws IOException {
        while (true) {
            dialog("確認", List.of(question, "Y / Enter: 実行     N / Esc: 中止"));
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            if (key.getKeyType() == KeyType.Enter) return true;
            if (key.getKeyType() == KeyType.Escape) return false;
            if (key.getKeyType() == KeyType.Character) {
                char c = Character.toLowerCase(key.getCharacter());
                if (c == 'y') return true;
                if (c == 'n') return false;
            }
        }
    }

    private void info(String title, List<String> lines) throws IOException {
        List<String> all = new ArrayList<>(lines);
        all.add("");
        all.add("Enter / Esc で閉じる");
        dialog(title, all);
        while (true) {
            KeyStroke key = screen.readInput();
            if (key != null && (key.getKeyType() == KeyType.Enter || key.getKeyType() == KeyType.Escape)) break;
        }
    }

    private void dialog(String title, List<String> lines) throws IOException {
        render();
        TerminalSize size = screen.getTerminalSize();
        int w = Math.min(72, size.getColumns() - 6);
        int height = Math.min(size.getRows() - 4, lines.size() + 5);
        int x = (size.getColumns() - w) / 2, y = (size.getRows() - height) / 2;
        TextGraphics g = screen.newTextGraphics();
        fill(g, x, y, w, height, PANEL);
        fill(g, x, y, w, 1, MAGENTA);
        put(g, x + 3, y + 1, title, WHITE, PANEL, w - 6);
        for (int i = 0; i < lines.size() && i < height - 3; i++)
            put(g, x + 3, y + 3 + i, lines.get(i), i == lines.size() - 1 ? CYAN : WHITE, PANEL, w - 6);
        screen.refresh();
    }

    private static void fill(TextGraphics g, int x, int y, int width, int height, TextColor bg) {
        if (width <= 0 || height <= 0) return;
        g.setBackgroundColor(bg);
        g.fillRectangle(new TerminalPosition(x, y), new TerminalSize(width, height), ' ');
    }

    private static void put(TextGraphics g, int x, int y, String text, TextColor fg, TextColor bg, int width) {
        if (width <= 0) return;
        g.setForegroundColor(fg);
        g.setBackgroundColor(bg);
        g.putString(x, y, fit(Objects.toString(text, ""), width));
    }

    private static String pad(String value, int width) {
        String s = fit(value, width);
        return s + " ".repeat(Math.max(0, width - cellWidth(s)));
    }

    private static String fit(String value, int width) {
        if (width <= 0) return "";
        if (cellWidth(value) <= width) return value;
        int limit = Math.max(0, width - 1);
        StringBuilder result = new StringBuilder();
        int used = 0;
        for (int i = 0; i < value.length();) {
            int cp = value.codePointAt(i);
            int cells = cellWidth(cp);
            if (used + cells > limit) break;
            result.appendCodePoint(cp);
            used += cells;
            i += Character.charCount(cp);
        }
        return result + "…";
    }

    private static int cellWidth(String value) {
        return value.codePoints().map(App::cellWidth).sum();
    }

    private static int cellWidth(int cp) {
        if (cp >= 0x1100 && (cp <= 0x115F || cp >= 0x2329 && cp <= 0x232A
                || cp >= 0x2E80 && cp <= 0xA4CF || cp >= 0xAC00 && cp <= 0xD7A3
                || cp >= 0xF900 && cp <= 0xFAFF || cp >= 0xFE10 && cp <= 0xFE6F
                || cp >= 0xFF01 && cp <= 0xFF60 || cp >= 0xFFE0 && cp <= 0xFFE6)) return 2;
        return 1;
    }
}
