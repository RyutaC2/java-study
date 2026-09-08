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
		
		// Step202クラスのインスタンス生成
		Step202 step202 = new Step202();
		
		// Step202クラスのdispClassNameメソッドを実行する
		step202.dispClassName();
		
		// 改行
		System.out.println();
		
		// Step203クラスのインスタンス生成
		Step203 s203 = new Step203();
		int returnInt = s203.receiveInt();
		System.out.println("Step203のreceiveIntメソッドからの戻り値: " + returnInt);
		System.out.println("Step203のreceiveStringメソッドからの戻り値: " + s203.receiveString());
		System.out.println("Step203のreturnCheckメソッドからの戻り値: " + s203.returnCheck());
		
		// 改行
		System.out.println();
		
		// Step204クラスのインスタンス生成
		Step204 s204 = new Step204();
		s204 .sendInt(100);
		s204.send2Int(10, 20);
		s204.reverseBoolean(true); // 真: true　偽: false
	}

}
