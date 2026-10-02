/*
 * タイトル: 実行ファイル
 * 作成者: J1A101 井口 隆太
 */

package chapter09;

/*
 * 実行クラス
 */
public class Main {
	
	/*
	 * メインメソッド
	 */
	public static void main(String[] args) {
		
		// 1. 勇者を生み出す
		Hero hero = new Hero();
		
		// 2. 名前とhpを設定する
		hero.hp = 100;
		hero.name = "田中平蔵";
		
		// 3. 剣を生み出す
		Sword sword = new Sword();
		
		// 4. 名前とダメージを設定する
		sword.name = "DXおもちゃ竜撃剣";
		sword.damage = 110;
		
		// 5. 勇者に剣を装備する
		hero.sword = sword;
		
		// 6. 勇者の剣の名前を表示する
		System.out.println("現在の装備は" + sword.name);
		
		// 7. 別の勇者を生み出す
		Hero hero2 = new Hero();
		hero2.name = "上岡権兵衛";
		hero2.hp = 50;
		
		// 8. 魔法使いを生み出す
		Wizard wizard = new Wizard();
		wizard.name = "高田鼎";
		wizard.hp = 1;
		
		// 9. 回復する
		wizard.heal(hero);
		wizard.heal(hero2);
		
		// 6. 指示を出す
		// ①5秒座る
		hero.sit(5);
		
		// ②転ぶ
		hero.slip();
		
		// ③25秒座る
		hero.sit(25);
		
		// ④逃げる
		hero.run();
	}
}
