package br.ce.aquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

    public static  DiaSemanaMachers caiEm(Integer diaSemana){
        return new DiaSemanaMachers(diaSemana);
    }

    public static DiaSemanaMachers caiNumaSegunda(){
        return new DiaSemanaMachers(Calendar.MONDAY);
    }

    public static DataDiferencaDiasMatchers ehHojeComDiferencaDeDias(Integer qtdDias){
        return new DataDiferencaDiasMatchers(qtdDias);
    }

    public static DataDiferencaDiasMatchers ehHoje(){
        return new DataDiferencaDiasMatchers(0);
    }
}
