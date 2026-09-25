/*
 * タイトル: 練習問題6-2
 * 作成者: J1A101 井口 隆太
 */

package step6;

import java.util.Random;

import tools.KeyIn;

/*
 * ジャンケンゲーム
 */
public class Step602 {
	KeyIn key; // フィールド変数
	
	/*
	 * コンストラクタ
	 * インスタンス生成時に最初に動く
	 */
	public Step602() {
		// KeyInクラスのインスタンスを生成
		key = new KeyIn();
	}
	
	/*
	 * ジャンケンメソッド
	 */
	public void RockPaperScissors() {
		String[] hand = {"グー", "チョキ", "パー"};
		String[] judge = {"あいこ", "負け", "勝ち"};
		
		int playerHand = 999;
		
		Random rand = new Random();
		int enemyHand = rand.nextInt(3);
		
		int result = 0;
		
		String input = key.readKey("グーチョキパーのどれかを入力");
		
		// プレイヤーの手を0~2に変換
		for (int i = 0; i < hand.length; i++) {
			// もし、入力値が配列の要素と同じなら
			if (input.equals(hand[i])) {
				// プレイヤーの手を数値として保管
				playerHand = i;
			}
		}
		
		// もし、playerHandが999のままなら入力値がおかしい
		if (playerHand == 999) {
			System.out.println("入力エラーです");
		} else { // それ以外なら、正しく変換ができたということなので
			// 今回の手を出力
			System.out.println("自分の手: " + hand[playerHand]);
			System.out.println("相手の手: " + hand[enemyHand]);
			
			// 今回の勝敗を計算する
			result = playerHand - enemyHand;
			// 勝敗を出力する
			System.out.println(judge[result]);
		}
	}
}
