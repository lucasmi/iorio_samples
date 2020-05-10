package br.com.bradesco.cardtech;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;

import br.com.bradesco.cardtech.http.HttpItegration;
import br.com.bradesco.cardtech.http.bean.ConsultaCartoesRequestBean;
import br.com.bradesco.cardtech.http.bean.ConsultaLimitesBean;
import br.com.bradesco.cardtech.http.bean.ConsultarExtratoAbertoBean;
import br.com.bradesco.cardtech.http.bean.ConsultarExtratoFechadoBean;
import br.com.bradesco.cardtech.http.bean.ConsultarLancamentosBean;
import br.com.bradesco.cardtech.http.bean.OAuthTokenRequestBean;
import br.com.bradesco.cardtech.http.bean.OAuthTokenResponseBean;
import br.com.bradesco.cardtech.http.bean.ObterLinhaDigitavelBean;
import br.com.bradesco.cardtech.http.bean.SelecionarOpcaoFaturaBean;
import br.com.bradesco.cardtech.jwt.JwtCreationBean;
import br.com.bradesco.cardtech.utils.Utils;

public class App {
	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();

		// Parametros de entrada do App
		AppParametersBean appParametersBean = new AppParametersBean();

		appParametersBean.setHost("");
		appParametersBean.setPort("843");
		appParametersBean.setCaminhoChavePrivada("");
		appParametersBean.setServerStore("");
		appParametersBean.setServerStorePassword("password");
		appParametersBean.setClientStore("");
		appParametersBean.setClientStorePassword("password");
		appParametersBean.setNomeCadastrado("");
		appParametersBean.setSecretKeyCadastrada("");


		// Comunicacao com a CA
		HttpItegration httpIntegration = new HttpItegration(appParametersBean);

		System.out.println("*********************************");
		System.out.println("CRIANDO JWT");
		// Preparado para receber de parametro
		Calendar c1 = Calendar.getInstance();
		Instant now = c1.getTime().toInstant();
		JwtCreationBean bean = new JwtCreationBean();
		bean.setIss(appParametersBean.getNomeCadastrado());
		bean.setAud("https://api.endereco.com.br/auth/server/v1/token");
		bean.setSub(appParametersBean.getSecretKeyCadastrada());
		bean.setIat(Date.from(now));
		bean.setExpt(Date.from(now.plus(1L, ChronoUnit.HOURS)));
		bean.setJti(Utils.nonceGenerator());
		bean.setVer("1.0");
		String createdJWT = httpIntegration.createJWT(bean);
		System.out.println(createdJWT);

