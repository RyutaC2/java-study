/*
 * タイトル: 実行ファイル
 * 作成者: J1A101 井口 隆太
 */
package step02;

/*
 * Step02のクラスを実行する用のクラス
 */
public class Step200 {
	
	public static void main(String[] args) {
		
		// Step201クラスの実行
		System.out.println("Step201クラスを実行します");
		System.out.println("ほかのクラスを使用する練習です");
		
		// Step201クラスのインスタンス生成
		Step201 step201 = new Step201();
		
		// Step201クラスのdispMessageメソッドを実行する
		step201.dispMessage();
	}

}
