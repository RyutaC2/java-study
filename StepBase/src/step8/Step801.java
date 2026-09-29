/*
 * タイトル: 練習問題8-1
 * 作成者: J1A101 井口 隆太
 */

package step8;

import java.util.Random;

/*
 * クラス変数、クラスメソッドの練習
 */
public class Step801 {
	/*
	 * インスタンスメンバ
	 * ・インスタンス変数（フィールド）
	 * ・インスタンスメソッド
	 * これらは生成したインスタンス（オブジェクト）ごとに持つ
	 */
	
	/*
	 * 静的（static）メンバ
	 * ・static変数（クラス変数）
	 * ・staticメソッド（クラスメソッド）
	 * これらはクラスごとに持つので、インスタンス生成せずに使える
	 */
	
	// インスタンス変数（フィールド）
	String title = "Step801クラスの持つフィールド";
	
	// スタティック変数（クラス変数）
	static String title2 = "Step801クラスの持つクラス変数";
	
	public static String uranai() {
		// 占いの結果の配列
		String[] words = {"大吉", "中吉", "小吉", "吉", "末吉", "凶", "大凶"};
		
		Random rand = new Random();
		int num = rand.nextInt(words.length);
		
		return words[num];
	}
}
