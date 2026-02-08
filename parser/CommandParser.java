package parser;

import model.Command;
import model.CommandType;
import exceptions.InvalidCommandException;
import exceptions.InvalidTTLException;

public class CommandParser {
    
    public static Command parse(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException("Empty command");
        }
        
        String[] tokens = input.trim().split("\\s+");
        String commandStr = tokens[0].toUpperCase();
        
        try {
            CommandType type = CommandType.valueOf(commandStr);
            
            switch (type) {
                case PUT:
                    return parsePutCommand(tokens);
                case GET:
                case DELETE:
                    return parseSingleKeyCommand(type, tokens);
                case STOP:
                case START:
                case EXIT:
                    return parseSimpleCommand(type, tokens);
                default:
                    throw new InvalidCommandException("Unknown command: " + commandStr);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Invalid command: " + commandStr);
        }
    }
    
    private static Command parsePutCommand(String[] tokens) throws InvalidCommandException {
        if (tokens.length < 3 || tokens.length > 4) {
            throw new InvalidCommandException("PUT requires 2 or 3 arguments: PUT <key> <value> [ttl]");
        }
        
        try {
            Integer key = Integer.parseInt(tokens[1]);
            String value = tokens[2];
            Long ttl = null;
            
            if (tokens.length == 4) {
                ttl = Long.parseLong(tokens[3]);
                if (ttl <= 0) {
                    throw new InvalidTTLException("TTL must be positive: " + ttl);
                }
            }
            
            return new Command(CommandType.PUT, key, value, ttl);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid key or TTL format");
        }
    }
    
    private static Command parseSingleKeyCommand(CommandType type, String[] tokens) 
            throws InvalidCommandException {
        if (tokens.length != 2) {
            throw new InvalidCommandException(type + " requires 1 argument: " + type + " <key>");
        }
        
        try {
            Integer key = Integer.parseInt(tokens[1]);
            return new Command(type, key, null, null);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid key format: " + tokens[1]);
        }
    }
    
    private static Command parseSimpleCommand(CommandType type, String[] tokens) 
            throws InvalidCommandException {
        if (tokens.length != 1) {
            throw new InvalidCommandException(type + " takes no arguments");
        }
        return new Command(type);
    }
}
