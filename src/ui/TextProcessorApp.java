package ui;

import controller.TextProcessorController;
import exceptions.FileProcessingException;
import exceptions.InvalidRegexPatternException;
import exceptions.NoPatternSelectedException;
import exceptions.SummarizationException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import utils.AlertUtils;
import utils.CollectionManager;
//import utils.UIFactory;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class TextProcessorApp extends Application {
    private final TextProcessorController controller = new TextProcessorController();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Text Processor Tool");

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Enter text here");

        ComboBox<String> regexDropdown = new ComboBox<>();
        regexDropdown.getItems().addAll("Email", "URL", "Dates");
        regexDropdown.setPromptText("Select a Regex Pattern");

        TextField customRegexField = new TextField();
        customRegexField.setPromptText("Or enter a custom pattern");

        HBox regexBox = new HBox(10, regexDropdown, customRegexField);

        TextField replacementField = new TextField();
        replacementField.setPromptText("Enter replacement text (optional)");

//        TextField filePathField = new TextField();
//        filePathField.setPromptText("Enter file path");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        ComboBox<String> cleanupOptions = new ComboBox<>() ;
        cleanupOptions.getItems().addAll("Trim & Lowercase", "Remove Empty Lines", "Uppercase Lines");
        cleanupOptions.setPromptText("Select Cleanup/Formatting");

        final File[] uploadedFile = new File[1];

        Button matchBtn = UIFactory.createStyledButton("Find Matches");
        matchBtn.setOnAction(e -> {
            String text = inputArea.getText();
            String customPattern = customRegexField.getText();
            String selectedPattern = regexDropdown.getValue();
            try {
                List<String> results = controller.findMatches(text, selectedPattern, customPattern);
                outputArea.setText(String.join("\n", results));
            } catch (NoPatternSelectedException ex) {
                AlertUtils.showError(ex.getMessage());
            } catch (InvalidRegexPatternException ex) {
                AlertUtils.showError("Invalid regex pattern: " + ex.getMessage());
            } catch (Exception ex) {
                AlertUtils.showError("No regex pattern provided or selected");
            }
        });

        Button replaceBtn = UIFactory.createStyledButton("Replace Text");
        replaceBtn.setOnAction(e -> {
            String text = inputArea.getText();
            String regex = customRegexField.getText();
            String replacement = replacementField.getText();
            outputArea.setText(controller.replaceText(text, regex, replacement));
        });

        Button frequencyBtn = UIFactory.createStyledButton("Word Frequency");
        frequencyBtn.setOnAction(e -> {
            String text = inputArea.getText();
            outputArea.setText(controller.wordFrequency(text).toString());
        });

        Button uploadFileBtn = UIFactory.createStyledButton("Upload File");
        Button processBtn = UIFactory.createStyledButton("Process File");
        Button exportBtn = UIFactory.createStyledButton("Export Processed File");
        processBtn.setDisable(true);
        exportBtn.setDisable(true);

        uploadFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload Text File");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    inputArea.setText(controller.readFile(file.getAbsolutePath()));
                    uploadedFile[0] = file;
                    processBtn.setDisable(false);
                } catch (FileProcessingException ex) {
                    AlertUtils.showError("File error: " + ex.getMessage());
                } catch (Exception ex) {
                    AlertUtils.showError("Unexpected file read error: " + ex.getMessage());
                }
            }
        });

        processBtn.setOnAction(e -> {
            if (uploadedFile[0] == null) {
                AlertUtils.showError("Please upload a file first.");
                return;
            }

            try {
                Predicate<String> filter = line -> true;
                Function<String, String> transformer = Function.identity();

                String option = cleanupOptions.getValue();
                if (option != null) {
                    switch (option) {
                        case "Trim & Lowercase" -> transformer = line -> line.trim().toLowerCase();
                        case "Remove Empty Lines" -> filter = line -> !line.trim().isEmpty();
                        case "Uppercase Lines" -> transformer = String::toUpperCase;
                    }
                }

                List<String> result = controller.batchProcessFile(uploadedFile[0].getAbsolutePath(), filter, transformer);
                outputArea.setText(String.join("\n", result));
                exportBtn.setDisable(false);

                Label summaryLabel = new Label();
                long startTime = System.currentTimeMillis();
                long timeTaken = System.currentTimeMillis() - startTime;
                summaryLabel.setText("Lines Processed: " + result.size() + " | Time Taken: " + timeTaken + " ms");

            } catch (FileProcessingException ex) {
                AlertUtils.showError("Processing error: " + ex.getMessage());
            } catch (Exception ex) {
                AlertUtils.showError("Unexpected processing error: " + ex.getMessage());
            }
        });

        exportBtn.setOnAction(e -> {
            FileChooser saveChooser = new FileChooser();
            saveChooser.setTitle("Save Processed File");
            File saveFile = saveChooser.showSaveDialog(primaryStage);
            if (saveFile != null) {
                try {
                    controller.writeFile(saveFile.getAbsolutePath(), outputArea.getText());
                    AlertUtils.showInfo("File saved successfully!");
                } catch (Exception ex) {
                    AlertUtils.showError("Error saving file: " + ex.getMessage());
                }
            }
        });

        Button replaceFileBtn = UIFactory.createStyledButton("Write To File");
        replaceFileBtn.setOnAction(e -> {
            // Validate inputArea and replacementField
            String inputText = inputArea.getText();
            String replacementText = replacementField.getText();

            if (inputText == null || inputText.trim().isEmpty()) {
                AlertUtils.showError("Error: Input text area is empty.");
                return;
            }

            if (replacementText == null || replacementText.trim().isEmpty()) {
                AlertUtils.showError("Error: Replacement field is empty.");
                return;
            }

            // Proceed to file save
            FileChooser saveChooser = new FileChooser();
            saveChooser.setTitle("Save Processed File");
            File saveFile = saveChooser.showSaveDialog(primaryStage);

            if (saveFile == null) {
                outputArea.setText("Save operation was canceled.");
                return;
            }

            try {
                String result = controller.replaceAndWriteToFile(
                        inputText,
                        customRegexField.getText(),
                        replacementText,
                        saveFile.getAbsolutePath()
                );
                outputArea.setText(result);
            } catch (Exception ex) {
                AlertUtils.showError("Error: " + ex.getMessage());
            }
        });


        Spinner<Integer> sentenceCountSpinner = new Spinner<>(1, 10, 3);

        Button summarizeBtn = UIFactory.createStyledButton("Summarize Text");
        summarizeBtn.setOnAction(e -> {
            try {
                String input = inputArea.getText();
                if (input == null || input.trim().isEmpty()) {
                    AlertUtils.showError("Please enter or upload some text to summarize.");
                    return;
                }
                int numSentences = sentenceCountSpinner.getValue();
                String summary = controller.summarizeText(input, numSentences);
                outputArea.setText(summary);
            } catch (SummarizationException ex) {
                AlertUtils.showError("Summarization failed: " + ex.getMessage());
            } catch (Exception ex) {
                AlertUtils.showError("Unexpected error during summarization: " + ex.getMessage());
            }
        });

        Button clearBtn = UIFactory.createStyledButton("Clear/Erase");
        clearBtn.setOnAction(e -> inputArea.clear());

        // Collection Manager Setup to save reusable text entries
        CollectionManager collectionManager = new CollectionManager();
        ListView<String> collectionListView = new ListView<>();
        collectionListView.setPrefHeight(100);
        TextField collectionInput = new TextField();
        collectionInput.setPromptText("Enter text to save in list");

        Button addEntryBtn = UIFactory.createStyledButton("Add Entry");
        Button updateEntryBtn = UIFactory.createStyledButton("Update Entry");
        Button deleteEntryBtn = UIFactory.createStyledButton("Delete Entry");

        addEntryBtn.setOnAction(e -> {
            String text = collectionInput.getText();
            if (text == null || text.trim().isEmpty()) {
                AlertUtils.showError("Entry cannot be empty.");
                return;
            }
            collectionManager.addEntry(text);
            collectionInput.clear();
            collectionListView.getItems().setAll(collectionManager.getAllEntries());
        });

        updateEntryBtn.setOnAction(e -> {
            int selectedIndex = collectionListView.getSelectionModel().getSelectedIndex();
            String newText = collectionInput.getText();

            if (selectedIndex == -1) {
                AlertUtils.showError("Select an entry to update.");
                return;
            }

            if (newText == null || newText.trim().isEmpty()) {
                AlertUtils.showError("Updated text cannot be empty.");
                return;
            }

            try {
                boolean success = collectionManager.updateEntry(selectedIndex, newText);
                if (!success) throw new Exception("Invalid index or empty text.");
                collectionListView.getItems().setAll(collectionManager.getAllEntries());
            } catch (Exception ex) {
                AlertUtils.showError("Update failed: " + ex.getMessage());
            }
        });

        deleteEntryBtn.setOnAction(e -> {
            int selectedIndex = collectionListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex == -1) {
                AlertUtils.showError("Select an entry to delete.");
                return;
            }

            try {
                boolean success = collectionManager.deleteEntry(selectedIndex);
                if (!success) throw new Exception("Delete failed due to invalid index.");
                collectionListView.getItems().setAll(collectionManager.getAllEntries());
            } catch (Exception ex) {
                AlertUtils.showError("Delete failed: " + ex.getMessage());
            }
        });

        VBox collectionControls = new VBox(5, collectionInput, new HBox(10, addEntryBtn, updateEntryBtn, deleteEntryBtn));
        VBox collectionBox = new VBox(5, new Label("Text Collection"), collectionListView, collectionControls);
        collectionBox.setPadding(new Insets(10));
        collectionBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-border-width: 1;");

        VBox root = new VBox(10,
                inputArea,
                replacementField,
//                filePathField,
                UIFactory.createHBox(new Label("Text Processing:"),matchBtn, replaceBtn, frequencyBtn, replaceFileBtn, clearBtn),
                UIFactory.createHBox(new Label("Regex Operations:"),regexBox),
                UIFactory.createHBox(new Label("Batch Processing: "), uploadFileBtn, cleanupOptions, processBtn, exportBtn),
                UIFactory.createHBox(new Label("No. of Sentences:"), sentenceCountSpinner, summarizeBtn),
                collectionBox,
                outputArea
        );

        root.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.show();
    }
}
