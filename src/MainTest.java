import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

public class MainTest 
{
	public static void main(String[] args) {

//		LinkedList<Table> tables = new LinkedList<>();
		
		Parser.parseWHEREClause("((B=1) OR (NOT A AND X))", null);
		TableMgr manager = TableMgr.getInstance();
		LinkedList<String> cols = new LinkedList<>();
		cols.add("ID");
		cols.add("Name");
		Table table = new Table("TEST", cols, TableMode.PERMENANT);
//		tables.add(table);
		Scanner scanner = new Scanner(System.in);

		HashMap<String, String> adds = new HashMap<>();

		adds.put("ID", "1");
		adds.put("Name", "Farzin");
		table.insert(adds);
		adds.put("ID", "2");
		adds.put("Name", "saeid");
		table.insert(adds);

//		String input = scanner.nextLine();
//		while(true)
//		{
//			String[] splited = input.split("\\s+");
//			String[] Cols = null;
//			LinkedList<String> Columns = new LinkedList<>();
//			if(splited[0].equals("SELECT"))
//			{
//				Cols = splited[1].split(",");
//				for (int i = 0; i < Cols.length; i++) {
//					Columns.add(Cols[i]);
//				}
//				for (int i = 0; i < tables.size(); i++) {
//					if(tables.get(i).getName().equals(splited[3])
//					{
//						tables.get(i).select(Columns);
//						break;
//					}
//				}
//			}
//
//			String[] splited3 = input.split("\\s+");
//			String[] Cols2 = null;
//			if(splited3[0].equals("UPDATE"))
//			{
//				Cols2 = splited3[3].split("=");
//				for (int i = 0; i < tables.size(); i++) {
//					if(tables.get(i).getName().equals(splited3[1]))
//					{
//						tables.get(i).update(Cols2);
//						tables.get(i).print();
//						break;
//					}
//				}
//			}
//
//			String[] splited2 = input.split("\\s+");
//			if(splited2[0].equals("DELETE"))
//			{
//				for (int i = 0; i < tables.size(); i++) {
//					if(tables.get(i).getName().equals(splited2[2]))
//					{
//						tables.get(i).delete();
//						tables.get(i).print();
//						break;
//					}
//				}
//			}
//			input = scanner.nextLine();
//		}
	}
}
