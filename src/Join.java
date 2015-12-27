import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Join {
	public static Table joinQuery(String query,LinkedList<String>inputCols) {
		String []rawJoin=query.split(" JOIN ");
		String firstName=rawJoin[0].substring(rawJoin[0].lastIndexOf(" ")+1);
		String secondName=rawJoin[1].substring(0, rawJoin[1].indexOf(" "));
		Table first = TableMgr.getInstance().getTableByName(firstName);
		Table second = TableMgr.getInstance().getTableByName(secondName);
		LinkedList<String> cols = new LinkedList<>();
		for (String colName : first.getHeader())
			cols.add(firstName + "." + colName);
		for (String colName : second.getHeader())
			cols.add(secondName + "." + colName);
		if (TableMgr.getInstance().getTableByName("joined") != null) {
			TableMgr.getInstance().deleteTableByName("joined");
		}
		Table newTable = new Table("joined", cols, null);
		newTable.createIndex("nothing", firstName + "." + first.getPrimaryKey());
		TableMgr.getInstance().addTable(newTable);
		ForeignKey reference = null;
		for (ForeignKey fk : second.getForeignKey()) {
			if (fk.getReferencedTable().equals(firstName))
				reference = fk;
		}
		for (Long secondTableUniqueid : second.getRows().keySet()) {
			Row r=new Row();
			LinkedHashMap<String,String>newColValues=new LinkedHashMap<>();
			Row secondTableRow = second.getRows().get(secondTableUniqueid);
			Row firstFromSecondRef = first.getRows().get(first.getIndexByName(first.getPrimaryKey()).index
					.get(secondTableRow.getColumns().get(reference.getColumnName())).get(0));
			for (String firstColName : firstFromSecondRef.getColumns().keySet()) {
				newColValues.put(firstName + "." + firstColName, firstFromSecondRef.getColumns().get(firstColName));
			}
			for (String secondColName : secondTableRow.getColumns().keySet()) {
				newColValues.put(secondName + "." + secondColName, secondTableRow.getColumns().get(secondColName));
			}
			r.setColumns(newColValues);
			newTable.insert(r);
		}
		return newTable.select(inputCols, newTable);
		// DELETE THE TABLE
	}
}
