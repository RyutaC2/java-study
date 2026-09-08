package com.example.library;

public class Book {
	
	private int id;
	private String title;
	private String author ;
	private boolean borrowed;
	
	public Book(int id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.borrowed = false; // 登録時は貸出可能状態で登録する
	}
	
	// ＩＤ取得
	public int getId() {
		return this.id;
	}
	
	// タイトル取得
	public String getTitle() {
		return this.title;
	}
	
	// 著者取得
	public String getAuthor() {
		return this.author;
	}
	
	// 貸出状態取得
	public boolean isBorrowed() {
		return this.borrowed;
	}
	
	// 貸出状態変更
	public void setBorrowed(boolean borrowed) {
		this.borrowed = borrowed;
	}
	
	// 内容表示
	public String toString() {
		
		// 貸出状態を日本語で出力するための変換処理
		String borrowed_str;
		
		if (this.borrowed) {
			borrowed_str = "貸出中";
		} else {
			borrowed_str = "貸出可能";
		}
		
		// 出力用テキスト生成
		String text = String.format(
				"ＩＤ: %d / タイトル: %s / 著者: %s / 状態: %s",
				this.id, this.title, this.author, borrowed_str
				);
		
		return text;
	}
}
