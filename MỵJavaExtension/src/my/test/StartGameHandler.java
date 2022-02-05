package my.test;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class StartGameHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject event) {
		((BattleDemoExtension)getParentExtension()).startBattle();
	}

}
