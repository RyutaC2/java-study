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
			case 1 -> { // 値が1の場合
				System.out.println("nは1でした");
			}
			case 2 -> { // 値が2の場合
				System.out.println("nは2でした");
			}
			default -> { // それ以外の場合
				System.out.println("それ以外です");
			}
		}
		
		String x = "Debian";
		// breakで処理を終了する方法(昔の書き方)
		switch (x) {
			case "Ubuntu":
				System.out.println("初心者から上級者まで");
				break;
			
			case "ArchLinux":
				System.out.println("変人向け");
				break;
				
			case "Fedora":
				System.out.println("一番微妙");
				break;
				
			case "mintOS":
				System.out.println("化石PCを蘇生しよう");
				
				
			default:
				System.out.println("もしかしてセルフコンパイル？");
				break; // 一応、defaultにもbreakは書く
		}
	}
}
