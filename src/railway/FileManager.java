package railway;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Reads and writes simple pipe-delimited TXT files in the local data folder. */
public class FileManager {
    private final Path folder;

    public FileManager() {
        this(Paths.get("data"));
    }

    public FileManager(Path folder) {
        this.folder = folder;
        try {
            Files.createDirectories(folder);
            createIfMissing("trains.txt");
            createIfMissing("passengers.txt");
            createIfMissing("schedules.txt");
            createIfMissing("reservations.txt");
        } catch (IOException e) {
            throw new IllegalStateException("Could not create the data folder: " + e.getMessage(), e);
        }
    }

    private void createIfMissing(String name) throws IOException {
        Path p = folder.resolve(name);
        if (!Files.exists(p)) Files.createFile(p);
    }

    public List<String> readLines(String fileName) throws IOException {
        Path p = folder.resolve(fileName);
        if (!Files.exists(p)) return new ArrayList<>();
        return Files.readAllLines(p, StandardCharsets.UTF_8);
    }

    public void writeLines(String fileName, List<String> lines) throws IOException {
        Path target = folder.resolve(fileName);
        Path temp = folder.resolve(fileName + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Path getFolder() { return folder; }
}
