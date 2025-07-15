package com.example;

import kong.unirest.Unirest; // 3.14.5 גרסה

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

//import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;

//import java.net.http.HttpResponse; // 1.4.9
//import com.mashape.unirest.http.Unirest; // 1.4.9
//import com.mashape.unirest.http.exceptions.UnirestException; //1.4.9

// הוספנו ספריית יונירסט - Unirest
// זאת ספרייה שמאפשרת לשלוח בקשות HTTP
// בצורה פשוטה, מבלי להתעסק יותר מדי בפרטים הטכניים כמו פתיחת חיבורים, קריאת תגובות ועוד.

public class LabyrinthApi {
    public static void main(String[] args) {
        // UNIREST:
        try {
            HttpResponse<String> response = Unirest.get("https://app.seker.live/fm1/get-points").asString();
            // System.out.println(response.getBody()); // רואים שזה מערך כשמדפיסים את זה
            // מערך שבכל איבר יש איקס, וויי ובוליאן האם זה לבן (תמיד טרו כי זה מראה רק את
            // המשבצות הלבנות במבוך)
            // נהפוך את המחרוזת לג׳ייסון כדי שיהיה אפשר לטייל בין האיברים שלה בפשטות
            JSONArray array = new JSONArray(response.getBody()); // מערך אובייקטים של התגובה
            // {"x":0.0,"y":0.0,"white":true} איבר במערך לדוגמה
            for (int i = 0; i < array.length(); i++) {
                JSONObject point = array.getJSONObject(i); // עבור כל איבר בכל אינקדס במערך הריספונס ניצור אובייקט
                                                           // נקודה
                int x = point.getInt("x"); // לוקח את הערך אינטג׳ר עם המפתח שהוא המחרוזת ״איקס״
                int y = point.getInt("y");
                System.out.println("x = " + x + " , y = " + y);
                // (הברירת מחדל זה רשת של 30*30)
            }
        } catch (UnirestException e) {
            throw new RuntimeException();
        }

        /////////////////////////////////////////////////
        // OKHTTP :
        try {
            String url = "https://app.seker.live/fm1/get-points";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> Response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            String body = Response.body();

            JSONArray jsonArray = new JSONArray(body);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i); // point
                int x = jsonObject.getInt("x"); // לוקח את הערך אינטג׳ר עם המפתח שהוא המחרוזת ״איקס״
                int y = jsonObject.getInt("y");
            }
        } catch (UnirestException e) {
            throw new RuntimeException();
        }

        /////////////////////////////////////////////////
        // תזכורת:
        // בקשת גט - אני מבקשת מהשרת מידע
        // בקשת פוסט - אני שןלחת לשרת מידע כדי שיעשה עם זה משהו

        // כל איי פי איי עובד עם ג׳ייסונים
        // שניים מהדברים החשובים שאיי פי איי מחזיר זה האם זה הצליח ואם לא הצליח אז למה
        // לא הצליח
        // "success" (true/false)
        // כל פעם שאנחנו עובדים עם איי פי איי כלקוחות אנחנו קודם כל צריכים לראות מה
        // המבנה שחוזר אלינו

        // במקרה כאן עם האיי פי איי הזה אפשר לשלוח טווח רק של בין 5 עד 100 ואם לא שולחים
        // אז ברירת המחדל היא 30*30.
        //

    }
}