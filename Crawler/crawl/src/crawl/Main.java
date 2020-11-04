package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		int num = 1;/**/
		File file = new File("r.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String paperUrl;
			int paperID = num - 1;

			while ((paperUrl = br.readLine()) != null) {
//				System.out.println(paperUrl);
				paperID++;
				ScopusBasicInfo bi = new ScopusBasicInfo(paperID);
				bi.crawl(paperUrl);
			}
			System.out.println("paperCounter : " + paperID);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
