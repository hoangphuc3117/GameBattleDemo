package my.test;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class EndTurnHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		// The player's ready for a new turn.
		((BattleDemoExtension)getParentExtension()).setWhoseTurn(0);
	}

}
