package br.com.mcog.appcontagem.app.objects;

public class Contagem {

    int idContagem;
    String codLeitor;
    String dataContagem;
    String area;
    String unidadeContagem;
    String reincidencia;
    String codOperador;
    String tipoContagem;
    String permissaoEdicaoCelulas;

    public Contagem() {}

    public Contagem(int idContagem) {
        this.idContagem = idContagem;
    }

    public String getDataContagem() {
        return dataContagem;
    }

    public void setDataContagem(String dataContagem) {
        this.dataContagem = dataContagem;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUnidadeContagem() {
        return unidadeContagem;
    }

    public void setUnidadeContagem(String unidadeContagem) {
        this.unidadeContagem = unidadeContagem;
    }

    public String getReincidencia() {
        return reincidencia;
    }

    public void setReincidencia(String reincidencia) {
        this.reincidencia = reincidencia;
    }

    public String getCodOperador() {
        return codOperador;
    }

    public void setCodOperador(String codOperador) {
        this.codOperador = codOperador;
    }

    public String getTipoContagem() {
        return tipoContagem;
    }

    public void setTipoContagem(String tipoContagem) {
        this.tipoContagem = tipoContagem;
    }

    public String getPermissaoEdicaoCelulas() {
        return permissaoEdicaoCelulas;
    }

    public void setPermissaoEdicaoCelulas(String permissaoEdicaoCelulas) {
        this.permissaoEdicaoCelulas = permissaoEdicaoCelulas;
    }

    public int getIdContagem() {
        return idContagem;
    }

    public void setIdContagem(int idContagem) {
        this.idContagem = idContagem;
    }

    public String getCodLeitor() {
        return codLeitor;
    }

    public void setCodLeitor(String codLeitor) {
        this.codLeitor = codLeitor;
    }
}
