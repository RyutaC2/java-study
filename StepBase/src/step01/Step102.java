/*
 * タイトル: 練習問題1-2
 * 作成者: J1A101 井口 隆太
 */

package step01;

public class Step102 {
	public static void main(String[] args) {
		
		//よく使うデータ型の変数に値を入れてみましょう
		
		int intVariable; 	// 型名 変数名;（変数宣言）
		intVariable = 0;	// 変数名 = 値; （値の代入）
		
		String stringVariable = "String型は文字列です";
		char charVariable = 'A';
		boolean booleanVariable = true; // 偽ならfalse
		
		// 変数の中身を表示する
		
		System.out.println("int型の変数の中身: " + intVariable);
		System.out.println("String型の変数の中身: " + stringVariable);
		System.out.println("char型の変数の中身: " + charVariable);
		System.out.println("boolean型の変数の中身: " + booleanVariable);
		
		// 変数に新しい値を代入する
		intVariable = 1000;
		
		// 改行を入れた表示
		System.out.println("int型の変数の中身: \n" + intVariable);
		
		// 定数（今後変わらない値を格納する）
		final int INTVARIABLE = 10; // final 型 定数名（大文字）
		System.out.println(INTVARIABLE);
	}
}
