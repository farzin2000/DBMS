import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws IOException {
		
		Scanner input2 = new Scanner(System.in);
		FileWriter w = new FileWriter(new File("out.txt"));
		long a =  System.currentTimeMillis();
		System.out.println("started");
		while(input2.hasNext())
		{
			String rawinput=input2.nextLine();

			System.out.println(Parser.parseQuery(rawinput));
		}
		long b = System.currentTimeMillis();
		System.out.println((double)(b-a)/1000);
		w.close();
		
		
	}
}
