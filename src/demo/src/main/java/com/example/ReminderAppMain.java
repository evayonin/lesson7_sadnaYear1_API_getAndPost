package com.example;

import java.net.IDN;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class ReminderAppMain {
  // 4 בקשות:
  // 1 - להרשם למערכת
  // 2 - לקבל רשימת משימות שנשמרועבור משתמש ספציפי
  // 3 - להוסיף משימה למשתמש ספציפי
  // 4 - לסמן משימה כבוצעה

  // בקשה מספר 1:
  // תיאור: הרשמה למערכת, שלאחריה ניתן יהיה לשמור משימות בשרת.
  // נתיב: register
  // סוג הבקשה: POST
  // רשימת פרמטרים:
  // String id :תעודת זהות של המשתמש שנרשם למערכת
  // קודי שגיאה אפשריים:
  // 1002 - לא נשלח הפרמטר id
  // 1003 - הערך של הפרמטר id כבר קיים במערכת, כלומר כבר נרשם

  // בקשה מספר 2:
  // תיאור: קבלת כל המשימות המקושרות למשתמש במערכת
  // נתיב: get-tasks:
  // סוג הבקשה: GET
  // רשימת פרמטרים:
  // שימת פרמטרים:
  // String id:מחרוזת, תעודת זהות שעבורה מבקשים את המשימות
  // קודי שגיאה אפשריים:
  // 1000- לא נשלח הפרמטר id
  // 1001- תעודת הזהות המבוקשת לא נרשמה במערכת

  // בקשה מספר 3:
  // תיאור: שמירת משימה במערכת עבור משתמש ספציפי
  // נתיב: add-task
  // סוג הבקשה: POST
  // רשימת פרמטרים:
  // String id: תעודת זהות של המשתמש שעבורו מעוניינים לשמור את המשימה
  // String text: תיאור טקסטואלי של המשימה
  // קודי שגיאה אפשריים:
  // 1002 - לא נשלח הפרמטר id
  // 1004 - לא נשלח הפרמטר text
  // 1001 - תעודת הזהות המבוקשת לא נרשמה במערכת
  // 1005 - עבור המשתמש הנוכחי קיימת משימה פתוחה זהה

  // בקשה מספר 4:
  // תיאור: סימון משימה ספציפית כבוצעה
  // נתיב: set-task-done
  // סוג הבקשה: POST
  // רשימת פרמטרים:
  // String id: תעודת זהות של המשתמש שעבורו מעוניינים לעדכן את המשימה
  // String text :תיאור טקסטואלי של המשימה שאותה מעוניינים לעדכן
  // קודי שגיאה אפשריים:
  // 1002 - לא נשלח הפרמטר id
  // 1004 - לא נשלח הפרמטר text
  // 1001 - תעודת הזהות המבוקשת לא נרשמה במערכת
  // 1006 - המשימה לא קיימת אצל המשתמש
  // 1007- המשימה כבר מסומנת ככזו שבוצעה

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out
        .println(
            "What would you like to do?\n1. Register\n2. To see my tasks\n3. Add a task\n4. Mark a task as done\nEnter your choice (1-4): ");
    int choice = scanner.nextInt();
    while (choice != 1 && choice != 2 && choice != 3 && choice != 4) {
      System.out.println("Incorrect input. Enter a number between 1-4:  ");
      choice = scanner.nextInt();
    }
    scanner.nextLine(); // חייב

    // registration:
    if (choice == 1) {
      System.out.println("Enter your ID:  ");
      String id = scanner.nextLine();
      Integer error = register(id);
      if (error == null) {
        System.out.println("You have been registered successfully!");
      } else if (error == 1002) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("An id wasn't entered.");
      } else if (error == 1003) {
        System.out.println("You are already registered.");
      }
    }
    // asking for the tasks:
    else if (choice == 2) {
      System.out.println("Enter your ID:  ");
      String id = scanner.nextLine();
      Integer error = getTasks(id);
      if (error == 1000) {
        System.out.println("This ID is not registered.");
      } else if (error == 1001) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("An id wasn't entered.");
      }
    }
    // adding a task:
    else if (choice == 3) {
      System.out.println("Enter your ID:  ");
      String id = scanner.nextLine();
      System.out.println("Enter the task you'd like to add:  ");
      String task = scanner.nextLine();
      Integer error = addTask(id, task);
      if (error == null) {
        System.out.println("The task was added successfully!");
      } else if (error == 1001) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("This ID is not registered.");
      } else if (error == 1002) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("The parameter 'id' wasn't sent.");
      } else if (error == 1004) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("The parameter 'text' wasn't sent.");
      } else if (error == 1005) { // לא באמת מגיעים לפה כי בקוד אין אפשרות כזאת
        System.out.println("A similar task already exists.");
      }

    }
    // marking a task as done:
    else if (choice == 4) {

    }

  }

  // פונקציה שתחזיר את מספר השגיאה אם קרתה ואם לא ידפיס שההרשמה הצליחה
  public static Integer register(String id) { // UNIREST
    // בקשת פוסט:
    try {
      // נשלח מפה עם הפרמטרים שאנחנו רוצים לשלוח:

      // ביונירס לא צריך לעשות קודם מחרוזת גייסון במבנה המתאים מהאובייקט כדי לשלוח את
      // מה שרוצים בבקשת פוסט. מספיק לעשות מפה של הפרמטרים (כמו באובג׳קט מאפר רק קצר
      // יותר) ולהכניס למפה כמו שמכניסים לגייסון ואת המפה שולחים בבקשה של יונירסט. זה
      // כאילט מפת הפרמטרים של אובייקט הגייסון כמחרוזת.
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);

      // {"success":false,"errorCode":1002,"extra":null} ראינו את המבנה
      HttpResponse<String> response = Unirest // <T>
          .post("https://app.seker.live/fm1/register") // post
          .queryString(params)
          .asString();

      // ״גט״ למה שהכנסנו - לברות אם הצליח
      JSONObject jsonObject = new JSONObject(response.getBody());
      boolean success = jsonObject.getBoolean("success"); // תשיג את הערך הבוליאני של המפתח ״סקסס״
      if (success) {
        return null;
      } else {
        Integer errorCode = jsonObject.getInt("errorCode");
        return errorCode;
      }
    } catch (UnirestException e) {
      throw new RuntimeException();
    }
  }

  public static Integer getTasks(String id) {
    try {
      // נשלח מפה עם הפרמטרים שאנחנו רוצים לשלוח:
      Map<String, Object> idParams = new HashMap<>();
      idParams.put("id", id);

      HttpResponse<String> response = Unirest
          .get("https://app.seker.live/fm1/get-tasks") // get
          .queryString(idParams)
          .asString();

      JSONObject jsonObject = new JSONObject(response.getBody());
      boolean success = jsonObject.getBoolean("success");
      if (success) {
        JSONArray tasks = jsonObject.getJSONArray("tasks");
        for (int i = 0; i < tasks.length(); i++) {
          JSONObject task = tasks.getJSONObject(i);
          System.out.println(task); // ידפיס את המטלה
        }
        return null;
      } else {
        Integer errorCode = jsonObject.getInt("errorCode");
        return errorCode;
      }
    } catch (UnirestException e) {
      throw new RuntimeException();
    }
  }

  public static Integer addTask(String id, String task) {
    try {
      // נשלח מפה עם הפרמטרים שאנחנו רוצים לשלוח:
      Map<String, Object> idParams = new HashMap<>();
      idParams.put("id", id);
      idParams.put("text", task);

      HttpResponse<String> response = Unirest
          .post("https://app.seker.live/fm1/add-task") // post
          .queryString(idParams)
          .asString();
      JSONObject jsonObject = new JSONObject(response.getBody());
      boolean success = jsonObject.getBoolean("success");
      if (success) {
        System.out.println(jsonObject); // ידפיס את התגובה
        return null;
      } else {
        Integer errorCode = jsonObject.getInt("errorCode");
        return errorCode;
      }
    } catch (UnirestException e) {
      throw new RuntimeException();
    }
  }

  //////////////////////////////////////////////////////////////

  // איך היינו עושים את הפונקציה הראשונה (רג׳יסטר -פוסט) ב OkHttp:

  public static Integer register2(String id) { // OKHTTP
    // בקשת פוסט:
    String relevant = "register";
    String apiUrl = "https://app.seker.live/fm1" + relevant;
    // OR:
    apiUrl = "https://app.seker.live/fm1/register"; // or /get-tasks (מה שצריך)

    // ניצור אובייקט גייסון שיתאים למבנה של השרת ונשים בו את מה שנרצה לשלוח:
    JSONObject json = new JSONObject();
    json.put("id", id);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
        .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    // כדי שיתמקפל חייב לעשות import java.net.http.HttpResponse;
    // ולא של יונירסט
    // אבל לא נותן כי יש שימוש בספריית יונירסט וספריית okhttp
    // באותה המחלקה מה שמגביל את השימוש בתחביר מסוג אחד.

    // ״גט״ למה שהכנסנו - לבדוק אם הצליח (נחזיר את הקוד)
    JSONObject jsonObject = new JSONObject(response.getBody());
    boolean success = jsonObject.getBoolean("success"); // תשיג את הערך הבוליאני של המפתח ״סקסס״
    if (success) {
      return null;
    } else {
      Integer errorCode = jsonObject.getInt("errorCode");
      return errorCode;
    }
  }
  // ביונירס לא צריך לעשות קודם מחרוזת גייסון במבנה המתאים מהאובייקט כדי לשלוח את
  // מה שרוצים בבקשת פוסט. מספיק לעשות מפה של הפרמטרים (כמו באובג׳קט מאפר רק קצר
  // יותר) ולהכניס למפה כמו שמכניסים לגייסון ואת המפה שולחים בבקשה של יונירסט. זה
  // כאילט מפת הפרמטרים של אובייקט הגייסון כמחרוזת.
}
