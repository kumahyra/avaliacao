package testes;
import java.util.List;

public class CapaLanceHelper {

	private String item;
	private List<LanceDesclassificacaoHelper> lancesDesclassificacao;
	private List<LanceNegociacaoHelper> lancesNegociacao;
	private List<LancePreferenciaHelper> lancesPreferencia;
	private List<LanceHelper> lances;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public List<LanceDesclassificacaoHelper> getLancesDesclassificacao() {
		return lancesDesclassificacao;
	}
	public void setLancesDesclassificacao(List<LanceDesclassificacaoHelper> lancesDesclassificacao) {
		this.lancesDesclassificacao = lancesDesclassificacao;
	}
	public List<LanceNegociacaoHelper> getLancesNegociacao() {
		return lancesNegociacao;
	}
	public void setLancesNegociacao(List<LanceNegociacaoHelper> lancesNegociacao) {
		this.lancesNegociacao = lancesNegociacao;
	}
	public List<LancePreferenciaHelper> getLancesPreferencia() {
		return lancesPreferencia;
	}
	public void setLancesPreferencia(List<LancePreferenciaHelper> lancesPreferencia) {
		this.lancesPreferencia = lancesPreferencia;
	}
	public List<LanceHelper> getLances() {
		return lances;
	}
	public void setLances(List<LanceHelper> lances) {
		this.lances = lances;
	}
	
}
