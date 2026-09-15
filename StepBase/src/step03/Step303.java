/*
* タイトル: 練習問題3-3
* 作成者: J1A101 井口 隆太
 */

package step03;

/*
 * Switch文の練習
 */
public class Step303 {
	/*
	 * 以下の条件を満たす場合、switch文を使える
	 * ①変数の内容が値と一致するか判定したい
	 * ②比較す値が以下のデータ型の場合
	 * 	整数型・文字型・文字列型
	 * 	※少数型・真偽値型は使えない
	 */
	
	public void switchTraining() {
		// それぞれの処理をブロックで囲む方法
		int n = 2; // 判定したい値
		
		switch (n) {
			// caseの後ろには定数かリテラル値を書く
			case 1 -> {
				System.out.println("nは1でした");
			}
			case 2 -> {
				System.out.println("nは2でした");
			}
		}
	}
}
