package com.fortytwo.beerninja.model;

/**
 * To be implemented by the classes which needs notification about the game state.
 * @author raravind
 *
 */
public interface GameNotificationListener {
	/**
	 * Game finished and there is a winner.
	 * @param botName
	 * @param details
	 */
	void gameFinished(String botName, String details);
	/**
	 * Unable to execute the game due to internal error.
	 * @param error
	 */
	void gameError(String error);
	/**
	 * Game is aborted due to timeout.
	 */
	void gameTimeout();
}
