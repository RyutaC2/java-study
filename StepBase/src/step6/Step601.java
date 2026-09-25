/*
 * タイトル: 練習問題6-1
 * 作成者: J1A101 井口 隆太
 */

package step6;

import java.util.ArrayList;
import java.util.Iterator;

import tools.KeyIn;

/*
 * arrayListの練習用クラス
 */
public class Step601 {
	
	KeyIn key; // フィールド変数
	
	public Step601() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	public void arrayListTraining1() {
		// String型のArrayList 「arrayStr」を宣言
		ArrayList<String> arrayStr = new ArrayList<String>();
		
		// arrayStrにデータを登録する
		arrayStr.add("おはよう");
		arrayStr.add("こんにちは");
		arrayStr.add("こんばんは");
		
		// listの中身を表示（0番目のデータ)
		System.out.println("\n①中身の表示");
		System.out.println(arrayStr.get(0));
		
		// データの入れ替え（上書き）
		arrayStr.set(0, "グッドモーニング");
		
		// データを差し込む（追加）
		arrayStr.add(1, "さようなら");
		
		// listの中身をすべて表示
		System.out.println("\n②中身をすべて表示（for文）");
		for (int i = 0; i < arrayStr.size(); i++) {
			System.out.println(arrayStr.get(i));
		}
		
		// listの中身をすべて表示
		System.out.println("\n③中身をすべて表示（for-each文）");
		for (String i : arrayStr) {
			System.out.println(i);
		}
		
		// String型のイテレータのインスタンス生成
		// arrayStrの持つiteratorメソッドの戻り値として、Iteratorを受け取る
		Iterator<String> it = arrayStr.iterator();
		
		// .hasNext() : 次の要素があればtrueを返す
		while (it.hasNext()) {
			// .next() : 次の要素を返して、カーソルをひとつずらす
			System.out.println(it.next());
		}
	}
	
	/*
	 * コンソールから入力した値をArrayListに格納する（999なら終了)
	 * 格納した値をすべて表示する
	 */
	public void arrayListTraining2() {
		
		// Integer型のArrayListを宣言する
		ArrayList<Integer> arrayInt = new ArrayList<Integer>();
		
		// キーボードから受け取った値を格納する変数
		int input;
		
		// 終了を表す値
		final int END = 999;
		
		// リストに格納していく
		while (true) {
			input = key.readInt("値を入力: ");
			if (input == END) {
				break;
			} else {
				arrayInt.add(input);
			}
		}
		
		// リストの中身をすべて表示
//		for (int i : arrayInt) {
//			System.out.println(i);
//		}
		
		for (int i =0; i < arrayInt.size(); i++) {
			System.out.println(i + "番目" + arrayInt.get(i));
		}
	}
}
