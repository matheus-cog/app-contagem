package br.com.mcog.appcontagem.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.mcog.appcontagem.app.objects.Contagem;
import br.com.mcog.appcontagem.app.objects.Produto;
import br.com.mcog.appcontagem.app.objects.ProdutoEmpresa;

public class DBAdapter {

    Context context;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    public void salvarDadosContagem(int idContagem, String codLeitor, String dataContagem, String area, String unidadeContagem, String reincidencia, String codOperador, String tipoContagem, String permissaoEdicaoCelulas){
        try{
            db = helper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.ID_CONTAGEM_EXTERNO, idContagem);
            contentValues.put(Constants.COD_LEITOR, codLeitor);
            contentValues.put(Constants.DATA_CONTAGEM, dataContagem);
            contentValues.put(Constants.AREA, area);
            contentValues.put(Constants.UNIDADE_CONTAGEM, unidadeContagem);
            contentValues.put(Constants.REINCIDENCIA, reincidencia);
            contentValues.put(Constants.COD_OPERADOR, codOperador);
            contentValues.put(Constants.TIPO_CONTAGEM, tipoContagem);
            contentValues.put(Constants.PERMISSAO_EDICAO_CELULAS, permissaoEdicaoCelulas);

            db.insert(Constants.TB_DADOS_CONTAGEM, Constants.ID_CONTAGEM_INTERNO, contentValues);
        } catch (SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
    }

