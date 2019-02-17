package com.r2dev.r2fit;

import com.r2dev.r2fit.domain.Batimento;
import com.r2dev.r2fit.domain.Intervalo;
import com.r2dev.r2fit.domain.Passo;
import com.r2dev.r2fit.domain.Treino;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MockData {

    public MockData() {
    }

    public void createIntervalos(Treino treino) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(treino.getStart()));

        long endTime;
        long startTime;

        // Caminhando por 10 minutos
        startTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 10);
        endTime = cal.getTimeInMillis();
        treino.addIntervalo(new Intervalo(Intervalo.TipoAtividade.Caminhada, startTime, endTime));

        // Correndo por 30 minutos
        cal.add(Calendar.MINUTE, 1);
        startTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 30);
        endTime = cal.getTimeInMillis();
        treino.addIntervalo(new Intervalo(Intervalo.TipoAtividade.Corrida, startTime, endTime));

        // Caminhando por 5 minutos
        cal.add(Calendar.MINUTE, 1);
        startTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 5);
        endTime = cal.getTimeInMillis();
        treino.addIntervalo(new Intervalo(Intervalo.TipoAtividade.Caminhada, startTime, endTime));
    }

    public void createBPM(Treino treino) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(treino.getStart()));

        long startTime;
        long endTime = new Date(treino.getEnd()).getTime();

        boolean next = true;
        int min = 65;
        int max = 150;

        Random r = new Random();
        while(next) {
            int bpm = r.nextInt(max - min + 1) + min;

            cal.add(Calendar.MINUTE, 10);
            startTime = cal.getTimeInMillis();
            cal.add(Calendar.MINUTE, 1);
            long ftime = cal.getTimeInMillis();
            if (ftime >= endTime) return;

            treino.addBatimento(new Batimento(startTime, ftime, bpm));

            if (startTime >= endTime) {
                next = false;
            }
        }
    }

    public void createPassos(Treino treino) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(treino.getStart()));

        long startTime;
        long endTime = new Date(treino.getEnd()).getTime();

        boolean next = true;
        int min = 65;
        int max = 150;

        Random r = new Random();
        while(next) {
            int quantidade = r.nextInt(max - min + 1) + min;

            cal.add(Calendar.MINUTE, 10);
            startTime = cal.getTimeInMillis();
            cal.add(Calendar.MINUTE, 1);
            long ftime = cal.getTimeInMillis();
            if (ftime >= endTime) return;

            treino.addPasso(new Passo(startTime, ftime, quantidade));

            if (startTime >= endTime) {
                next = false;
            }
        }
    }
}
