package com.splitscale.reems;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import org.junit.Before;
import org.junit.Test;

public class AppTest {
  Firestore db;
  DocumentReference docRef;

  @Before
  public void setup() throws IOException {
    FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setProjectId("reems-b3fb1")
        .build();

    FirebaseApp.initializeApp(options);
    db = FirestoreClient.getFirestore();
    docRef = db.collection("reems").document();
  }

  @Test
  public void userFlow() throws Exception {
    create();
    read();
    update();
    read();
    delete();
  }

  public void create() throws Exception {
    Map<String, Object> data = new HashMap<>();
    data.put("firstname", "Steve");
    data.put("lastname", "Ballaret");
    data.put("age", 20);
    data.put("gwapo", true);

    ApiFuture<WriteResult> future = docRef.set(data);
    System.out.println("Added a document: " + future.get().getUpdateTime());
  }

  public void read() throws Exception {

    ApiFuture<DocumentSnapshot> future = docRef.get();

    DocumentSnapshot document = future.get();
    if (document.exists()) {
      System.out.println("Document data: " + document.getData());
    } else {
      System.out.println("No such document!");
    }
  }

  public void update() throws Exception {

    ApiFuture<WriteResult> future = docRef.update("firstname", "Jiv");
    System.out.println("Updated data:" + future.get().getUpdateTime());
  }

  public void delete() throws Exception {

    ApiFuture<WriteResult> writeResult = docRef.delete();
    System.out.println("Document deleted : " + writeResult.get().getUpdateTime());
  }
}
