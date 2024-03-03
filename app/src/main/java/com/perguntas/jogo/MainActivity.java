package com.perguntas.jogo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView perguntaTextView;
    private Button mostrarRespostaButton, criarPerguntaButton, mNextButton, mBackButton, mDeleteButton;
    private final List<PerguntaResposta> questionList = new ArrayList<>();

    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        perguntaTextView = findViewById(R.id.perguntaTextView);
        mostrarRespostaButton = findViewById(R.id.mostrarRespostaButton);
        criarPerguntaButton = findViewById(R.id.criarPerguntaButton);
        mNextButton = findViewById(R.id.next_button);
        mBackButton = findViewById(R.id.back_button);
        mDeleteButton = findViewById(R.id.deletar);
        loadQuestions();
        mDeleteButton.setOnClickListener(view -> deleteCurrentQuestion());
        mBackButton.setOnClickListener(view -> showPreviousQuestion());
        mNextButton.setOnClickListener(view -> showNextQuestion());
        mostrarRespostaButton.setOnClickListener(view -> mostrarResposta());
        criarPerguntaButton.setOnClickListener(view -> criarPergunta());

    }

    public void criarPergunta() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    private void mostrarResposta() {
        if (!questionList.isEmpty()) {
            PerguntaResposta currentQuestion = questionList.get(currentIndex);
            if (mostrarRespostaButton.getText().toString().equals("Resposta")) {
                perguntaTextView.setText(currentQuestion.getResposta());
                mostrarRespostaButton.setText("Pergunta");
            } else {
                perguntaTextView.setText(currentQuestion.getPergunta());
                mostrarRespostaButton.setText("Resposta");
            }
        }else {
            Toast.makeText(this, "Lista de perguntas vazia!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCurrentQuestion() {
        if (!questionList.isEmpty()) {
            PerguntaResposta currentQuestion = questionList.get(currentIndex);
            db.collection("perguntas")
                    .whereEqualTo("pergunta", currentQuestion.getPergunta())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MainActivity.this, "Pergunta excluída com sucesso!", Toast.LENGTH_SHORT).show();
                                        questionList.remove(currentIndex); // Remover a pergunta da lista
                                        updateQuestion(); // Atualizar a pergunta exibida
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(MainActivity.this, "Erro ao excluir pergunta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Erro ao excluir pergunta", e);

                                        return;
                                    });
                        }

                        // Se chegou aqui, a pergunta não foi encontrada

                        Toast.makeText(MainActivity.this, "Pergunta não encontrada.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "Erro ao procurar pergunta: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadQuestions() {
        db.collection("perguntas")
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PerguntaResposta question = document.toObject(PerguntaResposta.class);
                                questionList.add(question);
                            }
                            Collections.shuffle(questionList);
                        } else {
                            Log.e(TAG, "Falha ao carregar perguntas", task.getException());
                            Toast.makeText(MainActivity.this, "Falha ao carregar perguntas", Toast.LENGTH_SHORT).show();
                        }
                        updateQuestion();
                });
    }

    private void showNextQuestion() {
        if (!questionList.isEmpty()) {
            currentIndex = (currentIndex + 1) % questionList.size();
            updateQuestion();
        }
    }

    private void showPreviousQuestion() {
        if (!questionList.isEmpty()) {
            currentIndex = (currentIndex - 1 + questionList.size()) % questionList.size();
            updateQuestion();
        }
    }

    private void updateQuestion() {
        if (!questionList.isEmpty()) {
            PerguntaResposta currentQuestion = questionList.get(currentIndex);
                perguntaTextView.setText(currentQuestion.getPergunta());
            } else {
                perguntaTextView.setText("Cadastre a pergunta");
            }
        }
    }
