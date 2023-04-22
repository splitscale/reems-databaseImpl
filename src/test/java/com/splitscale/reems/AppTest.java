package com.splitscale.reems;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
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
  public void add() throws Exception {
    Map<String, Object> data = new HashMap<>();
    data.put("firstname", "Steven");
    data.put("lastname", "Ballaret");
    docRef.set(data);

    System.out.println("Added document with ID: " + docRef.getId());
  }

  // @Test
  // public void read() throws Exception {
  //   // DocumentReference docRef = db.collection("cities").document("SF");
   
  //   ApiFuture<DocumentSnapshot> future = docRef.get();

  //   DocumentSnapshot document = future.get();
  //   if (document.exists()) {
  //     System.out.println("Document data: " + document.getData());
  //   } else {
  //     System.out.println("No such document!");
  //   }
  // }

  // @Test
  // public void update() throws Exception {
  //   // DocumentReference docRef = db.collection("users").document("eIfF8Kc9Gyh5pWbd4keX");

  //   ApiFuture<WriteResult> future = docRef.update("Jerome", true);
    
  //   WriteResult result = future.get();
  //   System.out.println("Write result: " + result);
  // }

  // @Test
  // public void delete() throws Exception {
  //   ApiFuture<WriteResult> writeResult = db.collection("cities").document("DC").delete();

  //   System.out.println("Update time : " + writeResult.get().getUpdateTime());
  // }
}
