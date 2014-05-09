import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.Properties;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

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
            
            while(true) {
                System.out.printf("watching for files in %s ... %n", input);
                final WatchKey key = watchService.take(); // blocking
                
                for (final WatchEvent<?> watchEvent : key.pollEvents()) {
                    Kind<?> kind = watchEvent.kind();
                    
                    if (StandardWatchEventKinds.OVERFLOW == kind) continue;

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> wePath = (WatchEvent<Path>)watchEvent; // safe to cast
                    Path path = wePath.context();
                    String inputName = path.toString();
                    FileObject inputFile = fsm.toFileObject(input.resolve(path).toFile());
                    FileObject outputFile = outputFolder.resolveFile(inputName);
                    inputFile.moveTo(outputFile);

                    System.out.printf("Moved file from %s to %s %n", inputFile, outputFile);
                }

                if (!key.reset()) break;
            }
        }
    }
}