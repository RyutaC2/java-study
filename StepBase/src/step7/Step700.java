/*
 * タイトル: 実行ファイル
 * 作成者: J1A101 井口 隆太
 */

package step7;

public class Step700 {

	public static void main(String[] args) {
		Step701 s701 = new Step701();
		// s701.method1();
		// s701.method2();
		//s701.method3();
		//s701.method4();
		
		Step702 s702 = new Step702();
		try {
			s702.method1();
		} catch (NumberFormatException e) {
			System.out.println("変換できませんでした");
		}
		
		s702.method2();
	}

}
