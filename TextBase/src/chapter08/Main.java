/*
 * タイトル: 実行ファイル
 * 作成者: J1A101 井口 隆太
 */

package chapter08;

/*
 * 実行クラス
 */
public class Main {
	
	/*
	 * メインメソッド
	 */
	public static void main(String[] args) {
		
		// 1. 勇者を生み出す
		Hero hero = new Hero();
		
		// 2. 名前とhpを設定する
		hero.hp = 100;
		hero.name = "田中平蔵";
		
		// 3. 指示を出す
		// ①5秒座る
		hero.sit(5);
		
		// ②転ぶ
		hero.slip();
		
		// ③25秒座る
		hero.sit(25);
		
		// ④逃げる
		hero.run();
	}
}
