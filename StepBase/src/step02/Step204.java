/*
 * タイトル: 練習問題2-4
 * 作成者: J1A101 井口 隆太
 */

package step02;

/*
 * 引数の練習用クラス
 */
public class Step204 {
	
	/*
	 * int型の値を受け取り、中身を表示する
	 */
	public void sendInt(int intParameter) {
		System.out.println("受け取った値: " + intParameter);
	}
	
	/*
	 * 2つ値を受け取り、中身を表示する
	 */
	public void send2Int(int intParameter1, int intParameter2) {
		System.out.println("１つ目の値: " + intParameter1);
		System.out.println("２つ目の値: " + intParameter2);
	}
	
	/*
	 * 引数で論理値を受け取り、反転して返す
	 */
	public boolean reverseBoolean(boolean value) {
		// 論理値を反転させるには、前に「！」をつける
		return !(value);
	}
}
