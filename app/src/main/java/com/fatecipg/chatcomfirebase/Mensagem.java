package com.fatecipg.chatcomfirebase;

import java.util.Date;

class Mensagem implements Comparable<Mensagem>{

    private String texto;
    private String email;
    private Date data;

    @Override
    public int compareTo(Mensagem mensagem) {
        return this.data.compareTo(mensagem.data);
    }

    public Mensagem(String texto, String email, Date data) {
        this.texto = texto;
        this.email = email;
        this.data = data;
    }

    public Mensagem() {
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


}
