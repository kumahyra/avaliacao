package testes;

import java.util.ArrayList;
import java.util.List;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.BulletListVariable;
import pl.jsolve.templ4docx.variable.DocumentVariable;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;
import pl.jsolve.templ4docx.variable.Variables;

public class DocumentTest {

	public static final String OUTPUT_FILE = "ata2.docx";

	public static void main(String[] args) throws Exception {
		
		//Docx docxOrigem = new Docx("C:\\Desenvolvimento\\sub.docx");
		Docx docx = new Docx("C:\\Desenvolvimento\\ata.docx");
		docx.setVariablePattern(new VariablePattern("${", "}"));
		
		Variables vars = new Variables();
		vars.addTextVariable(new TextVariable("${data_hora}", "09h:30min do dia 03 de Julho de 2017"));
		vars.addTextVariable(new TextVariable("${nome_pregoeiro}", "RONAN TAVARES CAMARGO"));
		vars.addTextVariable(new TextVariable("${numero_decreto}", "147/2014"));
		vars.addTextVariable(new TextVariable("${modalidade_pregao}", "PREGÃO PRESENCIAL"));
		vars.addTextVariable(new TextVariable("${numero_licitacao}", "0010/2017"));
		vars.addTextVariable(new TextVariable("${tipo_pregao}", "MENOR PREÇO POR ITEM"));
		
		List<Variable> teste = new ArrayList<Variable>();
		teste.add(new TextVariable("${credenciamento}", " IPARTS COMPONENTES LTDA - EPP inscrita no CNPJ/MF sob o n� 05.755.760/0001-22, estabelecida no endere�o , , APARECIDA DE GOIANIA - GO, neste ato representada por , portador da CI n� 0 e CPF n� 0, residente no munic�pio  - ;"));
		teste.add(new TextVariable("${credenciamento}", " JOSE EURIPEDES DE SOUZA inscrita no CNPJ/MF sob o n� 235.034.401-00, estabelecida no endere�o , , APARECIDA DE GOIANIA - GO, neste ato representada por , portador da CI n� 0 e CPF n� 0, residente no munic�pio  - ;"));
		
		BulletListVariable bulletListVariable = new BulletListVariable("${credenciamento}", teste);
		
		vars.addBulletListVariable(bulletListVariable);
		vars.addBulletListVariable(getListaVariaveisLances());
		
		docx.fillTemplate(vars);
		
		docx.save(OUTPUT_FILE);
		
	}
	
	private static List<CapaLanceHelper> getLances(){
		List<CapaLanceHelper> lances = new ArrayList<CapaLanceHelper>();
		
		for(int i = 1; i <= 4; i++){
			CapaLanceHelper lance = new CapaLanceHelper();
			lance.setItem("Item " + i + ": Produto " + i);
			
			lance.setLancesNegociacao(new ArrayList<LanceNegociacaoHelper>());
			for(int j = 1; j <= 2; j++){
				LanceNegociacaoHelper lanceNegociacao = new LanceNegociacaoHelper();
				lanceNegociacao.setFornecedor("Fornecedor " + i + " " + j);
				lanceNegociacao.setCpfCnpj("000.000.000-0" + i + " " + j);
				lanceNegociacao.setValor("R$ 10,0" + i + " " + j);
				lance.getLancesNegociacao().add(lanceNegociacao);
			}
			
			lance.setLancesPreferencia(new ArrayList<LancePreferenciaHelper>());
			for(int j = 1; j <= 2; j++){
				LancePreferenciaHelper lanceNegociacao = new LancePreferenciaHelper();
				lanceNegociacao.setFornecedor("Fornecedor " + i + " " + j);
				lanceNegociacao.setCpfCnpj("000.000.000-0" + i + " " + j);
				lanceNegociacao.setValor("R$ 10,0"+ i + " " + j);
				lance.getLancesPreferencia().add(lanceNegociacao);
			}
			
			lance.setLancesDesclassificacao(new ArrayList<LanceDesclassificacaoHelper>());
			for(int j = 1; j <= 2; j++){
				LanceDesclassificacaoHelper lanceNegociacao = new LanceDesclassificacaoHelper();
				lanceNegociacao.setFornecedor("Fornecedor " + i + " " + j);
				lanceNegociacao.setCpfCnpj("000.000.000-0" + i + " " + j);
				lanceNegociacao.setMotivo("Motivo " + i + " " + j);
				lance.getLancesDesclassificacao().add(lanceNegociacao);
			}
			
			lance.setLances(new ArrayList<LanceHelper>());
			for(int j = 1; j <= 4; j++){
				LanceHelper lanceNegociacao = new LanceHelper();
				lanceNegociacao.setRodada("" + i + " " + j);
				lanceNegociacao.setSituacao("Situação " + i + " " + j);
				lanceNegociacao.setFornecedor("Fornecedor " + i + " " + j);
				lanceNegociacao.setCpfCnpj("000.000.000-0" + i + " " + j);
				lanceNegociacao.setValor("R$ 10,0" + i + " " + j);
				lance.getLances().add(lanceNegociacao);
			}
			
			lances.add(lance);
		}
		
		return lances;
	}
	
