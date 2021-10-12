package huji.postpc2021.hujiassistant;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FireStoreReader {

//    private FirebaseFirestore db;

    FirebaseFirestore firestore;
    ArrayList<ArrayList<String>> dictionary;
    public FireStoreReader() {
         firestore = FirebaseFirestore.getInstance();

//        this.db = HujiAssistentApplication.getInstance().getDataBase();  // -------------------------------------------- Change this one if needed ----------------------------
        dictionary = new ArrayList<>();
    }




/**
        firestore.collection("faculties").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("TAG", "onSuccess: faculties - first onSuccess");
                List<DocumentSnapshot> faculties = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot faculty : faculties) {
//                    Log.e("TAG", "faculty: " + faculty.get("facultyId"));
                    if (faculty.contains("facultyId")) {

                        String currFaculty = faculty.get("facultyId").toString();


                        firestore.collection("faculties").document(faculty.get("facultyId").toString()).collection("chugimInFaculty").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                Log.e("TAG", "onSuccess: chugim - second onSuccess");
                                List<DocumentSnapshot> chugim = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot chug : chugim) {

                                    ArrayList<String> facultyList = new ArrayList<>(); // list of all the chug's in a specific faculty
                                    facultyList.add(currFaculty);

//                                Log.e("TAG", "chug: " + chug.get("chugId").toString());
                                    if (chug.contains("chugId"))
                                        firestore.collection("faculties").document(currFaculty).collection("chugimInFaculty").document(chug.get("chugId").toString()).collection(" maslulimInChug").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                Log.e("TAG", "onSuccess: maslulim - third onSuccess");
                                                List<DocumentSnapshot> maslulim = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot maslul : maslulim) {
//                                            Log.e("TAG", "onSuccess: " + maslul.get("id"));
                                                    if (maslul.contains("id"))
                                                        facultyList.add(maslul.get("id").toString());
                                                }
                                                dictionary.add(facultyList);
                                                Log.e("TAG", "FireStoreReader: " + dictionary);

                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("TAG", "onFailure: third onFailure");
                                            }
                                        });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "onFailure: second onFailure");
                            }
                        });
                    }


                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: first onFailure");
            }
        });
*/

public void work(){


        String maslulId;

        String[] facultyArray = {"01", "02", "03", "04", "05", "06", "07", "08","09", "11", "12", "16", "30"};

        for (String facultyId : facultyArray) {
            System.out.println("faculty id: " + facultyId);

            firestore.collection("faculties").document(facultyId).collection("chugimInFaculty")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document1 : documents) {
                        Chug chug = document1.toObject(Chug.class);
                        String chugId = chug.getId();

                        Task<QuerySnapshot> querySnapshotTask = firestore.collection("faculties").document(facultyId)
                                .collection("chugimInFaculty").document(document1.getId().toString())
                                .collection("maslulimInChug").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> documents1 = task.getResult().getDocuments();
                                        ArrayList<String> maslulimInFaculty = new ArrayList<>();

                                        for (DocumentSnapshot document2 : documents1) {
                                            // String docId  = document1.getId().toString();
                                            Maslul maslul = document2.toObject(Maslul.class);
                                            System.out.println("maslul " + maslul.toStringP());
                                            String maslulTitle = maslul.getTitle();
                                            maslulimInFaculty.add(maslulTitle);
                                        }
                                    }
                                });


                    } // inner for
                } // inner on complete
            });
        }
    }
} // end of class











