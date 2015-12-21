import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws IOException {
		
		Scanner input = new Scanner(System.in);
		Scanner input2=new Scanner(new File("/home/mohammad/Desktop/99/in.txt"));
//		BufferedReader reader = new BufferedReader(new FileReader(new File("../PPhase1-Test/99/in.txt")));
//		String line;
//		while ((line = reader.readLine()) != null) 
//		{
//			System.out.println(Parser.parseQuery(line));
//		}
		while(input2.hasNext())
		{
			String rawinput=input2.nextLine();
//			if(rawinput.toUpperCase().contains("INDEX")){
//				System.err.println("Press enter...");
//				input.nextLine();
//			}
			System.out.println(Parser.parseQuery(rawinput));
		}
		
		
		
////		System.out.println(Parser.parseQuery("CREATE TABLE STT(STID INT,STNAME VARCHAR,STDEG VARCHAR,STMJR VARCHAR,STDID INT)"));
//		LinkedList<String> cols= new LinkedList<>();
//		cols.add("name");
//		cols.add("family");
//		
//		Table table = new Table("table1", cols, null);
//		Row toInsert=new Row();
//		HashMap<String, String>row=new HashMap<>();
//		row.put("name", "borna");
//		row.put("family", "ghtb");
//		toInsert.setColumns(row);
//		
//		table.insert(toInsert);
//		
//		toInsert=new Row();
//		row=new HashMap<>();
//		row.put("name", "mohi");
//		row.put("family", "kjhgf");
//		toInsert.setColumns(row);
//		
//		table.insert(toInsert);
//		
//	//	table.createIndex("chert", "name");
//		LinkedList<String> col=new LinkedList<>();
//		col.add("family");
//		col.add("name");
//		
//		table.select(col);
////		HashMap<String, LinkedList<Long>> indexed=table.getIndexes().get(0).index;
////		for(String key:indexed.keySet()){
////			LinkedList<Long> ids=table.getIndexes().get(0).index.get(key);
////			for(Long id:ids)
////				System.err.println(id);
//		}
	}
}
