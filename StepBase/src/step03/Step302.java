/*
 * タイトル:練習問題3-2
 * 作成者: J1A101 井口 隆太
 */

package step03;

import tools.KeyIn;

/*
 * if-else分の練習
 */
public class Step302 {

	KeyIn key; //フィールド変数

	/**
	 * コンストラクタ
	 * インスタンス生成時に最初に動く
	 */
	public Step302() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}

	public void if_elseTraining() {
		int score = 60; // 点数

		if (score < 60) {
			System.out.println("60点未満");
		} else {
			System.out.println("60点以上");
		}
	}

	public void if_else_ifTraining() {
		int age = 18;
		String name = "井口";
		String inputName = key.readKey("名前を入力");

		if (age <= 20) {
			// 名前が同じか判定
			if (name.equals(inputName)) {
				System.out.println("同じ名前");
			} else {
				System.out.println("違う名前");
			}

		} else if (age <= 60) {
			if (age <= 40) {
				System.out.println("21歳から60歳ですね");
			}
		}
	}

}