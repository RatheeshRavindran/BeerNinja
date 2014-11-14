package com.fortytwo.beerninja.model.client;

/**
 * Exception thrown when the passed argument is not valid.
 * This can normally happen when making an invalid move.
 * 
 * @author raravind
 *
 */
public class InvalidArgumentException extends Exception {
	public InvalidArgumentException(String message) {
		super(message);
	}
}
