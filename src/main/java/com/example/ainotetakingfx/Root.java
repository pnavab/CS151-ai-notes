package com.example.ainotetakingfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

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
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Root");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        textArea = new TextArea();
        titleField = new TextField();
        queryField = new TextField(); // Initialize the new TextField
        listModel = FXCollections.observableArrayList();
        noteList = new ListView<>(listModel);

        HBox buttonPanel = new HBox();
        buttonPanel.setAlignment(Pos.CENTER); // Center content both horizontally and vertically
        Button newButton = new Button("New");
        newButton.getStyleClass().add("note-title");
        Button saveButton = new Button("Save");
        Button queryButton = new Button(">"); // Button for sending queries

        buttonPanel.getChildren().addAll(newButton, saveButton, queryButton);

        newButton.setOnAction(e -> {
            textArea.setText("");
            titleField.setText("");
            noteList.getSelectionModel().clearSelection();
        });

        saveButton.setOnAction(e -> {
            String currentNoteContent = textArea.getText();
            String currentTitle = titleField.getText();
            //check if a title was specified, set title to '*untitled note' if not
            if( currentTitle.equals("")){
                currentTitle = "*untitled note";
            }
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

        // HBox for the title field and label
        HBox titlePanel = new HBox();
        titlePanel.setAlignment(Pos.CENTER); // Center content both horizontally and vertically
        titlePanel.getChildren().addAll(new Label("Title"), titleField);
        HBox.setHgrow(titlePanel, Priority.ALWAYS); // Make it grow horizontally

        // HBox for the query field and button
        HBox queryPanel = new HBox();
        queryPanel.setAlignment(Pos.CENTER); // Center content both horizontally and vertically
        queryPanel.getChildren().addAll(new Label("Query"), queryField, queryButton);
        HBox.setHgrow(queryPanel, Priority.ALWAYS); // Make it grow horizontally

        // VBox for the text area 
        VBox textAreaPanel = new VBox();
        textAreaPanel.setAlignment(Pos.CENTER); // Center content both horizontally and vertically
        textAreaPanel.getChildren().addAll(new Label("Text Area"), textArea);
        VBox.setVgrow(textAreaPanel, Priority.ALWAYS); // Make it grow both horizontally and vertically

        VBox root = new VBox();
        root.getChildren().addAll(titlePanel, queryPanel, textAreaPanel, noteList, buttonPanel);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);

        notes = new ArrayList<>();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
