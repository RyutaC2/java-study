/*
 * タイトル:練習問題4-2
 * 作成者: J1A101 井口 隆太
 */

package step04;

import tools.KeyIn;

public class Step402 {
	KeyIn key; //フィールド変数
	
	/**
	 * コンストラクタ
	 * インスタンス生成時に最初に動く
	 */
	public Step402() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	/*
	 * ネスト(入れ子構造)の練習
	 */
	public void forNest() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.println("i: " + i + " j: " + j);
			} // 内側ループ
		} // 外側ループ
	} // forNext()
	
	public void forNest2() {
		for (int i = 0; i < 3; i++) {
			System.out.println("iのループの中" + (i + 1) + "回目");
			for (int j = 0; j < 5; j++) {
				System.out.println("jループの中" + (j + 1) + "回目");
			} // 内側のループ
			
			System.out.println("jのループを抜けました");
		} // 外側のループ
	} // forNext2()
	
	/*
	 * while文とfor文の入れ子
	 */
	public void nest() {
		// キーボードから値を受け取る
		// 999が入力されるまで、合計し続ける
		// 入力された値を5回足し算する
		int input_value = 0;
		int sum_value = 0;
		
		input_value = key.readInt("値を入力: ");
		
		while (input_value != 999) {
			for (int i = 0; i < 5; i++) {
				sum_value += input_value;
			}
			System.out.println("5回加算: " + sum_value);
			
			input_value = key.readInt("値を入力: ");
		}
		System.out.println("最終値: " + sum_value);
	}
}
