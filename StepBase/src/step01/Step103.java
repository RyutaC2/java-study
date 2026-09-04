/*
 * タイトル: 練習問題1-3
 * 作成者: J1A101 井口 隆太
 */

package step01;

public class Step103 {

	public static void main(String[] args) {
		// 出力後に改行しない出力命令
		System.out.print("Hello");
		System.out.print("World");
		
		// 改行のみを行いたい場合
		System.out.println();
		System.out.print("\n");
		
		// その他のエスケープシーケンス
		System.out.println("円マークを表示する: \\ ");
		System.out.println("ダブルクォーテーションを表示する: \" ");
		System.out.println("シングルクォーテーションを表示する: \' ");
	}

}
