package my.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import my.test.model.ArmyModel;
import util.Commands;
import util.Constants;

public class BattleDemoExtension extends SFSExtension {

	private User player1;

	private List<ArmyModel> teamA;

	private List<ArmyModel> teamB;

	private boolean isTeamAWon = false;

	private boolean isTeamBWon = false;

	private int turn = 0;

	private int whoseTurn = -1; // 0 player, 1 computer

	private GameController gameController;

	@Override
	public void init() {
		teamA = new ArrayList<>();
		teamB = new ArrayList<>();

		// MARK: REGISTER EVENT HANDLERS
		addEventHandler(SFSEventType.USER_JOIN_ROOM, JoinBattleHandler.class);

		// MARK: REGISTER REQUEST HANDLERS
		addRequestHandler(Commands.CMD_START_BATTLE, StartGameHandler.class);
		addRequestHandler(Commands.CMD_END_TURN, EndTurnHandler.class);
	}

	public void startBattle() {
		trace("Battle started");
		turn = 0;
		gameController = new GameController(this);
		gameController.start();

		send(Commands.CMD_START_BATTLE, null, player1);
	}

	public void endBattle() {
		trace("Battle ended");

		gameController.setBattleRunning(false);
		gameController = null;
		
		if (isTeamAWon) {
			trace("Team A won");
		} else {
			trace("Team B won");
		}

		// Send result
		SFSObject sfsObject = new SFSObject();
		sfsObject.putBool(Constants.BATTLE_PLAYER_WON, isTeamAWon);
		send(Commands.CMD_END_BATTLE, sfsObject, player1);
	}

	public void teamAAttacks() {
		trace("Team A attacks:");
		teamAttacks(teamA, teamB);
	}

	public void teamBAttacks() {
		trace("Team B attacks:");
		teamAttacks(teamB, teamA);
	}

	public boolean checkBattleEnd() {
		boolean teamBWon = true;
		for (int i = 0; i < teamA.size(); i++) {
			if (teamA.get(i).getHeal() > 0) {
				teamBWon = false;
				break;
			}
		}

		boolean teamAWon = true;
		for (int i = 0; i < teamB.size(); i++) {
			if (teamB.get(i).getHeal() > 0) {
				teamAWon = false;
				break;
			}
		}

		isTeamAWon = teamAWon;
		isTeamBWon = teamBWon;

		if (isTeamAWon || isTeamBWon) {
			return true;
		}
		
		return false;
	}

	// team 1: attacker, team 2: attacked
	private void teamAttacks(List<ArmyModel> team1, List<ArmyModel> team2) {
		for (int i = 0; i < team1.size(); i++) {
			if (teamA.get(i).getHeal() > 0) {
				for (int j = 0; j < team2.size(); j++) {
					if (team1.get(j).getHeal() > 0) {
						ArmyModel attacker = team1.get(i);
						ArmyModel attacked = team2.get(j);

						int attackerStrong = attacker.getStrong();

						int attackedShield = attacked.getShield();
						int attackedHeal = attacked.getHeal();
						attacked.setHeal(attackedHeal - (attackerStrong - attackedShield));

						// Log attack result
						trace("Attacker = " + attacker.getName() + " , Strength = " + attackerStrong + " | Attcacked = "
								+ attacked.getName() + ", heal = " + attackedHeal + ", shield = " + attackedShield
								+ ", new Heal = " + attacked.getHeal());

						// Send to client for attack result
						SFSObject sfsObj = new SFSObject();
						sfsObj.putInt(Constants.ATTACKER_ID, attacker.getId());
						sfsObj.putInt(Constants.ATTACKED_ID, attacked.getId());
						sfsObj.putInt(Constants.ATTACKED_NEW_HEAL, attacked.getHeal());
						if (whoseTurn == 0) {
							send(Commands.CMD_PLAYER_ATTACK, sfsObj, player1);
						} else {
							send(Commands.CMD_COMPUTER_ATTACK, sfsObj, player1);
						}
						break;
					}
				}

				continue;
			}
		}
	}

	public void initialBattleInfo() {
		teamA.clear();
		teamB.clear();		
		// TEAM A
		ArmyModel army1 = new ArmyModel();
		army1.setId(Constants.ARMY_1_ID);
		army1.setName("A1");
		army1.setType("Army");
		army1.setHeal(300);
		army1.setStrong(100);
		army1.setShield(20);

		ArmyModel army2 = new ArmyModel();
		army2.setId(Constants.ARMY_2_ID);
		army2.setName("A2");
		army2.setType("Army");
		army2.setHeal(300);
		army2.setStrong(150);
		army2.setShield(20);

		ArmyModel army3 = new ArmyModel();
		army3.setId(Constants.ARMY_3_ID);
		army3.setName("A3");
		army3.setType("Army");
		army3.setHeal(300);
		army3.setStrong(200);
		army3.setShield(20);

		teamA.add(army1);
		teamA.add(army2);
		teamA.add(army3);

		// TEAM B
		ArmyModel demon1 = new ArmyModel();
		demon1.setId(Constants.Demon_1_ID);
		demon1.setName("Demon1");
		demon1.setType("Demon");
		demon1.setHeal(100);
		demon1.setStrong(10);
		demon1.setShield(5);

		ArmyModel demon2 = new ArmyModel();
		demon2.setId(Constants.Demon_2_ID);
		demon2.setName("Demon2");
		demon2.setType("Demon");
		demon2.setHeal(100);
		demon2.setStrong(30);
		demon2.setShield(30);

		ArmyModel demon3 = new ArmyModel();
		demon3.setId(Constants.Demon_3_ID);
		demon3.setName("Demon3");
		demon3.setType("Demon");
		demon3.setHeal(100);
		demon3.setStrong(30);
		demon3.setShield(20);

		teamB.add(army1);
		teamB.add(army2);
		teamB.add(army3);

		ISFSObject respObj = new SFSObject();
		Gson gson = new Gson();
		String jsonTeamA = gson.toJson(teamA);
		respObj.putText(Constants.BATTLE_TEAN_A_ARMY, jsonTeamA);
		String jsonTeamB = gson.toJson(teamB);
		respObj.putText(Constants.BATTLE_TEAN_B_ARMY, jsonTeamB);

		// Send team info to the player
		send(Commands.CMD_TEAM_INFO, respObj, player1);
	}

	@Override
	public void destroy() {
		gameController.setBattleRunning(false);
		gameController = null;
	}

	public User getPlayer1() {
		return player1;
	}

	public void setPlayer1(User player1) {
		this.player1 = player1;
	}

	public List<ArmyModel> getTeamA() {
		return teamA;
	}

	public void setTeamA(List<ArmyModel> teamA) {
		this.teamA = teamA;
	}

	public List<ArmyModel> getTeamB() {
		return teamB;
	}

	public void setTeamB(List<ArmyModel> teamB) {
		this.teamB = teamB;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void increaseTurn() {
		this.turn += 1;
	}

	public boolean isTeamAWon() {
		return isTeamAWon;
	}

	public void setTeamAWon(boolean isTeamAWon) {
		this.isTeamAWon = isTeamAWon;
	}

	public boolean isTeamBWon() {
		return isTeamBWon;
	}

	public void setTeamBWon(boolean isTeamBWon) {
		this.isTeamBWon = isTeamBWon;
	}

	public int getWhoseTurn() {
		return whoseTurn;
	}

	public void setWhoseTurn(int whoseTurn) {
		this.whoseTurn = whoseTurn;
	}

}
