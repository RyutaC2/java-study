/*
 * タイトル: 練習問題2-3
 * 作成者: J1A101 井口 隆太
 */

package step02;

/*
 * 戻り値練習用クラス
 */
public class Step203 {
	
	/*
	 * 戻り値でint型の値を返すメソッド
	 */
	public int receiveInt() {
		// 呼び出し元に100を返す
		return 100;
	}
	
	/*
	 * 戻り値でString型の値を返すメソッド
	 */
	public String receiveString() {
		String name = "山田太郎";
		return name;
	}
	
	/*
	 * 戻り値で別のメソッドの戻り値を返すメソッド
	 */
	public int returnCheck() {
		// 戻り値の型が一致していないとエラーになる
		return receiveInt();
	}
}
