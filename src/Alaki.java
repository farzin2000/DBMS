import java.util.ArrayList;
import java.util.List;

public interface Alaki {
	public static void main(String[] args) {
		String computable="1+4";
		if(Character.isDigit(computable.charAt(0)))
		{
			if(!computable.contains("+")&&!computable.contains("-")&&!computable.contains("*")&&!computable.contains("/")) {
				List<String>oneNum=new ArrayList<>();
				int count=0;
				while(count!=computable.length()) {
					oneNum.add(computable.charAt(count)+"");
					count++;
				}
				StringBuilder listString = new StringBuilder();
				for (String s : oneNum)
					listString.append(s+"");
				System.err.println(Integer.parseInt(listString.toString())+"");
			}
			int count=0;
			Integer num=null;
			char lastOperand = 0;
			List<String>oneNum=new ArrayList<>();
			while(count!=computable.length()+1) {
				if(count!=computable.length()&&Character.isDigit(computable.charAt(count))) {
					oneNum.add(computable.charAt(count)+"");
					count++;
					continue;
				}
				else if(num==null) {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s+"");
					num=Integer.parseInt(listString.toString());
					lastOperand=computable.charAt(count);
					count++;
					oneNum=new ArrayList<>();
				}
				else {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s+"");
					switch (lastOperand) {
					case '+':
						num+=Integer.parseInt(listString.toString());
						break;
					case '-':
						num-=Integer.parseInt(listString.toString());
						break;
					case '*':
						num*=Integer.parseInt(listString.toString());
						break;
					case '/':
						num/=Integer.parseInt(listString.toString());
						break;
					}
					if(count==computable.length())
						break;
					lastOperand=computable.charAt(count);
					count++;
					oneNum=new ArrayList<>();
				}
			}
			System.err.println(String.valueOf(num));
		}
	}
}
