package worldbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

public class WorldBackup {

    private List<String> fileList;
    private static String sourceFolder;
    private static String outputPath;
    private static String host;
    private static String password;
    private static int port;

    public WorldBackup() {
        this.fileList = new ArrayList<>();
        try {
            File config = new File("config.properties");
            Properties prop = new Properties();
            if (!config.exists()) {
                try (OutputStream output = new FileOutputStream(config)) {
                    prop.setProperty("rcon.host", "localhost");
                    prop.setProperty("rcon.port", "25575");
                    prop.setProperty("rcon.password", "password");
                    prop.setProperty("source.folder", "/home/username/minecraft/world");
                    prop.setProperty("output.path", "/home/username/backups");
                    prop.store(output, "WorldBackup configuration file." + System.lineSeparator() + "Make sure you use / when specifying file paths !!!" + System.lineSeparator() + "Created by: Pequla ( https://pequla.github.io/ )");
                }
            }
            try (InputStream input = new FileInputStream(config)) {
                prop.load(input);
                host = prop.getProperty("rcon.host");
                port = Integer.valueOf(prop.getProperty("rcon.port"));
                password = prop.getProperty("rcon.password");
                sourceFolder = prop.getProperty("source.folder");
                outputPath = prop.getProperty("output.path") + "/backup-" + getDate() + ".zip";
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        Rcon rcon = null;
        try {
            WorldBackup backup = new WorldBackup();
            rcon = new Rcon(host, port, password.getBytes());
            System.out.println("Backup has started...");
            rcon.command("tellraw @a [\"\",{\"text\":\"[WorldBackup] \",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"World backup started\"}]");
            rcon.command("save-off");
            rcon.command("tellraw @a [\"\",{\"text\":\"[WARNING]\",\"bold\":true,\"color\":\"red\"},{\"text\":\" Server might lag !\"}]");
            rcon.command("save-all");
            backup.generateFileList(new File(sourceFolder));
            backup.zipIt(outputPath);
            rcon.command("tellraw @a [\"\",{\"text\":\"[WorldBackup] \",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"World backup finished\"}]");
        } catch (IOException ex) {
            System.err.println("An error occured while connecting to the server...");
            ex.printStackTrace(System.out);
        } catch (AuthenticationException ex) {
            System.err.println("An error occured while trying to login...");
            ex.printStackTrace(System.out);
        } finally {
            try {
                rcon.command("save-on");
                rcon.disconnect();
            } catch (IOException ex) {
                System.err.println("An error occured while closing resources");
                System.err.println("More info: " + ex.getMessage());
            }
        }
    }

    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void generateFileList(File node) {
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(sourceFolder.length() + 1, file.length());
    }

    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(sourceFolder).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (String file : this.fileList) {
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(sourceFolder + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();
            System.out.println("Backup successfully finished");

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
