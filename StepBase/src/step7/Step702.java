/*
 * タイトル: 練習問題7-2
 * 作成者: J1A101 井口 隆太
 */

package step7;

import tools.KeyIn;

/*
 * 例外処理（throws）
 */
public class Step702 {

	KeyIn key; // フィールド変数
	
	public Step702() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	/*
	 * throwsの練習
	 * 例外処理をメソッド呼び出し元に投げる（throwsする）
	 */
	public void method1() throws NumberFormatException {
		// キーボードから数字を入力させる
		String input = key.readKey("数字を入力してください");
		// 数字に変換する
		Integer.parseInt(input);
	}
	
	/*
	 * throwの練習
	 */
	public void method2() {
		String name = "ハッピーめるちゃむ最高！踊ろうよ～いつものお顔で！ハッピーめるちゃむランクアップ！行きすぎよ～達コメすれば～頭がパラパラダイス～";
		if (name.length() > 5) {
			try {
				// 例外を発生させる
				throw(new Exception());
			} catch (Exception e) {
				System.out.println("はげた");
			}
		}
	}
}
