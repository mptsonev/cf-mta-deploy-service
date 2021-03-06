package com.sap.cloud.lm.sl.cf.core.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    public static void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new DeleteDirVisitor());
    }

    public static void copyDirectory(Path fromPath, Path toPath) throws IOException {
        Files.walkFileTree(fromPath, new CopyDirVisitor(fromPath, toPath));
    }

    public static void copyFile(Path fromPath, Path toPath) throws IOException {
        if (Files.notExists(toPath.getParent())) {
            Files.createDirectories(toPath.getParent());
        }
        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }
    
    private static class DeleteDirVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
            throw exc;
        }
    }

    private static class CopyDirVisitor extends SimpleFileVisitor<Path> {

        private Path fromPath;
        private Path toPath;

        CopyDirVisitor(Path fromPath, Path toPath) {
            this.fromPath = fromPath;
            this.toPath = toPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path targetPath = toPath.resolve(fromPath.relativize(dir));
            if (!Files.exists(targetPath)) {
                Files.createDirectory(targetPath);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }
    }
}
