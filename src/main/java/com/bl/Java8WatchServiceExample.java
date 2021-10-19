package com.bl;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Java8WatchServiceExample {
    private final WatchService watcher;

    private final Map<WatchKey, Path> dirwatchers;

    public Java8WatchServiceExample(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dirwatchers = new HashMap<WatchKey, Path>();
        scanAndRegisterDirectories(dir);


    }

    public void registerDirWatchers(Path dir) {
        WatchKey key = dir.register(watcher, ENTERY_CREATE, ENTERY_DELETE, ENTERY_MODIFY);
        dirwatchers.put(key, dir);
    }

    private void scanAndRegisterDirectories(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVistDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirWatchers(dir);
                return FileVisitResult.CONTINUE;
            }


        });

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void processEvents() throws IOException {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();

            } catch (InterruptedException x) {
                return;
            }

            Path dir = dirwatchers.get(key);
            if (dir == null) continue;
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                Path name = ((WatchEvent<Path>) event).context();
                Path child = dir.resolve(name);
                System.out.format("%s: %s\n", event.kind().name(), child);

                if (kind == ENTERY_CREATE) {
                    try {
                        if (Files.isDirectory(child)) scanAndRegisterDirectories(child);
                    } catch (IOException x) {

                    }
                } else if (kind.equals(ENTERY_DELETE)) {
                    if (Files.isDirectory(child)) dirwatchers.remove(key);
                }


            }
            boolean valid = key.reset();
            if (!valid) {
                dirwatchers.remove(key);
                if (dirwatchers.isEmpty()) break;
            }


        }
    }
}