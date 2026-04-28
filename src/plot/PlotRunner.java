package plot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

/**
 * PlotRunner — appelle plot_scores.py via ProcessBuilder.
 *
 * Usage :
 *   PlotRunner.generate(
 *       Path.of("outputs/scores.csv"),
 *       Path.of("outputs/plots")
 *   );
 */

public class PlotRunner {

    private static final String PYTHON = "python3";
    private static final String SCRIPT = "src/plot/plot_scores.py";

    public static void generate(Path csvPath, Path configPath, Path outputDir) throws IOException, InterruptedException {
        List<String> command = List.of(
                PYTHON,
                SCRIPT,
                csvPath.toAbsolutePath().toString(),
                configPath.toAbsolutePath().toString(),
                outputDir.toAbsolutePath().toString()
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // stderr fusionné dans stdout

        Process process = pb.start();

        // Lire la sortie du script
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[plot_scores] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("plot_scores.py a échoué avec le code : " + exitCode);
        }
    }
}
