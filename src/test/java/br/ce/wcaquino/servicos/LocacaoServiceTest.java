package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.matchers.DiaSemanaMachers;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {

    @InjectMocks
    private LocacaoService service;

    @Mock
    private LocacaoDAO dao;
    @Mock
    private SPCService spc;
    @Mock
    private EmailService email;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        System.out.println("Before");
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
        //Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));
        //Cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28,4,2017));
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());

        //acao
        Locacao locacao = null;

        service.alugarFilme(usuario,filmes);
    }

    @Test
    public void naoDeveALugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme", 0, 5.00),new Filme("Filme", 0, 5.00));
        System.out.println(filmes.toString());
        //acao
        try {
            service.alugarFilme(usuario,filmes);
            Assert.fail("Deveria lançar uma exceção");
        }catch (Exception e){
            System.out.println("Mensagem do Erro : " + e.getMessage());
            Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
            System.out.println("lançou exception");
        }
    }

    @Test(expected=Exception.class)
    public void testLocacao_FilmeSemEstoque_3() throws Exception {
        //Cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());

        //acao
        service.alugarFilme(usuario,filmes);
    }

    @Test
    public void devePagar75PCTNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
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
    public void naoDeveDevolverFilmeNoDomingo() throws Exception {
        //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));

        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0));

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29,4,2017));
        //acao
        Locacao retorno = service.alugarFilme(usuario,filmes);
        //verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        Assert.assertTrue(ehSegunda);
        Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMachers(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());

    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2,4.0));

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29,4,2017));

        //acao
        Locacao retorno = service.alugarFilme(usuario,filmes);
        //verificacao
        Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
    }

    @Test
    public void naoDeveLugarFilneParaNegativado() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        //Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
        Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
//        exception.expect(LocadoraException.class);
//        exception.expectMessage("Usuário Negativado");

        //ação
        try {
            service.alugarFilme(usuario,filmes);
            //verificacao
            Assert.fail();
        } catch (LocadoraException e) {
           Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
        }
        Mockito.verify(spc).possuiNegativacao(usuario);

    }

    @Test
    public void deveEnviarEmailParaLocacaoAtrasadas(){
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario atrasado").agora();
        List<Locacao> locaoes = Arrays.asList(
                LocacaoBuilder.umLocacao()
                        .comUsuario(usuario)
                        .atrasado()
                        .agora(),
                LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
                LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora(),
                LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora());
        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locaoes);

        //acao
        service.notificarAtrasos();

        //verificacao
        Mockito.verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
        Mockito.verify(email).notificarAtraso(usuario);
        Mockito.verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
        Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
        Mockito.verifyNoMoreInteractions(email);
        Mockito.verifyNoMoreInteractions(spc);
    }

    @Test
    public void deveTratarErronoSPC() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catrastrófica"));

        //verificacao
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");

        //acao
        service.alugarFilme(usuario,filmes);

        //verificacao
    }

    @Test
    public void deveProrrogarUmaLocacao(){
        //cenario
        Locacao locacao = LocacaoBuilder.umLocacao().agora();

        //Acao
        service.prorrogarLocacao(locacao,3);

        //verificacao
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetomada = argCapt.getValue();

        error.checkThat(locacaoRetomada.getValor(),is(12.0));
        error.checkThat(locacaoRetomada.getDataLocacao(),is(ehHoje()));
        error.checkThat(locacaoRetomada.getDataRetorno(),is(ehHojeComDiferencaDeDias(3)));

    }

    public static void main (String[] args){
        new BuilderMaster().gerarCodigoClasse(Locacao.class);
    }
}
