package UtilClasses;

import java.io.Serializable;
import java.util.HashMap;

public class NewDocumentInfo implements Serializable {
    private String documentName, documentExtension, documentContent;
    private HashMap<Integer, Integer> collaboratorMap=new HashMap<Integer,Integer>();

    public NewDocumentInfo(String documentName, String documentExtension, String documentContent, HashMap<Integer, Integer> collaboratorMap){
        this.documentName = documentName;
        this.documentExtension = documentExtension;
        this.documentContent = documentContent;
        this.collaboratorMap = collaboratorMap;
    }

    public HashMap<Integer, Integer> getCollaboratorMap() {
        return collaboratorMap;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public String getDocumentExtension() {
        return documentExtension;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setCollaboratorMap(HashMap<Integer, Integer> collaboratorMap) {
        this.collaboratorMap = collaboratorMap;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public void setDocumentExtension(String documentExtension) {
        this.documentExtension = documentExtension;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
