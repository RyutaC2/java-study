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
	
	public static void main(String[] args) {
		// 初期デモデータを追加
		book_list.add(new Book(1, "これ一冊でjava入門!!", "金澤勇樹"));
		book_list.add(new Book(2, "これ一冊でjava基礎!!", "金澤勇樹"));
		book_list.add(new Book(3, "これ一冊でjava応用!!", "金澤勇樹"));
		
		// 0が入力されるまでメニューを表示し続ける
		while (!input_value.equals("0")) {	
			System.out.print(MENU_TEXT);
			input_value = scanner.nextLine();
			
			// 入力に応じて処理を行う
			switch (input_value) {
				case "1" -> {
					n1_book_list();
				}
				case "2" -> {
					n2_book_search();
				}
				case "3" -> {
					n3_book_add();
				}
				case "4" -> {
					select_info(input_value, "利用者を新たに登録します");
				}
				case "5" -> {
					select_info(input_value, "本を貸し出します");
				}
				case "6" -> {
					select_info(input_value, "本を返却します");
				}
				case "0" -> {
					select_info(input_value, "プログラムを終了します");
				}
				default -> {
					select_info(input_value, "この機能は未実装です");
				}
			}
			System.out.println();
		}
		
		scanner.close();
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
		
		if (keyword.isBlank()) {
			System.out.println("検索キーワードを入力してください。");
			return;
		}
		
		int result_num = 0;
		
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
		
		// 現在のリストのサイズで連番を採用
		int next_num = book_list.size() + 1;
		
		// 本を新規登録
		book_list.add(new Book(next_num, input_title, input_author));
		
		// 登録内容を確認出力（IDは1からの連番のため-1をする）
		Book book = book_list.get(next_num - 1);
		System.out.println("登録完了: " + book.toString());
	}
	
	// 選択された内容を表示する
	private static void select_info(String value, String text) {
		System.out.println("\n\n\n" + value + " ➡ " + text);
	}
}