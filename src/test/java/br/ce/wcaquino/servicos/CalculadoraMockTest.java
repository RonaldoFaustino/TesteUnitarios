package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);
        Mockito.when(cal.somar(Mockito.anyInt(),Mockito.anyInt())).thenReturn(5);

        Assert.assertEquals(5,cal.somar(1, 8));
    }

}
