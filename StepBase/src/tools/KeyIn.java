package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * キーボードからの入力を受け付けるクラス
 */
public class KeyIn {
	// フィールド
	private BufferedReader br;
	
	// コンストラクタ
	public KeyIn() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * キーボードから文字列を受け取る
	 * @param msg
	 * @return
	 */
	public String readKey(String msg) {
		System.out.print(msg + "：");
		
		String buff = "";
		try {
			buff = br.readLine();
		} catch (IOException e) {
			System.out.println("入力エラーです");
		}
		return buff;
	}

	/**
	 * キーボードから文字列を受け取り、整数に変換する
	 * @param msg
	 * @return
	 */
	public int readInt(String msg) {
		int ret;	// 戻り値
		while (true) {		// 無限ループ
			try {
				ret = Integer.parseInt(readKey(msg));
				return ret;
			} catch (NumberFormatException e) {
				System.out.println("文字から整数値への変換エラーです。もう一度入力してください。");
			}
		}
	}

	/**
	 * キーボードから文字列を受け取り、小数に変換する
	 * @param msg
	 * @return
	 */
	public double readDouble(String msg) {
		double ret;	// 戻り値
		while (true) {		// 無限ループ
			try {
				ret = Double.parseDouble(readKey(msg));
				return ret;
			} catch (NumberFormatException e) {
				System.out.println("文字から小数値への変換エラーです。もう一度入力してください。");
			}
		}
	}
	
}// KeyIn
