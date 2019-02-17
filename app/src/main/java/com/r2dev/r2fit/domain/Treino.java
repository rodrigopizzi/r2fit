package com.r2dev.r2fit.domain;

import java.util.ArrayList;
import java.util.List;

public class Treino {

    private long start;
    private long end;
    private List<Intervalo> intervalos;
    private List<Batimento> batimentos;
    private List<Passo> passos;

    public Treino(long start, long end) {
        this.start = start;
        this.end = end;

        this.intervalos = new ArrayList<>();
        this.batimentos = new ArrayList<>();
        this.passos = new ArrayList<>();
    }

    public void addIntervalo(Intervalo intervalo) {
        this.intervalos.add(intervalo);
    }

    public void addBatimento(Batimento batimento) {
        this.batimentos.add(batimento);
    }

    public void addPasso(Passo passo) {
        this.passos.add(passo);
    }

    public List<Intervalo> getIntervalos() {
        return intervalos;
    }

    public List<Batimento> getBatimentos() {
        return batimentos;
    }

    public List<Passo> getPassos() {
        return passos;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
