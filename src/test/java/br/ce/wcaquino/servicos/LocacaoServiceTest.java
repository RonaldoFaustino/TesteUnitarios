package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ErrorCollector;



import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Test
    public void testLocacao() throws Exception {

        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario");
        Filme filme = new Filme("Filme",3,5.00);

        //acao
        Locacao locacao = null;

        locacao = service.alugarFilme(usuario,filme);


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
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario");
        Filme filme = new Filme("Filme",0,5.00);

        //acao
        Locacao locacao = null;

        service.alugarFilme(usuario,filme);
    }

    @Test
    public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
        //cenario
        LocacaoService service = new LocacaoService();
        Filme filme = new Filme("Filme",1,5.00);

        //acao
        try {
            service.alugarFilme(null,filme);
            Assert.fail("Era pra lançar exception");
        } catch (LocadoraException e) {
           Assert.assertThat(e.getMessage(),is("Usuario vazio"));
        }
    }

    @Test
    public void testLocacao_FilmeSemEstoque_2() {
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario");
        Filme filme = new Filme("Filme",0,5.00);

        //acao
        try {
            service.alugarFilme(usuario,filme);
            Assert.fail("Deveria lançar uma exceção");
        }catch (Exception e){
            Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
            System.out.println("lançou exception");
        }

    }

    @Test(expected=Exception.class)
    public void testLocacao_FilmeSemEstoque_3() throws Exception {
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario");
        Filme filme = new Filme("Filme",0,5.00);

        //acao

        service.alugarFilme(usuario,filme);
    }

}