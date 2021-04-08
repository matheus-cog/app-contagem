package br.com.mcog.appcontagem.app.objects;

public class ProdutoEmpresa extends Produto {

    private String descricao;
    private String nomeMarca;
    private String codFabrica;
    private double preco;
    private int quantidadeEmbalagem;
    private String tipo;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeMarca() {
        return nomeMarca;
    }

    public void setNomeMarca(String nomeMarca) {
        this.nomeMarca = nomeMarca;
    }

    public String getCodFabrica() {
        return codFabrica;
    }

    public void setCodFabrica(String codFabrica) {
        this.codFabrica = codFabrica;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEmbalagem() {
        return quantidadeEmbalagem;
    }

    public void setQuantidadeEmbalagem(int quantidadeEmbalagem) {
        this.quantidadeEmbalagem = quantidadeEmbalagem;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
