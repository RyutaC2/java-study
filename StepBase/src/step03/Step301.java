/*
 * タイトル:練習問題3-1
 * 作成者: J1A101 井口 隆太
 */

package step03;

import tools.KeyIn;

/*
 * if分の練習用クラス
 */
public class Step301 {
	
	KeyIn key; //フィールド変数
	
	/**
	 * コンストラクタ
	 * インスタンス生成時に最初に動く
	 */
	public Step301() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	/**
	 * if文練習用
	 */
	public void ifTraining() {
		int score = 0; // 点数
		
		score = key.readInt("Javaの点数");
		
		if (score >= 60) {
			System.out.println("合格");
		}
	}
	
	/**
	 *  複合条件の練習
	 */
	public void ifTraining2() {
		int score = 0; // 点数
			
		// キーボードから入力
		score = key.readInt("Javaの点数");
		
		// && and条件
		if (score >= 0 && score <= 100){
			System.out.println("正常な値です");
		}
		
		// || or条件
		if (score < 0 || score >100){
			System.out.println("不正な値です");
		}
	}
	
	/**
	 * 文字列の比較
	 */
	public void compareString() {
		String inputStr = "";		// 入力用
		String name = "井口隆太";	// 自分の名前
		
		inputStr = key.readKey("あなたの名前を入力");
		
	/*
	 *  文字列を比較するときは[equalsメソッド]を使う
	 *  書き方 : 文字列1.equals(文字列2)
	 *  このように書くと、文字列1と2が等しいか判定する
	 */
		if (inputStr.equals(name)) {
			System.out.println("名前が一致しました");
			
		}
		
	}
	
	/**
	 * 論地理(boolean型の値)で条件判定
	 */
	public void compareBoolean() {
		boolean check = true; //真
		
		// 真偽を反転するなら!()で囲む
		if (check){
			System.out.println("中身はtrueです");
		}
	}
}