		System.out.println("*********************************");
		System.out.println("FAZENDO LOGIN");
		OAuthTokenRequestBean oAuthTokenRequestBean = new OAuthTokenRequestBean();
		oAuthTokenRequestBean.setJwt(createdJWT);
		OAuthTokenResponseBean oAuthTokenResponseBean = httpIntegration.doLogin(oAuthTokenRequestBean);

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO CONSULTA LIMITES");
		ConsultaLimitesBean consultaLimitesBean = new ConsultaLimitesBean();
		consultaLimitesBean.setMt013we_ret_code("0");
		consultaLimitesBean.setMt013we_local_erro("0");
		consultaLimitesBean.setMt013we_ident_perif("MATEUS");
		consultaLimitesBean.setMt013we_ident_canal("001");
		consultaLimitesBean.setMt013we_nome_layout("mt013we");
		consultaLimitesBean.setMt013we_usuario("MATEUS");
		consultaLimitesBean.setMt013we_cta_cartao("0004224630053486029");
		consultaLimitesBean.setMt013we_tam_layout("72");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/consultarLimites",
				gson.toJson(consultaLimitesBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO LISTA CARTOES");
		ConsultaCartoesRequestBean consultaCartoesRequestBean = new ConsultaCartoesRequestBean();
		consultaCartoesRequestBean.setMt042we_tam_layout("168");
		consultaCartoesRequestBean.setMt042we_ident_perif("MATEUS");
		consultaCartoesRequestBean.setMt042we_ret_code("0");
		consultaCartoesRequestBean.setMt042we_ident_canal("001");
		consultaCartoesRequestBean.setMt042we_cpf_cnpj("4935411287"); // 4935411287 M0049
		consultaCartoesRequestBean.setMt042we_chave_restart_e("0");
		consultaCartoesRequestBean.setMt042we_nome_layout("mt042we");
		consultaCartoesRequestBean.setMt042we_local_erro("0");
		consultaCartoesRequestBean.setMt042we_usuario("MATEUS");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/listarCartoes",
				gson.toJson(consultaCartoesRequestBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO CONSULTA EXTRATO FECHADO");
		ConsultarExtratoFechadoBean consultarExtratoFechadoBean = new ConsultarExtratoFechadoBean();
		consultarExtratoFechadoBean.setMt018we_nome_layout("mt018we");
		consultarExtratoFechadoBean.setMt018we_usuario("MATEUS");
		consultarExtratoFechadoBean.setMt018we_ident_perif("MATEUS");
		consultarExtratoFechadoBean.setMt018we_local_erro("0");
		consultarExtratoFechadoBean.setMt018we_tam_layout("72");
		consultarExtratoFechadoBean.setMt018we_ident_canal("001");
		consultarExtratoFechadoBean.setMt018we_ret_code("0");
		consultarExtratoFechadoBean.setMt018we_e_conta("0004224630707163000");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/consultarExtratoFechado",
				gson.toJson(consultarExtratoFechadoBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO CONSULTA EXTRATO ABERTO");
		ConsultarExtratoAbertoBean consultarExtratoAbertoBean = new ConsultarExtratoAbertoBean();
		consultarExtratoAbertoBean.setMt025we_chave_restart("0");
		consultarExtratoAbertoBean.setMt025we_ident_perif("MATEUS");
		consultarExtratoAbertoBean.setMt025we_local_erro("0");
		consultarExtratoAbertoBean.setMt025we_conta("0004224630718312000");
		consultarExtratoAbertoBean.setMt025we_vencto("10042020");
		consultarExtratoAbertoBean.setMt025we_cartao("0004224630718312024");
		consultarExtratoAbertoBean.setMt025we_ident_canal("001");
		consultarExtratoAbertoBean.setMt025we_tam_layout("199");
		consultarExtratoAbertoBean.setMt025we_nome_layout("mt025we");
		consultarExtratoAbertoBean.setMt025we_ret_code("0");
		consultarExtratoAbertoBean.setMt025we_usuario("MATEUS");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/consultarExtratoAberto",
				gson.toJson(consultarExtratoAbertoBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO OBTER LINHA DIGITAVEL");
		ObterLinhaDigitavelBean obterLinhaDigitavelBean = new ObterLinhaDigitavelBean();
		obterLinhaDigitavelBean.setMt030we_nome_layout("mt030we");
		obterLinhaDigitavelBean.setMt030we_local_erro("0");
		obterLinhaDigitavelBean.setMt030we_ident_perif("MATEUS");
		obterLinhaDigitavelBean.setMt030we_ident_canal("001");
		obterLinhaDigitavelBean.setMt030we_tam_layout("147");
		obterLinhaDigitavelBean.setMt030we_ret_code("0");
		obterLinhaDigitavelBean.setMt030we_usuario("MATEUS");
		obterLinhaDigitavelBean.setMt030we_conta_cartao("0004224630053486000");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/obterLinhaDigitavel",
				gson.toJson(obterLinhaDigitavelBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO CONSULTAR LANCAMENTOS");
		ConsultarLancamentosBean consultarLancamentosBean = new ConsultarLancamentosBean();
		consultarLancamentosBean.setMt031we_chave_restart_e("0");
		consultarLancamentosBean.setMt031we_ident_perif("MATEUS");
		consultarLancamentosBean.setMt031we_ret_code("0");
		consultarLancamentosBean.setMt031we_e_data_corte("20200330");
		consultarLancamentosBean.setMt031we_e_cartao("0004224630053486029");
		consultarLancamentosBean.setMt031we_tam_layout("72");
		consultarLancamentosBean.setMt031we_local_erro("0");
		consultarLancamentosBean.setMt031we_nome_layout("mt031we");
		consultarLancamentosBean.setMt031we_ident_canal("001");
		consultarLancamentosBean.setMt031we_usuario("MATEUS");
		consultarLancamentosBean.setMt031we_e_conta("0004224630053486000");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/consultarLancamentosCartao",
				gson.toJson(consultarLancamentosBean), oAuthTokenResponseBean.getAccess_token()));

		System.out.println("*********************************");
		System.out.println("CHAMANDO SERVICO SELECIONAR OPCAO FATURA");
		SelecionarOpcaoFaturaBean selecionarOpcaoFaturaBean = new SelecionarOpcaoFaturaBean();
		selecionarOpcaoFaturaBean.setMt037we_optin_optout("A");
		selecionarOpcaoFaturaBean.setMt037we_local_erro("0");
		selecionarOpcaoFaturaBean.setMt037we_ident_perif("MATEUS");
		selecionarOpcaoFaturaBean.setMt037we_numero_cartao("0004224630655939000");
		selecionarOpcaoFaturaBean.setMt037we_email("");
		selecionarOpcaoFaturaBean.setMt037we_ident_canal("001");
		selecionarOpcaoFaturaBean.setMt037we_ident_parceiro("MTT");
		selecionarOpcaoFaturaBean.setMt037we_ret_code("0");
		selecionarOpcaoFaturaBean.setMt037we_usuario("MATEUS");
		selecionarOpcaoFaturaBean.setMt037we_nome_layout("mt037we");
		selecionarOpcaoFaturaBean.setMt037we_tam_layout("147");
		System.out.println(httpIntegration.doSinedPost("/cartoes/parceiros/selecionarOpcaoFatura",
				gson.toJson(selecionarOpcaoFaturaBean), oAuthTokenResponseBean.getAccess_token()));
	}
}
