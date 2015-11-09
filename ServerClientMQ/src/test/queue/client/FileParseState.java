package test.queue.client;

public enum FileParseState {
	WAIT_FILE,
	WAIT_PARSE,
	PARSING,
	PARSE_ERROR,
	PARSED,
	TRANSMIT_ERROR,
	TRANSMIT_COMPLETE
}
