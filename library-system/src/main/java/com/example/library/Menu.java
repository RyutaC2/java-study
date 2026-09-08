package com.example.library;

import java.util.Scanner;

public class Menu {

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
		
		while (input_value != "0") {	
			System.out.print(MENU_TEXT);
			input_value = scanner.nextLine();
			
			switch (input_value) {
				case "1":
					break;
				case "2":
					break;
				case "3":
					break;
				case "4":
					break;
				case "5":
					break;
				case "6":
					break;
				case "0":
					break;
				default:
					System.out.println("この機能は未実装です");
			}
		}
		
		scanner.close();
	}
	
}