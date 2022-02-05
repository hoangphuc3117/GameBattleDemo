package my.test;

import java.util.Timer;
import java.util.TimerTask;

import com.smartfoxserver.v2.entities.data.SFSObject;

import util.Commands;
import util.Constants;

public class GameController extends Thread {
	/** Reference to main extension */
	private BattleDemoExtension extension = null;

	/** Continue flag */
	private boolean battleRunning = true;

	/** Timer is used for waiting the player */
	private boolean startTimer = false;

	private Timer timer = null;

	public GameController(BattleDemoExtension extension) {
		this.extension = extension;
	}

	/**
	 * Main run method
	 */
	public void run() {
		while (battleRunning) {
			switch (this.extension.getWhoseTurn()) {
			case 0:
				cancelTimer();

				// New turn
				this.extension.increaseTurn();
				this.extension.trace("Turn " + this.extension.getTurn());
				// Inform the player to new turn
				SFSObject sfsObj = new SFSObject();
				sfsObj.putInt(Constants.BATTLE_TURN, this.extension.getTurn());
				this.extension.send(Commands.CMD_NEXT_TURN, sfsObj, this.extension.getPlayer1());

				// Player
				this.extension.teamAAttacks();
				if (!this.extension.checkBattleEnd()) {
					this.extension.setWhoseTurn(1);
				} else {
					this.extension.setWhoseTurn(-1);
					logCompleteTurn();
					battleRunning = false;
					this.extension.endBattle();
				}
				break;
			case 1:
				// Computer
				this.extension.teamBAttacks();
				this.extension.setWhoseTurn(-1);
				logCompleteTurn();
				if (this.extension.checkBattleEnd()) {
					battleRunning = false;
					this.extension.endBattle();
				}

				break;
			default:
				startTimer();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Halt this thread
				extension.trace("BattleFarm extension was halted");
			}
		}
	}

	private void startTimer() {
		if (!startTimer) {
			if (timer == null) {
				timer = new Timer();
			}
			startTimer = true;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (startTimer) {
						startTimer = false;
						GameController.this.extension.setWhoseTurn(0);
					}
				}
			}, Constants.COUNT_DOWN_TIME * 1000);
			
			// Trigger client to show timer count down
			SFSObject sfsObj = new SFSObject();
			sfsObj.putInt("COUNT_DOWN_TIME", Constants.COUNT_DOWN_TIME);
			this.extension.send(Commands.CMD_COUNT_DOWN_TIMER, sfsObj, this.extension.getPlayer1());
		}
	}

	private void cancelTimer() {
		if (startTimer) {
			if (timer != null) {
//				timer.cancel();
			}
			startTimer = false;
		}
	}

	private void logCompleteTurn() {
		this.extension.trace("Complete turn " + this.extension.getTurn());

		// Inform to the player complete turn
		SFSObject sfsObj = new SFSObject();
		sfsObj.putInt(Constants.BATTLE_TURN, this.extension.getTurn());
		this.extension.send(Commands.CMD_END_TURN, sfsObj, this.extension.getPlayer1());
	}

	public void setBattleRunning(boolean running) {
		this.battleRunning = running;
	}
}
