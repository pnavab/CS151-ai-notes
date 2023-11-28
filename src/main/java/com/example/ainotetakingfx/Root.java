package com.example.ainotetakingfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Root extends Application {
    private TextArea textArea;
    private TextField titleField;
    private TextField queryField; // New TextField for queries
    private ListView<Note> noteList;
    private ObservableList<Note> listModel;

    private List<Note> notes;

    public Root() {
        // No need for the constructor, you can initialize your variables in the start() method.
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Root");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        textArea = new TextArea();
        titleField = new TextField();
        queryField = new TextField(); // Initialize the new TextField
        listModel = FXCollections.observableArrayList();
        noteList = new ListView<>(listModel);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        HBox buttonPanel = new HBox();
        Button newButton = new Button("New");
        newButton.getStyleClass().add("note-title");
        Button saveButton = new Button("Save");
        Button queryButton = new Button("Query"); // Button for sending queries

        buttonPanel.getChildren().addAll(newButton, saveButton, queryButton);

        newButton.setOnAction(e -> {
            textArea.setText("");
            titleField.setText("");
            noteList.getSelectionModel().clearSelection();
        });

        saveButton.setOnAction(e -> {
            String currentNoteContent = textArea.getText();
            String currentTitle = titleField.getText();
            int selectedIndex = noteList.getSelectionModel().getSelectedIndex();
            textArea.clear();
            titleField.clear();
            queryField.clear();

            if (selectedIndex != -1) {
                Note selectedNote = notes.get(selectedIndex);
                selectedNote.setTitle(currentTitle);
                selectedNote.setContent(currentNoteContent);
                listModel.set(selectedIndex, selectedNote);
            } else {
                Note newNote = new Note(currentTitle, currentNoteContent);
                notes.add(newNote);
                listModel.add(newNote);
            }
        });

        queryButton.setOnAction(e -> {
            String query = queryField.getText();
            String response = Gpt.chatGPT(query);
            textArea.appendText("\n" + response);
        });

        noteList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Note selectedNote = noteList.getSelectionModel().getSelectedItem();
            if (selectedNote != null) {
                textArea.setText(selectedNote.getContent());
                titleField.setText(selectedNote.getTitle());
            }
        });

        // Add the title field and query field above the text area
        VBox inputPanel = new VBox();
        inputPanel.getChildren().addAll(titleField, queryField);
        // BorderPane.setAlignment(inputPanel, Pos.CENTER);
        // BorderPane.setMargin(inputPanel, new Insets(10, 0, 10, 0));

        BorderPane textAreaPanel = new BorderPane();
        textAreaPanel.setTop(inputPanel);
        textAreaPanel.setCenter(scrollPane);

        VBox root = new VBox();
        root.getChildren().addAll(textAreaPanel, noteList, buttonPanel);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Styling
        noteList.setId("note-title");
        textArea.setId("note-title");
        newButton.setId("button");
        saveButton.setId("button");
        queryButton.setId("button");
        // titleField.setId("text-area");
        // textArea.setId("text-area");

        notes = new ArrayList<>();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
