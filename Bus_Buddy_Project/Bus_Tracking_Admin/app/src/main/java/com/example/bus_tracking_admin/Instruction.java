package com.example.bus_tracking_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Instruction extends AppCompatActivity {

    private EditText editText,date;
    private DatabaseReference databaseReference;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        editText = findViewById(R.id.editText);
        date = findViewById(R.id.date);
        Button button = findViewById(R.id.button);
        String datePattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        String date1 = dateFormat.format(currentDate);
        date.setText(date1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMessage();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize message adapter with empty list
        messageAdapter = new MessageAdapter(new ArrayList<Message>());
        recyclerView.setAdapter(messageAdapter);

        // Attach event listener to Firebase database reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.setMessageList(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MainActivity", "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void postMessage() {
        String newMessage = editText.getText().toString();
        String key = databaseReference.push().getKey();
        String get_date = date.getText().toString();
        Message message = new Message(key, newMessage,get_date);
        databaseReference.child(key).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Instruction.this, "Message posted successfully", Toast.LENGTH_SHORT).show();
                    editText.setText(""); // Clear text box
                } else {
                    Toast.makeText(Instruction.this, "Failed to post message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}