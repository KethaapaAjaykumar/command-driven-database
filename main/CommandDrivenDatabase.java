package main;

import model.Command;
import parser.CommandParser;
import database.InMemoryDatabase;
import database.CommandExecutor;
import exceptions.InvalidCommandException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandDrivenDatabase {
    public static void main(String[] args) {
        System.out.println("=== Command-Driven In-Memory Database ===");
        System.out.println("Supported commands:");
        System.out.println("  PUT <key> <value> [ttl]");
        System.out.println("  GET <key>");
        System.out.println("  DELETE <key>");
        System.out.println("  STOP");
        System.out.println("  START");
        System.out.println("  EXIT");
        System.out.println("==========================================");
        
        InMemoryDatabase<String> database = new InMemoryDatabase<>();
        BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
        AtomicInteger processedCommands = new AtomicInteger(0);
        
        // Create executor threads
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(new CommandExecutor(database, commandQueue, processedCommands));
        }
        
        // Main thread for reading input
        Scanner scanner = new Scanner(System.in);
        
        try {
            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                try {
                    Command command = CommandParser.parse(input);
                    
                    if (command.getType() == model.CommandType.EXIT) {
                        // Add EXIT command for each executor
                        for (int i = 0; i < threadCount; i++) {
                            commandQueue.put(new Command(model.CommandType.EXIT));
                        }
                        break;
                    }
                    
                    commandQueue.put(command);
                    
                } catch (InvalidCommandException e) {
                    System.err.println("Command error: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            // Clean shutdown
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            
            database.shutdown();
            scanner.close();
            
            System.out.println("\n=== Database Statistics ===");
            System.out.println("Total commands processed: " + processedCommands.get());
            System.out.println("Final database size: " + database.size());
            System.out.println("Database shutdown complete.");
        }
    }
}
