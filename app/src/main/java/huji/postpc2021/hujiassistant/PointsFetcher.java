package huji.postpc2021.hujiassistant;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointsFetcher {

    public PointsFetcher() {
    }

    private void getPointsData() throws IOException {

        String facultyId = "2";
        String maslulID = "3010";

        URL url = new URL("http://moon.cc.huji.ac.il/nano/pages/wfrMaslulDetails.aspx?year=2021&faculty=2&chugId=521&maslulId=" + facultyId + maslulID + "");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("Windows-1255")));
        String line = bufferedReader.readLine();
        int patternIndex = 0;
        Pattern pattern;
        Pattern minPointsForDegree = Pattern.compile("<td colspan=\"2\">סה\"כ נ\"ז למסלול מינימום<\\/td><td>.*<\\/td>", Pattern.CASE_INSENSITIVE);
        Pattern cornerStonePoints = Pattern.compile("<td colspan=\"2\">אבני פינה<\\/td><td>.*<\\/td>", Pattern.CASE_INSENSITIVE);
    }
}
        /**



        while (line != null) {  // course details
            //pattern = patterns.get(patternIndex);
            pattern = chugPattern;
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                // rawData = rawData + matcher.group(1) + " ";
                //  System.out.println(line);
                String chugId = line.substring(22, 26);
                // System.out.println(chugId);
                String parentFaculty = line.substring(19, 21);
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
            }
        }
    }
*/