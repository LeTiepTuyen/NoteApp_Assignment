package com.example.diary1913;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diary1913.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseFirestore fireStore;

    private RecyclerView rvNotes;
    private FloatingActionButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("posts");
        fireStore = FirebaseFirestore.getInstance();
        login("tuyenle@gmail.com","123456");
        creatNewUser("newuser@gmail.com","123456");
//        postDataToRealTimeDB("Hello");
//        readDataFromRealTimeDB();
//
//        postDataToFireStore();
//        String id = myRef.push().getKey();
//        addPostData(new Post(id,"Le Tuyen","Android With Firebase"));
//        addPostData(new Post("Tieu My","Complain everyday"));
//        addPostData(new Post("Le Tuyen","Make money"));
//        removeAllPostData();

        rvNotes = findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        btnAdd = findViewById(R.id.btn_add);
//
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
    }

    public void addNote() {
        String id = myRef.push().getKey();
        String title = "Test title";
        String content = "Test content";

        myRef.child(id).setValue(new Post(id,title,content))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG","post data successfully");
                        } else {
                            Log.d("DEBUG","Fail to post to data");
                        }
                    }
                })
        ;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(myRef, Post.class)
                        .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            @Override
            public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_items, parent, false);

                return new PostHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostHolder holder, int position, Post model) {
                holder.tvContent.setText(model.getTitle());
                holder.tvTitle.setText(model.getContent());
            }
        };

        rvNotes.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvContent;

        public PostHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title);
            tvContent = view.findViewById(R.id.tv_content);
        }


    }


    public void login (String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG","Login Successfull");
                        } else {
                            Log.d("DEBUG","Login fail");
                        }
                    }
                });
    }

    public void creatNewUser(String userName,String password) {
        mAuth.createUserWithEmailAndPassword(userName, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG","Create new user successfully");
                        } else {
                            Log.d("DEBUG","Fail to create new user");
                        }
                    }
                });
    }


    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG","send email to reset password successfully");
                        } else {
                            Log.d("DEBUG","Fail to send email to reset password");
                        }
                    }
                });
    }


    private void signOut() {
        mAuth.signOut();
    }

    private void postDataToRealTimeDB(String data) {
        myRef.setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG","post data" + data +"successfully");
                        } else {
                            Log.d("DEBUG","Fail to post to data");
                        }
                    }
                });
    }

    private void readDataFromRealTimeDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("DEBUG", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DEBUG", "Failed to read value.", error.toException());
            }
        });
    }

    private void postDataToFireStore() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        fireStore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DEBUG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DEBUG", "Error adding document", e);
                    }
                });
    }


    public void addPostData(Post data) {
        DatabaseReference myRefRoot = database.getReference().child("posts");
        String postId = myRefRoot.push().getKey(); // Tạo khóa duy nhất cho bài đăng mới
        myRefRoot.child(postId).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("DEBUG", "post data " + data + " successfully");
                        } else {
                            Log.d("DEBUG", "Fail to post to data");
                        }
                    }
                });
    }


    public void removeAllPostData() {
        DatabaseReference myRefRoot = database.getReference().child("posts");
        myRefRoot.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DEBUG", "All posts data deleted successfully");
                        } else {
                            Log.d("DEBUG", "Failed to delete posts data");
                        }
                    }
                });
    }
}