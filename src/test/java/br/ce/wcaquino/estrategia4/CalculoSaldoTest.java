package br.ce.wcaquino.estrategia4;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.dao.SaldoDAO;
import br.ce.wcaquino.dao.impl.SaldoDAOImpl;
import br.ce.wcaquino.dao.utils.ConnectionFactory;
import br.ce.wcaquino.dbunit.ImportExport;
import br.ce.wcaquino.utils.DataUtils;

public class CalculoSaldoTest {

	// 1 usuario
	// 1 conta
	// 1 transacao
	
	//+deve considerar transacoes do mesmo usuário
	//+deve considerar transacoes da mesma conta
	//+deve considerar transacoes pagas
	//+deve considerar transacoes até a data corrente 
		// +ontem / +hoje / +amanhã
	//+deve somar receitas
	//+deve subtrair despesas
	
	@Test
	public void deveCalcularSaldoCorreto() throws Exception {
//		ImportExport.importarBanco("saldo.xml");
		
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet dataSet = builder.build(new FileInputStream("massas" + File.separator + "saldo.xml"));
		
		ReplacementDataSet dataSetAlterado = new ReplacementDataSet(dataSet);
		dataSetAlterado.addReplacementObject("[hoje]", new Date());
		dataSetAlterado.addReplacementObject("[ontem]", DataUtils.obterDataComDiferencaDias(-1));
		dataSetAlterado.addReplacementObject("[amanha]", DataUtils.obterDataComDiferencaDias(1));
		
		DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSetAlterado);
		
		
		SaldoDAO saldoDAO = new SaldoDAOImpl();
		Assert.assertEquals(new Double(162d), saldoDAO.getSaldoConta(1L));
		Assert.assertEquals(new Double(8d), saldoDAO.getSaldoConta(2L));
		Assert.assertEquals(new Double(4d), saldoDAO.getSaldoConta(3L));
	}
}
