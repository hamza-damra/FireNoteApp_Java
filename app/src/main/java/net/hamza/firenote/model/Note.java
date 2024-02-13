package net.hamza.firenote.model;

public class Note {
    private String title;
    private String content;
    private String docId; // Added field for Firestore document ID

    // Default constructor required for calls to DataSnapshot.getValue(Note.class)
    public Note() {
    }

    // Constructor with document ID
    public Note(String docId, String title, String content) {
        this.docId = docId;
        this.title = title;
        this.content = content;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public String toString() {
        return "Note{" +
                "docId='" + docId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