    public boolean salvarProduto(Produto produto){

        try{
            // Verifica se produto existe no banco
            db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+Constants.TB_CONTAGEM_NAME+" WHERE cod_barras = '"+produto.getCodBarras()+"'", null);
            cursor.moveToNext();
            Log.d("","Procurou pelo produto.");

            // Se o produto já existir é feito um update
            if(cursor.getCount() > 0){
                // Verifica se o produto é associado ou não
                Cursor cursor2 = db.rawQuery("SELECT * FROM "+Constants.TB_PRODUTOS_EMPRESA_NAME+" WHERE cod_barras = '"+produto.getCodBarras()+"'", null);
                ContentValues contentValues = new ContentValues();
                Log.d("", "nao entrou |"+produto.getCodBarras()+"|");
                if(cursor2 != null && cursor2.moveToNext()){
                    Log.e("", "entrou");
                    if(cursor2.getString(8).equals("ass")){
                        String quantidade = cursor.getString(3);
                        String subtotal = cursor.getString(4);
                        int quantidadeEmbalagemBanco = cursor2.getInt(7);
                        int quantidadeBanco = Integer.parseInt(quantidade);
                        int subtotalBanco = Integer.parseInt(subtotal);

                        // Update no banco com soma
                        int novaQuantidade = (produto.getQuantidade()*quantidadeEmbalagemBanco)+quantidadeBanco;
                        int novoSubtotal = (produto.getSubTotal()*quantidadeEmbalagemBanco)+subtotalBanco;
                        contentValues.put(Constants.QUANTIDADE, novaQuantidade);
                        contentValues.put(Constants.SUBTOTAL, novoSubtotal);
                    } else{
                        String quantidade = cursor.getString(3);
                        String subtotal = cursor.getString(4);
                        int quantidadeBanco = Integer.parseInt(quantidade);
                        int subtotalBanco = Integer.parseInt(subtotal);

                        // Insert no banco com soma ao produto já existente
                        int novaQuantidade = produto.getQuantidade()+quantidadeBanco;
                        int novoSubtotal = produto.getSubTotal()+subtotalBanco;
                        contentValues.put(Constants.QUANTIDADE, novaQuantidade);
                        contentValues.put(Constants.SUBTOTAL, novoSubtotal);
                    }
                } else{
                    return false;
                }
                Log.d("", "prod em cima do result |"+produto.getCodBarras()+"|");
                long result = db.update(Constants.TB_CONTAGEM_NAME, contentValues, "cod_barras='"+produto.getCodBarras()+"'", null);
                Log.d("","O número de linhas afetadas pelo update do produto: "+result);
                cursor.close();
                cursor2.close();
                return result > 0;

            } else{
                Log.d("","Produto não estava cadastrado.");
                Cursor cursor2 = db.rawQuery("SELECT * FROM "+Constants.TB_PRODUTOS_EMPRESA_NAME+" WHERE cod_barras = '"+produto.getCodBarras()+"'", null);
                // Insert no banco
                db = helper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                if (cursor2 != null && cursor2.moveToNext()){
                    if(cursor2.getString(8).equals("ass")){
                        int quantidadeEmbalagemBanco = cursor2.getInt(7);
                        int novaQuantidade = produto.getQuantidade() * quantidadeEmbalagemBanco;
                        int novoSubTotal = produto.getSubTotal() * quantidadeEmbalagemBanco;

                        cv.put(Constants.IDENTIFICADOR, cursor2.getString(1));
                        cv.put(Constants.COD_BARRAS, produto.getCodBarras());
                        cv.put(Constants.QUANTIDADE, novaQuantidade);
                        cv.put(Constants.SUBTOTAL, novoSubTotal);
                    } else{
                        cv.put(Constants.IDENTIFICADOR, cursor2.getString(1));
                        cv.put(Constants.COD_BARRAS, produto.getCodBarras());
                        cv.put(Constants.QUANTIDADE, produto.getQuantidade());
                        cv.put(Constants.SUBTOTAL, produto.getSubTotal());
                    }
                } else{
                    return false;
                }

                long result = db.insert(Constants.TB_CONTAGEM_NAME, Constants.ID_LANCAMENTO, cv);
                Log.d("","O número de linhas afetadas pelo insert do produto: "+result);
                cursor.close();
                cursor2.close();
                return result > 0;
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
        return false;
    }

    public boolean updateProduto(Produto produto, int novaQuantidade, int novoSubTotal){

        try{
            db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+Constants.TB_PRODUTOS_EMPRESA_NAME+" WHERE cod_barras = '"+produto.getCodBarras()+"'", null);
            ContentValues cv = new ContentValues();
            if (cursor != null && cursor.moveToNext()){
                cv.put(Constants.COD_BARRAS, produto.getCodBarras());
                cv.put(Constants.QUANTIDADE, novaQuantidade);
                cv.put(Constants.SUBTOTAL, novoSubTotal);
            } else {
                return false;
            }

            long result = db.update(Constants.TB_CONTAGEM_NAME, cv, "cod_barras='"+produto.getCodBarras()+"'", null);
            Log.d("","O número de linhas afetadas pelo insert do produto: "+result);
            cursor.close();
            return result > 0;
        } catch (SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
        return false;
    }

    public void salvarProdutoEmpresa(ProdutoEmpresa produto){
        try {
            db = helper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.IDENTIFICADOR, produto.getIdentificador());
            contentValues.put(Constants.DESCRICAO, produto.getDescricao());
            contentValues.put(Constants.COD_BARRAS, produto.getCodBarras());
            contentValues.put(Constants.NOME_MARCA, produto.getNomeMarca());
            contentValues.put(Constants.COD_FABRICA, produto.getCodFabrica());
            contentValues.put(Constants.PRECO, produto.getPreco());
            contentValues.put(Constants.QUANTIDADE_EMBALAGEM, produto.getQuantidadeEmbalagem());
            contentValues.put(Constants.TIPO, produto.getTipo());

            db.insert(Constants.TB_PRODUTOS_EMPRESA_NAME, Constants.ID_LANCAMENTO, contentValues);
        } catch (SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
    }

    public ArrayList<Produto> getProdutosContagem(){
        ArrayList<Produto> arrayListProdutos = new ArrayList<>();
        String[] colunas = {Constants.ID_LANCAMENTO, Constants.IDENTIFICADOR, Constants.COD_BARRAS, Constants.QUANTIDADE, Constants.SUBTOTAL};

        try {
            db = helper.getWritableDatabase();
            Cursor cursor = db.query(Constants.TB_CONTAGEM_NAME, colunas, null, null, null,null, null);

            Produto produto;
            if (cursor != null){
                while (cursor.moveToNext()){
                    String identificador = cursor.getString(1);
                    String cod_barras = cursor.getString(2);
                    String quantidade = cursor.getString(3);
                    String subtotal = cursor.getString(4);

                    produto = new Produto();
                    produto.setIdentificador(identificador);
                    produto.setCodBarras(cod_barras);
                    produto.setQuantidade(Integer.parseInt(quantidade));
                    produto.setSubTotal(Integer.parseInt(subtotal));

                    arrayListProdutos.add(produto);
                }
                cursor.close();
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }

        return arrayListProdutos;
    }

    // Verifica se os produtos empresa já estão cadastrados na tabela do banco interno
    public boolean verificaTabelaProdutoEmpresa(){
        try{
            db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+Constants.TB_PRODUTOS_EMPRESA_NAME, null);

            // Verifica se existem ao menos 40 mil produtos no banco interno
            boolean result = cursor.getCount() > 40000;
            cursor.close();
            return result;
        } catch (SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
        return false;
    }
    // Verifica se já existem contagens listadas no banco interno
    public Contagem verificaTabelaContagens(){
        Contagem contagem = new Contagem();
        try{
            db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+Constants.TB_DADOS_CONTAGEM, null);

            if(cursor != null && cursor.moveToNext()) {
                Log.d("", Integer.toString(cursor.getInt(1)));
                Log.d("", cursor.getString(2));
                Log.d("", cursor.getString(3));
                Log.d("", cursor.getString(4));
                Log.d("", cursor.getString(5));
                Log.d("", cursor.getString(6));
                Log.d("", cursor.getString(7));
                Log.d("", cursor.getString(8));
                Log.d("", cursor.getString(9));

                contagem.setIdContagem(Integer.parseInt(cursor.getString(1)));
                contagem.setCodLeitor(cursor.getString(2));
                contagem.setArea(cursor.getString(3));
                contagem.setUnidadeContagem(cursor.getString(4));
                contagem.setDataContagem(cursor.getString(5));
                contagem.setReincidencia(cursor.getString(6));
                contagem.setCodOperador(cursor.getString(7));
                contagem.setTipoContagem(cursor.getString(8));
                contagem.setPermissaoEdicaoCelulas(cursor.getString(9));

                cursor.close();
                return contagem;
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        } finally {
            db.close();
            helper.close();
        }
        return contagem;
    }

    public void deletarContagem(){
        db = helper.getWritableDatabase();
        db.delete(Constants.TB_CONTAGEM_NAME,null,null);
    }

    public void deletarDadosContagem(){
        db = helper.getWritableDatabase();
        db.delete(Constants.TB_DADOS_CONTAGEM,null,null);
    }

    public void deletarProdutosEmpresa(){
        db = helper.getWritableDatabase();
        db.delete(Constants.TB_PRODUTOS_EMPRESA_NAME, null, null);
    }
}