package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SaveService {

    public static final Path SAVES_DIR = Path.of("saves");
    public static final Path DEFAULT_SAVE_PATH = SAVES_DIR.resolve("latest.ser");

    private SaveService() {
    }

    /**
     * Returns the save file path for a given sport type, e.g.
     * saves/football_save.ser
     */
    public static Path getSavePath(SportType sportType) {
        String fileName = sportType.name().toLowerCase() + "_save.ser";
        return SAVES_DIR.resolve(fileName);
    }

    public static void save(GameState state, Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
            output.writeObject(state);
        }
    }

    public static GameState load(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
            Object loaded = input.readObject();
            if (!(loaded instanceof GameState state)) {
                throw new IOException("Kayıt dosyası GameState içermiyor.");
            }
            return state;
        }
    }

    /**
     * Lists all .ser save files in the saves directory.
     * Returns a list of Paths that can be loaded.
     */
    public static List<Path> listSaves() {
        List<Path> saves = new ArrayList<>();
        if (!Files.isDirectory(SAVES_DIR)) {
            return saves;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAVES_DIR, "*.ser")) {
            for (Path entry : stream) {
                saves.add(entry);
            }
        } catch (IOException ignored) {
            // Return empty list if directory cannot be read
        }
        return saves;
    }

    /**
     * Tries to read the sport type display name from a save file without fully
     * loading it.
     * Returns a human-readable description of the save.
     */
    public static String describesSave(Path path) {
        try {
            GameState state = load(path);
            String sport = state.getSelectedSport().getDisplayName();
            String team = state.getManagedTeam() == null ? "-" : state.getManagedTeam().getName();
            String week = "Hafta " + state.getCurrentWeek();
            String time = state.getLastSavedAt() == null ? "-" : state.getLastSavedAt().toString();
            return sport + " | " + team + " | " + week + " | " + time;
        } catch (IOException | ClassNotFoundException exception) {
            return path.getFileName().toString() + " (okunamadı)";
        }
    }
}
