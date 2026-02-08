package database;

import model.Command;
import exceptions.DatabaseStoppedException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandExecutor implements Runnable {
    private final InMemoryDatabase<String> database;
    private final BlockingQueue<Command> commandQueue;
    private final AtomicInteger processedCommands;
    
    public CommandExecutor(InMemoryDatabase<String> database, 
                          BlockingQueue<Command> commandQueue,
                          AtomicInteger processedCommands) {
        this.database = database;
        this.commandQueue = commandQueue;
        this.processedCommands = processedCommands;
    }
    
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " started");
        
        while (true) {
            try {
                Command command = commandQueue.poll(100, TimeUnit.MILLISECONDS);
                
                if (command == null) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    continue;
                }
                
                if (command.getType() == model.CommandType.EXIT) {
                    break;
                }
                
                executeCommand(command, threadName);
                processedCommands.incrementAndGet();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println(threadName + " terminated");
    }
    
    private void executeCommand(Command command, String threadName) {
        System.out.println(threadName + " executing: " + command);
        
        try {
            switch (command.getType()) {
                case PUT:
                    database.put(command.getKey(), command.getRawValue(), command.getTtl());
                    break;
                    
                case GET:
                    String value = database.get(command.getKey());
                    if (value != null) {
                        System.out.println("Value for key " + command.getKey() + ": " + value);
                    }
                    break;
                    
                case DELETE:
                    database.delete(command.getKey());
                    break;
                    
                case STOP:
                    database.stop();
                    break;
                    
                case START:
                    database.start();
                    break;
                    
                default:
                    System.err.println("Unknown command type: " + command.getType());
            }
        } catch (DatabaseStoppedException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
