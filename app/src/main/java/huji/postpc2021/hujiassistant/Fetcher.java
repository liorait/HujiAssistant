package huji.postpc2021.hujiassistant;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fetcher extends AsyncTask<Void, Void, Void> {

    private String rawData = "";

    private String yearByUser = "2022";
    private String chugNumberByUser = "532";
    private String maslulNumberByUser = "3080";
    String[] faculty = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "11", "12", "16", "30"};
    private String number;
    private String name;
    private String points;
    private String semester;
    private String type;
    private String chug;
    String collection_name = "faculties";
    private String maslul;
    private ArrayList<Chug> chugsA = new ArrayList<>();
    private HashMap<String, Chug> chugs = new HashMap<>();
    private HashMap<String, String> faculties = new HashMap<>();
    private ArrayList<Faculty> arrayListFaculty = new ArrayList<>();
    private ArrayList<Maslul> arrayListMaslulim = new ArrayList<>();
    ArrayList<String> chugsIdList = new ArrayList<>();
    String year = "2022";
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    public LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();

    @Override
    protected Void doInBackground(Void... voids) {
        try {

            ArrayList<String> chugimLines = new ArrayList<>();

            faculties.put("01", "הפקולטה למדעי הרוח");
            faculties.put("02", "הפקולטה למדעי הטבע");
            faculties.put("03", "הפקולטה למשפטים");
            faculties.put("04", "הפקולטה לרפואה");
            faculties.put("05", "הפקולטה לרפואת שיניים");
            faculties.put("06", "ביה\"ס למנהל העסקים");
            faculties.put("07", "הפקולטה למדעי החברה");
            faculties.put("08", "הפקולטה לחקלאות");
            faculties.put("09", "ביה\"ס לעבודה סוציאלית");
            faculties.put("11", "מרכז אדמונד ולילי ספרא למדעי המוח");
            faculties.put("12", "ביה\"ס להנדסה ולמדעי המחשב");
            faculties.put("16", "מכינה");
            faculties.put("30", "תוכניות מיוחדות");

            for (String value : faculty){
                String name = faculties.get(value);
                Faculty newFaculty = new Faculty(name, value);
                arrayListFaculty.add(newFaculty); // array list of all faculties

                firebaseInstancedb.collection(collection_name).add(newFaculty);
                int facultyId = Integer.parseInt(value);

                DocumentReference document = firebaseInstancedb.collection(collection_name).document(value);
                document.set(newFaculty).addOnSuccessListener(aVoid -> {
                    Log.i("tag", "added a new order to firestore db");
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("tag", "failure a new order to firestore db");
                    }
                });
            }

            getChugsData();
            getMaslulimData();
            /**
            // For loops get data of all chugs for each faculty id
            for (String facultyId : faculty) {

                URL url = new URL("https://shnaton.huji.ac.il/mapa.php/" + facultyId + "/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("Windows-1255")));
                String line = bufferedReader.readLine();
                int patternIndex = 0;
                Pattern pattern;
                Pattern chugPattern = Pattern.compile("<a href=\"mapa.php\\/.*\\/.*\\/.*\" title=\".*\" onclick=\"document.documentElement.style.cursor = 'progress'\" >.*<\\/a>  <\\/div> ", Pattern.CASE_INSENSITIVE);

                while (line != null) {  // course details
                    //pattern = patterns.get(patternIndex);
                    pattern = chugPattern;
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        // rawData = rawData + matcher.group(1) + " ";
                        //  System.out.println(line);
                        String chugId = line.substring(22, 26);
                        System.out.println(chugId);
                        String parentFaculty = line.substring(19,21);
                        System.out.println(parentFaculty);
                        String[] split = line.split(">");
                        String newLine = split[1].replace("<\\/a", "");
                        String fline = split[1];
                        System.out.println(fline);

                        String replace1 = fline.replace("</a", "");
                        System.out.println(replace1);
                        String title = replace1.replace("רשימת כל המסלולים בחוג", "");
                        System.out.println(title);
                        chugsIdList.add(chugId);
                       // String title = replace2.replace("I/System.out:", "");

                        Chug newChug = new Chug(title, chugId, parentFaculty);
                        System.out.println(newChug.toStringP());

                      //  chugs.put(chugId, newChug);
                        chugs.put(title, newChug);

                        chugsA.add(newChug);
                        chugimLines.add(line);
                    }
                    line = bufferedReader.readLine();
                }

                //for (int i =0 ; i < chugsA.size(); i++){
                  //  System.out.println(chugsA.get(i).toStringP());
                //}
            } // end of for loop

            // For loop for each chug gets it's maslulim
           // for (int i =0 ; i < chugsIdList.size(); i++){
             //   System.out.println(chugsIdList.get(i));
            //}


            System.out.println("h");


            // todo for loop clicking on all items getting all information
            // get for each search the id by parsing
            // save all data in firestore

          //  while ((line = bufferedReader.readLine()) != null) {
          //      Log.i("data", line);
          //      System.out.println("data"+ line);
          //  }



            String[] faculty = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "11", "12", "16", "30"};

            for (String facultyId : faculty) {
                //System.out.println(facultyId);
                URL url = new URL("https://shnaton.huji.ac.il/mapa.php/" + facultyId + "/");
             //   URL url = new URL("view-source:https://shnaton.huji.ac.il/mapa.php/" + facultyId + "/");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();
                while ((line = bufferedReader.readLine()) != null) {
                    Log.i("data", line);
                    System.out.println("data" + line);
                }

            }





            Pattern chugPattern = Pattern.compile("ChugName\\\" class=\\\"toarTitle\\\">(.*)</span>", Pattern.CASE_INSENSITIVE);
            Pattern maslulPattern = Pattern.compile("lMaslulName\">(.*)</br>נכון לשנתון: </span>", Pattern.CASE_INSENSITIVE);
            Pattern numberPattern = Pattern.compile("CourseNumber&#39;,&#39;&#39;\\)\\\">([0-9]{5})</a>", Pattern.CASE_INSENSITIVE);
            Pattern namePattern = Pattern.compile("CourseName&#39;,&#39;&#39;\\)\\\">(.*)</a>", Pattern.CASE_INSENSITIVE);
            Pattern pointsPattern = Pattern.compile("CoursePoints\\\" style=\\\"display:inline-block;\\\">([0-9])</span>", Pattern.CASE_INSENSITIVE);
            Pattern semesterPattern = Pattern.compile("CourseSemester\\\">(.*)</span>", Pattern.CASE_INSENSITIVE);
            Pattern courseTypePattern = Pattern.compile("EshkolType\\\" style=\\\"display:inline-block;font-size:15px;\\\">(.*)</span>", Pattern.CASE_INSENSITIVE);
            Pattern courseYearPattern = Pattern.compile("_lblYearToar\\\">(.*)'</span>", Pattern.CASE_INSENSITIVE);

            ArrayList<Pattern> patterns = new ArrayList<Pattern>();
            patterns.add(numberPattern);
            patterns.add(namePattern);
            patterns.add(pointsPattern);
            patterns.add(semesterPattern);

            ArrayList<Pattern> specialPatterns = new ArrayList<Pattern>();
            specialPatterns.add(chugPattern);
            specialPatterns.add(maslulPattern);
            specialPatterns.add(courseTypePattern);
            specialPatterns.add(courseYearPattern);

            data.add(number);
            data.add(name);
            data.add(points);
            data.add(semester);
            data.add(chug);
            data.add(maslul);
            data.add(type);
            data.add(year);

            // ---------------------------------------- start reading and parsing ----------------------------------------


            while (line != null) {  // course details
                pattern = patterns.get(patternIndex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    rawData = rawData + matcher.group(1) + " ";
                    data.set(patternIndex, matcher.group(1));

                    patternIndex = patternIndex + 1;
                    if (patternIndex == patterns.size()) rawData = rawData + "\n";

                } else { // course year, type, chug, maslul
                    int index = -1;
                    for (Pattern pat : specialPatterns) {
                        Matcher specialMatcher = pat.matcher(line);
                        index = index + 1;
                        if (specialMatcher.find()) {
                            rawData = rawData + "------------------- " + specialMatcher.group(1) + " -------------------\n";
                            data.set(index + 4, specialMatcher.group(1));
                        }
                    }
                }

                if (patternIndex >= patterns.size()) {
                    patternIndex = 0;
                   // new Course(data.get(0), data.get(1), data.get(2), data.get(3), data.get(6), data.get(7), data.get(4), data.get(5),yearByUser);
                }
                line = bufferedReader.readLine();
            }

*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void getChugsData() throws IOException {

        // For loops get data of all chugs for each faculty id
        for (String facultyId : faculty) {

            String name = faculties.get(facultyId);
            Faculty newFaculty = new Faculty(name, facultyId);

            URL url = new URL("https://shnaton.huji.ac.il/mapa.php/" + facultyId + "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("Windows-1255")));
            String line = bufferedReader.readLine();
            int patternIndex = 0;
            Pattern pattern;
            Pattern chugPattern = Pattern.compile("<a href=\"mapa.php\\/.*\\/.*\\/.*\" title=\".*\" onclick=\"document.documentElement.style.cursor = 'progress'\" >.*<\\/a>  <\\/div> ", Pattern.CASE_INSENSITIVE);

            while (line != null) {  // course details
                //pattern = patterns.get(patternIndex);
                pattern = chugPattern;
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // rawData = rawData + matcher.group(1) + " ";
                    //  System.out.println(line);
                    String chugId = line.substring(22, 26);
                   // System.out.println(chugId);
                    String parentFaculty = line.substring(19,21);
                   // System.out.println(parentFaculty);
                    String[] split = line.split(">");
                    String newLine = split[1].replace("<\\/a", "");
                    String fline = split[1];
                  //  System.out.println(fline);

                    String replace1 = fline.replace("</a", "");
                  //  System.out.println(replace1);
                    String title = replace1.replace("רשימת כל המסלולים בחוג", "");
                  //  System.out.println(title);
                    chugsIdList.add(chugId);
                    // String title = replace2.replace("I/System.out:", "");

                    Chug newChug = new Chug(title, chugId, parentFaculty);
                  //  System.out.println(newChug.toStringP());

                    chugs.put(chugId, newChug);
                    newFaculty.addChugToFaculty(newChug);

                    DocumentReference document = firebaseInstancedb.collection(collection_name).document(facultyId).collection("chugimInFaculty").document(chugId);
                    document.set(newChug).addOnSuccessListener(aVoid -> {
                        Log.i("tag", "added a new order to firestore db");
                    });



                  //  firebaseInstancedb.collection(collection_name).document(parentFaculty).collection("chugimInFaculty").add

                   /// DocumentReference document = firebaseInstancedb.collection(collection_name).document(parentFaculty);
                   // document.set(newFaculty).addOnSuccessListener(aVoid -> {
                   //     Log.i("tag", "added a new order to firestore db");
                  //  });


                    //chugs.put(title, newChug);

                    chugsA.add(newChug);
                }
                line = bufferedReader.readLine();
            }

            arrayListFaculty.add(newFaculty);

            //for (int i =0 ; i < chugsA.size(); i++){
            //  System.out.println(chugsA.get(i).toStringP());
            //}
        } // end of for loop
    }

    private void getMaslulimData() throws IOException {

        // For loops get data of all chugs for each faculty id
        for (String chugId : chugsIdList) {
            //String chugId = "0101";
            Pattern firstPattern = Pattern.compile(" <a href=\"index.php\\/Program\\/.*\\/.*\\/.*\\/.*\" title=\".*\" onclick=\"document.documentElement.style.cursor = 'progress'\" target=\"_blank\"  >.*<\\/a>  <\\/div>", Pattern.CASE_INSENSITIVE);

            String facultyParentId = Objects.requireNonNull(chugs.get(chugId)).facultyParentId;



            URL url = new URL("https://shnaton.huji.ac.il/mapa.php/" + facultyParentId + "/" + chugId + "/" + year);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("Windows-1255")));
            String line = bufferedReader.readLine();
            int patternIndex = 0;
            Pattern pattern;

           // while ((line = bufferedReader.readLine()) != null) {
               // Log.i("data", line);
              //  System.out.println("data" + line);
           // }
          //  Pattern chugPattern = Pattern.compile("<a href=\"mapa.php\\/.*\\/.*\\/.*\" title=\".*\" onclick=\"document.documentElement.style.cursor = 'progress'\" >.*<\\/a>  <\\/div> ", Pattern.CASE_INSENSITIVE);

            while (line != null) {  // course details
                //pattern = patterns.get(patternIndex);
                pattern = firstPattern;
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // rawData = rawData + matcher.group(1) + " ";
                    System.out.println(line);

                    // Gets maslul number and name
                    String maslulId = line.substring(36, 40);
                    System.out.println("maslulId " + maslulId);
                    String chugOfMaslul = chugId;
                    System.out.println("chugId " + chugId);

                    // Get maslul title
                    String[] split = line.split(">");
                    String newLine = split[1].replace("<\\/a", "");
                    String fline = split[1];
                    //  System.out.println(fline);

                    String replace1 = fline.replace("</a", "");
                    //  System.out.println(replace1);
                    String title = replace1.replace("לתכנית לימודים במסלול", "");
                    System.out.println(title);

                    // Create new maslul object
                    Maslul newMaslul = new Maslul(title, maslulId, chugOfMaslul);
                    arrayListMaslulim.add(newMaslul);

                    DocumentReference document = firebaseInstancedb.collection(collection_name).document(facultyParentId).collection("chugimInFaculty").document(chugId).collection("maslulimInChug").document(maslulId);
                    document.set(newMaslul).addOnSuccessListener(aVoid -> {
                        Log.i("tag", "added a new order to firestore db");
                    });

                   // firebaseInstancedb.collection(collection_name).document(facultyParentId).collection("chugimInFaculty").document(chugOfMaslul).collection("maslulimInChug").add(newMaslul);

                   // DocumentReference document = firebaseInstancedb.collection(collection_name).document(facultyParentId).collection("chugimInFaculty").document(chugOfMaslul);
                   // document.set(newMaslul).addOnSuccessListener(aVoid -> {
                  //      Log.i("tag", "added a new order to firestore db");
                  //  });

                    //  chugs.put(chugId, newChug);
                   // chugs.put(title, newChug);

                   // chugsA.add(newChug);
                }
                line = bufferedReader.readLine();
            }

            System.out.println("p");

            //for (int i =0 ; i < chugsA.size(); i++){
            //  System.out.println(chugsA.get(i).toStringP());
            //}
        } // end of for loop
        System.out.println("gets all maslulim");
    }
    //}

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
       // MainActivity.textView.setText(rawData);

    }

    public HashMap<String, Chug> getChugsMap(){
        return chugs;
    }
}