package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);
        Mockito.when(cal.somar(Mockito.anyInt(),Mockito.anyInt())).thenReturn(5);

        System.out.println(cal.somar(1,8));
    }

}
