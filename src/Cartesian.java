import java.util.LinkedHashMap;
import java.util.LinkedList;


public class Cartesian {
	public static Table getAnswerByCartesian(String firstName,String secondName,String query){
		Table first=TableMgr.getInstance().getTableByName(firstName);
		Table second=TableMgr.getInstance().getTableByName(secondName);
		LinkedList<String> cols = new LinkedList<>();
		for(String colName:first.getHeader())
			cols.add(firstName+"."+colName);
		for(String colName:second.getHeader())
			cols.add(secondName+"."+colName);
		if(TableMgr.getInstance().getTableByName("cartesian")!=null){
			TableMgr.getInstance().deleteTableByName("cartesian");
		}
		Table newTable=new Table("caertesian", cols,null);
		TableMgr.getInstance().addTable(newTable);
		for(Long firstRowUniqueID:first.getRows().keySet()){
			Row firstRow=first.getRows().get(firstRowUniqueID);
			for(Long secondRowUniqueID:second.getRows().keySet()){
				Row secondRow=second.getRows().get(secondRowUniqueID);
				Row r=new Row();
				LinkedHashMap<String,String>newColValues=new LinkedHashMap<>();
				for(String firstColName:firstRow.getColumns().keySet()){
					newColValues.put(firstName+"."+firstColName, firstRow.getColumns().get(firstColName));
				}
				for(String secondColName:secondRow.getColumns().keySet()){
					newColValues.put(secondName+"."+secondColName, secondRow.getColumns().get(secondColName));
				}
				r.setColumns(newColValues);
				newTable.insert(r);
			}
		}
		return newTable;
		//DELETE THE TABLE
	}
}
