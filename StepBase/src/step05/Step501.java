/*
 * タイトル:練習問題5-1
 * 作成者: J1A101 井口 隆太
 */

package step05;

public class Step501 {
	
	/*
	 * 配列練習用クラス
	 */
	public void arrayTraining1() {
		// ①配列の宣言
		// int型の配列変数scoreを宣言
		
		int[] score;
		
		// ②配列のインスタンスを生成
		// 要素を3つ格納できるint型の
		// 配列のインスタンスを生成し、
		// その参照をscoreに代入する。
		score = new int[3];
		
		// ③scoreの最初の要素に125を代入する
		score[0] = 125;
		
		// scoreの2番目の要素に10を代入する
		score[1] = 10;
		
		// scoreの3番目の要素に6を代入する
		score[2] = 6;
		
		// ④scoreのすべての要素を画面に出力
		System.out.println("score[0]: " + score[0]);
		System.out.println("score[1]: " + score[1]);
		System.out.println("score[2]: " + score[2]);
	}
	
	public void arrayTraining2() {
		// 配列の宣言と初期化を同時に行う
		// 大文字阿多の配列変数strArrayに赤、黄色、
		// 緑、black、pinkの5つを代入する。
		
		String[] strArray = {
				"赤", "黄色", "緑", "black", "pink"
		};
		
		for (int i = 0; i < strArray.length; i++) {
			System.out.println("strArray[]の" + i + "番目の要素: " + strArray[i]);
		}
	}
	
	public void arrayTraining3(String[] array) {
		// 引数に文字列型の配列arrayを設定
		// 受け取った配列の中身をすべて表示する
		for (int i = 0; i < array.length; i ++) {
			System.out.println("要素" + i + ": " + array[i]);
		}
	}
	
	public void arrayTraining4(String str) {
		// 受け取った引数を「,」で分解する
		// 文字列型配列strArrayに格納する
		String[] strArray = str.split(",");
		
		// 配列の中身をすべて出力
		for (int i = 0; i < strArray.length; i++) {
			System.out.println("要素" + i + ": " + strArray[i]);
		}
	}
	
	public void arrayTraining5() {
		// int型の配列scoreに100、200、300を代入
		int[] score = {
				100, 200, 300
		};
		
		// 配列scoreの中身をfor-each文を使って表示
		for (int i : score) {
			System.out.println(i);
		}
	}
}