	private static TableVariable getVariaveisTabelaLances(List<LanceHelper> lances){
		
		TableVariable tableVariable = new TableVariable();
		
		List<Variable> colunaRodada = new ArrayList<Variable>();
		List<Variable> colunaSituacao = new ArrayList<Variable>();
		List<Variable> colunaFornecedor = new ArrayList<Variable>();
		List<Variable> colunaCpfCnpj = new ArrayList<Variable>();
		List<Variable> colunaLance = new ArrayList<Variable>();
		
		for(LanceHelper lance : lances){
			colunaRodada.add(new TextVariable("${lance.rodada}", lance.getRodada()));
			colunaSituacao.add(new TextVariable("${lance.situacao}", lance.getSituacao()));
			colunaFornecedor.add(new TextVariable("${lance.fornecedor}", lance.getFornecedor()));
			colunaCpfCnpj.add(new TextVariable("${lance.cpf}", lance.getCpfCnpj()));
			colunaLance.add(new TextVariable("${lance.valor}", lance.getValor()));
		}
		
		tableVariable.addVariable(colunaRodada);
		tableVariable.addVariable(colunaSituacao);
		tableVariable.addVariable(colunaFornecedor);
		tableVariable.addVariable(colunaCpfCnpj);
		tableVariable.addVariable(colunaLance);
		
		return tableVariable;
	}
	
	private static TableVariable getVariaveisTabelaLancesNegociacao(List<LanceNegociacaoHelper> lances){
		
		TableVariable tableVariable = new TableVariable();
		
		List<Variable> colunaFornecedor = new ArrayList<Variable>();
		List<Variable> colunaCpfCnpj = new ArrayList<Variable>();
		List<Variable> colunaLance = new ArrayList<Variable>();
		
		for(LanceNegociacaoHelper lance : lances){
			colunaFornecedor.add(new TextVariable("${neg.fornecedor}", lance.getFornecedor()));
			colunaCpfCnpj.add(new TextVariable("${neg.cpf}", lance.getCpfCnpj()));
			colunaLance.add(new TextVariable("${neg.valor}", lance.getValor()));
		}
		
		tableVariable.addVariable(colunaFornecedor);
		tableVariable.addVariable(colunaCpfCnpj);
		tableVariable.addVariable(colunaLance);
		
		return tableVariable;
	}
	
	private static TableVariable getVariaveisTabelaLancesPreferencia(List<LancePreferenciaHelper> lances){
			
		TableVariable tableVariable = new TableVariable();
		
		List<Variable> colunaFornecedor = new ArrayList<Variable>();
		List<Variable> colunaCpfCnpj = new ArrayList<Variable>();
		List<Variable> colunaLance = new ArrayList<Variable>();
		
		for(LancePreferenciaHelper lance : lances){
			colunaFornecedor.add(new TextVariable("${pref.fornecedor}", lance.getFornecedor()));
			colunaCpfCnpj.add(new TextVariable("${pref.cpf}", lance.getCpfCnpj()));
			colunaLance.add(new TextVariable("${pref.valor}", lance.getValor()));
		}
		
		tableVariable.addVariable(colunaFornecedor);
		tableVariable.addVariable(colunaCpfCnpj);
		tableVariable.addVariable(colunaLance);
		
		return tableVariable;
	}
	
	private static TableVariable getVariaveisTabelaLancesDesclassificacao(List<LanceDesclassificacaoHelper> lances){
		
		TableVariable tableVariable = new TableVariable();
		
		List<Variable> colunaFornecedor = new ArrayList<Variable>();
		List<Variable> colunaCpfCnpj = new ArrayList<Variable>();
		List<Variable> colunaLance = new ArrayList<Variable>();
		
		for(LanceDesclassificacaoHelper lance : lances){
			colunaFornecedor.add(new TextVariable("${desc.fornecedor}", lance.getFornecedor()));
			colunaCpfCnpj.add(new TextVariable("${desc.cpf}", lance.getCpfCnpj()));
			colunaLance.add(new TextVariable("${desc.motivo}", lance.getMotivo()));
		}
		
		tableVariable.addVariable(colunaFornecedor);
		tableVariable.addVariable(colunaCpfCnpj);
		tableVariable.addVariable(colunaLance);
		
		return tableVariable;
	}
	
	private static Docx getDocumentSubReportLance(CapaLanceHelper lance){
		
		Docx docx = new Docx("C:\\Desenvolvimento\\ataPregao_lance.docx");
		
		Variables vars = new Variables();
		vars.addTableVariable(getVariaveisTabelaLances(lance.getLances()));
		vars.addTableVariable(getVariaveisTabelaLancesPreferencia(lance.getLancesPreferencia()));
		vars.addTableVariable(getVariaveisTabelaLancesNegociacao(lance.getLancesNegociacao()));
		vars.addTableVariable(getVariaveisTabelaLancesDesclassificacao(lance.getLancesDesclassificacao()));
		vars.addTextVariable(new TextVariable("${item}", lance.getItem()));
		
		docx.fillTemplate(vars);
		
		return docx;
	}
	
	private static BulletListVariable getListaVariaveisLances(){
		
		List<Variable> documentVariables = new ArrayList<Variable>();
		
		List<CapaLanceHelper> lances = getLances();
		
		for(CapaLanceHelper lance : lances){
			documentVariables.add(new DocumentVariable("${subreport_lance}", getDocumentSubReportLance(lance)));
		}
		
		return new BulletListVariable("${subreport_lance}", documentVariables);
	}

}
