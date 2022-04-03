package com.example.qrhunt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OwnerActivity extends Activity {
    public boolean isValidClick = false;
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<GameQRCode> codes = new ArrayList<>();
    private ListView playersListview = null;
    private ListView codesListview = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  CollectionReference collectionReference = db.collection("Players");
    private  CollectionReference collectionReference2 = db.collection("Players");
    private String TAG = "players";
    private String chosenUUID = null;
    private String chosenCode = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // 定位界面元素：
        playersListview = findViewById(R.id.players_list);
        codesListview = findViewById(R.id.codes_list);
        Button deletePlayerButton = findViewById(R.id.button_delete_player);
        Button deleteCodeButton = findViewById(R.id.button_delete_code);
        Button XButton = findViewById(R.id.X);

        // 初始化不可见设定：
        codesListview.setVisibility(View.INVISIBLE);
        deletePlayerButton.setVisibility(View.INVISIBLE);
        deleteCodeButton.setVisibility(View.INVISIBLE);

        // 通过players数组获取各 player的username:String ，放入 playerUsernames:ArrayList<String> 以用于Adapter装填；



        // Todo - 飞鱼：从数据库读取各个 player:Player 到 Players:ArrayList<Player>：
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> players = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String uuid = document.getId();
                            players.add(uuid);
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    ArrayAdapter<String> playersAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.content, R.id.game_code_text, players);
                    playersListview.setAdapter(playersAdapter);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        playersListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 捕获条目点击，找出targetPlayer并获取其对应codes：
                isValidClick = true;
                String uuid = (String) playersListview.getItemAtPosition(position);
                chosenUUID = uuid;
                getPlayerCodes(uuid);

                // 将codes的hash装填到第二listview
                //playersListview.setAdapter(new ArrayAdapter<>(getBaseContext(), R.layout.content, refreshCodesInfo()));

                // 展示更多功能：
                codesListview.setVisibility(View.VISIBLE);
                deletePlayerButton.setVisibility(View.VISIBLE);


                // 监听deletePlayerButton点击：
                deletePlayerButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (chosenUUID == null)
                            return;
                        // 从显示移除targetPlayer：
                        //players.remove(targetPlayer);
                        removePlayer(chosenUUID);

                        // Todo - 飞鱼：从db移除targetPlayer：
                        // ...

                        // 任务信息反馈：
                        Toast.makeText(getApplicationContext(), "Player Deleted", Toast.LENGTH_LONG).show();
                        ArrayAdapter<String> playersAdapter = (ArrayAdapter<String>) playersListview.getAdapter();
                        playersAdapter.remove(chosenUUID);
                        playersListview.setAdapter(playersAdapter);

                        // 执行操作后各部恢复不可见:
                        deletePlayerButton.setVisibility(View.INVISIBLE);
                        codesListview.setVisibility(View.INVISIBLE);
                        chosenUUID = null;
                    }
                });

            }
        });

        codesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 捕获条目点击，并获取其对应codes：
                if (isValidClick) {
                    String targetCode = (String) codesListview.getItemAtPosition(position);
                    chosenCode = targetCode;

                    // 展示更多功能：
                    deleteCodeButton.setVisibility(View.VISIBLE);

                    // 监听deleteCodeButton点击：
                    deleteCodeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (chosenCode == null)
                                return;
                            // 从显示移除targetPlayer：
                            removeCode(chosenCode);


                            ArrayAdapter<String> codesAdapter = (ArrayAdapter<String>) codesListview.getAdapter();
                            codesAdapter.remove(chosenCode);
                            codesListview.setAdapter(codesAdapter);

                            chosenCode = null;

                            // Todo - 飞鱼：从db移除targetPlayer下的targetCode：
                            // ...

                            // 任务信息反馈：
                            Toast.makeText(getApplicationContext(), "Code Deleted", Toast.LENGTH_LONG).show();

                            // 执行操作后各部恢复不可见:
                            deleteCodeButton.setVisibility(View.INVISIBLE);
                            deletePlayerButton.setVisibility(View.INVISIBLE);
                            //codesListview.setVisibility(View.INVISIBLE);

                        }
                    });
                }

            }
        });

        // 监听XButton点击：
        XButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // 若点击则关闭当前activity，退出ownerPage；
                finish();
            }
        });

    }

    public void getPlayerCodes(String currUuid) {
        collectionReference2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> codesHash = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String uuid = document.getId();
                            if (currUuid.equals(uuid)) {
                                ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                                for (Map<String, Object> code : codes) {
                                    GameQRCode newCode = new GameQRCode((String) code.get("content"));
                                    codesHash.add(newCode.getHash());
                                }
                            }
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    //
                    ArrayAdapter<String> codesAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.content, R.id.game_code_text, codesHash);
                    codesListview.setAdapter(codesAdapter);
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void removePlayer(String currUuid) {
        collectionReference.document(currUuid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }

    public void removeCode(String currCode) {
        List<GameQRCode> newCodes = new ArrayList<>();
        GameQRCode oldCode = new GameQRCode("");
        oldCode.setHash(currCode);
        DocumentReference documentReference = collectionReference.document(chosenUUID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Map<String, Object>> codes = (ArrayList<Map<String, Object>>) document.get("codes");
                        for (Map<String, Object> code : codes) {
                            GameQRCode newCode = new GameQRCode((String) code.get("content"));
                            if (!newCode.getHash().equals(oldCode.getHash()))
                                newCodes.add(newCode);
                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + newCode.getContent());
                        }
                        documentReference.update("codes", newCodes);
                        // Callback:

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}