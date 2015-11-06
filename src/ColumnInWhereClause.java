import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class ColumnInWhereClause
{

	public static void main(String[] args){
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlparser.sqltext = "Select firstname, lastname, age from Clients where  (Z!=1) and ((sex=1) or (kir=2))";
		int i = sqlparser.parse( );
		sqlparser.sqlstatements.get( 0 ).getWhereClause( ).getCondition( ).addORCondition("asdf");
//		System.out.println(sqlparser.sqlstatements.get(getWhereClause().getCondition().);

		if (i == 0)
		{
			WhereCondition w = new WhereCondition(sqlparser.sqlstatements.get( 0 ).getWhereClause( ).getCondition( ));
			w.printColumn();
		}
		else
			System.out.println(sqlparser.getErrormessage( ));
	}
}
