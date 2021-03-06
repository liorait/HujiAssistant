package huji.postpc2021.hujiassistant;

import java.util.ArrayList;

public class UploadChug {

    public String title = "";
    public String chugId = "";
    public String facultyParentId = "";
    public ArrayList<UploadMaslul> listOfMaslulim = new ArrayList<>();

    public UploadChug() {

    }

    public UploadChug(String title1, String chugId1, String facultyParentId1) {
        title = title1;
        chugId = chugId1;
        facultyParentId = facultyParentId1;
        listOfMaslulim = new ArrayList<>();
    }

    public String toStringP() {
        return "title: " + title + "chugId: " + chugId + "faculty: " + facultyParentId;
    }

    public void addMaslulToChug(UploadMaslul uploadMaslul) {
        listOfMaslulim.add(uploadMaslul);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return chugId;
    }


}
