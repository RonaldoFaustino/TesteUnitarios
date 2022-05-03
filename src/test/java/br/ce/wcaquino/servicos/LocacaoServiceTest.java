package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;

import org.junit.rules.ErrorCollector;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class LocacaoServiceTest {

    private LocacaoService service;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Before
    public void setup(){
        System.out.println("Before");
        service = new LocacaoService();
    }

    @After
    public void tearDown(){
        System.out.println("After");
    }

    @BeforeClass
    public static void setupClass(){
        System.out.println("BeforeClass");
    }

    @AfterClass
    public static void tearDownClass(){
        System.out.println("AfterClass");
    }

    @Test
    public void testLocacao() throws Exception {

        //Cenario
        Usuario usuario = new Usuario("Usuario");
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 3, 5.00));

        System.out.println("Teste!");

        //acao
        Locacao locacao = null;

        locacao = service.alugarFilme(usuario,filmes);


            //verificacao
            Assert.assertTrue(locacao.getValor() == 5.00);
            Assert.assertThat(locacao.getValor(), CoreMatchers.is(5.00));
            Assert.assertThat(locacao.getValor(), CoreMatchers.is(not(6.00)));
            Assert.assertEquals(5.0, locacao.getValor(),0.01);
            Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
            Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(),DataUtils.obterDataComDiferencaDias(1)));

            error.checkThat(locacao.getValor(), CoreMatchers.is(5.00));
            error.checkThat(locacao.getValor(), CoreMatchers.is(not(6.00)));

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void testLocacao_FilmeSemEstoque() throws Exception {
        //Cenario
        Usuario usuario = new Usuario("Usuario");
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 0, 5.00));

        //acao
        Locacao locacao = null;

        service.alugarFilme(usuario,filmes);
    }

    @Test
    public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 1, 5.00));
        //acao
        try {
            service.alugarFilme(null,filmes);
            Assert.fail("Era pra lançar exception");
        } catch (LocadoraException e) {
           Assert.assertThat(e.getMessage(),is("Usuario vazio"));
        }
    }

    @Test
    public void testLocacao_FilmeSemEstoque_2() {
        //Cenario
        Usuario usuario = new Usuario("Usuario");
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 0, 5.00),new Filme("Filme", 0, 5.00));
        System.out.println(filmes.toString());
        //acao
        try {
            service.alugarFilme(usuario,filmes);
            Assert.fail("Deveria lançar uma exceção");
        }catch (Exception e){
            Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
            System.out.println("lançou exception");
        }

    }

    @Test(expected=Exception.class)
    public void testLocacao_FilmeSemEstoque_3() throws Exception {
        //Cenario
        Usuario usuario = new Usuario("Usuario");
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 0, 5.00));


        //acao

        service.alugarFilme(usuario,filmes);
    }

}
