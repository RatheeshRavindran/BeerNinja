package com.fortytwo.beerninja.engine;

import com.fortytwo.beerninja.model.client.Move;

/**
 * This will be implemented by the class which needs to know when the GUI animation (moving bots to new position) is completed.
 * @author raravind
 *
 */
public interface GUINotifictaion {
	/**
	 * Method is invoked when the animation is finished.
	 * @param botName
	 * @param direction
	 */
	void animationCompleted(String botName, Move direction);
}
