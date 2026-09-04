package com.example.library;

import java.util.Scanner;

public class menu {

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
		System.out.print(MENU_TEXT);
		
		Scanner scanner = new Scanner(System.in);
		String input_value = scanner.nextLine();
		
		System.out.println(input_value);
		scanner.close();
	}

}