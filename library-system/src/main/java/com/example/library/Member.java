package com.example.library;

import java.util.ArrayList;

public class Member {
	
	private int id;
	private String name;
	private ArrayList<Integer> borrowedBooks;
	
	public Member(int id, String name) {
		this.id = id;
		this.name = name;
		this.borrowedBooks = new ArrayList<>();
	}
	
	// ＩＤ取得
	public int getId() {
		return this.id;
	}
	
	// 利用者名取得
	public String getName() {
		return this.name;
	}
	
	// 貸し出し中の本ＩＤ一覧取得
	public ArrayList<Integer> getBorrowedBooks() {
		return this.borrowedBooks;
	}
	
	// 借りた本を登録
	public void addBorrowedBooks(int book_id) {
		this.borrowedBooks.add(book_id);
	}
	
	public String toString() {
		
		// 貸し出し中の本IDをStringで出力するための変換処理
		String strBor = "";
		
		// 本IDが0（未貸し出し）ではない場合のみ出力用変数に追加
		for (int i : borrowedBooks) {
			if (i != 0) {
				strBor = strBor + ", " + String.valueOf(i);
			}
		}
		
		// 貸し出しがなく中身のないままの場合に「無し」を代入
		if (strBor.isEmpty()) {
			strBor = "無し";
		}
		
		// 出力用テキスト生成
		String text = String.format(
			"ＩＤ: %d / 利用者名: %s / 貸し出し中の本ＩＤ: %s",
			this.id, this.name, strBor
		);
		
		return text;
	}
}
