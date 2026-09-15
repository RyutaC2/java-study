package com.example.library;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
	
	// メニュー表示
	private static final String MENU_TEXT = """
			==== 図書館管理システム ====
			1. 本の一覧を表示
			2. 本を検索
			3. 本を登録
			4. 利用者を登録
			5. 本を貸し出す
			6. 本を返却する
			0. 終了
			番号を入力してください: """;
	
	private static ArrayList<Book> book_list = new ArrayList<>();
	
	public static void main(String[] args) {
		book_list.add(new Book(1, "これ一冊でjava入門!!", "金澤勇樹"));
		book_list.add(new Book(2, "これ一冊でjava基礎!!", "金澤勇樹"));
		book_list.add(new Book(3, "これ一冊でjava応用!!", "金澤勇樹"));
		
		
		String input_value = "";
		Scanner scanner = new Scanner(System.in);
		
		// 0が入力されるまでメニューを表示し続ける
		while (!input_value.equals("0")) {	
			System.out.print(MENU_TEXT);
			input_value = scanner.nextLine();
			
			// 入力に応じて処理を行う
			switch (input_value) {
				case "1":
					select_info(input_value, "本の一覧を表示します");
					
					// 本の登録数が0の場合のメッセージ
					if (book_list.size() <= 0) {
						System.out.println("登録されている本はありません。");
						break;
					}
					
					// TODO 10件ごとの表示をさせる
					// 本の情報を順番に出力する
					for (Book book: book_list) {
						System.out.println(book.toString());
					}
					break;
					
				case "2":
					select_info(input_value, "本を検索します");
					break;
					
				case "3":
					select_info(input_value, "本を新たに登録します");
					break;
					
				case "4":
					select_info(input_value, "利用者を新たに登録します");
					break;
					
				case "5":
					select_info(input_value, "本を貸し出します");
					break;
					
				case "6":
					select_info(input_value, "本を返却します");
					break;
					
				case "0":
					select_info(input_value, "プログラムを終了します");
					break;
					
				default:
					select_info(input_value, "この機能は未実装です");
			}
			System.out.println();
		}
		
		scanner.close();
	}
	
	private static void select_info(String value, String text) {
		System.out.println("\n\n\n" + value + " ➡ " + text);
	}
}