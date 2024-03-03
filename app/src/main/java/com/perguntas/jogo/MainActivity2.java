package com.perguntas.jogo;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity2 extends AppCompatActivity {
   private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editPergunta;
    private EditText editResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editPergunta = findViewById(R.id.editPergunta);
        editResposta = findViewById(R.id.editResposta);
    }

    public void salvarDados(View view) {

        String pergunta = editPergunta.getText().toString().trim();
        String resposta = editResposta.getText().toString().trim();
        if(!pergunta.isEmpty() && !resposta.isEmpty()) {
            PerguntaResposta newQuestion = new PerguntaResposta(pergunta, resposta);
            addQuestionToFirestore(newQuestion);
        }else{
            Toast.makeText(this, "Preencha os campos!", Toast.LENGTH_SHORT).show();
        }
    }
    private void addQuestionToFirestore(PerguntaResposta question) {
        try {
        db.collection("perguntas")
                .add(question)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Pergunta cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    clearFields();
                        Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                     // Encerra a atividade após o cadastro bem-sucedido
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao cadastrar pergunta: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void clearFields() {
        editPergunta.setText("");
        editResposta.setText("");
    }
    public void cancela(View view) {
        clearFields();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Encerra a conexão com o Firestore
        db.terminate();
    }
}