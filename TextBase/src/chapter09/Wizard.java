/*
 * タイトル: chapter09
 * 作成者: J1A101 井口 隆太
 */

package chapter09;

/*
 * 魔法使いクラス
 */
public class Wizard {
	String name;	// 名前
	int hp;				// hp
	
	/*
	 * 回復メソッド
	 */
	public void heal(Hero h) {
		h.hp += 10;		// 10回復
		System.out.println(h.name + "のHPを10回復した！");
	}
}
