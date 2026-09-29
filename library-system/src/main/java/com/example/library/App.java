package com.example.library;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
	
	private static Scanner scanner = new Scanner(System.in);
	private static String input_value = "";
	
	// メニュー表示
	private static final String MENU_TEXT = """
			
			┏━図書館管理システム━┓
			┃1. 本の一覧を表示  　　┃
			┃2. 本を検索  　　　　　┃
			┃3. 本を登録  　　　　　┃
			┃4. 利用者一覧を表示  　┃
			┃5. 利用者を検索  　　　┃
			┃6. 利用者を登録  　　　┃
			┃7. 本を貸し出す  　　　┃
			┃8. 本を返却する  　　　┃
			┃0. 終了  　　　　　　　┃
			┗━━━━━━━━━━━┛
			番号を入力してください: """;
	
	private static ArrayList<Book> book_list = new ArrayList<>();
	private static ArrayList<Member> member_list = new ArrayList<>();
	
	private static final int MAX_BOOKS = 3;
	
	public static void main(String[] args) {
		// 初期デモデータを追加
		book_list.add(new Book(1, "これ一冊でjava入門!!", "金澤勇樹"));
		book_list.add(new Book(2, "これ一冊でjava基礎!!", "金澤勇樹"));
		book_list.add(new Book(3, "これ一冊でjava応用!!", "金澤勇樹"));
		book_list.add(new Book(4, "基本情報技術者試験（FE）確定合格999%!?", "芽瑠頓"));
		book_list.add(new Book(5, "応用情報技術者試験（AP）確定合格999%!?", "芽瑠頓"));
		book_list.add(new Book(6, "人生完全攻略メソッド 絶対に勝率999999% 確定演出!?", "芽瑠頓"));
		
		member_list.add(new Member(1, "田中平蔵"));
		member_list.add(new Member(2, "度遣反蒼"));
		member_list.add(new Member(3, "宇宙海賊ゴー☆ジャス"));
		
		// 0が入力されるまでメニューを表示し続ける
		while (!input_value.equals("0")) {	
			System.out.print(MENU_TEXT);
			input_value = scanner.nextLine();
			
			// 入力に応じて処理を行う
			switch (input_value) {
				case "1" -> { // 本の一覧表示
					n1_book_list();
				}
				case "2" -> { // 本の検索
					n2_book_search();
				}
				case "3" -> { // 本の追加
					n3_book_add();
				}
				case "4" -> { // 利用者の一覧表示
					n4_member_list();
				}
				case "5" -> { // 利用者の検索
					n5_member_search();
				}
				case "6" -> { // 利用者の追加
					n6_member_add();
				}
				case "7" -> { // 本の貸し出し
					n7_book_lending();
				}
				case "8" -> { // 本の返却
					n8_book_return();
				}
				case "0" -> {
					select_info(input_value, "プログラムを終了します");
				}
				default -> {
					select_info(input_value, "この機能は未実装です");
				}
			}
			
			if (!input_value.equals("0")) {
				System.out.print("\n任意の入力で続行: ");
				input_value = scanner.nextLine();
			}
		}
	}
	
	// 1が選択された際、本の一覧を表示する処理
	private static void n1_book_list() {
		select_info(input_value, "本の一覧を表示します");
		
		// 本の登録数が0の場合のメッセージ
		if (book_list.isEmpty()) {
			System.out.println("登録されている本はありません。");
			return;
		}
		
		// TODO 10件ごとの表示をさせる
		// 本の情報を順番に出力する
		for (Book book: book_list) {
			System.out.println(book.toString());
		}
	}
	
	// 2が選択された際、本を検索する処理
	private static void n2_book_search() {
		select_info(input_value, "本を検索します");
		
		System.out.print("検索キーワードを入力: ");
		String keyword = scanner.nextLine();
		System.out.println();
		
		if (keyword.isBlank()) {
			System.out.println("検索キーワードを入力してください。");
			return;
		}
		
		int result_num = 0;
		// キーワードと合致する本のタイトル、著者名がないか照合
		for (Book book: book_list) {
			if (book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
				System.out.println("タイトルと合致: " + book.toString());
				result_num++;
			}
			if (book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
				System.out.println("著者名と合致: " + book.toString());
				result_num++;
			}
		}
		
		if (result_num > 0) {
			System.out.println("検索件数: " + result_num);
		} else {
			System.out.println("検索結果がありません。");
		}
	}
	
	// 3が選択された際、本を新たに追加する処理
	private static void n3_book_add() {
		select_info(input_value, "本を新たに登録します");
		
		System.out.print("登録する本のタイトルを入力: ");
		String input_title = scanner.nextLine();
		if (input_title.isBlank()) {
			System.out.println("タイトルなしでは登録できません。");
			return;
		}
		
		System.out.print("登録する本の著者名を入力: ");
		String input_author = scanner.nextLine();
		if (input_author.isBlank()) {
			System.out.println("著者名なしでは登録できません。");
			return;
		}
		
		// 現在のリストの最大のID+1で連番を採用
		int next_num = book_list.get(book_list.size()).getId() + 1;
		
		// 本を新規登録
		book_list.add(new Book(next_num, input_title, input_author));
		
		// 登録内容を確認出力（IDは1からの連番のため-1をする）
		Book book = book_list.get(next_num - 1);
		System.out.println("登録完了: " + book.toString());
	}
	
	// 4が選択された際、利用者の一覧を表示する処理
	private static void n4_member_list() {
		select_info(input_value, "利用者をの一覧を表示します");
		
		// 本の登録数が0の場合のメッセージ
		if (member_list.isEmpty()) {
			System.out.println("登録されている利用者はありません。");
			return;
		}
		
		// 利用者の情報を順番に出力する
		for (Member member: member_list) {
			System.out.println(member.toString());
		}
	}
	
	// 5が選択された際、利用者を検索する処理
	private static void n5_member_search() {
		select_info(input_value, "利用者を検索します");
		System.out.print("利用者名または貸し出し中の本ＩＤを入力: ");
		String keyword = scanner.nextLine();
		System.out.println();
		
		if (keyword.isBlank()) {
			System.out.println("利用者名または貸し出し中の本ＩＤを入力してください。");
			return;
		}
		
		int result_num = 0;
		
		// キーワードと合致する利用者名がないか照合
		for (Member member: member_list) {
			if (member.getName().toLowerCase().contains(keyword.toLowerCase())) {
				System.out.println("利用者名と合致: " + member.toString());
				result_num++;
			}
			// 利用者ごとに、借りている本のIDとキーワードが合致するか照合
			for (int i: member.getBorrowedBooks()) {
				if (Integer.toString(i) .equals(keyword)) {
					System.out.println("貸し出し中の本ＩＤと合致: ");
					result_num++;
				}
			}
		}
		
		if (result_num > 0) {
			System.out.println("検索件数: " + result_num);
		} else {
			System.out.println("検索結果がありません。");
		}
	}
	
	// 6が選択された際、利用者を新たに追加する処理
	private static void n6_member_add() {
		select_info(input_value, "利用者を新たに登録します");
		
		System.out.print("登録する利用者の名前を入力: ");
		String input_name = scanner.nextLine();
		if (input_name.isBlank()) {
			System.out.println("利用者名なしでは登録できません。");
			return;
		}
		
		// 現在のリストの最大のID+1で連番を採用
		int next_num = member_list.get(member_list.size()).getId() + 1;
		
		// 利用者を新規登録
		member_list.add(new Member(next_num, input_name));
		
		// 登録内容を確認出力（IDは1からの連番のため-1をする）
		Member member = member_list.get(next_num -1);
		System.out.println("登録完了: " + member.toString());
	}
	
	// 7が選択された際、本の貸し出しをする処理
	private static void n7_book_lending() {
		select_info(input_value, "本を貸し出します");
		
		Member target_member = null;
		Book target_book = null;
		String input;
		
		System.out.print("\n利用者ＩＤを入力: ");
		input = scanner.nextLine();
		
		// 合致する利用者がいるか順番に照合
		for (Member member: member_list) {
			if (input.equals(Integer.toString(member.getId()))) {
				target_member = member;
				System.out.println(target_member.toString());
			}
		}
		// 合致せず0のままならエラー
		if (target_member == null) {
			System.out.println("\n利用者が見つかりません。");
			return;
		}
		
		System.out.print("\n借りる本のＩＤを入力: ");
		input = scanner.nextLine();
		
		// 合致する本があるかいるか順番に照合
		for (Book book: book_list) {
			if (input.equals(Integer.toString(book.getId()))) {
				target_book = book;
			}
		}
		// 合致せずそのままならエラー
		if (target_book == null) {
			System.out.println("\n本が見つかりません。");
			return;
		}
		
		// 既に貸し出し中ならエラー
		if (target_book.isBorrowed() == true) {
			System.out.println("\nこの本は既に貸し出し中です。");
			return;
		}
		
		// 既に3冊以上借りていたらエラー
		if (target_member.getBorrowedBooks().size() >= MAX_BOOKS) {
			System.out.println("\n借りられる本の数は3冊までです。あなたは既に3冊の本を借りています。");
			return;
		}
		
		// 借りるかどうか確認してから処理を実行
		System.out.println(target_book.toString());
		System.out.print("\n上記の本を借りますか？(Yes / No): ");
		input = scanner.nextLine();
		// 入力の判定
		if (input.equals("Yes") || input.equals("yes") || input.equals("Y") || input.equals("y")) {
			target_book.setBorrowed(true); // 貸し出し中にステータスを変更
			target_member.addBorrowedBooks(target_book.getId()); // 貸し出し中の本一覧に追加
			
			// 借りている本の一覧表示
			System.out.println("\n本を借りました。現在借りている本の一覧: ");
			for (int book_id: target_member.getBorrowedBooks()) {
				System.out.println(book_list.get(book_id - 1).toString());
			}
		} else {
			System.out.println("\n操作をキャンセルしました。");
			return;
		}
	}
	
	// 8が選択された際、本の返却をする処理
	private static void n8_book_return() {
		select_info(input_value, "本を返却します");
		
	}
	
	// 選択された内容を表示する
	private static void select_info(String value, String text) {
		System.out.println("\n\n\n" + value + " ➡ " + text);
	}
}