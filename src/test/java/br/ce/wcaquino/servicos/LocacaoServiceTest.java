package br.ce.wcaquino.servicos;

import br.ce.aquino.matchers.DiaSemanaMachers;
import br.ce.aquino.matchers.MatchersProprios;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.aquino.matchers.MatchersProprios.ehHoje;
import static br.ce.aquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
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
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));
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
            error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDeDias(1));
            error.checkThat(locacao.getDataLocacao(), ehHoje());

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void deveLancarExceptionAoAlugarFilmeSemEstoque() throws Exception {
        //Cenario
        Usuario usuario = new Usuario("Usuario");
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 0, 5.00));

        //acao
        Locacao locacao = null;

        service.alugarFilme(usuario,filmes);
    }

    @Test
    public void naoDeveALugarFilmeSemUsuario() throws FilmeSemEstoqueException {
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

    @Test
    public void devePagar75PCTNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0),
                                           new Filme("Filme 2", 2,4.0),
                                           new Filme("Filme 3", 2,4.0));
        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);
        //verificacao
        Assert.assertThat(resultado.getValor(),is(11.0));
    }

    @Test
    public void devePagar50PCTNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0),
                new Filme("Filme 2", 2,4.0),
                new Filme("Filme 3", 2,4.0),
                new Filme("Filme 4", 2,4.0));
        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);
        //verificacao
        Assert.assertThat(resultado.getValor(),is(13.0));
    }

    @Test
    public void devePagar25PCTNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0),
                new Filme("Filme 2", 2,4.0),
                new Filme("Filme 3", 2,4.0),
                new Filme("Filme 4", 2,4.0),
                new Filme("Filme 5", 2,4.0));
        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);
        //verificacao
        Assert.assertThat(resultado.getValor(),is(14.0));
    }

    @Test
    public void devePagar0PCTNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0),
                new Filme("Filme 2", 2,4.0),
                new Filme("Filme 3", 2,4.0),
                new Filme("Filme 4", 2,4.0),
                new Filme("Filme 5", 2,4.0),
                new Filme("Filme 6", 2,4.0));
        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);
        //verificacao
        Assert.assertThat(resultado.getValor(),is(14.0));
    }

    @Test
    public void naoDeveDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0));
        //acao
        Locacao retorno = service.alugarFilme(usuario,filmes);
        //verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        Assert.assertTrue(ehSegunda);
        Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMachers(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());

    }
}
