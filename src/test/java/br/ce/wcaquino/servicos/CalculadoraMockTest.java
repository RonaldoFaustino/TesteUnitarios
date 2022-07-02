package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Locacao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {


    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }


    /****
     * Mockito devolve 0 quando não sabe o que fazer
     * Spy devove o resultado do metodo real
     */
    @Test
    public void deveMostrarDiferencaEntreMockSpy(){
        Mockito.when(calcMock.somar(1,2)).thenReturn(8);
        //Mockito.when(calcSpy.somar(1,2)).thenReturn(8);
        Mockito.doReturn(5).when(calcSpy).somar(1,2);
        Mockito.when(calcMock.subtrair(5,3)).thenCallRealMethod();

        System.out.println("Mockito : " + calcMock.somar(1,2));
        System.out.println("Spy : " + calcSpy.somar(1,5));
        System.out.println("Mockito chamando o metodo real: " + calcMock.subtrair(5,3));
    }



    @Test
    public void teste(){
        Calculadora cal = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(cal.somar(argCapt.capture(),Mockito.anyInt())).thenReturn(4);

        Assert.assertEquals(4,cal.somar(6, 4));
        System.out.println(argCapt.getValue());
    }

}
