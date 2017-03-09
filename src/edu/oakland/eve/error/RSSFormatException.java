package edu.oakland.eve.error;

/**
 * Represents a malformed RSS feed
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class RSSFormatException extends Exception {
	public RSSFormatException(String message) { super(message); }
}
