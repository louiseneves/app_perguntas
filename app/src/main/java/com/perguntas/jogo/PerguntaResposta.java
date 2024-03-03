package com.perguntas.jogo;


public class PerguntaResposta {
    public String pergunta;
    public String resposta;

    public PerguntaResposta() {
        // Construtor vazio necessário para o Firebase
    }
    public PerguntaResposta(String pergunta, String resposta) {
        this.pergunta = pergunta;
        this.resposta = resposta;
    }
    public String getPergunta() {
        return pergunta;
    }

    public String getResposta() {
        return resposta;
    }
}
