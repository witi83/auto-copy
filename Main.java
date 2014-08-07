import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import java.nio.file.*;
import java.util.Properties;

public class Main {
    public static void main(String... args) throws Exception {
        final Properties props = new Properties();
        props.load(Main.class.getResourceAsStream("config.conf"));
        final Path input = Paths.get(props.getProperty("input"));
        final String output = props.getProperty("output");
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(new FileSystemOptions(), true);
        final FileSystemManager fsm = VFS.getManager();
        final FileObject outputFolder = fsm.resolveFile(output);

        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            input.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                System.out.printf("watching for files in %s ... %n", input);
                final WatchKey key = watchService.take(); // blocking
                key.pollEvents().stream()
                        .filter(event -> event.kind() != StandardWatchEventKinds.OVERFLOW)
                        .map(event -> (Path) event.context())
                        .forEach(path -> {
                            try {
                                String inputName = path.toString();
                                FileObject inputFile = fsm.toFileObject(input.resolve(path).toFile());
                                FileObject outputFile = outputFolder.resolveFile(inputName);
                                inputFile.moveTo(outputFile);
                                System.out.printf("Moved file from %s to %s %n", inputFile, outputFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                if (!key.reset()) break;
            }
        }
    }
}