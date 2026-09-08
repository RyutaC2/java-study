package com.example.library;

import java.util.Scanner;

public class App {
	
	private static final String MENU_TEXT = """
			==== 図書館管理システム ====
			1. 本の一覧を表示
			2. 本を検索
			3. 本を登録
			4. 利用者を登録
			5. 本を貸し出す
			6. 本を返却する
			0. 終了
			番号を入力してください:
			""";
	
	public static void main(String[] args) {
		
		String input_value = "";
		Scanner scanner = new Scanner(System.in);
		
		while (!input_value.equals("0")) {	
			System.out.print(MENU_TEXT);
			input_value = scanner.nextLine();
			
			String select_text;
			
			switch (input_value) {
				case "1":
					select_text = "本の一覧を表示します";
					break;
					
				case "2":
					select_text = "本を検索します";
					break;
					
				case "3":
					select_text = "本を新たに登録します";
					break;
					
				case "4":
					select_text = "利用者を新たに登録します";
					break;
					
				case "5":
					select_text = "本を貸し出します";
					break;
					
				case "6":
					select_text = "本を返却します";
					break;
					
				case "0":
					select_text = "プログラムを終了します";
					break;
					
				default:
					select_text = "この機能は未実装です";
			}
			
			System.out.println("\n\n\n" + input_value + " ➡ " + select_text + "\n\n");
		}
		
		scanner.close();
	}
	
}