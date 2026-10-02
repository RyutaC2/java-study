/*
 * タイトル: chapter08
 * 作成者: J1A101 井口 隆太
 */

package chapter08;

/*
 * フィールドの宣言
 * クラスブロックの直下
 */
public class Hero {
	String name = "";	// 型 フィールド名;
	int hp = 100;			// 型 フィールド名;
	
	// final 型 定数名（大文字）;
	final int LEVEL = 10;
	
	/*
	 * 眠るメソッド
	 * hpを100にする
	 */
	public void sleep() {
		this.hp = 100;
		// ○○は眠って回復した！
		System.out.println(this.name + "は眠って回復した！");
	}
	
	/*
	 * 座るメソッド
	 * 座った秒数だけhpを回復
	 * 仮引数secで秒数を受け取る
	 */
	public void sit(int sec) {
		this.hp += sec;
		
		System.out.println(this.name + "は" + sec + "秒座った！");
		System.out.println("HPが" + sec + "ポイント回復した");
	} // sit()
	
	/*
	 * 転ぶメソッド
	 */
	public void slip() {
		int damage = 5;
		this.hp -= damage;
		System.out.println(this.name + "は転んだ！");
		System.out.println(damage + "のダメージ！");
	}
	
	public void run() {
		System.out.println(this.name + "は逃げ出した！");
		System.out.println("GAME OVER");
		System.out.println("最終HPは" + this.hp + "でした！");
	}
}
