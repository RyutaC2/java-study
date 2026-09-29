/*
 * タイトル: 実行ファイル
 * 作成者: J1A101 井口 隆太
 */

package step8;

public class Step800 {

	public static void main(String[] args) {
		
		// インスタンス変数の使い方
		Step801 s801 = new Step801();
		System.out.println(s801.title);
		
		// クラス変数の使い方
		System.out.println(Step801.title2);
		
		// staticメソッドの使い方
		System.out.println(Step801.uranai());
	}

}
