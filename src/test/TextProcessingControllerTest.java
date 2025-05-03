package test;

import controller.TextProcessorController;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

public class TextProcessingControllerTest {

    private TextProcessorController controller;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        controller = new TextProcessorController();
        tempFile = Files.createTempFile("test_output", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReplaceText_basic() {
        String input = "Hello World";
        String regex = "World";
        String replacement = "Java";

        String result = controller.replaceText(input, regex, replacement);
        assertEquals("Hello Java", result);
    }

    @Test
    void testReplaceText_regexPattern() {
        String input = "cat bat mat";
        String regex = "[cb]at";
        String replacement = "rat";

        String result = controller.replaceText(input, regex, replacement);
        assertEquals("rat rat mat", result);
    }

    @Test
    void testReplaceText_emptyInput() {
        String input = "";
        String regex = "foo";
        String replacement = "bar";

        String result = controller.replaceText(input, regex, replacement);
        assertEquals("", result);
    }

    @Test
    void testReplaceText_nullInputThrows() {
        assertThrows(NullPointerException.class, () -> {
            controller.replaceText(null, "a", "b");
        });
    }

    @Test
    void testReplaceAndWriteToFile_writesCorrectly() throws IOException {
        String input = "apple banana";
        String regex = "banana";
        String replacement = "grape";

        controller.replaceAndWriteToFile(input, regex, replacement, tempFile.toString());

        String content = Files.readString(tempFile);
        assertEquals("apple grape", content);
    }

    @Test
    void testReplaceAndWriteToFile_emptyFilePathThrows() {
        assertThrows(NullPointerException.class, () -> {
            controller.replaceAndWriteToFile("a", "a", "b", null);
        });
    }

    @Test
    void testReplaceAndWriteToFile_invalidPathThrows() {//will fail
        String invalidPath = "/non/existing/path/output.txt";
        assertThrows(IOException.class, () -> {
            controller.replaceAndWriteToFile("text", "t", "T", invalidPath);
        });
    }
}

