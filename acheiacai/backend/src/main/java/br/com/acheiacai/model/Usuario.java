package br.com.acheiacai.model;

public record Usuario(
        Long id,
        String nomeUsuario,
        String senha,
        String funcao
) {}
