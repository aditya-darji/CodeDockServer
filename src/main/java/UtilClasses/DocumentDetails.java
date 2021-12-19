package UtilClasses;

import java.io.Serializable;
import java.sql.Timestamp;

public class DocumentDetails implements Serializable {
    private int roomId, creatorId, access;
    private String documentName, documentExtension, documentContent;
    private Timestamp createdAt;

    public DocumentDetails(int roomId, int creatorId, int access, String documentName, String documentExtension, String documentContent, Timestamp createdAt){
        this.roomId = roomId;
        this.creatorId = creatorId;
        this.access = access;
        this.documentName = documentName;
        this.documentExtension = documentExtension;
        this.documentContent = documentContent;
        this.createdAt = createdAt;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentExtension() {
        return documentExtension;
    }

    public int getAccess() {
        return access;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getRoomId() {
        return roomId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setDocumentExtension(String documentExtension) {
        this.documentExtension = documentExtension;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
