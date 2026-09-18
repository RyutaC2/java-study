/*
 * タイトル: 練習問題5-2
 * 作成者: J1A101 井口 隆太
 */

package step05;

/*
 * 二次元配列の練習用クラス
 */
public class Step502 {
	
	public void arrayTraining() {
		
		// 2行3列の2次元配列を定義
		int[][] intArray = new int[2][3];
		
		// 0行目0列目に11を代入
		intArray[0][0] = 11;
		// 0行目1列目に11を代入
		intArray[0][1] = 12;
		// 0行目2列目に11を代入
		intArray[0][2] = 13;
		
		// 1行目1列目に11を代入
		intArray[1][0] = 21;
		// 1行目2列目に11を代入
		intArray[1][1] = 22;
		// 1行目3列目に11を代入
		intArray[1][2] = 23;
		
		// 配列の行数を出力する
		System.out.println("配列の行数: " + intArray.length);
		
		// 配列の列数を出力する
		System.out.println("配列の行数: " + intArray[0].length);
		
		// 中身の確認（for文）
		System.out.println("\nfor文で配列の中身を確認");
		
		// ①まず2次元配列から配列に分解する
		for (int i = 0; i < intArray.length; i++) {
			// ②さらに配列から変数に分解する
			for (int j = 0; j < intArray[0].length; j++) {
				System.out.println("intArray[" + i + "][" + j + "]: " + intArray[i][j]);
			} // 内側
		} // 外側
	}
	
	public void arrayTraining2() {
		System.out.println("\nfor-each文で配列の中身を確認2");
		
		// 配列の定義と初期化を同時に行う
		String[][] strArray = {
				{"1-1", "1-1", "1-1", "1-1"},
				{"2-1", "2-2", "2-3", "2-4"},
				{"3-1", "3-2", "3-3", "3-4"}
			};
		
		// for-each文を使って、中身を出力する。
		for (String[] i : strArray) {
			for (String j : i) {
				System.out.println("内容: " + j);
			}
		}
	}
}
