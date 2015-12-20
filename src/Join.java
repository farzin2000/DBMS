
public class Join {
	public static String generateNewQuery(String query){
		String []rawJoin=query.split(" JOIN ");
		String firstTableName=rawJoin[0].substring(rawJoin[0].lastIndexOf(" ")+1);
		String secondTableName=rawJoin[1].substring(0, rawJoin[1].indexOf(" "));
		
		Table first=TableMgr.getInstance().getTableByName(firstTableName);
		Table second=TableMgr.getInstance().getTableByName(secondTableName);
		String firstColumn=first.getPrimaryKey();
		String secondColumn;
		secondColumn="STID";
//		for(ForeignKey fk:second.getForeignKey()){
//			if(fk.getReferencedTableString().equals(firstTableName))
//				secondColumn=fk.getColumnName();
//		}
//		if(secondColumn==null)
//			throw new Exception("FARZINE AHMAGH!!!!");
		String newQuery="";
		newQuery+=query.substring(0,query.toUpperCase().indexOf("FROM"))+"FROM "+firstTableName+","+secondTableName+" WHERE ";
		String tuple=secondTableName+"."+secondColumn+"="+firstTableName+"."+firstColumn;
		if(query.substring(query.indexOf("WHERE")+6,query.length()).toUpperCase().equals("TRUE")){
			newQuery+=tuple+";";
		}
		else{
			newQuery+="("+tuple+") AND ("+query.substring(query.indexOf("WHERE")+6,query.length())+");";
		}
	//	System.out.println(newQuery);
		return newQuery;
	}
}
