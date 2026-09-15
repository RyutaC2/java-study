/*
 * タイトル:練習問題4-1
 * 作成者: J1A101 井口 隆太
 */

package step04;

import tools.KeyIn;

public class Step401 {
	KeyIn key; //フィールド変数

	/**
	 * コンストラクタ
	 * インスタンス生成時に最初に動く
	 */
	public Step401() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
		
	}
	
	/**
	 * for分の練習
	 */
	public void forTraining() {
		// 繰り返す回数が決まっている場合に使用する
		// ループ変数; 繰り返し条件; 増減値
		// ++(インクリメント) --(デクリメント)
		for(int i = 0; i < 10; i++) {
			System.out.println("iの値:" + i);
		}
		
		// 初期値と更新処理を複数書く場合
		for (int i = 1, j = 10; i <= j; i++, j--) {
			System.out.println("iは" + i + ", jは" + j);
		}
		
		System.out.println();
		for (int i = 1; i <= 10; i++) {
			// iが偶数の時処理をスキップ
			if ( i % 2 == 0) {		// i/2の余りが0
				continue;				// スキップ
			}
			System.out.println("i: " + i);
		}
		
		System.out.println();
		// 無限ループ（for文の場合）
		int i = 0;
		for ( ; ; ) {
			i++;
			if (i > 10) {
				break;
			}
		}
		
		// 無限ループ②（for文）
		for (int j = 0; ; j++) {
			if (j > 10) {
				break;
			}
		}
	}
	
	/*
	 * while文の練習
	 */
	public void whileTraining() {
		
		int i = 0; // ループ変数の宣言と初期化
		
		while(i < 10) {
			System.out.println("iの値: " + i);
			i++; // インクリメント i = i + 1 i += 1
		}
		
		int j = 1;
		
		// jが10以下なら
		while (j <= 10) {
			System.out.println("j: " + j);
			j++;
			if (j > 5) {
				break;
			}
		}
		
		// 無限ループ
		int k = 1;
		while (true) {
			k++;
			if (k > 10) {
				break;
			}
		}
	}
	
	/*
	 * do-while文の練習
	 * ブロックを実行してから、条件式を判定
	 */
	public void do_whileTreaining() {
		int i = 0;
		
		do {
			System.out.println("iの値: " + i);
			i++; // インクリメント
		} while (i < 10);
	}
	
	/*
	 * ++や--の書き方で、結果が異なることを確認
	 */
	public void incrementTraining() {
		
		int a = 10;
		int b = 10;
		
		System.out.println(a++ + 50); // 後置き
		System.out.println(++b + 50); // 前置き
		// バグの原因なので、基本的に単独で使用する
	}
}
