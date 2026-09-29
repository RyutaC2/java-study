/*
 * タイトル: 練習問題7-1
 * 作成者: J1A101 井口 隆太
 */

package step7;

import tools.KeyIn;

/*
 * 例外処理の練習用クラス
 */
public class Step701 {
	
	KeyIn key; // フィールド変数
	
	public Step701() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	/*
	 * 配列の要素外を参照する
	 */
	public void method1() {
		// java入門
		String[] teacher = {"谷村", "中垣内", "村野", "佐野", "梶野", "槇島"};
		
		// 配列の添え字10を表示する処理
		System.out.println(teacher[10]);
	}
	
	public void method2() {
		// java入門
		String[] teacher = {"谷村", "中垣内", "村野", "佐野", "梶野", "槇島"};
		
		// 配列の添え字10を表示する処理
		try { // とりあえずこの中の処理を実行
			System.out.println(teacher[10]);
		
		// tryでエラーが起きたらcatchブロックに移行
		} catch (ArrayIndexOutOfBoundsException e) {
			
			System.out.println("配列の要素の範囲外を指定しました\n");
			System.out.println("エラー内容: " + e.getMessage());
			e.printStackTrace();
		} // catchブロック
	} // method2ブロック
	
	/*
	 * 文字列を数字に変換する際の例外
	 */
	public void method3() {
		// キーボードから数字を入力させる
		String input = key.readKey("数字を入力してください");
		// 数字に変換する
		Integer.parseInt(input);
	}
	
	/*
	 * 文字列を数字に変換する際の例外（例外処理）
	 */
	public void method4() {
		String[] teacher = {"谷村", "中垣内", "村野", "佐野", "梶野", "槇島"};
		// キーボードから数字を入力させる
		String input = key.readKey("数字を入力してください");
		
		try {
			int inputInt = Integer.parseInt(input);
			System.out.println(teacher[inputInt]);
		} catch (NumberFormatException e) {
			System.out.println("数字以外が入力されました\n");
			System.out.println("エラー内容: " + e.getMessage());
		
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("配列の要素の範囲外を指定しました\n");
			System.out.println("エラー内容: " + e.getMessage());
		
		} finally {
			System.out.println("どちらの場合でも実行される内容");
		}
	}
}
