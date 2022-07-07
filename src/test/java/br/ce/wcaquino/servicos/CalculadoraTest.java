package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.runners.ParalleRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(ParalleRunner.class)
public class CalculadoraTest {

    private  Calculadora cal;

    @Before
    public void setup(){
        cal = new Calculadora();
    }

    @Test
    public void deveSomarDoisValores(){
        //cenario
        int a = 5;
        int b = 9;

        //acao
        int resultado = cal.somar(a,b);

        //verificacao
        Assert.assertEquals(14,resultado);
    }

    @Test
    public void deveSubtrairDoisValores(){
        //cenario
        int a = 5;
        int b = 9;

        //acao
        int resultado = cal.subtrair(a,b);

        //verificacao
        Assert.assertEquals(-4,resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 6;
        int b = 3;

        //acao
        int resultado = cal.dividir(a,b);

        //verificacao
        Assert.assertEquals(2,resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExceptionAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 6;
        int b = 0;

        //acao
        int resultado = cal.dividir(a,b);

        //verificacao
        Assert.assertEquals(2,resultado);
    }

}
